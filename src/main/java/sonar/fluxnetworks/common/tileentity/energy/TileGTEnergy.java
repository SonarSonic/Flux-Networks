package sonar.fluxnetworks.common.tileentity.energy;

import gregtech.api.capability.GregtechCapabilities;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import sonar.fluxnetworks.common.core.GTEnergyWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileGTEnergy extends TileIC2Energy {

    private final GTEnergyWrapper mGTEnergyWrapper = new GTEnergyWrapper(this);

    @Override
    @Optional.Method(modid = "gregtech")
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    @Optional.Method(modid = "gregtech")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER)
            return (T) mGTEnergyWrapper;
        return super.getCapability(capability, facing);
    }
}
