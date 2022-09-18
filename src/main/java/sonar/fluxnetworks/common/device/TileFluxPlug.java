package sonar.fluxnetworks.common.device;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.device.FluxDeviceType;
import sonar.fluxnetworks.api.device.IFluxPlug;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.common.util.FluxGuiStack;
import sonar.fluxnetworks.common.util.FluxUtils;
import sonar.fluxnetworks.register.RegistryBlockEntityTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileFluxPlug extends TileFluxConnector implements IFluxPlug {

    private final FluxPlugHandler mHandler = new FluxPlugHandler();

    private final LazyOptional<?>[] mEnergyCaps = new LazyOptional[FluxUtils.DIRECTIONS.length];

    public TileFluxPlug(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(RegistryBlockEntityTypes.FLUX_PLUG.get(), pos, state);
    }

    @Nonnull
    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.PLUG;
    }

    @Nonnull
    @Override
    public FluxPlugHandler getTransferHandler() {
        return mHandler;
    }

    @Nonnull
    @Override
    public ItemStack getDisplayStack() {
        return FluxGuiStack.FLUX_PLUG;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (int i = 0, e = mEnergyCaps.length; i < e; i++) {
            if (mEnergyCaps[i] != null) {
                mEnergyCaps[i].invalidate();
                mEnergyCaps[i] = null;
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!isRemoved()) {
            if (cap == ForgeCapabilities.ENERGY || cap == FluxCapabilities.FN_ENERGY_STORAGE) {
                final int index = side == null ? 0 : side.get3DDataValue();
                LazyOptional<?> handler = mEnergyCaps[index];
                if (handler == null) {
                    final EnergyStorage storage = new EnergyStorage(
                            side == null ? Direction.from3DDataValue(0) : side);
                    handler = LazyOptional.of(() -> storage);
                    mEnergyCaps[index] = handler;
                }
                return handler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    private class EnergyStorage implements IEnergyStorage, IFNEnergyStorage {

        @Nonnull
        private final Direction mSide;

        public EnergyStorage(@Nonnull Direction side) {
            mSide = side;
        }

        ///// FORGE \\\\\

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (getNetwork().isValid()) {
                return (int) mHandler.receive(maxReceive, mSide, simulate, getNetwork().getBufferLimiter());
            }
            return 0;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return (int) Math.min(getEnergyStoredL(), Integer.MAX_VALUE);
        }

        @Override
        public int getMaxEnergyStored() {
            return (int) Math.min(getMaxEnergyStoredL(), Integer.MAX_VALUE);
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return getNetwork().isValid();
        }

        ///// FLUX EXTENDED \\\\\

        @Override
        public long receiveEnergyL(long maxReceive, boolean simulate) {
            if (getNetwork().isValid()) {
                return mHandler.receive(maxReceive, mSide, simulate, getNetwork().getBufferLimiter());
            }
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
            return Math.max(mHandler.getBuffer(), mHandler.getLimit());
        }
    }
}
