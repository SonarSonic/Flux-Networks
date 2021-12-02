package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.CommonRegistration.sNetwork;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class ClientMessages {

    public static void sendDeviceEntity(TileFluxDevice device, byte id, Object val) {
        assert id > 0;
        var buf = NetworkHandler.buffer(Messages.C2S_DEVICE_ENTITY);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(id);
        switch (id) {
            case FluxConstants.C2S_CUSTOM_NAME -> buf.writeUtf((String) val, 96);
            case FluxConstants.C2S_PRIORITY -> buf.writeVarInt(((Number) val).intValue());
            case FluxConstants.C2S_LIMIT -> buf.writeVarLong(((Number) val).longValue());
            default -> {
                return;
            }
        }
        sNetwork.sendToServer(buf);
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<LocalPlayer> player) {
        Minecraft minecraft = Minecraft.getInstance();
        switch (index) {
            case Messages.S2C_DEVICE_ENTITY -> onDeviceEntity(payload, player, minecraft);
        }
    }

    private static void onDeviceEntity(FriendlyByteBuf payload, Supplier<LocalPlayer> player,
                                       BlockableEventLoop<?> looper) {
        looper.execute(() -> {
            LocalPlayer p = player.get();
            if (p != null && p.clientLevel.getBlockEntity(payload.readBlockPos()) instanceof TileFluxDevice e) {
                byte id = payload.readByte();
                e.readPacket(payload, p, id);
            }
            payload.release();
        });
        payload.retain();
    }
}
