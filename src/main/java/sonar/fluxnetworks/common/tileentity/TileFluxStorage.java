package sonar.fluxnetworks.common.tileentity;

import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxEnergy;
import sonar.fluxnetworks.api.tiles.IFluxStorage;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.connection.handler.SingleTransferHandler;
import sonar.fluxnetworks.common.connection.transfer.StorageTransfer;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import li.cil.oc.api.machine.Arguments;
import mcjty.lib.api.power.IBigPower;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Optional;

import java.util.HashMap;
import java.util.Map;

@Optional.Interface(iface = "mcjty.lib.api.power.IBigPower", modid = "theoneprobe")
public class TileFluxStorage extends TileFluxCore implements IFluxStorage, IFluxEnergy, IBigPower {

    public final SingleTransferHandler handler = new SingleTransferHandler(this, new StorageTransfer(this));

    public static final int C = 1000000;
    public static final int D = -10000;

    public int energyStored;
    public int maxEnergyStorage;

    private boolean needSyncEnergy = false;

    public ItemStack stack = ItemStack.EMPTY;

    public TileFluxStorage() {
        this(FluxConfig.basicCapacity);
        customName = "Basic Storage";
        limit = FluxConfig.basicTransfer;
        stack = new ItemStack(RegistryBlocks.FLUX_STORAGE_1);
    }

    private TileFluxStorage(int maxEnergyStorage) {
        this.maxEnergyStorage = maxEnergyStorage;
    }

    public static class Herculean extends TileFluxStorage {

        public Herculean() {
            super(FluxConfig.herculeanCapacity);
            customName = "Herculean Storage";
            limit = FluxConfig.herculeanTransfer;
            stack = new ItemStack(RegistryBlocks.FLUX_STORAGE_2);
        }
    }

    public static class Gargantuan extends TileFluxStorage {

        public Gargantuan() {
            super(FluxConfig.gargantuanCapacity);
            customName = "Gargantuan Storage";
            limit = FluxConfig.gargantuanTransfer;
            stack = new ItemStack(RegistryBlocks.FLUX_STORAGE_3);
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
    public NBTTagCompound writeCustomNBT(NBTTagCompound tag, NBTType type) {
        super.writeCustomNBT(tag, type);
        tag.setInteger("energy", energyStored);
        return tag;
    }

    public long addEnergy(long amount, boolean simulate) {
        long energyReceived = Math.min(maxEnergyStorage - energyStored, amount);
        if (!simulate) {
            energyStored += energyReceived;
            needSyncEnergy = true;
        }
        return energyReceived;
    }

    public long removeEnergy(long amount, boolean simulate) {
        long energyExtracted = Math.min(energyStored, amount);
        if (!simulate) {
            energyStored -= energyExtracted;
            needSyncEnergy = true;
        }
        return energyExtracted;
    }

    /** on server side **/
    public void sendPacketIfNeeded() {
        if (needSyncEnergy) {
            if ((world.getWorldTime() & 3) == 0) {
                sendPackets();
                needSyncEnergy = false;
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
    public void readCustomNBT(NBTTagCompound tag, NBTType type) {
        super.readCustomNBT(tag, type);
        energyStored = tag.getInteger("energy");
    }

    public ItemStack writeStorageToDisplayStack(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();

        NBTTagCompound subTag = new NBTTagCompound();
        subTag.setInteger("energy", energyStored);
        subTag.setInteger(FluxNetworkData.NETWORK_ID, networkID);

        tag.setTag(FluxUtils.FLUX_DATA, subTag);
        tag.setBoolean(FluxUtils.GUI_COLOR, true);

        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public ItemStack getDisplayStack() {
        return writeStorageToDisplayStack(stack);
    }

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
}
