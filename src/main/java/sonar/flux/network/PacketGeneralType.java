package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public enum PacketGeneralType {
	CREATE_NETWORK(PacketGeneralHelper::doNetworkCreationPacket), //
	EDIT_NETWORK(PacketGeneralHelper::doNetworkEditPacket), //
	DELETE_NETWORK(PacketGeneralHelper::doNetworkDeletePacket), //
	ADD_PLAYER(PacketGeneralHelper::doAddPlayerPacket), //
	REMOVE_PLAYER(PacketGeneralHelper::doRemovePlayerPacket), //
	CHANGE_PLAYER(PacketGeneralHelper::doChangePlayerPacket), //
	DEBUG_CONNECTED_BLOCKS(PacketGeneralHelper::doResetConnectedBlocksPacket), //
	DEBUG_FLUX_CONNECTIONS(PacketGeneralHelper::doValidateConnectionsPacket),
	GUI_STATE_CHANGE(PacketGeneralHelper::doStateChangePacket),
	SWITCH_OWNERSHIP(PacketGeneralHelper::doChangeNetworkOwner);
	public IPacketAction action;

	PacketGeneralType(IPacketAction action) {
		this.action = action;
	}

	public IMessage doPacket(EntityPlayer player, NBTTagCompound packetTag) {
		return action.doPacket(player, packetTag);
	}

	public interface IPacketAction {
		IMessage doPacket(EntityPlayer player, NBTTagCompound packetTag);
	}
}