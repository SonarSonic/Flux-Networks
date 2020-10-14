package sonar.fluxnetworks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
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
            boolean before = (mFlags & mask) == mask;
            boolean current = TileEntityHandler.canRenderConnection(neighbor, facing.getOpposite());
            if (before != current) {
                mFlags ^= mask;
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
            BlockState newState = FluxConnectorBlock.getConnectedState(getBlockState(), getFluxWorld(), getPos());
            world.setBlockState(pos, newState, 1 | 2);
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
