package sonar.fluxnetworks.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.common.handler.PacketHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class AbstractPacket {

    public AbstractPacket() {
    }

    public AbstractPacket(PacketBuffer buf) {
    }

    public final void handle(@Nonnull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> reply(ctx.get(), handle(ctx.get())));
        ctx.get().setPacketHandled(true);
    }

    public abstract void encode(PacketBuffer buf);

    @Nullable
    public abstract Object handle(NetworkEvent.Context ctx);

    public void reply(NetworkEvent.Context ctx, @Nullable Object msg) {
        if (msg != null) {
            PacketHandler.CHANNEL.reply(msg, ctx);
        }
    }
}
