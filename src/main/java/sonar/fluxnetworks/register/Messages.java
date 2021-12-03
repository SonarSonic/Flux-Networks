package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import icyllis.modernui.forge.PacketDispatcher;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.CommonRegistration.sNetwork;

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

    static final int S2C_DEVICE_BUFFER = 0;
    static final int S2C_RESPONSE = 1;

    @Nonnull
    public static PacketDispatcher getDeviceBuffer(TileFluxDevice device, byte id) {
        assert id < 0;
        var buf = NetworkHandler.buffer(S2C_DEVICE_BUFFER);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(id);
        device.writePacket(buf, id);
        return sNetwork.dispatch(buf);
    }

    private static void sendResponse(byte transaction, byte code, Player player) {
        var buf = NetworkHandler.buffer(S2C_RESPONSE);
        buf.writeByte(transaction);
        buf.writeByte(code);
        sNetwork.dispatch(buf).sendToPlayer(player);
    }

    @Nonnull
    static NetworkHandler.ClientListener msg() {
        return ClientMessages::msg;
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<ServerPlayer> player) {
        MinecraftServer server = player.get().getLevel().getServer();
        switch (index) {
            case C2S_DEVICE_BUFFER -> onDeviceBuffer(payload, player, server);
            case C2S_SUPER_ADMIN -> onSuperAdmin(payload, player, server);
            default -> kick(player.get(), new RuntimeException());
        }
    }

    private static void kick(ServerPlayer p, RuntimeException e) {
        p.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_packet"));
        FluxNetworks.LOGGER.info("Kicked {} due to protocol attack", p.getGameProfile().getName(), e);
    }

    private static void error(Throwable t) {
        FluxNetworks.LOGGER.error("An error occurred during packet handling", t);
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
                            throw new RuntimeException();
                        }
                        if (payload.readableBytes() > 0) {
                            throw new DecoderException();
                        }
                    }
                }
            } catch (RuntimeException e) {
                kick(p, e);
            } catch (Throwable t) {
                error(t);
            }
            payload.release();
        });
        payload.retain();
    }

    private static void onSuperAdmin(FriendlyByteBuf payload, Supplier<ServerPlayer> player,
                                     BlockableEventLoop<?> looper) {

    }
}
