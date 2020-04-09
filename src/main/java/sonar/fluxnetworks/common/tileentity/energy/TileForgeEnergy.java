package sonar.fluxnetworks.common.tileentity.energy;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.api.tiles.IFluxPhantomEnergy;
import sonar.fluxnetworks.common.core.ForgeEnergyWrapper;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class TileForgeEnergy extends TileFluxCore implements IFluxPhantomEnergy {

    public Map<Direction, ForgeEnergyWrapper> wrappers = new HashMap<>();
    {
        wrappers.put(null, new ForgeEnergyWrapper(this, null));
        for(Direction face : Direction.values()) {
            wrappers.put(face, new ForgeEnergyWrapper(this, face));
        }
    }

    public TileForgeEnergy(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public ForgeEnergyWrapper getEnergyWrapper(Direction facing) {
        return wrappers.get(facing);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY){
            return LazyOptional.of(() -> getEnergyWrapper(side)).cast();
        }
        return super.getCapability(cap, side);
    }
}
