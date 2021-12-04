package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import icyllis.modernui.forge.PacketDispatcher;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkManager;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.Registration.sNetwork;

/**
 * Network messages.
 * <p>
 * Security check is necessary on the server side.
 *
 * @author BloCamLimb
 * @since 7.0
 */
@ParametersAreNonnullByDefault
public class Messages {

    static final int C2S_DEVICE_BUFFER = 0;
    static final int C2S_SUPER_ADMIN = 1;
    static final int C2S_EDIT_DEVICE = 2;
    static final int C2S_CREATE_NETWORK = 3;

    static final int S2C_DEVICE_BUFFER = 0;
    static final int S2C_RESPONSE = 1;
    static final int S2C_SUPER_ADMIN = 2;

    @Nonnull
    public static PacketDispatcher getDeviceBuffer(TileFluxDevice device, byte id) {
        assert id < 0;
        var buf = NetworkHandler.buffer(S2C_DEVICE_BUFFER);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(id);
        device.writePacket(buf, id);
        return sNetwork.dispatch(buf);
    }

    private static void sendResponse(byte transaction, int code, Player player) {
        if (transaction < 0) return;
        var buf = NetworkHandler.buffer(S2C_RESPONSE);
        buf.writeByte(transaction);
        buf.writeByte(code);
        sNetwork.dispatch(buf).sendToPlayer(player);
    }

    public static void sendSuperAdmin(boolean enable, Player player) {
        var buf = NetworkHandler.buffer(S2C_SUPER_ADMIN);
        buf.writeBoolean(enable);
        sNetwork.dispatch(buf).sendToPlayer(player);
    }

    @Nonnull
    static NetworkHandler.ClientListener msg() {
        return ClientMessages::msg;
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<ServerPlayer> player) {
        MinecraftServer server = player.get().getLevel().getServer();
        try {
            switch (index) {
                case C2S_DEVICE_BUFFER -> onDeviceBuffer(payload, player, server);
                case C2S_SUPER_ADMIN -> onSuperAdmin(payload, player, server);
                case C2S_EDIT_DEVICE -> onEditDevice(payload, player, server);
                default -> kick(player.get(), new RuntimeException("Unidentified message index " + index));
            }
        } catch (RuntimeException e) {
            kick(player.get(), e);
        }
    }

    private static void kick(ServerPlayer p, RuntimeException e) {
        p.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_packet"));
        FluxNetworks.LOGGER.info("Kicked {} due to protocol attack", p.getGameProfile().getName(), e);
    }

    private static void onDeviceBuffer(FriendlyByteBuf payload, Supplier<ServerPlayer> player,
                                       BlockableEventLoop<?> looper) {
        looper.execute(() -> {
            ServerPlayer p = player.get();
            try {
                if (p != null && p.getLevel().getBlockEntity(payload.readBlockPos()) instanceof TileFluxDevice e) {
                    if (e.canPlayerAccess(p)) {
                        byte id = payload.readByte();
                        if (id > 0) {
                            e.readPacket(payload, id);
                        } else {
                            throw new IllegalStateException();
                        }
                        if (payload.readableBytes() > 0) {
                            throw new DecoderException();
                        }
                    }
                }
            } catch (RuntimeException e) {
                kick(p, e);
            }
            payload.release();
        });
        payload.retain();
    }

    private static void onSuperAdmin(FriendlyByteBuf payload, Supplier<ServerPlayer> player,
                                     BlockableEventLoop<?> looper) {

    }

    private static void onEditDevice(FriendlyByteBuf payload, Supplier<ServerPlayer> player,
                                     BlockableEventLoop<?> looper) {
        int networkId = payload.readVarInt();
        if (networkId == FluxConstants.INVALID_NETWORK_ID) {
            BlockPos pos = payload.readBlockPos();
            CompoundTag tag = payload.readNbt();
            Objects.requireNonNull(tag);
            looper.execute(() -> {
                ServerPlayer p = player.get();
                if (p == null) return;
                try {
                    if (p.getLevel().getBlockEntity(pos) instanceof TileFluxDevice e) {
                        if (e.canPlayerAccess(p)) {
                            e.readCustomTag(tag, FluxConstants.TYPE_TILE_SETTING);
                        }
                    }
                } catch (RuntimeException e) {
                    kick(p, e);
                }
            });
        } else {
            int size = payload.readVarInt();
            if (size <= 0) {
                throw new IllegalStateException();
            }
            List<GlobalPos> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add(FluxUtils.readGlobalPos(payload));
            }
            CompoundTag tag = payload.readNbt();
            Objects.requireNonNull(tag);
            looper.execute(() -> {
                ServerPlayer p = player.get();
                if (p == null) return;
                try {
                    FluxNetwork network = FluxNetworkManager.getNetwork(networkId);
                    if (network.getPlayerAccess(p).canEdit()) {
                        for (GlobalPos pos : list) {
                            network.getConnectionByPos(pos).ifPresent(f -> {
                                if (f instanceof TileFluxDevice e) {
                                    e.readCustomTag(tag, FluxConstants.TYPE_TILE_SETTING);
                                }
                            });
                        }
                    }
                } catch (RuntimeException e) {
                    kick(p, e);
                }
            });
        }
        if (payload.readableBytes() > 0) {
            throw new DecoderException();
        }
    }
}
