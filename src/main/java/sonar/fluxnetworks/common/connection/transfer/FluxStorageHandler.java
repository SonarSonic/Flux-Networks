package sonar.fluxnetworks.common.connection.transfer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.connection.TransferHandler;

import javax.annotation.Nonnull;

public abstract class FluxStorageHandler extends TransferHandler {

    private long mAdded;
    private long mRemoved;

    protected FluxStorageHandler(long limit) {
        super(limit);
    }

    @Override
    public void onCycleStart() {
    }

    @Override
    public void onCycleEnd() {
        mChange = mAdded - mRemoved;
        mAdded = 0;
        mRemoved = 0;
    }

    @Override
    public void insert(long energy) {
        mBuffer += energy;
        mAdded += energy;
    }

    @Override
    public long extract(long energy) {
        long op = Math.min(Math.min(energy, mBuffer), getLimit() - mRemoved);
        assert op >= 0;
        mBuffer -= op;
        mRemoved += op;
        return op;
    }

    @Override
    public long getRequest() {
        return Math.max(0, Math.min(getMaxEnergyStorage() - mBuffer, getLimit() - mAdded));
    }

    public abstract long getMaxEnergyStorage();

    @Override
    public int getPriority() {
        return super.getPriority() - STORAGE_PRI_DECR;
    }

    @Override
    public void setLimit(long limit) {
        super.setLimit(Math.min(limit, getMaxEnergyStorage()));
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, int type) {
        if (type == FluxConstants.TYPE_TILE_UPDATE || type == FluxConstants.TYPE_CONNECTION_UPDATE) {
            super.writeCustomTag(tag, type); // read by PhantomFluxDevice
        }
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            tag.putLong(FluxConstants.ENERGY, mBuffer);
        }
    }

    @Override
    public void readCustomTag(@Nonnull CompoundTag tag, int type) {
        if (type == FluxConstants.TYPE_TILE_UPDATE) {
            super.readCustomTag(tag, type);
        }
        if (type == FluxConstants.TYPE_SAVE_ALL || type == FluxConstants.TYPE_TILE_DROP) {
            mBuffer = tag.getLong(FluxConstants.ENERGY);
        }
    }

    @Override
    public void writePacket(@Nonnull FriendlyByteBuf buffer, byte id) {
        if (id == FluxConstants.S2C_STORAGE_ENERGY) {
            buffer.writeLong(this.mBuffer);
        } else {
            super.writePacket(buffer, id);
        }
    }

    @Override
    public void readPacket(@Nonnull FriendlyByteBuf buffer, byte id) {
        if (id == FluxConstants.S2C_STORAGE_ENERGY) {
            this.mBuffer = buffer.readLong();
        } else {
            super.readPacket(buffer, id);
        }
    }

    public static class Basic extends FluxStorageHandler {

        public Basic() {
            super(FluxConfig.basicTransfer);
        }

        @Override
        public long getMaxEnergyStorage() {
            return FluxConfig.basicCapacity;
        }
    }

    public static class Herculean extends FluxStorageHandler {

        public Herculean() {
            super(FluxConfig.herculeanTransfer);
        }

        @Override
        public long getMaxEnergyStorage() {
            return FluxConfig.herculeanCapacity;
        }
    }

    public static class Gargantuan extends FluxStorageHandler {

        public Gargantuan() {
            super(FluxConfig.gargantuanTransfer);
        }

        @Override
        public long getMaxEnergyStorage() {
            return FluxConfig.gargantuanCapacity;
        }
    }
}
