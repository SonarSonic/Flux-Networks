package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import sonar.fluxnetworks.common.handler.PacketHandler;

import java.util.function.Supplier;

public abstract class AbstractPacket {

    public AbstractPacket() {}

    public AbstractPacket(PacketBuffer buf) {}

    public final void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            reply(ctx.get(), handle(ctx.get()));
        });
    }

    public abstract void encode(PacketBuffer buf);

    public abstract Object handle(NetworkEvent.Context ctx);

    public void reply(NetworkEvent.Context ctx, Object msg){
        if(msg == null){
            return;
        }
        if(ctx.getDirection() == NetworkDirection.PLAY_TO_SERVER){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->(ServerPlayerEntity)PacketHandler.getPlayer(ctx)), msg);
        }
        if(ctx.getDirection() == NetworkDirection.PLAY_TO_CLIENT){
            PacketHandler.INSTANCE.sendToServer(msg);
        }
    }

    public void reply(PlayerEntity player, Object msg){
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->(ServerPlayerEntity)player), msg);
    }

}
