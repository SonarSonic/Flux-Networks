package sonar.fluxnetworks.common.tileentity.energy;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import sonar.fluxnetworks.common.core.ForgeEnergyWrapper;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public abstract class TileForgeEnergy extends TileFluxCore {

    public Map<EnumFacing, ForgeEnergyWrapper> wrappers = new EnumMap<>(EnumFacing.class);

    {
        for (EnumFacing face : EnumFacing.VALUES) {
            wrappers.put(face, new ForgeEnergyWrapper(this, face));
        }
    }

    public ForgeEnergyWrapper getEnergyWrapper(EnumFacing facing) {
        return wrappers.get(facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing != null && capability == CapabilityEnergy.ENERGY)
            return (T) getEnergyWrapper(facing);
        return super.getCapability(capability, facing);
    }
}
