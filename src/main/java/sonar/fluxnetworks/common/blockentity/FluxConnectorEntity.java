package sonar.fluxnetworks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Constants;
import sonar.fluxnetworks.common.block.FluxConnectorBlock;
import sonar.fluxnetworks.common.connection.transfer.FluxConnectorHandler;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class FluxConnectorEntity extends FluxDeviceEntity {

    protected FluxConnectorEntity(@Nonnull BlockEntityType<?> type, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(type, pos, state);
    }

    @Nonnull
    @Override
    public abstract FluxConnectorHandler getTransferHandler();

    @Override
    protected void onFirstLoad() {
        super.onFirstLoad();
        int newState = 0;
        for (Direction direction : FluxUtils.DIRECTIONS) {
            //noinspection ConstantConditions
            BlockEntity target = level.getBlockEntity(worldPosition.relative(direction));
            newState = getTransferHandler().updateSideTransfer(direction, target, direction.get3DDataValue() == 5);
        }
        sendBlockUpdateIfNeeded(newState);
    }

    public void updateSideTransfer(@Nonnull Direction direction, @Nullable BlockEntity target) {
        int newState = getTransferHandler().updateSideTransfer(direction, target, true);
        sendBlockUpdateIfNeeded(newState);
    }

    private void sendBlockUpdateIfNeeded(int newState) {
        assert level != null && !level.isClientSide;
        if ((mFlags & CONNECTION_MASK) == newState) {
            return;
        }
        mFlags = (mFlags & ~CONNECTION_MASK) | newState;
        sendBlockUpdate();
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
        level.setBlock(worldPosition, state,
                Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.RERENDER_MAIN_THREAD);
    }
}
