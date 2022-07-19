package sonar.fluxnetworks.register;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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

import static sonar.fluxnetworks.register.Channel.sChannel;

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
        var buf = Channel.buffer(Messages.C2S_DEVICE_BUFFER);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(type);
        device.writePacketBuffer(buf, type);
        sChannel.sendToServer(buf);
    }

    /**
     * Request super admin permission.
     *
     * @param token a token, can be invalid (0)
     */
    public static void superAdmin(int token, boolean enable) {
        var buf = Channel.buffer(Messages.C2S_SUPER_ADMIN);
        buf.writeByte(token);
        buf.writeBoolean(enable);
        sChannel.sendToServer(buf);
    }

    /**
     * Request to create a new network.
     *
     * @param token must be valid
     */
    public static void createNetwork(int token, String name, int color,
                                     SecurityLevel security, String password) {
        var buf = Channel.buffer(Messages.C2S_CREATE_NETWORK);
        buf.writeByte(token);
        buf.writeUtf(name, 256);
        buf.writeInt(color);
        buf.writeByte(security.getId());
        if (security == SecurityLevel.ENCRYPTED) {
            buf.writeUtf(password, 256);
        }
        sChannel.sendToServer(buf);
    }

    /**
     * Request to delete an existing network.
     *
     * @param token must be valid
     */
    public static void deleteNetwork(int token, FluxNetwork network) {
        var buf = Channel.buffer(Messages.C2S_DELETE_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        sChannel.sendToServer(buf);
    }

    /**
     * Request to edit an interacting block entity.
     *
     * @param token must be valid
     */
    public static void editTile(int token, TileFluxDevice device, CompoundTag tag) {
        var buf = Channel.buffer(Messages.C2S_EDIT_TILE);
        buf.writeByte(token);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeNbt(tag);
        sChannel.sendToServer(buf);
    }

    // set (connect to) network for a block entity
    public static void tileNetwork(int token, TileFluxDevice device, FluxNetwork network, String password) {
        var buf = Channel.buffer(Messages.C2S_TILE_NETWORK);
        buf.writeByte(token);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeVarInt(network.getNetworkID());
        buf.writeUtf(password, 256);
        sChannel.sendToServer(buf);
    }

    public static void editMember(int token, FluxNetwork network, UUID uuid, byte type) {
        var buf = Channel.buffer(Messages.C2S_EDIT_MEMBER);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        buf.writeUUID(uuid);
        buf.writeByte(type);
        sChannel.sendToServer(buf);
    }

    public static void editNetwork(int token, FluxNetwork network, String name, int color,
                                   SecurityLevel security, String password) {
        var buf = Channel.buffer(Messages.C2S_EDIT_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        buf.writeUtf(name, 256);
        buf.writeInt(color);
        buf.writeByte(security.getId());
        if (security == SecurityLevel.ENCRYPTED) {
            buf.writeUtf(password, 256);
        }
        sChannel.sendToServer(buf);
    }

    public static void editConnection(int token, FluxNetwork network, List<GlobalPos> list, CompoundTag tag) {
        if (list.isEmpty()) {
            return;
        }
        var buf = Channel.buffer(Messages.C2S_EDIT_CONNECTION);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        buf.writeVarInt(list.size());
        for (var pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        buf.writeNbt(tag);
        sChannel.sendToServer(buf);
    }

    /**
     * Request the server to update all certain data of a network.
     *
     * @param token a valid token
     */
    public static void updateNetwork(int token, FluxNetwork network, byte type) {
        var buf = Channel.buffer(Messages.C2S_UPDATE_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(1); // size
        buf.writeVarInt(network.getNetworkID());
        buf.writeByte(type);
        sChannel.sendToServer(buf);
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
        var buf = Channel.buffer(Messages.C2S_UPDATE_NETWORK);
        buf.writeByte(token);
        buf.writeVarInt(networks.size());
        for (var network : networks) {
            buf.writeVarInt(network.getNetworkID());
        }
        buf.writeByte(type);
        sChannel.sendToServer(buf);
    }

    public static void wirelessMode(int token, int wirelessMode, int wirelessNetwork) {
        var buf = Channel.buffer(Messages.C2S_WIRELESS_MODE);
        buf.writeByte(token);
        buf.writeInt(wirelessMode);
        buf.writeVarInt(wirelessNetwork);
        sChannel.sendToServer(buf);
    }

    public static void disconnect(int token, FluxNetwork network, List<GlobalPos> list) {
        if (list.isEmpty()) {
            return;
        }
        var buf = Channel.buffer(Messages.C2S_DISCONNECT);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        buf.writeVarInt(list.size());
        for (var pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        sChannel.sendToServer(buf);
    }

    public static void updateConnections(int token, FluxNetwork network, List<GlobalPos> list) {
        if (list.isEmpty()) {
            return;
        }
        var buf = Channel.buffer(Messages.C2S_UPDATE_CONNECTIONS);
        buf.writeByte(token);
        buf.writeVarInt(network.getNetworkID());
        buf.writeVarInt(list.size());
        for (var pos : list) {
            FluxUtils.writeGlobalPos(buf, pos);
        }
        sChannel.sendToServer(buf);
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<LocalPlayer> player) {
        Minecraft minecraft = Minecraft.getInstance();
        switch (index) {
            case Messages.S2C_DEVICE_BUFFER -> onDeviceBuffer(payload, player, minecraft);
            case Messages.S2C_RESPONSE -> onResponse(payload, player, minecraft);
            case Messages.S2C_CAPABILITY -> onCapability(payload, player, minecraft);
            case Messages.S2C_UPDATE_NETWORK -> onUpdateNetwork(payload, player, minecraft);
            case Messages.S2C_DELETE_NETWORK -> onDeleteNetwork(payload, player, minecraft);
            case Messages.S2C_UPDATE_CONNECTIONS -> onUpdateConnections(payload, player, minecraft);
        }
    }

    private static void onDeviceBuffer(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                       BlockableEventLoop<?> looper) {
        payload.retain();
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p != null && p.clientLevel.getBlockEntity(payload.readBlockPos()) instanceof TileFluxDevice e) {
                byte id = payload.readByte();
                if (id < 0) {
                    e.readPacketBuffer(payload, id);
                }
            }
            payload.release();
        });
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

    private static void onCapability(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                     BlockableEventLoop<?> looper) {
        final boolean superAdmin = payload.readBoolean();
        final int wirelessMode = payload.readInt();
        final int wirelessNetwork = payload.readVarInt();
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p == null) {
                return;
            }
            ClientCache.sSuperAdmin = superAdmin;
            ClientCache.sWirelessMode = wirelessMode;
            ClientCache.sWirelessNetwork = wirelessNetwork;
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

    private static void onUpdateConnections(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                        BlockableEventLoop<?> looper) {
        final int id = payload.readVarInt();
        final int size = payload.readVarInt();
        final List<CompoundTag> tags = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            tags.add(payload.readNbt());
        }
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p == null) {
                return;
            }
            ClientCache.updateConnections(id, tags);
            if (p.containerMenu instanceof FluxMenu m && m.mOnResultListener != null) {
                m.mOnResultListener.onResult(m, FluxConstants.REQUEST_UPDATE_CONNECTION, 0);
            }
        });
    }
}
