package sonar.fluxnetworks.common.integration.cctweaked;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.common.device.TileFluxController;
import sonar.fluxnetworks.common.device.TileFluxDevice;

public class PeripheralHandler implements IPeripheralProvider {

    @Override
    public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side) {
        BlockEntity tile = world.getBlockEntity(pos);

        if (tile instanceof TileFluxDevice fluxDevice) return LazyOptional.of(() -> new CCTPeripheral(fluxDevice));
        return LazyOptional.empty();
    }
}