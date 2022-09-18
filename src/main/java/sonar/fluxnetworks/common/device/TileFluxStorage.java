package sonar.fluxnetworks.common.device;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.device.FluxDeviceType;
import sonar.fluxnetworks.api.device.IFluxStorage;
import sonar.fluxnetworks.common.util.FluxGuiStack;
import sonar.fluxnetworks.register.*;

import javax.annotation.Nonnull;

public abstract class TileFluxStorage extends TileFluxDevice implements IFluxStorage {

    private final FluxStorageHandler mHandler;

    protected TileFluxStorage(@Nonnull BlockEntityType<?> type, @Nonnull BlockPos pos, @Nonnull BlockState state,
                              @Nonnull FluxStorageHandler handler) {
        super(type, pos, state);
        mHandler = handler;
    }

    public static class Basic extends TileFluxStorage {

        public Basic(@Nonnull BlockPos pos, @Nonnull BlockState state) {
            super(RegistryBlockEntityTypes.BASIC_FLUX_STORAGE.get(), pos, state, new FluxStorageHandler.Basic());
        }

        @Nonnull
        @Override
        public ItemStack getDisplayStack() {
            return writeToDisplayStack(FluxGuiStack.BASIC_STORAGE);
        }
    }

    public static class Herculean extends TileFluxStorage {

        public Herculean(@Nonnull BlockPos pos, @Nonnull BlockState state) {
            super(RegistryBlockEntityTypes.HERCULEAN_FLUX_STORAGE.get(), pos, state, new FluxStorageHandler.Herculean());
        }

        @Nonnull
        @Override
        public ItemStack getDisplayStack() {
            return writeToDisplayStack(FluxGuiStack.HERCULEAN_STORAGE);
        }
    }

    public static class Gargantuan extends TileFluxStorage {

        public Gargantuan(@Nonnull BlockPos pos, @Nonnull BlockState state) {
            super(RegistryBlockEntityTypes.GARGANTUAN_FLUX_STORAGE.get(), pos, state, new FluxStorageHandler.Gargantuan());
        }

        @Nonnull
        @Override
        public ItemStack getDisplayStack() {
            return writeToDisplayStack(FluxGuiStack.GARGANTUAN_STORAGE);
        }
    }

    @Override
    public long getMaxTransferLimit() {
        return mHandler.getMaxEnergyStorage();
    }

    @Override
    protected void onServerTick() {
        super.onServerTick();
        if ((mFlags & FLAG_ENERGY_CHANGED) == 0 && mHandler.getChange() != 0) {
            mFlags |= FLAG_ENERGY_CHANGED;
        }
        if ((mFlags & FLAG_ENERGY_CHANGED) == FLAG_ENERGY_CHANGED) {
            //noinspection ConstantConditions
            if ((level.getGameTime() & 0b111) == 0) {
                // update model data to players who can see it
                Channel.get().sendToTrackingChunk(Messages.makeDeviceBuffer(this,
                                FluxConstants.DEVICE_S2C_STORAGE_ENERGY),
                        level.getChunkAt(worldPosition));
                mFlags &= ~FLAG_ENERGY_CHANGED;
            }
        }
    }

    @Nonnull
    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.STORAGE;
    }

    @Nonnull
    @Override
    public FluxStorageHandler getTransferHandler() {
        return mHandler;
    }

    /**
     * Make this storage full of energy (debug or admin function).
     */
    public void fillUp() {
        mHandler.fillUp();
        // this may happen without a valid network, so force to sync
        mFlags |= FLAG_ENERGY_CHANGED;
    }

    /**
     * Write data for client
     *
     * @return item stack with NBT
     * @see sonar.fluxnetworks.client.render.FluxStorageItemRenderer
     */
    @Nonnull
    protected ItemStack writeToDisplayStack(@Nonnull ItemStack stack) {
        CompoundTag subTag = stack.getOrCreateTagElement(FluxConstants.TAG_FLUX_DATA);
        //noinspection ConstantConditions
        if (level.isClientSide)
            stack.getOrCreateTag().putBoolean(FluxConstants.FLUX_COLOR, true);
        else {
            stack.getOrCreateTag().putBoolean(FluxConstants.FLUX_COLOR, false);
            subTag.putInt(FluxConstants.CLIENT_COLOR, getNetwork().getNetworkColor());
        }
        subTag.putLong(FluxConstants.ENERGY, getTransferBuffer());
        return stack;
    }

    /* TODO OPEN COMPUTERS INTEGRATION
    @Override
    public String getPeripheralName() {
        return "flux_storage";
    }

    @Override
    public Object[] invokeMethods(String method, Arguments arguments) {
        if(method.equals("getFluxInfo")) {
            Map<Object, Object> map = new HashMap<>();
            map.put("customName", customName);
            map.put("priority", priority);
            map.put("transferLimit", limit);
            map.put("surgeMode", surgeMode);
            map.put("unlimited", disableLimit);
            map.put("energyStored", getTransferHandler().getEnergyStored());
            map.put("maxStorage", maxEnergyStorage);
            return new Object[]{map};
        }
        return super.invokeMethods(method, arguments);
    }
    */
}
