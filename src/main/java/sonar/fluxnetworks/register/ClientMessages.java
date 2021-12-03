package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static sonar.fluxnetworks.register.CommonRegistration.sNetwork;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class ClientMessages {

    private static final AtomicInteger sNextTransactionId = new AtomicInteger();

    private static final Callback[] sCommittedTransactions = new Callback[64];

    // from 0 to 63
    private static int generateTransactionId() {
        int v;
        do {
            v = sNextTransactionId.get();
        } while (!sNextTransactionId.compareAndSet(v, (v + 1) & 0x3F));
        return v;
    }

    /**
     * Callback for any request message. Note that callback will be invoked on Netty IO thread.
     */
    @FunctionalInterface
    public interface Callback {

        void onResponse(byte code);
    }

    public static void sendDeviceBuffer(TileFluxDevice device, byte id) {
        assert id > 0;
        var buf = NetworkHandler.buffer(Messages.C2S_DEVICE_BUFFER);
        buf.writeBlockPos(device.getBlockPos());
        buf.writeByte(id);
        device.writePacket(buf, id);
        sNetwork.sendToServer(buf);
    }

    public static void sendSuperAdmin(boolean enable, @Nullable Callback cb) {
        var buf = NetworkHandler.buffer(Messages.C2S_SUPER_ADMIN);
        if (cb != null) {
            int transaction = generateTransactionId();
            buf.writeByte(transaction);
            synchronized (sCommittedTransactions) {
                sCommittedTransactions[transaction] = cb;
            }
        } else {
            buf.writeByte(-1);
        }
        buf.writeBoolean(enable);
        sNetwork.sendToServer(buf);
    }

    static void msg(short index, FriendlyByteBuf payload, Supplier<LocalPlayer> player) {
        Minecraft minecraft = Minecraft.getInstance();
        switch (index) {
            case Messages.S2C_DEVICE_BUFFER -> onDeviceBuffer(payload, player, minecraft);
            case Messages.S2C_RESPONSE -> onResponse(payload);
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
        payload.retain();
    }

    private static void onResponse(FriendlyByteBuf payload) {
        byte key = payload.readByte();
        byte code = payload.readByte();
        synchronized (sCommittedTransactions) {
            Callback cb = sCommittedTransactions[key];
            if (cb != null) {
                cb.onResponse(code);
                sCommittedTransactions[key] = null;
            }
        }
    }
}
