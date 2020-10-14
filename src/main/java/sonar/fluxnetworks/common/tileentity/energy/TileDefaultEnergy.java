package sonar.fluxnetworks.common.tileentity.energy;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.common.misc.DefaultEnergyWrapper;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;

public abstract class TileDefaultEnergy extends TileFluxDevice implements IFluxDevice {

    private final EnumMap<Direction, LazyOptional<DefaultEnergyWrapper>> wrappers = new EnumMap<>(Direction.class);

    public TileDefaultEnergy(TileEntityType<?> tileEntityTypeIn, String customName, long limit) {
        super(tileEntityTypeIn, customName, limit);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side != null) {
            if (cap == CapabilityEnergy.ENERGY || cap == FluxCapabilities.FN_ENERGY_STORAGE) {
                return wrappers.computeIfAbsent(side, s -> {
                    final DefaultEnergyWrapper wrapper = new DefaultEnergyWrapper(this, s);
                    return LazyOptional.of(() -> wrapper);
                }).cast();
            }
        }
        return super.getCapability(cap, side);
    }
}
