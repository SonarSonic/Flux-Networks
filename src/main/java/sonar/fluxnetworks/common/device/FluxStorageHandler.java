package sonar.fluxnetworks.common.device;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxConstants;

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
    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        super.writeCustomTag(tag, type);
        tag.putLong(FluxConstants.ENERGY, mBuffer);
    }

    @Override
    public void writePacket(@Nonnull FriendlyByteBuf buffer, byte id) {
        if (id == FluxConstants.DEVICE_S2C_STORAGE_ENERGY) {
            buffer.writeLong(mBuffer);
        } else {
            super.writePacket(buffer, id);
        }
    }

    @Override
    public void readPacket(@Nonnull FriendlyByteBuf buffer, byte id) {
        if (id == FluxConstants.DEVICE_S2C_STORAGE_ENERGY) {
            mBuffer = buffer.readLong();
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
