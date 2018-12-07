package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper;
import sonar.flux.api.ClientFlux;

import java.util.ArrayList;
import java.util.List;

public class PacketDisconnectedTiles implements IMessage {

    public List<ClientFlux> client_flux;

    public PacketDisconnectedTiles(){}

    public PacketDisconnectedTiles(List<ClientFlux> client_flux){
        this.client_flux = client_flux;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        client_flux = new ArrayList<>();
        clientFluxFromBuf(buf, client_flux);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        clientFluxToBuf(buf, client_flux);
    }

    public static void clientFluxFromBuf(ByteBuf buf, List<ClientFlux> tiles){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            NBTTagCompound tag = ByteBufUtils.readTag(buf);
            tiles.add(new ClientFlux(tag));
        }
    }

    public static void clientFluxToBuf(ByteBuf buf, List<ClientFlux> tiles){
        buf.writeInt(tiles.size());
        for(int i = 0; i < tiles.size(); i ++){
            ClientFlux flux = tiles.get(i);
            ByteBufUtils.writeTag(buf, flux.writeData(new NBTTagCompound(), NBTHelper.SyncType.SAVE));
        }
    }

    public static class Handler implements IMessageHandler<PacketDisconnectedTiles, IMessage> {

        @Override
        public IMessage onMessage(PacketDisconnectedTiles message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
                ClientNetworkCache.instance().disconnected_tiles = message.client_flux;
            });
            return null;
        }
    }
}
