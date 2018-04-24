package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.flux.common.tileentity.TileFlux;

public enum PacketType {
	SET_NETWORK(PacketHelper::doNetworkSetPacket), //
	CREATE_NETWORK(PacketHelper::doNetworkCreationPacket), //
	EDIT_NETWORK(PacketHelper::doNetworkEditPacket), //
	DELETE_NETWORK(PacketHelper::doNetworkDeletePacket), //
	SET_PRIORITY(PacketHelper::doSetPriorityPacket), //
	SET_TRANSFER_LIMIT(PacketHelper::doSetTransferLimitPacket), ADD_PLAYER(PacketHelper::doAddPlayerPacket), //
	REMOVE_PLAYER(PacketHelper::doRemovePlayerPacket), //
	CHANGE_PLAYER(PacketHelper::doChangePlayerPacket), //
	REMOVE_CONNECTION(PacketHelper::doDisconnectPacket), //
	GUI_STATE_CHANGE(PacketHelper::doStateChangePacket),
	DEBUG_CONNECTED_BLOCKS(PacketHelper::doResetConnectedBlocksPacket), //
	DEBUG_FLUX_CONNECTIONS(PacketHelper::doValidateConnectionsPacket);
	public IPacketAction action;

	PacketType(IPacketAction action) {
		this.action = action;
	}

	public IMessage doPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		return action.doPacket(source, player, packetTag);
	}

	public interface IPacketAction {
		IMessage doPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag);
	}
}