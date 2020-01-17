package icyllis.fluxnetworks.common.tileentity;

import icyllis.fluxnetworks.api.tile.IFluxPhantom;
import icyllis.fluxnetworks.api.tile.ITransferHandler;
import icyllis.fluxnetworks.common.block.BlockSidedConnection;
import icyllis.fluxnetworks.fluxnet.transfer.ConnectionTransferHandler;
import icyllis.fluxnetworks.system.handler.TileEntityHandler;
import icyllis.fluxnetworks.system.util.wrapper.ForgeEnergyWrapper;
import net.minecraft.tileentity.TileEntity;
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
 * plug or point
 */
public abstract class TileFluxConnector extends TileFluxCore implements IFluxPhantom {

    final ConnectionTransferHandler handler = new ConnectionTransferHandler(this);

    private Map<Direction, ForgeEnergyWrapper> forgeWrappers = new HashMap<>();

    TileFluxConnector(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        for (Direction side : Direction.values()) {
            forgeWrappers.put(side, new ForgeEnergyWrapper(this, side));
        }
    }

    @Override
    public void updateTransfers(Direction... sides) {
        super.updateTransfers(sides);
        if(world != null) {
            for (Direction side : sides) {
                TileEntity tile = world.getTileEntity(pos.offset(side));
                boolean c = TileEntityHandler.INSTANCE.canRenderConnection(tile, side.getOpposite());
                int index = side.getIndex();
                world.setBlockState(pos, getBlockState().with(BlockSidedConnection.FACES.get(index), c), 10);
            }
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
