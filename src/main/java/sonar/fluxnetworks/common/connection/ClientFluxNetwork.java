package sonar.fluxnetworks.common.connection;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientFluxNetwork extends FluxNetwork {

    public ClientFluxNetwork() {
    }

    @Override
    public void onEndServerTick() {
        throw new IllegalStateException();
    }

    @Nonnull
    @Override
    public List<TileFluxDevice> getLogicalEntities(int logic) {
        throw new IllegalStateException();
    }

    @Override
    public long getBufferLimiter() {
        throw new IllegalStateException();
    }

    @Override
    public boolean enqueueConnectionAddition(@Nonnull TileFluxDevice device) {
        throw new IllegalStateException();
    }

    @Override
    public void enqueueConnectionRemoval(@Nonnull TileFluxDevice device, boolean chunkUnload) {
        throw new IllegalStateException();
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
