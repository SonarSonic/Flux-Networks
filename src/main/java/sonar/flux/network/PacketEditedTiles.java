package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.flux.api.AdditionType;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.ConnectionSettings;
import sonar.flux.api.RemovalType;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.common.tileentity.TileFlux;

import java.util.ArrayList;
import java.util.List;

public class PacketEditedTiles implements IMessage {

    public List<ClientFlux> client_flux;

    public PacketEditedTiles(){}

    public PacketEditedTiles(List<ClientFlux> client_flux){
        this.client_flux = client_flux;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        client_flux = new ArrayList<>();
        PacketDisconnectedTiles.clientFluxFromBuf(buf, client_flux);
    }

    @Override
    public void toBytes(ByteBuf buf) {
       PacketDisconnectedTiles.clientFluxToBuf(buf, client_flux);
    }

    public static class Handler implements IMessageHandler<PacketEditedTiles, IMessage> {

        @Override
        public IMessage onMessage(PacketEditedTiles message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
                EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
                for(ClientFlux clientFlux : message.client_flux){
                    if(clientFlux.getCoords().isChunkLoaded()) {
                        TileEntity tile = clientFlux.getCoords().getTileEntity();
                        if (tile instanceof TileFlux) {
                            TileFlux flux = (TileFlux) tile;
                            if(flux.canAccess(player).canEdit()) {
                                flux.priority.setValue(clientFlux.priority);
                                flux.folder_id.setValue(clientFlux.folder_id);
                                flux.limit.setValue(clientFlux.limit);
                                flux.customName.setValue(clientFlux.customName);
                                flux.disableLimit.setValue(clientFlux.disableLimit);
                                flux.activation_type.setValue(clientFlux.activation_type);
                                flux.priority_type.setValue(clientFlux.priority_type);


                                if(flux.getNetworkID() != clientFlux.getNetworkID()){
                                    IFluxNetwork newNetwork = FluxNetworkCache.instance().getNetwork(clientFlux.network_id);
                                    flux.getNetwork().queueConnectionRemoval(flux, RemovalType.REMOVE);
                                    newNetwork.queueConnectionAddition(flux, AdditionType.ADD);
                                }

                                flux.markSettingChanged(ConnectionSettings.PRIORITY);
                                flux.markSettingChanged(ConnectionSettings.TRANSFER_LIMIT);
                                flux.markSettingChanged(ConnectionSettings.FOLDER_ID);
                                flux.markSettingChanged(ConnectionSettings.CUSTOM_NAME);
                            }
                        }
                    }
                }
            });
            return null;
        }
    }
}
