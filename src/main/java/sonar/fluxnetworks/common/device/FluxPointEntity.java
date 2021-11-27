package sonar.fluxnetworks.common.device;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.device.IFluxPoint;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.device.FluxDeviceType;
import sonar.fluxnetworks.common.util.FluxGuiStack;
import sonar.fluxnetworks.register.RegistryBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluxPointEntity extends FluxConnectorEntity implements IFluxPoint {

    private final FluxPointHandler mHandler = new FluxPointHandler();

    private LazyOptional<?> mEnergyCap;

    public FluxPointEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(RegistryBlocks.FLUX_POINT_ENTITY, pos, state);
    }

    @Nonnull
    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.POINT;
    }

    @Nonnull
    @Override
    public FluxPointHandler getTransferHandler() {
        return mHandler;
    }

    @Nonnull
    @Override
    public ItemStack getDisplayStack() {
        return FluxGuiStack.FLUX_POINT;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (mEnergyCap != null) {
            mEnergyCap.invalidate();
            mEnergyCap = null;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if ((mFlags & FLAG_FIRST_LOADED) == FLAG_FIRST_LOADED) {
            if (cap == CapabilityEnergy.ENERGY || cap == FluxCapabilities.FN_ENERGY_STORAGE) {
                if (mEnergyCap == null) {
                    final EnergyStorage storage = new EnergyStorage();
                    // save an immutable pointer to an immutable object
                    mEnergyCap = LazyOptional.of(() -> storage);
                }
                return mEnergyCap.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    private class EnergyStorage implements IEnergyStorage, IFNEnergyStorage {

        public EnergyStorage() {
        }

        ///// FORGE \\\\\

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(mHandler.getBuffer(), Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(Math.max(mHandler.getBuffer(), mHandler.getLimit()), Integer.MAX_VALUE);
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return false;
        }

        ///// FLUX EXTENDED \\\\\

        @Override
        public long receiveEnergyL(long maxReceive, boolean simulate) {
            return 0;
        }

        @Override
        public long extractEnergyL(long maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public long getEnergyStoredL() {
            return mHandler.getBuffer();
        }

        @Override
        public long getMaxEnergyStoredL() {
            return mHandler.getLimit();
        }
    }
}
