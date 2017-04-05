package sonar.flux.connection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.SonarCore;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyContainerHandler;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.utils.ActionType;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxPlayer;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFluxController;
import sonar.flux.api.IFluxController.TransmitterMode;
import sonar.flux.common.tileentity.TileEntityStorage;

public class FluxHelper {

	public static long pullEnergy(IFlux from, long maxTransferRF, ActionType actionType) {
		long extracted = 0;
		maxTransferRF = Math.min(maxTransferRF, from.getCurrentTransferLimit());
		if (from != null && maxTransferRF != 0) {
			switch (from.getConnectionType()) {
			case PLUG:
				TileEntity[] tiles = from.cachedTiles();
				for (int i = 0; i < 6; i++) {
					TileEntity tile = tiles[i];
					if (tile != null) {
						long remove = SonarAPI.getEnergyHelper().extractEnergy(tile, Math.min(maxTransferRF - extracted, from.getCurrentTransferLimit()), EnumFacing.values()[i].getOpposite(), actionType);
						if (!actionType.shouldSimulate())
							from.onEnergyRemoved(remove);
						extracted += remove;
					}
				}
				break;
			case STORAGE:
				TileEntityStorage tile = (TileEntityStorage) from;
				int remove = tile.storage.extractEnergy((int) Math.min(maxTransferRF - extracted, Integer.MAX_VALUE), actionType.shouldSimulate());
				if (!actionType.shouldSimulate())
					from.onEnergyRemoved(remove);
				extracted += remove;
				break;
			default:
				break;
			}
		}
		return extracted;
	}

	public static long pushEnergy(IFlux to, long maxTransferRF, ActionType actionType) {
		long received = 0;
		maxTransferRF = Math.min(maxTransferRF, to.getCurrentTransferLimit());
		if (to != null && maxTransferRF != 0) {
			// BlockCoords coords = to.getCoords();
			switch (to.getConnectionType()) {
			case POINT:
				TileEntity[] tiles = to.cachedTiles();
				for (int i = 0; i < 6; i++) {
					TileEntity tile = tiles[i];
					if (tile != null) {
						long added = SonarAPI.getEnergyHelper().receiveEnergy(tile, Math.min(maxTransferRF - received, to.getCurrentTransferLimit()), EnumFacing.values()[i].getOpposite(), actionType);
						if (!actionType.shouldSimulate())
							to.onEnergyAdded(added);
						received += added;
					}
				}
				break;
			case STORAGE:
				TileEntityStorage tile = (TileEntityStorage) to;
				int added = tile.storage.receiveEnergy((int) Math.min(maxTransferRF - received, Integer.MAX_VALUE), actionType.shouldSimulate());
				if (!actionType.shouldSimulate())
					to.onEnergyAdded(added);
				received += added;
				break;
			case CONTROLLER:
				IFluxController controller = (IFluxController) to;
				if (controller.getTransmitterMode() == TransmitterMode.OFF) {
					break;
				}
				ArrayList<FluxPlayer> playerNames = (ArrayList<FluxPlayer>) controller.getNetwork().getPlayers().clone();
				ArrayList<EntityPlayer> players = new ArrayList();
				for (FluxPlayer player : playerNames) {
					Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(player.id);
					if (entity != null && entity instanceof EntityPlayer) {
						players.add((EntityPlayer) entity);
					}
				}
				for (EntityPlayer player : players) {
					long receive = 0;
					switch (controller.getTransmitterMode()) {
					case HELD_ITEM:
						ItemStack stack = player.getHeldItemMainhand();
						if (stack != null && FluxHelper.canTransferEnergy(stack) != null) {
							receive = SonarAPI.getEnergyHelper().receiveEnergy(stack, maxTransferRF - received, actionType);
							received += receive;
							if (!actionType.shouldSimulate())
								to.onEnergyRemoved(receive);
							if (maxTransferRF - received <= 0) {
								break;
							}
						}
						break;
					case HOTBAR:
					case ON:
						IInventory inv = player.inventory;
						for (int i = 0; i < ((controller.getTransmitterMode() == TransmitterMode.ON) ? inv.getSizeInventory() : 9); i++) {
							ItemStack itemStack = inv.getStackInSlot(i);
							if (itemStack != null && FluxHelper.canTransferEnergy(itemStack) != null) {
								receive = SonarAPI.getEnergyHelper().receiveEnergy(itemStack, maxTransferRF - received, actionType);
								received += receive;
								if (!actionType.shouldSimulate())
									to.onEnergyRemoved(receive);
								if (maxTransferRF - received <= 0) {
									break;
								}
							}
						}
						break;
					default:
						break;
					}
				}

				break;
			default:
				break;
			}
		}
		return received;
	}

	public static List<ISonarEnergyHandler> getEnergyHandlers() {
		ArrayList<ISonarEnergyHandler> handlers = new ArrayList();
		for (ISonarEnergyHandler handler : SonarCore.energyHandlers) {
			if (FluxConfig.transfers.get(handler.getProvidedType()).a) {
				handlers.add(handler);
			}
		}
		return handlers;
	}

	public static List<ISonarEnergyContainerHandler> getEnergyContainerHandlers() {
		ArrayList<ISonarEnergyContainerHandler> handlers = new ArrayList();
		for (ISonarEnergyContainerHandler handler : SonarCore.energyContainerHandlers) {
			if (FluxConfig.transfers.get(handler.getProvidedType()).b) {
				handlers.add(handler);
			}
		}
		return handlers;
	}

	public static ISonarEnergyHandler canTransferEnergy(TileEntity tile, EnumFacing dir) {
		List<ISonarEnergyHandler> handlers = FluxNetworks.energyHandlers;
		for (ISonarEnergyHandler handler : handlers) {
			if (handler.canProvideEnergy(tile, dir)) {
				return handler;
			}
		}
		return null;
	}

	public static ISonarEnergyContainerHandler canTransferEnergy(ItemStack stack) {
		List<ISonarEnergyContainerHandler> handlers = FluxNetworks.energyContainerHandlers;
		for (ISonarEnergyContainerHandler handler : handlers) {
			if (handler.canHandleItem(stack)) {
				return handler;
			}
		}
		return null;
	}
	/** gets all the TileEntities which can send/receive energy adjacent to the given IFlux */
	/* public Map<TileEntity, EnumFacing> getConnections(IFlux flux) { Map<TileEntity, EnumFacing> tiles = new HashMap(); for (EnumFacing face : EnumFacing.VALUES) { World world = flux.getDimension(); TileEntity tile = world.getTileEntity(flux.getCoords().getBlockPos().offset(face)); if (tile == null || tile.isInvalid()) { continue; } if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, face) != null) { tiles.put(tile, face); } } return tiles; } */
}
