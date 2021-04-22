package sonar.fluxnetworks.common.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.network.SecurityType;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.GuiFluxAdminHome;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class C2SNetMsg {

    private static final NetworkHandler sNetwork = NetworkHandler.sInstance;

    // indices are defined in S2CNetMsg
    static final Functor[] sFunctors = new Functor[]{
            C2SNetMsg::tileEntity, // 0
            C2SNetMsg::feedback, // 1
            C2SNetMsg::updateSuperAdmin, // 2
            C2SNetMsg::lavaEffect, // 3
            C2SNetMsg::updateNetwork, // 4
            C2SNetMsg::updateAccess, // 5
            C2SNetMsg::updateConnection}; // 6

    @FunctionalInterface
    interface Functor {

        // handle S2C packets
        void f(@Nonnull PacketBuffer payload, @Nonnull ClientPlayerEntity player);
    }

    // request to change flux tile entity settings
    public static void tileEntity(@Nonnull TileFluxDevice tile, byte type) {
        PacketBuffer buf = sNetwork.targetAt(0);
        buf.writeBlockPos(tile.getPos());
        buf.writeByte(type);
        tile.writePacket(buf, type);
        sNetwork.sendToServer(buf);
    }

    // request to enable/disable super admin permission
    public static void requestSuperAdmin() {
        PacketBuffer buf = sNetwork.targetAt(1);
        sNetwork.sendToServer(buf);
    }

    // edit member permission
    public static void editMember(int networkID, UUID playerChanged, int type) {
        PacketBuffer buf = sNetwork.targetAt(2);
        buf.writeVarInt(networkID);
        buf.writeUniqueId(playerChanged);
        buf.writeVarInt(type);
        sNetwork.sendToServer(buf);
    }

    // edit network settings
    public static void editNetwork(int networkID, String name, int color, @Nonnull SecurityType security,
                                   String password) {
        PacketBuffer buf = sNetwork.targetAt(3);
        buf.writeVarInt(networkID);
        buf.writeString(name, 256);
        buf.writeInt(color);
        buf.writeVarInt(security.ordinal());
        buf.writeString(password, 256);
        sNetwork.sendToServer(buf);
    }

    // edit wireless mode
    public static void editWireless(int networkID, int mode) {
        PacketBuffer buf = sNetwork.targetAt(4);
        buf.writeVarInt(networkID);
        buf.writeVarInt(mode);
        sNetwork.sendToServer(buf);
    }

    // request a network update
    public static void requestNetworkUpdate(@Nonnull IFluxNetwork network, int type) {
        PacketBuffer buf = sNetwork.targetAt(5);
        buf.writeVarInt(type);
        buf.writeVarInt(1); // size = 1
        buf.writeVarInt(network.getNetworkID());
        sNetwork.sendToServer(buf);
    }

    // request a network update
    public static void requestNetworkUpdate(@Nonnull Collection<IFluxNetwork> networks, int type) {
        PacketBuffer buf = sNetwork.targetAt(5);
        buf.writeVarInt(type);
        buf.writeVarInt(networks.size());
        networks.forEach(net -> buf.writeVarInt(net.getNetworkID()));
        sNetwork.sendToServer(buf);
    }

    // set (connect to) network for a flux tile entity
    public static void setNetwork(BlockPos pos, int networkID, String password) {
        PacketBuffer buf = sNetwork.targetAt(6);
        buf.writeBlockPos(pos);
        buf.writeVarInt(networkID);
        buf.writeString(password, 256);
        sNetwork.sendToServer(buf);
    }

    // create a flux network
    public static void createNetwork(String name, int color, @Nonnull SecurityType security, String password) {
        PacketBuffer buf = sNetwork.targetAt(7);
        buf.writeString(name, 256);
        buf.writeInt(color);
        buf.writeVarInt(security.ordinal());
        buf.writeString(password, 256);
        sNetwork.sendToServer(buf);
    }

    // delete a flux network
    public static void deleteNetwork(int networkID) {
        PacketBuffer buf = sNetwork.targetAt(8);
        buf.writeVarInt(networkID);
        sNetwork.sendToServer(buf);
    }

    // request a network access level update for GUI
    public static void requestAccessUpdate(int networkID) {
        PacketBuffer buf = sNetwork.targetAt(9);
        buf.writeVarInt(networkID);
        sNetwork.sendToServer(buf);
    }

    public static void disconnect(int networkID, @Nonnull List<GlobalPos> list) {
        PacketBuffer buf = sNetwork.targetAt(10);
        buf.writeVarInt(networkID);
        buf.writeVarInt(FluxConstants.FLAG_EDIT_DISCONNECT);
        buf.writeVarInt(list.size());
        list.forEach(pos -> FluxUtils.writeGlobalPos(buf, pos));
        sNetwork.sendToServer(buf);
    }

    public static void editConnections(int networkID, @Nonnull List<GlobalPos> list, int flags, String name, int priority,
                                       long limit, boolean surgeMode, boolean disableLimit, boolean chunkLoading) {
        PacketBuffer buf = sNetwork.targetAt(10);
        buf.writeVarInt(networkID);
        buf.writeVarInt(flags);
        buf.writeVarInt(list.size());
        list.forEach(pos -> FluxUtils.writeGlobalPos(buf, pos));
        if ((flags & FluxConstants.FLAG_EDIT_NAME) != 0) {
            buf.writeString(name, 0x100);
        }
        if ((flags & FluxConstants.FLAG_EDIT_PRIORITY) != 0) {
            buf.writeInt(priority);
        }
        if ((flags & FluxConstants.FLAG_EDIT_LIMIT) != 0) {
            buf.writeLong(limit);
        }
        if ((flags & FluxConstants.FLAG_EDIT_SURGE_MODE) != 0) {
            buf.writeBoolean(surgeMode);
        }
        if ((flags & FluxConstants.FLAG_EDIT_DISABLE_LIMIT) != 0) {
            buf.writeBoolean(disableLimit);
        }
        if ((flags & FluxConstants.FLAG_EDIT_CHUNK_LOADING) != 0) {
            buf.writeBoolean(chunkLoading);
        }
        sNetwork.sendToServer(buf);
    }

    public static void requestConnectionUpdate(int networkID, @Nonnull List<GlobalPos> list) {
        PacketBuffer buf = sNetwork.targetAt(11);
        buf.writeVarInt(networkID);
        buf.writeVarInt(list.size());
        list.forEach(pos -> FluxUtils.writeGlobalPos(buf, pos));
        sNetwork.sendToServer(buf);
    }

    public static void configuratorNet(int id, String password) {
        PacketBuffer buf = sNetwork.targetAt(12);
        buf.writeVarInt(id);
        buf.writeString(password, 256);
        sNetwork.sendToServer(buf);
    }

    public static void configuratorEdit(String customName, CompoundNBT tag) {
        PacketBuffer buf = sNetwork.targetAt(13);
        buf.writeString(customName, 256);
        buf.writeCompoundTag(tag);
        sNetwork.sendToServer(buf);
    }

    ///  HANDLING  \\\

    private static void tileEntity(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
        final TileEntity tile = player.world.getTileEntity(buf.readBlockPos());
        if (tile instanceof TileFluxDevice) {
            ((TileFluxDevice) tile).readPacket(buf, buf.readByte());
        }
    }

    private static void feedback(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
        final FeedbackInfo info = FeedbackInfo.values()[buf.readVarInt()];
        final boolean action = info.action();
        final Screen screen = Minecraft.getInstance().currentScreen;
        if (!action) {
            FluxClientCache.setFeedbackText(info);
        } else if (screen instanceof GuiFluxCore) {
            ((GuiFluxCore) screen).onFeedbackAction(info);
        }
    }

    private static void updateSuperAdmin(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
        FluxClientCache.superAdmin = buf.readBoolean();
        final Screen screen = Minecraft.getInstance().currentScreen;
        if (screen instanceof GuiFluxAdminHome) {
            ((GuiFluxAdminHome) screen).superAdmin.toggled = FluxClientCache.superAdmin;
        }
    }

    private static void lavaEffect(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
        final BlockPos pos = buf.readBlockPos();
        final int count = buf.readVarInt();
        final ClientWorld world = player.worldClient;
        if (world != null) {
            for (int i = 0; i < count; i++) {
                world.addParticle(ParticleTypes.LAVA,
                        pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0);
            }
        }
    }

    private static void updateNetwork(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
        final int type = buf.readVarInt();
        Int2ObjectMap<CompoundNBT> updatedNetworks = new Int2ObjectArrayMap<>();
        final int size = buf.readVarInt();
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            updatedNetworks.put(buf.readVarInt(), buf.readCompoundTag());
        }
        FluxClientCache.updateNetworks(updatedNetworks, type);
    }

    private static void updateAccess(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
        AccessLevel access = AccessLevel.values()[buf.readVarInt()];
        Screen screen = Minecraft.getInstance().currentScreen;
        if (screen instanceof GuiFluxCore) {
            GuiFluxCore gui = (GuiFluxCore) screen;
            gui.accessLevel = access;
        }
    }

    private static void updateConnection(@Nonnull PacketBuffer buf, @Nonnull ClientPlayerEntity player) {
        final int networkID = buf.readVarInt();
        final int size = buf.readVarInt();
        final List<CompoundNBT> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            tags.add(buf.readCompoundTag());
        }
        FluxClientCache.updateConnections(networkID, tags);
    }
}
