package icyllis.fluxnetworks.common.tileentity;

import icyllis.fluxnetworks.api.tile.IFluxPhantom;
import icyllis.fluxnetworks.api.tile.ITransferHandler;
import icyllis.fluxnetworks.common.tileentity.component.ConnectionTransferHandler;
import icyllis.fluxnetworks.system.util.ForgeEnergyWrapper;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Plug or point tile entity (6 sides)
 */
public abstract class TileFluxConnection extends TileFluxCore implements IFluxPhantom {

    final ConnectionTransferHandler handler = new ConnectionTransferHandler(this);
    private Map<Direction, ForgeEnergyWrapper> forgeWrappers = new HashMap<>();

    TileFluxConnection(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        for (Direction side : Direction.values()) {
            forgeWrappers.put(side, new ForgeEnergyWrapper(this, side));
        }
    }

    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY && side != null) {
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() -> forgeWrappers.get(side)));
        }
        return super.getCapability(cap, side);
    }
}
