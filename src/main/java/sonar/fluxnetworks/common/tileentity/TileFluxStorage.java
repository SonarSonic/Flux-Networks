package sonar.fluxnetworks.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxEnergy;
import sonar.fluxnetworks.api.device.IFluxStorage;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.connection.handler.FluxStorageHandler;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.TileMessage;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;

public abstract class TileFluxStorage extends TileFluxDevice implements IFluxStorage, IFluxEnergy {

    public final FluxStorageHandler handler = new FluxStorageHandler(this);

    private static final int PRI_DIFF = 1000000;
    private static final int PRI_UPPER = -10000;

    public int energyStored;
    public int maxEnergyStorage;

    private boolean serverEnergyChanged = false;

    protected ItemStack stack = ItemStack.EMPTY;

    public TileFluxStorage(TileEntityType<?> tileEntityTypeIn, String customName, long limit, int maxEnergyStorage) {
        super(tileEntityTypeIn, customName, limit);
        this.maxEnergyStorage = maxEnergyStorage;
    }

    public static class Basic extends TileFluxStorage {

        public Basic() {
            super(RegistryBlocks.BASIC_FLUX_STORAGE_TILE, "Basic Storage", FluxConfig.basicTransfer, FluxConfig.basicCapacity);
            stack = new ItemStack(RegistryBlocks.BASIC_FLUX_STORAGE);
        }
    }

    public static class Herculean extends TileFluxStorage {

        public Herculean() {
            super(RegistryBlocks.HERCULEAN_FLUX_STORAGE_TILE, "Herculean Storage", FluxConfig.herculeanTransfer, FluxConfig.herculeanCapacity);
            stack = new ItemStack(RegistryBlocks.HERCULEAN_FLUX_STORAGE);
        }
    }

    public static class Gargantuan extends TileFluxStorage {

        public Gargantuan() {
            super(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_TILE, "Gargantuan Storage", FluxConfig.gargantuanTransfer, FluxConfig.gargantuanCapacity);
            stack = new ItemStack(RegistryBlocks.GARGANTUAN_FLUX_STORAGE);
        }
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.STORAGE;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Override
    public void writeCustomNBT(CompoundNBT tag, int flag) {
        super.writeCustomNBT(tag, flag);
        tag.putInt("energy", energyStored);
    }

    public long addEnergy(long amount, boolean simulate) {
        long energyReceived = Math.min(maxEnergyStorage - energyStored, amount);
        if (!simulate) {
            energyStored += energyReceived;
            serverEnergyChanged = true;
        }
        return energyReceived;
    }

    public long removeEnergy(long amount, boolean simulate) {
        long energyExtracted = Math.min(energyStored, amount);
        if (!simulate) {
            energyStored -= energyExtracted;
            serverEnergyChanged = true;
        }
        return energyExtracted;
    }

    /**
     * on server side
     */
    public void sendPacketIfNeeded() {
        if (serverEnergyChanged) {
            //noinspection ConstantConditions
            if ((world.getWorldInfo().getGameTime() & 3) == 0) {
                NetworkHandler.INSTANCE.sendToChunkTracking(new TileMessage(this, TileMessage.S2C_STORAGE_ENERGY), world.getChunkAt(pos));
                serverEnergyChanged = false;
            }
        }
    }

    @Override
    public long getEnergy() {
        return energyStored;
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

    @Override
    public void readCustomNBT(CompoundNBT tag, int flag) {
        super.readCustomNBT(tag, flag);
        energyStored = tag.getInt("energy");
    }

    public ItemStack writeStorageToDisplayStack(@Nonnull ItemStack stack) {
        CompoundNBT tag = new CompoundNBT();

        CompoundNBT subTag = new CompoundNBT();
        subTag.putInt("energy", energyStored);
        subTag.putInt(FluxNetworkData.NETWORK_ID, getNetworkID());

        tag.put(FluxUtils.FLUX_DATA, subTag);
        tag.putBoolean(FluxUtils.GUI_COLOR, true);

        stack.setTag(tag);
        return stack;
    }

    @Override
    public ItemStack getDisplayStack() {
        return writeStorageToDisplayStack(stack);
    }

    @Override
    public void writePacket(PacketBuffer buf, byte id) {
        super.writePacket(buf, id);
        switch (id) {
            case TileMessage.S2C_GUI_SYNC:
            case TileMessage.S2C_STORAGE_ENERGY:
                buf.writeInt(energyStored);
                break;
        }
    }

    @Override
    public void readPacket(PacketBuffer buf, byte id) {
        super.readPacket(buf, id);
        switch (id) {
            case TileMessage.S2C_GUI_SYNC:
            case TileMessage.S2C_STORAGE_ENERGY:
                energyStored = buf.readInt();
                break;
        }
    }

    /* TODO IBigPower - One Probe
    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getStoredPower(){
        return this.energyStored;
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getCapacity(){
        return this.maxEnergyStorage;
    }
    */
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
