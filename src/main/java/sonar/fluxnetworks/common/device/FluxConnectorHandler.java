package sonar.fluxnetworks.common.device;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.energy.IBlockEnergyConnector;
import sonar.fluxnetworks.common.connection.TransferHandler;
import sonar.fluxnetworks.common.util.EnergyUtils;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class FluxConnectorHandler extends TransferHandler {

    // lazy-loading elements
    protected final SideTransfer[] mTransfers = new SideTransfer[FluxUtils.DIRECTIONS.length];

    protected FluxConnectorHandler() {
        super(FluxConfig.defaultLimit);
    }

    @Override
    public void onCycleStart() {
        for (var transfer : mTransfers) {
            if (transfer != null) {
                transfer.onCycleStart();
            }
        }
    }

    @Override
    public void writeCustomTag(@Nonnull CompoundTag tag, byte type) {
        super.writeCustomTag(tag, type);
        tag.putLong(FluxConstants.BUFFER, mBuffer);
    }

    // server only
    public int updateSideTransfer(@Nonnull Direction side, @Nullable BlockEntity target, boolean fullState) {
        int index = side.get3DDataValue();
        SideTransfer transfer = mTransfers[index];
        int connection;
        final IBlockEnergyConnector connector;
        if (target == null || (connector = EnergyUtils.getConnector(target, side.getOpposite())) == null) {
            if (transfer != null) {
                transfer.set(null, null);
            }
            connection = 0;
        } else {
            if (transfer == null) {
                transfer = new SideTransfer(side);
                mTransfers[index] = transfer;
            }
            transfer.set(target, connector);
            connection = 1 << index;
        }
        if (fullState) {
            for (int i = 0, e = mTransfers.length; i < e; i++) {
                if (i == index) {
                    continue;
                }
                transfer = mTransfers[i];
                if (transfer != null && transfer.getTarget() != null) {
                    connection |= 1 << i;
                }
            }
        }
        return connection;
    }
}
