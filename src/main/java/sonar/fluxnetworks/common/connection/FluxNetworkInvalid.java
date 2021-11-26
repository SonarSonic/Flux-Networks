package sonar.fluxnetworks.common.connection;

import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.common.blockentity.FluxDeviceEntity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public final class FluxNetworkInvalid extends FluxNetworkBase {

    public static final FluxNetworkInvalid INSTANCE = new FluxNetworkInvalid();

    private FluxNetworkInvalid() {
        super(FluxConstants.INVALID_NETWORK_ID, "Please select a network",
                FluxConstants.INVALID_NETWORK_COLOR, Util.NIL_UUID);
    }

    @Override
    public void onEndServerTick() {
    }

    @Nonnull
    @Override
    public AccessLevel getPlayerAccess(@Nonnull Player player) {
        return AccessLevel.BLOCKED;
    }

    @Nonnull
    @Override
    public List<FluxDeviceEntity> getLogicalEntities(int logic) {
        return Collections.emptyList();
    }

    @Override
    public long getBufferLimiter() {
        return 0;
    }

    @Override
    public boolean enqueueConnectionAddition(@Nonnull FluxDeviceEntity device) {
        return true;
    }

    @Override
    public void enqueueConnectionRemoval(@Nonnull FluxDeviceEntity device, boolean chunkUnload) {
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
