package fluxnetworks.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public enum PacketGeneralType {
    CREATE_NETWORK(PacketGeneralHandler::handleCreateNetworkPacket),
    EDIT_NETWORK(PacketGeneralHandler::handleNetworkEditPacket),
    DELETE_NETWORK(PacketGeneralHandler::handleDeleteNetworkPacket),
    /*ADD_MEMBER(PacketGeneralHandler::handleAddMemberPacket),
    REMOVE_MEMBER(PacketGeneralHandler::handleRemoveMemberPacket),*/
    CHANGE_PERMISSION(PacketGeneralHandler::handleChangePermissionPacket),
    CHANGE_WIRELESS(PacketGeneralHandler::handleChangeWirelessPacket);

    public IPacketGeneralHandler handler;

    PacketGeneralType(IPacketGeneralHandler handler) {
        this.handler = handler;
    }

    public interface IPacketGeneralHandler {
        IMessage handlePacket(EntityPlayer player, NBTTagCompound nbtTag);
    }
}
