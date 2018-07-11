package sonar.flux.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.FluxNetworks;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.connection.NetworkSettings;
import sonar.flux.connection.transfer.stats.NetworkStatistics;

public class PacketNetworkStatistics implements IMessage {

    public int networkID;
    public NetworkStatistics stats;
    public NBTTagCompound received;

    public PacketNetworkStatistics() {}

    public PacketNetworkStatistics(IFluxNetwork network) {
        this.networkID = network.getSetting(NetworkSettings.NETWORK_ID);
        this.stats = network.getSetting(NetworkSettings.NETWORK_STATISTICS);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        networkID = buf.readInt();
        received = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(networkID);
        ByteBufUtils.writeTag(buf, stats.writeData(new NBTTagCompound(), SyncType.SAVE));
    }

    public static class Handler implements IMessageHandler<PacketNetworkStatistics, IMessage> {

        @Override
        public IMessage onMessage(PacketNetworkStatistics message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                SonarCore.proxy.getThreadListener(ctx.side).addScheduledTask(() -> {
                    IFluxNetwork common = FluxNetworks.getClientCache().getNetwork(message.networkID);
                    if (!common.isFakeNetwork()) {
                        common.getSetting(NetworkSettings.NETWORK_STATISTICS).readData(message.received, SyncType.SAVE);
                    }
                });
            }
            return null;
        }
    }

}