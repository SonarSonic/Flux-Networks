package fluxnetworks.common.tileentity.energy;

import fluxnetworks.api.tiles.IFluxPhantomEnergy;
import fluxnetworks.common.core.ForgeEnergyWrapper;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class TileForgeEnergy extends TileFluxCore implements IFluxPhantomEnergy {

    public Map<EnumFacing, ForgeEnergyWrapper> wrappers = new HashMap<>();
    {
        wrappers.put(null, new ForgeEnergyWrapper(this, null));
        for(EnumFacing face : EnumFacing.VALUES) {
            wrappers.put(face, new ForgeEnergyWrapper(this, face));
        }
    }

    public ForgeEnergyWrapper getEnergyWrapper(EnumFacing facing) {
        return wrappers.get(facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityEnergy.ENERGY)
            return (T) getEnergyWrapper(facing);
        return super.getCapability(capability, facing);
    }
}
