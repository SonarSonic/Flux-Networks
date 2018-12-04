package sonar.flux.connection.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.utils.ActionType;
import sonar.core.handlers.inventories.ItemStackHelper;
import sonar.flux.api.energy.internal.IEnergyTransfer;
import sonar.flux.api.energy.internal.ITransferHandler;

public class ConnectionTransfer extends BaseFluxTransfer implements IEnergyTransfer, ISidedTransfer {

	public final ITransferHandler transferHandler;
	public final ITileEnergyHandler handler;
	public final TileEntity tile;
	public final EnumFacing direction;
	public final ItemStack displayStack;

	public ConnectionTransfer(ITransferHandler transferHandler, ITileEnergyHandler handler, TileEntity tile, EnumFacing direction) {
		super(handler.getEnergyType());
		this.transferHandler = transferHandler;
		this.handler = handler;
		this.tile = tile;
		this.direction = direction;
		this.displayStack = ItemStackHelper.getBlockItem(tile.getWorld(), tile.getPos());
	}

	@Override
	public EnumFacing getDirection() {
		return direction;
	}

	@Override
	public TileEntity getTile() {
		return tile;
	}

	@Override
	public long addToNetwork(long maxTransferRF, ActionType actionType) {
		EnumFacing face = direction.getOpposite();
		if (handler.canRemoveEnergy(tile, face)) {
			long remove = handler.removeEnergy(maxTransferRF, tile, face, actionType);
			if (!actionType.shouldSimulate()) {
				addedToNetwork(remove, getEnergyType());
			}
			return remove;
		}
		return 0;

	}

	@Override
	public long removeFromNetwork(long maxTransferRF, ActionType actionType) {
		EnumFacing face = direction.getOpposite();
		if (handler.canAddEnergy(tile, face)) {
			long added = handler.addEnergy(maxTransferRF, tile, face, actionType);
			if (!actionType.shouldSimulate()) {
				removedFromNetwork(added, getEnergyType());
			}
			return added;
		}
		return 0;
	}

	@Override
	public boolean isInvalid() {
		return tile.isInvalid();
	}

	@Override
	public ItemStack getDisplayStack() {
		return displayStack;
	}
}
