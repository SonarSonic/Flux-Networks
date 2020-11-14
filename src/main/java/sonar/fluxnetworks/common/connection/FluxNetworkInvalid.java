package sonar.fluxnetworks.common.connection;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.api.network.FluxLogicType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluxNetworkInvalid extends BasicFluxNetwork {

    public static final FluxNetworkInvalid INSTANCE = new FluxNetworkInvalid();

    private FluxNetworkInvalid() {
        super(FluxConstants.INVALID_NETWORK_ID, "Please select a network",
                FluxConstants.INVALID_NETWORK_COLOR, Util.DUMMY_UUID);
    }

    @Override
    public void onEndServerTick() {

    }

    @Nonnull
    @Override
    public AccessLevel getPlayerAccess(PlayerEntity player) {
        return AccessLevel.BLOCKED;
    }

    @Nonnull
    @Override
    public <T extends IFluxDevice> List<T> getConnections(FluxLogicType type) {
        return new ArrayList<>();
    }

    @Override
    public long getBufferLimiter() {
        return 0;
    }

    @Override
    public void markSortConnections() {

    }

    @Override
    public void enqueueConnectionAddition(@Nonnull IFluxDevice device) {

    }

    @Override
    public void enqueueConnectionRemoval(@Nonnull IFluxDevice device, boolean chunkUnload) {

    }

    @Override
    public boolean isValid() {
        return false;
    }
}
