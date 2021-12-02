package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import icyllis.modernui.forge.PacketDispatcher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.CommonRegistration.sNetwork;

@ParametersAreNonnullByDefault
public class Messages {

    static final int C2S_DEVICE_ENTITY = 0;

    static final int S2C_DEVICE_ENTITY = 0;

    @Nonnull
    public static PacketDispatcher getDeviceEntity(TileFluxDevice device, byte id) {
        assert id < 0;
        var buf = NetworkHandler.buffer(S2C_DEVICE_ENTITY);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(id);
        device.writePacket(buf, id);
        return sNetwork.dispatch(buf);
    }

    @Nonnull
    static NetworkHandler.ClientListener msg() {
        return ClientMessages::msg;
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<ServerPlayer> player) {
        MinecraftServer server = player.get().getLevel().getServer();
        switch (index) {
            case C2S_DEVICE_ENTITY -> onDeviceEntity(payload, player, server);
        }
    }

    private static void kick(ServerPlayer p, RuntimeException e) {
        p.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_packet"));
        FluxNetworks.LOGGER.info("{} is kicked due to an invalid packet", p.getGameProfile().getName(), e);
    }

    private static void error(Throwable t) {
        FluxNetworks.LOGGER.error("An error occurred during packet handling", t);
    }

    private static void onDeviceEntity(FriendlyByteBuf payload, Supplier<ServerPlayer> player,
                                       BlockableEventLoop<?> looper) {
        looper.execute(() -> {
            ServerPlayer p = player.get();
            try {
                if (p != null && p.getLevel().getBlockEntity(payload.readBlockPos()) instanceof TileFluxDevice e) {
                    byte id = payload.readByte();
                    e.readPacket(payload, p, id);
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
}
