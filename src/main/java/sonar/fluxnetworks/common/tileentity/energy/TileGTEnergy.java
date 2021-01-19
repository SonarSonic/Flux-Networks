package sonar.fluxnetworks.common.tileentity.energy;

import sonar.fluxnetworks.common.core.GTEnergyWrapper;
import gregtech.api.capability.GregtechCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class TileGTEnergy extends TileIC2Energy {

    public Map<EnumFacing, GTEnergyWrapper> GTWrappers = new HashMap<>();
    {
        GTWrappers.put(null, new GTEnergyWrapper(this, null));
        for(EnumFacing face : EnumFacing.VALUES){
            GTWrappers.put(face, new GTEnergyWrapper(this, face));
        }
    }

    public GTEnergyWrapper getGTEnergyWrapper(EnumFacing facing) {
        return GTWrappers.get(facing);
    }

    @Override
    @Optional.Method(modid = "gregtech")
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @Optional.Method(modid = "gregtech")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER)
            return (T) getGTEnergyWrapper(facing);
        return super.getCapability(capability, facing);
    }
}
