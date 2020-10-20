package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

import javax.annotation.Nonnull;

public class ConnectionTransfer {

    private final ITileEnergyHandler energyHandler;
    private final TileEntity tile;
    private final Direction side;
    private final ItemStack displayStack;

    public long outbound;
    public long inbound;

    public ConnectionTransfer(ITileEnergyHandler energyHandler, @Nonnull TileEntity tile, @Nonnull Direction dir) {
        this.energyHandler = energyHandler;
        this.tile = tile;
        this.side = dir.getOpposite();
        this.displayStack = new ItemStack(tile.getBlockState().getBlock().asItem());
    }

    public long sendToTile(long amount, boolean simulate) {
        // we only receive energy from nearby tiles passively.
        if (energyHandler.canAddEnergy(tile, side)) {
            long added = energyHandler.addEnergy(amount, tile, side, simulate);
            if (!simulate) {
                inbound += added;
            }
            return added;
        }
        return 0;
    }

    public void onEnergyReceived(long amount) {
        outbound += amount;
    }

    public void onCycleStart() {
        outbound = 0;
        inbound = 0;
    }

    public void onCycleEnd() {

    }

    public TileEntity getTile() {
        return tile;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }
}
