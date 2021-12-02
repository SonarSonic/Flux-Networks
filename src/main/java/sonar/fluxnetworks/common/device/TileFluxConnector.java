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
    protected abstract FluxConnectorHandler getTransferHandler();

    @Override
    public void onLoad() {
        super.onLoad();
        //noinspection ConstantConditions
        if (!level.isClientSide) {
            int newState = 0;
            for (Direction direction : FluxUtils.DIRECTIONS) {
                BlockEntity target = level.getBlockEntity(worldPosition.relative(direction));
                newState = getTransferHandler().updateSideTransfer(direction, target, direction.get3DDataValue() == 5);
            }
            sendBlockUpdateIfNeeded(newState);
        }
    }

    public void updateSideTransfer(@Nonnull Direction direction, @Nullable BlockEntity target) {
        int newState = getTransferHandler().updateSideTransfer(direction, target, true);
        sendBlockUpdateIfNeeded(newState);
    }

    private void sendBlockUpdateIfNeeded(int newState) {
        assert level != null && !level.isClientSide;
        if ((mFlags & CONNECTION_MASK) != newState) {
            mFlags = (mFlags & ~CONNECTION_MASK) | newState;
            sendBlockUpdate();
        }
    }

    @Override
    public void sendBlockUpdate() {
        super.sendBlockUpdate();
        assert level != null && !level.isClientSide;
        BlockState state = getBlockState();
        for (Direction dir : FluxUtils.DIRECTIONS) {
            state = state.setValue(FluxConnectorBlock.SIDES_CONNECTED[dir.get3DDataValue()],
                    (mFlags & (1 << dir.get3DDataValue())) != 0);
        }
        level.setBlock(worldPosition, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
    }
}
