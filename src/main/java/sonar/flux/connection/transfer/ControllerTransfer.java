package sonar.flux.connection.transfer;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.ItemStackHelper;
import sonar.flux.api.energy.IEnergyTransfer;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.tiles.IFluxController;
import sonar.flux.api.tiles.IFluxController.TransmitterMode;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.common.tileentity.TileStorage;
import sonar.flux.connection.FluxHelper;

public class ControllerTransfer extends BaseFluxTransfer implements IEnergyTransfer {

	public final TileController controller;

	public ControllerTransfer(TileController tile) {
		super(EnergyType.RF);// FIXME - this should be the network default?
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
		List<EntityPlayer> players = Lists.newArrayList();
		for (FluxPlayer player : playerNames) {
			Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(player.id);
			if (entity != null && entity instanceof EntityPlayer) {
				players.add((EntityPlayer) entity);
			}
		}
		for (EntityPlayer player : players) {
			long receive;
			switch (controller.getTransmitterMode()) {
			case HELD_ITEM:
				ItemStack stack = player.getHeldItemMainhand();
				if (FluxHelper.canTransferEnergy(stack) != null) {
					receive = SonarAPI.getEnergyHelper().receiveEnergy(stack, maxTransferRF - received, actionType);
					received += receive;
					if (!actionType.shouldSimulate()) {
						removedFromNetwork(receive);
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
					if (FluxHelper.canTransferEnergy(itemStack) != null) {
						receive = SonarAPI.getEnergyHelper().receiveEnergy(itemStack, maxTransferRF - received, actionType);
						received += receive;
						if (!actionType.shouldSimulate()) {
							removedFromNetwork(receive);
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

}
