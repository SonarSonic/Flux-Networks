package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.flux.common.tileentity.TileFlux;

public enum PacketTileType {
	SET_NETWORK(PacketTileHelper::doNetworkSetPacket), //
	SET_PRIORITY(PacketTileHelper::doSetPriorityPacket), //
	SET_TRANSFER_LIMIT(PacketTileHelper::doSetTransferLimitPacket), //
	REMOVE_CONNECTION(PacketTileHelper::doDisconnectPacket), //
	GUI_STATE_CHANGE(PacketTileHelper::doStateChangePacket);
	public IPacketAction action;

	PacketTileType(IPacketAction action) {
		this.action = action;
	}

	public IMessage doPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag) {
		return action.doPacket(source, player, packetTag);
	}

	public interface IPacketAction {
		IMessage doPacket(TileFlux source, EntityPlayer player, NBTTagCompound packetTag);
	}
}