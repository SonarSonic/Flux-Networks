package sonar.fluxnetworks.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.FluxNetworkModel;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FluxClientCache {

    private static final Int2ObjectOpenHashMap<IFluxNetwork> networks = new Int2ObjectOpenHashMap<>();

    public static boolean superAdmin = false;
    public static boolean detailedNetworkView = false;

    public static int adminViewingNetwork = FluxConstants.INVALID_NETWORK_ID;

    private static FeedbackInfo feedback = FeedbackInfo.NONE; // Text message.

    private static int feedbackTimer = 0;

    public static void release() {
        networks.clear();
        networks.trim();
        adminViewingNetwork = FluxConstants.INVALID_NETWORK_ID;
        feedback = FeedbackInfo.NONE;
    }

    public static void updateNetworks(@Nonnull Int2ObjectMap<CompoundTag> serverSideNetworks, int type) {
        for (Int2ObjectMap.Entry<CompoundTag> entry : serverSideNetworks.int2ObjectEntrySet()) {
            int id = entry.getIntKey();
            CompoundTag nbt = entry.getValue();
            IFluxNetwork network = networks.get(id);
            if (type == FluxConstants.TYPE_NET_DELETE) {
                if (network != null) {
                    networks.remove(id);
                }
            } else {
                if (network == null) {
                    network = new FluxNetworkModel();
                    network.readCustomTag(nbt, type);
                    networks.put(id, network);
                } else {
                    network.readCustomTag(nbt, type);
                }
            }
        }
    }

    public static void updateConnections(int networkID, List<CompoundTag> tags) {
        IFluxNetwork network = networks.get(networkID);
        if (network != null) {
            for (CompoundTag tag : tags) {
                GlobalPos globalPos = FluxUtils.readGlobalPos(tag);
                network.getConnectionByPos(globalPos).ifPresent(c -> c.readCustomTag(tag,
                        FluxConstants.TYPE_CONNECTION_UPDATE));
            }
        }
    }

    @Nonnull
    public static IFluxNetwork getNetwork(int id) {
        return networks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    public static String getDisplayName(@Nonnull CompoundTag subTag) {
        IFluxNetwork network = getNetwork(subTag.getInt(FluxConstants.NETWORK_ID));
        if (network.isValid()) {
            return network.getNetworkName();
        }
        return "NONE";
    }

    @Nonnull
    public static String getFeedbackText() {
        return feedback.getText();
    }

    public static int getFeedbackColor() {
        return 16733525 | (int) (Math.min(feedbackTimer, 16) * Math.min(61 - feedbackTimer, 5) * 3.1875f) << 24;
    }

    public static void setFeedbackText(FeedbackInfo info) {
        feedback = info;
        if (feedbackTimer > 0) {
            feedbackTimer = 58;
        } else {
            feedbackTimer = 60;
        }
    }

    public static void tick() {
        if (feedback != FeedbackInfo.NONE) {
            if (--feedbackTimer <= 0) {
                feedback = FeedbackInfo.NONE;
            }
        }
    }

    @Nonnull
    public static List<IFluxNetwork> getAllNetworks() {
        return new ArrayList<>(networks.values());
    }
}
