package sonar.flux.connection.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.ItemStackHelper;
import sonar.flux.api.energy.IEnergyTransfer;
import sonar.flux.api.energy.ITransferHandler;
import sonar.flux.api.tiles.IFlux;

public class ConnectionTransfer extends BaseFluxTransfer implements IEnergyTransfer, ISidedTransfer {

	public final ITransferHandler transferHandler;
	public final ISonarEnergyHandler handler;
	public final TileEntity tile;
	public final EnumFacing direction;
	public long totalTransferMax; // may need to be changed

	public ConnectionTransfer(ITransferHandler transferHandler, ISonarEnergyHandler handler, TileEntity tile, EnumFacing direction) {
		super(handler.getProvidedType());
		this.transferHandler = transferHandler;
		this.handler = handler;
		this.tile = tile;
		this.direction = direction;
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
		//long simulate = SonarAPI.getEnergyHelper().extractEnergy(tile, maxTransferRF, face, ActionType.SIMULATE);
		long remove = SonarAPI.getEnergyHelper().extractEnergy(tile, maxTransferRF, face, actionType);
		if (!actionType.shouldSimulate()) {
			addedToNetwork(remove);
		}
		return remove;

	}

	@Override
	public long removeFromNetwork(long maxTransferRF, ActionType actionType) {
		EnumFacing face = direction.getOpposite();
		//long simulate = SonarAPI.getEnergyHelper().receiveEnergy(tile, maxTransferRF, face, ActionType.SIMULATE);
		long added = SonarAPI.getEnergyHelper().receiveEnergy(tile, maxTransferRF, face, actionType);
		if (!actionType.shouldSimulate()){
			removedFromNetwork(added);
		}
		return added;
	}

	@Override
	public boolean isInvalid() {
		return tile.isInvalid();
	}

	@Override
	public ItemStack getDisplayStack() {
		return ItemStackHelper.getBlockItem(tile.getWorld(), tile.getPos());
	}
}
