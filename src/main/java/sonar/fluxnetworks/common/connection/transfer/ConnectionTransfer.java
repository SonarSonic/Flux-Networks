package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

import javax.annotation.Nonnull;

public class ConnectionTransfer {

    private final ITileEnergyHandler energyHandler;
    private final TileEntity tile;
    private final EnumFacing side;
    private final ItemStack displayStack;

    public long outbound;
    public long inbound;

    public ConnectionTransfer(ITileEnergyHandler energyHandler, @Nonnull TileEntity tile, @Nonnull EnumFacing dir) {
        this.energyHandler = energyHandler;
        this.tile = tile;
        this.side = dir.getOpposite(); // the tile is on our north side, we charge it from its south side
        this.displayStack = new ItemStack(tile.getBlockType());
    }

    public long sendToTile(long amount, boolean simulate) {
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

    public TileEntity getTile() {
        return tile;
    }

    public ItemStack getDisplayStack() {
        return displayStack;
    }
}
