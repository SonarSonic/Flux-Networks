package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

@Deprecated
public enum GeneralPacketEnum {
    //CREATE_NETWORK(GeneralPacketHandler::handleCreateNetworkPacket),
    //EDIT_NETWORK(GeneralPacketHandler::handleNetworkEditPacket),
    //DELETE_NETWORK(GeneralPacketHandler::handleDeleteNetworkPacket),
    /*ADD_MEMBER(PacketGeneralHandler::handleAddMemberPacket),
    REMOVE_MEMBER(PacketGeneralHandler::handleRemoveMemberPacket),*/
    //CHANGE_PERMISSION(GeneralPacketHandler::handleChangePermissionPacket);
    //CHANGE_WIRELESS(GeneralPacketHandler::handleChangeWirelessPacket);
    ;

    public IPacketGeneralHandler handler;

    GeneralPacketEnum(IPacketGeneralHandler handler) {
        this.handler = handler;
    }

    public interface IPacketGeneralHandler {
        Object handlePacket(PlayerEntity player, CompoundNBT nbtTag);
    }
}
