package fluxnetworks.common.integration.oc;

import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OCDriver extends DriverSidedTileEntity {

    @Override
    public Class<?> getTileEntityClass() {
        return IOCPeripheral.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, BlockPos blockPos, EnumFacing enumFacing) {
        TileEntity tileEntity = world.getTileEntity(blockPos);
        if(tileEntity instanceof IOCPeripheral) {
            return new OCEnvironment((IOCPeripheral) tileEntity);
        }
        return null;
    }
}
