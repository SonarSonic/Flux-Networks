package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import icyllis.modernui.forge.PacketDispatcher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import sonar.fluxnetworks.common.device.FluxDeviceEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.CommonRegistration.sNetwork;

@ParametersAreNonnullByDefault
public class Messages {

    static final int C2S_DEVICE_ENTITY = 0;

    static final int S2C_DEVICE_ENTITY = 0;

    @Nonnull
    public static PacketDispatcher sendDeviceEntity(FluxDeviceEntity device, byte id) {
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

    private static void onDeviceEntity(FriendlyByteBuf payload, Supplier<ServerPlayer> player,
                                       BlockableEventLoop<?> looper) {
        looper.execute(() -> {
            ServerPlayer p = player.get();
            if (p != null && p.getLevel().getBlockEntity(payload.readBlockPos()) instanceof FluxDeviceEntity e) {
                if (e.canPlayerAccess(p)) {
                    byte id = payload.readByte();
                    e.readPacket(payload, id);
                }
            }
            payload.release();
        });
        payload.retain();
    }
}
