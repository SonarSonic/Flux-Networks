package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

import javax.annotation.Nonnull;

public class FluxStorageHandler extends BasicTransferHandler<TileFluxStorage> {

    public FluxStorageHandler(TileFluxStorage fluxStorage) {
        super(fluxStorage);
    }

    private long added;
    private long removed;

    @Override
    public void onCycleStart() {

    }

    @Override
    public void onCycleEnd() {
        change = added - removed;
        added = 0;
        removed = 0;
    }

    @Override
    public void addToBuffer(long energy) {
        if (energy <= 0) {
            return;
        }
        buffer += energy;
        added += energy;
        device.markServerEnergyChanged();
    }

    @Override
    public long removeFromBuffer(long energy) {
        long a = Math.min(Math.min(energy, buffer), device.getLogicLimit() - removed);
        if (a <= 0) {
            return 0;
        }
        buffer -= a;
        removed += a;
        device.markServerEnergyChanged();
        return a;
    }

    @Override
    public long getRequest() {
        return Math.max(0, Math.min(device.getMaxTransferLimit() - buffer, device.getLogicLimit() - added));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tag, NBTType type) {
        if (type == NBTType.TILE_UPDATE) {
            super.writeCustomNBT(tag, type); // read by PhantomFluxDevice
        }
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_DROP) {
            tag.setLong("energy", buffer);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag, NBTType type) {
        if (type == NBTType.TILE_UPDATE) {
            super.readCustomNBT(tag, type);
        }
        if (type == NBTType.ALL_SAVE || type == NBTType.TILE_DROP) {
            buffer = tag.getLong("energy");
        }
    }

    @Override
    public void updateTransfers(@Nonnull EnumFacing... faces) {

    }
}
