package sonar.fluxnetworks.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxStorage;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.connection.transfer.FluxStorageHandler;
import sonar.fluxnetworks.common.network.FluxTileMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

import javax.annotation.Nonnull;

public abstract class TileFluxStorage extends TileFluxDevice implements IFluxStorage {

    public final FluxStorageHandler handler = new FluxStorageHandler(this);

    public static final int PRI_DIFF = 1000000;
    public static final int PRI_UPPER = -10000;

    private final long maxEnergyStorage;

    private boolean serverEnergyChanged;

    private final ItemStack stack;

    public TileFluxStorage(TileEntityType<?> tileEntityTypeIn, String customName, long limit, long maxEnergyStorage, ItemStack stack) {
        super(tileEntityTypeIn, customName, limit);
        this.maxEnergyStorage = maxEnergyStorage;
        this.stack = stack;
    }

    public static class Basic extends TileFluxStorage {

        public Basic() {
            super(RegistryBlocks.BASIC_FLUX_STORAGE_TILE, "Basic Storage", FluxConfig.basicTransfer, FluxConfig.basicCapacity,
                    new ItemStack(RegistryBlocks.BASIC_FLUX_STORAGE));
        }
    }

    public static class Herculean extends TileFluxStorage {

        public Herculean() {
            super(RegistryBlocks.HERCULEAN_FLUX_STORAGE_TILE, "Herculean Storage", FluxConfig.herculeanTransfer, FluxConfig.herculeanCapacity,
                    new ItemStack(RegistryBlocks.HERCULEAN_FLUX_STORAGE));
        }
    }

    public static class Gargantuan extends TileFluxStorage {

        public Gargantuan() {
            super(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_TILE, "Gargantuan Storage", FluxConfig.gargantuanTransfer, FluxConfig.gargantuanCapacity,
                    new ItemStack(RegistryBlocks.GARGANTUAN_FLUX_STORAGE));
        }
    }

    @Override
    protected void sTick() {
        super.sTick();
        if (serverEnergyChanged) {
            //noinspection ConstantConditions
            if ((world.getWorldInfo().getGameTime() & 3) == 0) {
                NetworkHandler.INSTANCE.sendToChunkTracking(new FluxTileMessage(this, FluxTileMessage.S2C_STORAGE_ENERGY), world.getChunkAt(pos));
                serverEnergyChanged = false;
            }
        }
    }

    public void markServerEnergyChanged() {
        serverEnergyChanged = true;
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
    public long getLogicLimit() {
        return getDisableLimit() ? maxEnergyStorage : Math.min(limit, maxEnergyStorage);
    }

    @Override
    public int getLogicPriority() {
        return getSurgeMode() ? PRI_UPPER : Math.min(priority - PRI_DIFF, PRI_UPPER);
    }

    @Override
    public long getMaxTransferLimit() {
        return maxEnergyStorage;
    }

    /**
     * Write data for client
     *
     * @param stack stack
     * @return item stack with NBT
     * @see sonar.fluxnetworks.client.render.FluxStorageItemRenderer
     */
    @Nonnull
    private ItemStack writeStorageToDisplayStack(@Nonnull ItemStack stack) {
        CompoundNBT tag = new CompoundNBT();

        CompoundNBT subTag = new CompoundNBT();
        subTag.putInt(FluxConstants.CLIENT_COLOR, getNetwork().getNetworkColor());
        subTag.putLong(FluxConstants.ENERGY, getTransferBuffer());

        tag.put(FluxConstants.TAG_FLUX_DATA, subTag);
        tag.putBoolean(FluxConstants.FLUX_COLOR, true);

        stack.setTag(tag);
        return stack;
    }

    @Override
    public ItemStack getDisplayStack() {
        return writeStorageToDisplayStack(stack);
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
