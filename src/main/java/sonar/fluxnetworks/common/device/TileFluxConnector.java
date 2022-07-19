package sonar.fluxnetworks.common.device;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sonar.fluxnetworks.common.block.FluxConnectorBlock;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileFluxConnector extends TileFluxDevice {

    protected TileFluxConnector(@Nonnull BlockEntityType<?> type, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(type, pos, state);
    }

    @Nonnull
    @Override
    public abstract FluxConnectorHandler getTransferHandler();

    @Override
    protected void onFirstTick() {
        super.onFirstTick();
        //noinspection ConstantConditions
        if (!level.isClientSide) {
            int newState = 0;
            for (Direction direction : FluxUtils.DIRECTIONS) {
                BlockEntity target = level.getBlockEntity(worldPosition.relative(direction));
                newState |= getTransferHandler().updateSideTransfer(direction, target, false);
            }
            sendBlockUpdateIfNeeded(newState);
        }
    }

    public void updateSideTransfer(@Nonnull Direction side, @Nullable BlockEntity target) {
        int newState = getTransferHandler().updateSideTransfer(side, target, true);
        sendBlockUpdateIfNeeded(newState);
    }

    private void sendBlockUpdateIfNeeded(int newState) {
        assert level != null && !level.isClientSide;
        if ((mFlags & SIDES_CONNECTED_MASK) != newState) {
            mFlags = (mFlags & ~SIDES_CONNECTED_MASK) | newState;
            sendBlockUpdate();
        }
    }

    @Override
    public void sendBlockUpdate() {
        super.sendBlockUpdate();
        assert level != null && !level.isClientSide;
        BlockState state = getBlockState();
        for (Direction dir : FluxUtils.DIRECTIONS) {
            int index = dir.get3DDataValue();
            state = state.setValue(FluxConnectorBlock.SIDES_CONNECTED[index], (mFlags & (1 << index)) != 0);
        }
        level.setBlock(worldPosition, state, Block.UPDATE_IMMEDIATE);
    }
}
