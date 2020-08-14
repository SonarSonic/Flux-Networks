package sonar.fluxnetworks.common.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxEnergy;
import sonar.fluxnetworks.api.tiles.IFluxStorage;
import sonar.fluxnetworks.common.connection.handler.FluxStorageHandler;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.item.ItemStack;
import static sonar.fluxnetworks.common.network.TilePacketBufferConstants.*;

public abstract class TileFluxStorage extends TileFluxDevice implements IFluxStorage, IFluxEnergy {

    public final FluxStorageHandler handler = new FluxStorageHandler(this);

    public static final int C = 1000000;
    public static final int D = -10000;

    public int energyStored;
    public int maxEnergyStorage;

    private boolean energyChanged = false;

    public ItemStack stack = ItemStack.EMPTY;

    public TileFluxStorage(TileEntityType<?> tileEntityTypeIn, int maxEnergyStorage) {
        super(tileEntityTypeIn);
        this.maxEnergyStorage = maxEnergyStorage;
    }

    public static class Basic extends TileFluxStorage{

        public Basic() {
            super(RegistryBlocks.BASIC_FLUX_STORAGE_TILE, FluxConfig.basicCapacity);
            customName = "Basic Storage";
            limit = FluxConfig.basicTransfer;
            stack = new ItemStack(RegistryBlocks.BASIC_FLUX_STORAGE);
        }
    }

    public static class Herculean extends TileFluxStorage {

        public Herculean() {
            super(RegistryBlocks.HERCULEAN_FLUX_STORAGE_TILE, FluxConfig.herculeanCapacity);
            customName = "Herculean Storage";
            limit = FluxConfig.herculeanTransfer;
            stack = new ItemStack(RegistryBlocks.HERCULEAN_FLUX_STORAGE);
        }
    }

    public static class Gargantuan extends TileFluxStorage {

        public Gargantuan() {
            super(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_TILE, FluxConfig.gargantuanCapacity);
            customName = "Gargantuan Storage";
            limit = FluxConfig.gargantuanTransfer;
            stack = new ItemStack(RegistryBlocks.GARGANTUAN_FLUX_STORAGE);
        }
    }

    @Override
    public EnumConnectionType getConnectionType() {
        return EnumConnectionType.STORAGE;
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Override
    public CompoundNBT writeCustomNBT(CompoundNBT tag, NBTType type) {
        super.writeCustomNBT(tag, type);
        tag.putInt("energy", energyStored);
        return tag;
    }

    public long addEnergy(long amount, boolean simulate) {
        long energyReceived = Math.min(maxEnergyStorage - energyStored, amount);
        if (!simulate) {
            energyStored += energyReceived;
            energyChanged = true;
        }
        return energyReceived;
    }

    public long removeEnergy(long amount, boolean simulate) {
        long energyExtracted = Math.min(energyStored, amount);
        if (!simulate) {
            energyStored -= energyExtracted;
            energyChanged = true;
        }
        return energyExtracted;
    }

    /** on server side **/
    public void sendPacketIfNeeded() {
        if (energyChanged) {
            if ((world.getWorldInfo().getGameTime() & 3) == 0) {
                sendTilePacketToNearby(FLUX_STORAGE_ENERGY);
                energyChanged = false;
            }
        }
    }

    @Override
    public long getEnergy() {
        return energyStored;
    }

    @Override
    public long getCurrentLimit() {
        return disableLimit ? maxEnergyStorage : Math.min(limit, maxEnergyStorage);
    }

    @Override
    public int getPriority() {
        return surgeMode ? D : Math.min(priority - C, D);
    }

    @Override
    public long getMaxTransferLimit() {
        return maxEnergyStorage;
    }

    @Override
    public void readCustomNBT(CompoundNBT tag, NBTType type) {
        super.readCustomNBT(tag, type);
        energyStored = tag.getInt("energy");
    }

    public ItemStack writeStorageToDisplayStack(ItemStack stack) {
        CompoundNBT tag = new CompoundNBT();

        CompoundNBT subTag = new CompoundNBT();
        subTag.putInt("energy", energyStored);
        subTag.putInt(FluxNetworkData.NETWORK_ID, networkID);

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
        switch(id){
            case FLUX_GUI_SYNC:
            case FLUX_STORAGE_ENERGY:
                buf.writeInt(energyStored);
                break;
        }
    }

    @Override
    public void readPacket(PacketBuffer buf, byte id) {
        super.readPacket(buf, id);
        switch(id){
            case FLUX_GUI_SYNC:
            case FLUX_STORAGE_ENERGY:
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
