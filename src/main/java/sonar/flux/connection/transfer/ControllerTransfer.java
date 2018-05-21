package sonar.flux.connection.transfer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.ItemStackHelper;
import sonar.flux.api.energy.IItemEnergyHandler;
import sonar.flux.api.energy.internal.IEnergyTransfer;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.tiles.IFluxController.TransmitterMode;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.connection.FluxHelper;

import java.util.ArrayList;
import java.util.List;

public class ControllerTransfer extends BaseFluxTransfer implements IEnergyTransfer {

	public final TileController controller;

	public ControllerTransfer(TileController tile) {
		super(tile.getNetwork().getDefaultEnergyType());
		this.controller = tile;
	}

	@Override
	public long addToNetwork(long maxTransferRF, ActionType actionType) {
		return 0;
	}

	@Override
	public long removeFromNetwork(long maxTransferRF, ActionType actionType) {
		if (controller.getTransmitterMode() == TransmitterMode.OFF) {
			return 0;
		}
		long received = 0;
		List<FluxPlayer> playerNames = controller.getNetwork().getPlayers();
		List<EntityPlayer> players = new ArrayList<>();
		for (FluxPlayer player : playerNames) {
			Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(player.id);
			if (entity instanceof EntityPlayer) {
				players.add((EntityPlayer) entity);
			}
		}
		for (EntityPlayer player : players) {
			long receive;
			switch (controller.getTransmitterMode()) {
			case HELD_ITEM:
				ItemStack stack = player.getHeldItemMainhand();
				IItemEnergyHandler handler = FluxHelper.getValidAdditionHandler(stack);
				if (handler != null) {
					receive = handler.addEnergy(maxTransferRF - received, stack, actionType);
					received += receive;
					if (!actionType.shouldSimulate()) {
						removedFromNetwork(receive, getEnergyType());
					}
					if (maxTransferRF - received <= 0) {
						break;
					}
				}
				break;
			case HOTBAR:
			case ON:
				IInventory inv = player.inventory;
				for (int i = 0; i < (controller.getTransmitterMode() == TransmitterMode.ON ? inv.getSizeInventory() : 9); i++) {
					ItemStack itemStack = inv.getStackInSlot(i);
					handler = FluxHelper.getValidAdditionHandler(itemStack);
					if (handler != null) {
						receive = handler.addEnergy(maxTransferRF - received, itemStack, actionType);
						received += receive;
						if (!actionType.shouldSimulate()) {
							removedFromNetwork(receive, getEnergyType());
						}
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
		return received;
	}


	@Override
	public ItemStack getDisplayStack() {
		return ItemStackHelper.getBlockItem(controller.getWorld(), controller.getPos());
	}

	@Override
	public EnergyType getEnergyType() {
		return controller.getNetwork().getDefaultEnergyType();
	}

}
