package icyllis.fluxnetworks.system.util;

import icyllis.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.Optional;

public class FluxUtils {

    public static Direction getBlockDirection(BlockPos pos, BlockPos other) {
        float dx = other.getX() - pos.getX();
        float dy = other.getY() - pos.getY();
        float dz = other.getZ() - pos.getZ();
        return Direction.getFacingFromVector(dx, dy, dz);
    }

    public static Optional<TileFluxCore> getFluxTE(IWorldReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return Optional.ofNullable((te instanceof TileFluxCore) ? (TileFluxCore) te : null);
    }

}
