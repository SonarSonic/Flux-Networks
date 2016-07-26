package sonar.flux.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.network.PacketCoords;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.IFluxCommon.AccessType;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.client.GuiState;
import sonar.flux.common.ContainerFlux;
import sonar.flux.common.tileentity.TileEntityFlux;

public class PacketFluxButton extends PacketCoords {

	public Type type;
	public Object[] objects;

	public enum Type {
		
		/** when something on the list is clicked, requires the network ID to set and the owners name, if no such network is found a new network will be created */
		SET_NETWORK(0) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				String owner = (String) objs[1];
				if (flux.getNetwork().getNetworkID() == networkID) {
					return;
				}
				IFluxCommon network = FluxNetworks.cache.getNetwork(networkID);
				if (!network.isFakeNetwork() && network instanceof IFluxNetwork) {
					flux.getNetwork().removeFluxConnection(flux);
					flux.changeNetwork((IFluxNetwork) network, player);
				}
			}
		},
		/** creates a new network if the id is not already taken, requires the new name **/
		CREATE_NETWORK(1) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				String newName = (String) objs[0];
				CustomColour colour = (CustomColour) objs[1];
				AccessType access = (AccessType) objs[2];
				if (flux.getNetwork().getNetworkName().equals(newName) && flux.getNetwork().getOwnerName().equals(player.getName())) {
					return;
				}
				IFluxCommon network = FluxNetworks.cache.createNetwork(player.getName(), newName, colour, access);
				flux.changeNetwork((IFluxNetwork) network, player);
			}
		},
		/** requires the network ID and the new name */
		EDIT_NETWORK(2) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				String owner = (String) objs[1];
				String newName = (String) objs[2];
				CustomColour colour = (CustomColour) objs[3];
				AccessType access = (AccessType) objs[4];
				IFluxCommon common = FluxNetworks.cache.getNetwork(networkID);		
				if (!common.isFakeNetwork() && common instanceof IFluxNetwork) {
					IFluxNetwork network = ((IFluxNetwork) common);
					network.setNetworkName(newName);
					network.setAccessType(access);
					network.setCustomColour(colour);
					network.sendChanges();
				}
			}
		},
		/** requires the network ID to delete */
		DELETE_NETWORK(3) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				String playerName = player.getName();
				IFluxCommon toDelete = FluxNetworks.cache.getNetwork(networkID);
				if (!toDelete.isFakeNetwork() && toDelete instanceof IFluxNetwork && toDelete.getOwnerName().equals(playerName)) {
					FluxNetworks.cache.deleteNetwork(playerName, (IFluxNetwork) toDelete);
				}
				//FluxNetworks.cache.clearNetworks();
			}
		},
		/** requires the priority to set */
		SET_PRIORITY(4) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int priority = (Integer) objs[0];
				flux.priority.setObject(priority);
			}
		},
		/** requires the limit to set (should be a long) */
		SET_LIMIT(5) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				long priority = (Integer) objs[0];
				flux.limit.setObject(priority);
			}
		},
		/** requires the limit to set (should be a long) */
		CAN_CREATE(6) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				ArrayList<IFluxNetwork> common = FluxNetworks.cache.getAllNetworks();
				if (common != null) {
					for (IFluxCommon network : common) {
						if (network.getNetworkName().equals((String) objs[0])) {
							//player
						}
					}
				}
			}
		},
		STATE_CHANGE(-1) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				GuiState state = (GuiState) objs[0];
				Container container = player.openContainer;
				if (container != null && container instanceof ContainerFlux) {
					((ContainerFlux) container).switchState(player, flux, state);
				}
			}
		};
		;
		public int id;

		Type(int id) {
			this.id = id;
		}

		public static Type getTypeFromID(int id) {
			for (Type type : Type.values()) {
				if (type.id == id) {
					return type;
				}
			}
			return null;
		}

		public Object[] fromBuf(ByteBuf buf) {
			Object[] objs = null;
			switch (this) {
			case SET_NETWORK:
				objs = new Object[2];
				objs[0] = buf.readInt();
				objs[1] = ByteBufUtils.readUTF8String(buf);
				break;
			case CREATE_NETWORK:
				objs = new Object[3];
				objs[0] = ByteBufUtils.readUTF8String(buf);
				objs[1] = CustomColour.readFromBuf(buf);
				objs[2] = AccessType.values()[buf.readInt()];
				break;
			case EDIT_NETWORK:
				objs = new Object[5];
				objs[0] = buf.readInt();
				objs[1] = ByteBufUtils.readUTF8String(buf);
				objs[2] = ByteBufUtils.readUTF8String(buf);
				objs[3] = CustomColour.readFromBuf(buf);
				objs[4] = AccessType.values()[buf.readInt()];
				break;
			case DELETE_NETWORK:
				objs = new Object[1];
				objs[0] = buf.readInt();
				break;
			case SET_PRIORITY:
				objs = new Object[1];
				objs[0] = buf.readInt();
				break;
			case SET_LIMIT:
				objs = new Object[1];
				objs[0] = buf.readLong();
				break;
			case STATE_CHANGE:
				objs = new Object[1];
				objs[0] = GuiState.values()[buf.readInt()];
				break;
			default:
				objs = null;
				break;
			}
			return objs;
		}

		public Object[] toBuf(ByteBuf buf, Object[] objs) {
			switch (this) {
			case SET_NETWORK:
				buf.writeInt((Integer) objs[0]);
				ByteBufUtils.writeUTF8String(buf, (String) objs[1]);
				break;
			case CREATE_NETWORK:
				ByteBufUtils.writeUTF8String(buf, (String) objs[0]);
				CustomColour.writeToBuf((CustomColour) objs[1], buf);
				buf.writeInt(((AccessType) objs[2]).ordinal());
				break;
			case EDIT_NETWORK:
				buf.writeInt((Integer) objs[0]);
				ByteBufUtils.writeUTF8String(buf, (String) objs[1]);
				ByteBufUtils.writeUTF8String(buf, (String) objs[2]);
				CustomColour.writeToBuf((CustomColour) objs[3], buf);
				buf.writeInt(((AccessType) objs[4]).ordinal());
				break;
			case DELETE_NETWORK:
				buf.writeInt((Integer) objs[0]);
				break;
			case SET_PRIORITY:
				buf.writeInt((Integer) objs[0]);
				break;
			case SET_LIMIT:
				buf.writeLong((Long) objs[0]);
				break;
			case STATE_CHANGE:
				buf.writeInt(((GuiState) objs[0]).ordinal());
				break;
			default:
				objs = null;
				break;
			}
			return objs;
		}

		public abstract void process(TileEntityFlux flux, EntityPlayer player, Object[] objs);
	}

	public PacketFluxButton() {
	}

	public PacketFluxButton(Type type, BlockPos pos, Object... obj) {
		super(pos);
		this.type = type;
		this.objects = obj;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.type = Type.getTypeFromID(buf.readInt());
		this.objects = type.fromBuf(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(type.id);
		type.toBuf(buf, objects);
	}

	public static class Handler implements IMessageHandler<PacketFluxButton, IMessage> {

		@Override
		public IMessage onMessage(PacketFluxButton message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (player != null && player.worldObj != null) {
				TileEntity te = player.worldObj.getTileEntity(message.pos);
				if (te instanceof TileEntityFlux && message.objects != null) {
					TileEntityFlux flux = (TileEntityFlux) te;
					message.type.process(flux, player, message.objects);
				}
			}
			return null;
		}
	}
}
