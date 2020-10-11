package sonar.fluxnetworks.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.SimpleFluxNetwork;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FluxClientCache {

    private static final Int2ObjectMap<IFluxNetwork> NETWORKS = new Int2ObjectArrayMap<>();

    public static void release() {
        NETWORKS.clear();
    }

    public static void updateNetworks(@Nonnull Int2ObjectMap<CompoundNBT> serverSideNetworks, int flags) {
        for (Int2ObjectMap.Entry<CompoundNBT> entry : serverSideNetworks.int2ObjectEntrySet()) {
            int id = entry.getIntKey();
            CompoundNBT nbt = entry.getValue();
            IFluxNetwork network = NETWORKS.get(id);
            if (flags == FluxConstants.FLAG_NET_DELETE) {
                if (network != null) {
                    NETWORKS.remove(id);
                }
            } else {
                if (network == null) {
                    network = new SimpleFluxNetwork();
                    network.readCustomNBT(nbt, flags);
                    NETWORKS.put(id, network);
                } else {
                    network.readCustomNBT(nbt, flags);
                }
            }
        }
    }

    public static void updateDevices(int networkID, List<CompoundNBT> tags) {
        IFluxNetwork network = NETWORKS.get(networkID);
        if (network != null) {
            List<IFluxDevice> devices = network.getAllConnections();
            tags.forEach(t -> {
                GlobalPos globalPos = FluxUtils.readGlobalPos(t);
                devices.stream().filter(f -> f.getGlobalPos().equals(globalPos)).findFirst().ifPresent(f -> f.readCustomNBT(t, NBTType.DEFAULT));
            });
        }
    }

    @Nonnull
    public static IFluxNetwork getNetwork(int id) {
        return NETWORKS.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    @Nonnull
    public static List<IFluxNetwork> getAllNetworks() {
        return new ArrayList<>(NETWORKS.values());
    }
}
