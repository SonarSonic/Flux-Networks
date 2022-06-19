package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.client.ClientCache;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.Registration.sNetwork;

/**
 * C2S message specs and S2C message handling.
 *
 * @see Messages
 */
@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class ClientMessages {

    public static void deviceBuffer(TileFluxDevice device, byte type) {
        assert type > 0; // C2S positive
        var buf = NetworkHandler.buffer(Messages.C2S_DEVICE_BUFFER);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(type);
        device.writePacket(buf, type);
        sNetwork.sendToServer(buf);
    }

    /**
     * Request super admin permission.
     *
     * @param token a token, can be invalid (0)
     */
    public static void superAdmin(int token, boolean enable) {
        var buf = NetworkHandler.buffer(Messages.C2S_SUPER_ADMIN);
        buf.writeByte(token);
        buf.writeBoolean(enable);
        sNetwork.sendToServer(buf);
    }

    /**
     * Request to create a new network
     */
    public static void createNetwork(int token, String name, int color,
                                     SecurityLevel security, String password) {
        var buf = NetworkHandler.buffer(Messages.C2S_CREATE_NETWORK);
        buf.writeByte(token);
        buf.writeUtf(name, 256);
        buf.writeInt(color);
        buf.writeByte(security.getKey());
        if (security.isEncrypted()) {
            buf.writeUtf(password, 256);
        }
        sNetwork.sendToServer(buf);
    }

    /**
     * Request to delete an existing network
     */
    public static void deleteNetwork(int token, FluxNetwork network) {
        var buf = NetworkHandler.buffer(Messages.C2S_DELETE_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        sNetwork.sendToServer(buf);
    }

    public static void editDevice(TileFluxDevice device, CompoundTag tag) {
        var buf = NetworkHandler.buffer(Messages.C2S_EDIT_DEVICE);
        buf.writeVarInt(FluxConstants.INVALID_NETWORK_ID);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeNbt(tag);
        sNetwork.sendToServer(buf);
    }

    public static void editDevice(int networkId, List<GlobalPos> list, CompoundTag tag) {
        if (list.isEmpty()) return;
        var buf = NetworkHandler.buffer(Messages.C2S_EDIT_DEVICE);
        buf.writeVarInt(networkId);
        buf.writeVarInt(list.size());
        for (var pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        buf.writeNbt(tag);
        sNetwork.sendToServer(buf);
    }

    public static void editMember(int token, FluxNetwork network, UUID uuid, byte type) {
        var buf = NetworkHandler.buffer(Messages.C2S_EDIT_MEMBER);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        buf.writeUUID(uuid);
        buf.writeByte(type);
        sNetwork.sendToServer(buf);
    }

    // set (connect to) network for a block entity
    public static void setTileNetwork(int token, TileFluxDevice device, int networkID, String password) {
        var buf = NetworkHandler.buffer(Messages.C2S_CONNECT_DEVICE);
        buf.writeByte(token);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeVarInt(networkID);
        buf.writeUtf(password, 256);
        sNetwork.sendToServer(buf);
    }

    public static void editNetwork(int token, FluxNetwork network, String name, int color,
                                   SecurityLevel security, String password, int wireless) {
        var buf = NetworkHandler.buffer(Messages.C2S_EDIT_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        buf.writeUtf(name, 256);
        buf.writeInt(color);
        buf.writeByte(security.getKey());
        if (security.isEncrypted()) {
            buf.writeUtf(password, 256);
        }
        buf.writeInt(wireless);
        sNetwork.sendToServer(buf);
    }

    /**
     * Request the server to update all certain data of a network.
     *
     * @param token a valid token
     */
    public static void updateNetwork(int token, FluxNetwork network, byte type) {
        var buf = NetworkHandler.buffer(Messages.C2S_UPDATE_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(1); // size
        buf.writeVarInt(network.getNetworkID());
        buf.writeByte(type);
        sNetwork.sendToServer(buf);
    }

    /**
     * Request the server to update all certain data of networks.
     *
     * @param token a valid token
     */
    public static void updateNetwork(int token, Collection<FluxNetwork> networks, byte type) {
        if (networks.isEmpty()) {
            return;
        }
        var buf = NetworkHandler.buffer(Messages.C2S_UPDATE_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(networks.size());
        for (var network : networks) {
            buf.writeVarInt(network.getNetworkID());
        }
        buf.writeByte(type);
        sNetwork.sendToServer(buf);
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<LocalPlayer> player) {
        Minecraft minecraft = Minecraft.getInstance();
        switch (index) {
            case Messages.S2C_DEVICE_BUFFER -> onDeviceBuffer(payload, player, minecraft);
            case Messages.S2C_RESPONSE -> onResponse(payload, player, minecraft);
            case Messages.S2C_SUPER_ADMIN -> onSuperAdmin(payload, player, minecraft);
            case Messages.S2C_UPDATE_NETWORK -> onUpdateNetwork(payload, player, minecraft);
            case Messages.S2C_DELETE_NETWORK -> onDeleteNetwork(payload, player, minecraft);
        }
    }

    private static void onDeviceBuffer(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                       BlockableEventLoop<?> looper) {
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p != null && p.clientLevel.getBlockEntity(payload.readBlockPos()) instanceof TileFluxDevice e) {
                byte id = payload.readByte();
                if (id < 0) {
                    e.readPacket(payload, id);
                }
            }
            payload.release();
        });
        throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
    }

    private static void onResponse(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                   BlockableEventLoop<?> looper) {
        final int token = payload.readByte();
        final int key = payload.readShort();
        final int code = payload.readByte();
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p == null) {
                return;
            }
            if (p.containerMenu.containerId == token &&
                    p.containerMenu instanceof FluxMenu m &&
                    m.mOnResultListener != null) {
                m.mOnResultListener.onResult(m, key, code);
            }
        });
    }

    private static void onSuperAdmin(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                     BlockableEventLoop<?> looper) {
        final boolean enable = payload.readBoolean();
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p == null) {
                return;
            }
            ClientCache.sSuperAdmin = enable;
            // this is lightweight, so do not trigger an event explicitly
        });
    }

    private static void onUpdateNetwork(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                        BlockableEventLoop<?> looper) {
        final byte type = payload.readByte();
        final int size = payload.readVarInt();
        final Int2ObjectMap<CompoundTag> map = new Int2ObjectArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            final int id = payload.readVarInt();
            final CompoundTag tag = payload.readNbt();
            assert tag != null;
            map.put(id, tag);
        }
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p == null) {
                return;
            }
            ClientCache.updateNetwork(map, type);
            if (p.containerMenu instanceof FluxMenu m && m.mOnResultListener != null) {
                m.mOnResultListener.onResult(m, FluxConstants.REQUEST_UPDATE_NETWORK, 0);
            }
        });
    }

    private static void onDeleteNetwork(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                        BlockableEventLoop<?> looper) {
        final int id = payload.readVarInt();
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p == null) {
                return;
            }
            ClientCache.deleteNetwork(id);
            if (p.containerMenu instanceof FluxMenu m && m.mOnResultListener != null) {
                m.mOnResultListener.onResult(m, FluxConstants.REQUEST_DELETE_NETWORK, 0);
            }
        });
    }
}
