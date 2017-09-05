package sonar.flux.network;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.helpers.SonarHelper;
import sonar.core.network.PacketCoords;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxError;
import sonar.flux.api.network.IFluxCommon;
import sonar.flux.api.network.IFluxCommon.AccessType;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.common.ContainerFlux;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.connection.FluxHelper;

import java.util.ArrayList;
import java.util.UUID;

public class PacketFluxButton extends PacketCoords {

	public Type type;
	public Object[] objects;
	public int dimension;

	public enum Type {

        /**
         * when something on the list is clicked, requires the network ID to set and the owners name, if no such network is found a new network will be created
         */
		SET_NETWORK(0, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				if (flux.getNetwork().getNetworkID() == networkID) {
					return;
				}
                IFluxNetwork network = FluxNetworks.getServerCache().getNetwork(networkID);
                if (!network.isFakeNetwork()) {
                    if (network.getPlayerAccess(player).canConnect()) {
                        flux.getNetwork().removeConnection(flux);
                        network.addConnection(flux);
					} else {
						FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.ACCESS_DENIED), (EntityPlayerMP) player);
					}
				}
			}
		},
        /**
         * creates a new network if the id is not already taken, requires the new name
         **/
		CREATE_NETWORK(1, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				String newName = (String) objs[0];
				CustomColour colour = (CustomColour) objs[1];
				AccessType access = (AccessType) objs[2];
                if (flux.getNetwork().isFakeNetwork()) {
                    IFluxNetwork network = FluxNetworks.getServerCache().createNetwork(player, newName, colour, access);
                    flux.getNetwork().removeConnection(flux);
                    network.addConnection(flux);
				}
			}
		},
        /**
         * requires the network ID and the new name
         */
		EDIT_NETWORK(2, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				String newName = (String) objs[1];
				CustomColour colour = (CustomColour) objs[2];
				AccessType access = (AccessType) objs[3];
				IFluxNetwork common = FluxNetworks.getServerCache().getNetwork(networkID);
                if (!common.isFakeNetwork()) {
					if (common.getPlayerAccess(player).canEdit()) {
						common.setNetworkName(newName);
						common.setAccessType(access);
						common.setCustomColour(colour);
                        common.markDirty();
					} else {
						FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.EDIT_NETWORK), (EntityPlayerMP) player);
					}
				}
			}
		},
        /**
         * requires the network ID to delete
         */
		DELETE_NETWORK(3, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				IFluxNetwork toDelete = FluxNetworks.getServerCache().getNetwork(networkID);

				if (!toDelete.isFakeNetwork() && toDelete instanceof IFluxNetwork) {
					if (toDelete.getPlayerAccess(player).canDelete()) {
                        FluxNetworks.getServerCache().onPlayerRemoveNetwork(FluxHelper.getOwnerUUID(player), toDelete);
					} else {
						FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.NOT_OWNER), (EntityPlayerMP) player);
					}
				}
			}
		},
        /**
         * requires the priority to set
         */
		SET_PRIORITY(4, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int priority = (Integer) objs[0];
				flux.priority.setObject(priority);
			}
		},
        /**
         * requires the limit to set (should be a long)
         */
		SET_LIMIT(5, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				long priority = (Integer) objs[0];
				flux.limit.setObject(priority);
			}
		},
        /**
         * requires the limit to set (should be a long)
         */
		CAN_CREATE(6, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				ArrayList<IFluxNetwork> common = FluxNetworks.getServerCache().getAllNetworks();
				if (common != null) {
					for (IFluxCommon network : common) {
                        if (network.getNetworkName().equals(objs[0])) {
							// player
						}
					}
				}
			}
		},
		ADD_PLAYER(7, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				GameProfile profile = SonarHelper.getGameProfileForUsername((String) objs[1]);
				if (profile == null || profile.getId() == null) {
					FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.INVALID_USER), (EntityPlayerMP) player);
					return;
				}
				UUID newPlayer = profile.getId();
				PlayerAccess access = (PlayerAccess) objs[2];
				IFluxCommon common = FluxNetworks.getServerCache().getNetwork(networkID);
				if (!common.isFakeNetwork() && common instanceof IFluxNetwork) {
					if (((IFluxNetwork) common).getPlayerAccess(player).canEdit()) {
                        IFluxNetwork network = (IFluxNetwork) common;
						network.addPlayerAccess(newPlayer, access);
                        network.markDirty();
					} else {
						FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.EDIT_NETWORK), (EntityPlayerMP) player);
					}
				}
			}
		},
		REMOVE_PLAYER(8, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				UUID newPlayer = (UUID) objs[1];
				PlayerAccess access = (PlayerAccess) objs[2];
				IFluxCommon common = FluxNetworks.getServerCache().getNetwork(networkID);
				if (!common.isFakeNetwork() && common instanceof IFluxNetwork) {
					if (((IFluxNetwork) common).getPlayerAccess(player).canEdit()) {
                        IFluxNetwork network = (IFluxNetwork) common;
						network.removePlayerAccess(newPlayer, access);
                        network.markDirty();
					} else {
						FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.EDIT_NETWORK), (EntityPlayerMP) player);
					}
				}
			}
		},
		CHANGE_PLAYER(9, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				int networkID = (Integer) objs[0];
				UUID newPlayer = (UUID) objs[1];
				if (newPlayer != null) {
					PlayerAccess access = (PlayerAccess) objs[2];
					IFluxCommon common = FluxNetworks.getServerCache().getNetwork(networkID);
					if (!common.isFakeNetwork() && common instanceof IFluxNetwork) {
						if (((IFluxNetwork) common).getPlayerAccess(player).canEdit()) {
                            if (!FluxHelper.getOwnerUUID(player).equals(newPlayer)) {
                                IFluxNetwork network = (IFluxNetwork) common;
								network.addPlayerAccess(newPlayer, access);
                                network.markDirty();
							} else {
								// FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.NOT_OWNER), (EntityPlayerMP) player);
							}
						} else {
							FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.EDIT_NETWORK), (EntityPlayerMP) player);
						}
					}
				} else {
					FluxNetworks.network.sendTo(new PacketFluxError(flux.getPos(), FluxError.INVALID_USER), (EntityPlayerMP) player);
				}
			}
		},
		REMOVE_CONNECTION(10, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
				IFluxNetwork network = flux.getNetwork();
                if ((Integer) objs[0] == network.getNetworkID() && network.getPlayerAccess(player).canConnect() && flux.playerUUID.getUUID().equals(FluxHelper.getOwnerUUID(player))) {
                    network.removeConnection(flux);
                    //network.sendChanges(); //TODO trigger this another wa
				}
			}
		},
		STATE_CHANGE(-1, true) {
			@Override
			public void process(TileEntityFlux flux, EntityPlayer player, Object[] objs) {
                GuiTypeMessage state = (GuiTypeMessage) objs[0];
				Container container = player.openContainer;
				if (container != null && container instanceof ContainerFlux) {
                    ((ContainerFlux) container).switchState(state);
				}
			}
		};
        /**
         * the type's identification number
         */
		public int id;
        /**
         * if this affects the Flux Connection used to send the command (e.g. editing another connection is not local)
         */
		public boolean local;

		Type(int id, boolean local) {
			this.id = id;
			this.local = local;
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
            Object[] objs;
			switch (this) {
			case SET_NETWORK:
			case REMOVE_CONNECTION:
			case DELETE_NETWORK:
			case SET_PRIORITY:
				objs = new Object[1];
				objs[0] = buf.readInt();
				break;
			case CREATE_NETWORK:
				objs = new Object[3];
				objs[0] = ByteBufUtils.readUTF8String(buf);
				objs[1] = CustomColour.readFromBuf(buf);
				objs[2] = AccessType.values()[buf.readInt()];
				break;
			case EDIT_NETWORK:
				objs = new Object[4];
				objs[0] = buf.readInt();
				objs[1] = ByteBufUtils.readUTF8String(buf);
				objs[2] = CustomColour.readFromBuf(buf);
				objs[3] = AccessType.values()[buf.readInt()];
				break;
			case SET_LIMIT:
				objs = new Object[1];
				objs[0] = buf.readLong();
				break;
			case STATE_CHANGE:
				objs = new Object[1];
                    objs[0] = GuiTypeMessage.values()[buf.readInt()];
				break;
			case ADD_PLAYER:
				objs = new Object[3];
				objs[0] = buf.readInt();
				objs[1] = ByteBufUtils.readUTF8String(buf);
				objs[2] = PlayerAccess.values()[buf.readByte()];
				break;
			case REMOVE_PLAYER:
			case CHANGE_PLAYER:
				objs = new Object[3];
				objs[0] = buf.readInt();
				objs[1] = UUID.fromString(ByteBufUtils.readUTF8String(buf));
				objs[2] = PlayerAccess.values()[buf.readByte()];
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
			case REMOVE_CONNECTION:
			case DELETE_NETWORK:
			case SET_PRIORITY:
				buf.writeInt((Integer) objs[0]);
				break;
			case CREATE_NETWORK:
				ByteBufUtils.writeUTF8String(buf, (String) objs[0]);
				CustomColour.writeToBuf((CustomColour) objs[1], buf);
				buf.writeInt(((AccessType) objs[2]).ordinal());
				break;
			case EDIT_NETWORK:
				buf.writeInt((Integer) objs[0]);
				ByteBufUtils.writeUTF8String(buf, (String) objs[1]);
				CustomColour.writeToBuf((CustomColour) objs[2], buf);
				buf.writeInt(((AccessType) objs[3]).ordinal());
				break;
			case SET_LIMIT:
				buf.writeLong((Long) objs[0]);
				break;
			case STATE_CHANGE:
                    buf.writeInt(((GuiTypeMessage) objs[0]).ordinal());
				break;
			case ADD_PLAYER:
				buf.writeInt((Integer) objs[0]);
				ByteBufUtils.writeUTF8String(buf, (String) objs[1]);
				buf.writeByte(((PlayerAccess) objs[2]).ordinal());
				break;
			case REMOVE_PLAYER:
			case CHANGE_PLAYER:
				buf.writeInt((Integer) objs[0]);
                    ByteBufUtils.writeUTF8String(buf, objs[1].toString());
				buf.writeByte(((PlayerAccess) objs[2]).ordinal());
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

    /**
     * must be used if the TYPE isn't local
     */
	public PacketFluxButton(int dimension, Type type, BlockPos pos, Object... obj) {
        this(type, pos, obj);
		this.dimension = dimension;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.type = Type.getTypeFromID(buf.readInt());
		if (!type.local)
			dimension = buf.readInt();
		this.objects = type.fromBuf(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(type.id);
		if (!type.local)
			buf.writeInt(dimension);
		type.toBuf(buf, objects);
	}

	public static class Handler implements IMessageHandler<PacketFluxButton, IMessage> {

		@Override
		public IMessage onMessage(PacketFluxButton message, MessageContext ctx) {
            SonarCore.proxy.getThreadListener(ctx).addScheduledTask(() -> {
					EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
					if (player != null && player.getEntityWorld() != null) {
						World world = player.getEntityWorld();
						if (!message.type.local) {
							MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                        world = server.getWorld(message.dimension);
						}
						TileEntity te = world.getTileEntity(message.pos);
						if (te instanceof TileEntityFlux && message.objects != null) {
							TileEntityFlux flux = (TileEntityFlux) te;
							message.type.process(flux, player, message.objects);
						}
					}
            });
			
			return null;
		}
	}
}
