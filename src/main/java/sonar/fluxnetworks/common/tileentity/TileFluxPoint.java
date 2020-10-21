package sonar.fluxnetworks.common.tileentity;

import net.minecraft.item.ItemStack;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.device.IFluxPoint;
import sonar.fluxnetworks.api.network.FluxDeviceType;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.common.connection.transfer.FluxPointHandler;
import sonar.fluxnetworks.common.misc.FluxGuiStack;
import sonar.fluxnetworks.common.registry.RegistryBlocks;

import javax.annotation.Nonnull;

public class TileFluxPoint extends TileFluxConnector implements IFluxPoint {

    private final FluxPointHandler handler = new FluxPointHandler(this);

    public TileFluxPoint() {
        super(RegistryBlocks.FLUX_POINT_TILE, "Flux Point", FluxConfig.defaultLimit);
    }

    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.POINT;
    }

    @Nonnull
    @Override
    public ITransferHandler getTransferHandler() {
        return handler;
    }

    @Nonnull
    @Override
    public ItemStack getDisplayStack() {
        return FluxGuiStack.FLUX_POINT;
    }
}
