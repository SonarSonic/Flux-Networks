package sonar.fluxnetworks.common.device;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import sonar.fluxnetworks.api.device.FluxDeviceType;
import sonar.fluxnetworks.api.device.IFluxController;
import sonar.fluxnetworks.common.util.FluxGuiStack;
import sonar.fluxnetworks.register.RegistryBlockEntityTypes;

import javax.annotation.Nonnull;

public class TileFluxController extends TileFluxDevice implements IFluxController {

    private final FluxControllerHandler mHandler = new FluxControllerHandler(this);

    public TileFluxController(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super(RegistryBlockEntityTypes.FLUX_CONTROLLER.get(), pos, state);
    }

    @Nonnull
    @Override
    public FluxDeviceType getDeviceType() {
        return FluxDeviceType.CONTROLLER;
    }

    @Nonnull
    @Override
    public FluxControllerHandler getTransferHandler() {
        return mHandler;
    }

    @Nonnull
    @Override
    public ItemStack getDisplayStack() {
        return FluxGuiStack.FLUX_CONTROLLER;
    }
}
