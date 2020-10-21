package sonar.fluxnetworks.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxStorage;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.connection.transfer.FluxStorageHandler;
import sonar.fluxnetworks.common.misc.FluxGuiStack;
import sonar.fluxnetworks.common.network.FluxTileMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

import javax.annotation.Nonnull;

public abstract class TileFluxStorage extends TileFluxDevice implements IFluxStorage {

    public static final int PRI_DIFF = 1000000;
    public static final int PRI_UPPER = -10000;

    private static final int FLAG_ENERGY_CHANGED = 1 << 9;

    private final FluxStorageHandler handler = new FluxStorageHandler(this);

    public TileFluxStorage(TileEntityType<? extends TileFluxStorage> tileEntityTypeIn, String customName, long limit) {
        super(tileEntityTypeIn, customName, limit);
    }

    public static class Basic extends TileFluxStorage {

        public Basic() {
            super(RegistryBlocks.BASIC_FLUX_STORAGE_TILE, "Basic Storage", FluxConfig.basicTransfer);
        }

        @Override
        public long getMaxTransferLimit() {
            return FluxConfig.basicCapacity;
        }

        @Nonnull
        @Override
        public ItemStack getDisplayStack() {
            return writeToDisplayStack(FluxGuiStack.BASIC_STORAGE);
        }
    }

    public static class Herculean extends TileFluxStorage {

        public Herculean() {
            super(RegistryBlocks.HERCULEAN_FLUX_STORAGE_TILE, "Herculean Storage", FluxConfig.herculeanTransfer);
        }

        @Override
        public long getMaxTransferLimit() {
            return FluxConfig.herculeanCapacity;
        }

        @Nonnull
        @Override
        public ItemStack getDisplayStack() {
            return writeToDisplayStack(FluxGuiStack.HERCULEAN_STORAGE);
        }
    }

    public static class Gargantuan extends TileFluxStorage {

        public Gargantuan() {
            super(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_TILE, "Gargantuan Storage", FluxConfig.gargantuanTransfer);
        }

        @Override
        public long getMaxTransferLimit() {
            return FluxConfig.gargantuanCapacity;
        }

        @Nonnull
        @Override
        public ItemStack getDisplayStack() {
            return writeToDisplayStack(FluxGuiStack.GARGANTUAN_STORAGE);
        }
    }

    @Override
    protected void sTick() {
        super.sTick();
        if ((flags & FLAG_ENERGY_CHANGED) == FLAG_ENERGY_CHANGED) {
            //noinspection ConstantConditions
            if ((world.getWorldInfo().getGameTime() & 0x3) == 0) {
                // update model data to players who can see it
                NetworkHandler.INSTANCE.sendToChunkTracking(new FluxTileMessage(
                        this, FluxConstants.S2C_STORAGE_ENERGY), world.getChunkAt(pos));
                flags &= ~FLAG_ENERGY_CHANGED;
            }
        }
    }

    public void markServerEnergyChanged() {
        flags |= FLAG_ENERGY_CHANGED;
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.STORAGE;
    }

    @Nonnull
    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Override
    public int getLogicPriority() {
        return surgeMode ? PRI_UPPER : priority - PRI_DIFF;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = MathHelper.clamp(priority, Integer.MIN_VALUE + PRI_DIFF, PRI_UPPER + PRI_DIFF - 1);
    }

    /**
     * Write data for client
     *
     * @return item stack with NBT
     * @see sonar.fluxnetworks.client.render.FluxStorageItemRenderer
     */
    @Nonnull
    protected ItemStack writeToDisplayStack(@Nonnull ItemStack stack) {
        CompoundNBT subTag = stack.getOrCreateChildTag(FluxConstants.TAG_FLUX_DATA);
        //noinspection ConstantConditions
        if (world.isRemote)
            subTag.putInt(FluxConstants.CLIENT_COLOR, clientColor);
        else
            subTag.putInt(FluxConstants.CLIENT_COLOR, getNetwork().getNetworkColor());
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
