package fluxnetworks.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public enum PacketGeneralType {
    CREATE_NETWORK(PacketGeneralHandler::handleCreateNetworkPacket),
    DELETE_NETWORK(PacketGeneralHandler::handleDeleteNetworkPacket);

    public IPacketGeneralHandler handler;

    PacketGeneralType(IPacketGeneralHandler handler) {
        this.handler = handler;
    }

    public interface IPacketGeneralHandler {
        IMessage handlePacket(EntityPlayer player, NBTTagCompound nbtTag);
    }
}
