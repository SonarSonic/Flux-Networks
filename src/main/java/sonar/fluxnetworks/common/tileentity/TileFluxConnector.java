package sonar.fluxnetworks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import sonar.fluxnetworks.common.block.FluxConnectorBlock;
import sonar.fluxnetworks.common.handler.TileEntityHandler;
import sonar.fluxnetworks.common.tileentity.energy.TileDefaultEnergy;

public abstract class TileFluxConnector extends TileDefaultEnergy {

    public TileFluxConnector(TileEntityType<?> tileEntityTypeIn, String customName, long limit) {
        super(tileEntityTypeIn, customName, limit);
    }

    @Override
    public void updateTransfers(Direction... dirs) {
        super.updateTransfers(dirs);
        boolean sendUpdate = false;
        for (Direction facing : dirs) {
            //noinspection ConstantConditions
            TileEntity neighbor = world.getTileEntity(pos.offset(facing));
            int mask = 1 << facing.getIndex();
            boolean before = (flags & mask) == mask;
            boolean current = TileEntityHandler.canRenderConnection(neighbor, facing.getOpposite());
            if (before != current) {
                flags ^= mask;
                sendUpdate = true;
            }
        }
        if (sendUpdate) {
            sendFullUpdatePacket();
        }
    }

    @Override
    public void sendFullUpdatePacket() {
        //noinspection ConstantConditions
        if (!world.isRemote) {
            BlockState state = getBlockState();
            for (Direction dir : Direction.values()) {
                state = state.with(FluxConnectorBlock.SIDES_CONNECTED[dir.getIndex()],
                        (flags & 1 << dir.getIndex()) != 0);
            }
            world.setBlockState(pos, state, Constants.BlockFlags.NOTIFY_NEIGHBORS | Constants.BlockFlags.BLOCK_UPDATE);
        }
    }

    /* TODO - FIX ONE PROBE "IBIGPOWER"
    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getStoredPower(){
        return getBuffer();
    }

    @Override
    @Optional.Method(modid = "theoneprobe")
    public long getCapacity(){
        return getBuffer();
    }
    */
}
