package sonar.fluxnetworks.common.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import java.util.Arrays;

public final class FluxShapes {

    // flux connectors
    public static final VoxelShape
            FLUX_POINT_CENTRE_VOXEL = Block.box(5.5, 5.5, 5.5, 10.5, 10.5, 10.5),
            FLUX_PLUG_CENTRE_VOXEL = Block.box(4, 4, 4, 12, 12, 12);

    public static final AABB
            CONNECTOR_CABLE_BB_SHAPE = new AABB(7 / 16D, 1 / 16D, 7 / 16D, 9 / 16D, 6 / 16D, 9 / 16D),
            CONNECTOR_PLATE_BB_SHAPE = new AABB(5 / 16D, 0 / 16D, 5 / 16D, 11 / 16D, 1 / 16D, 11 / 16D);

    public static final VoxelShape[] CONNECTORS_ROTATED_VOXELS = Arrays.stream(FluxUtils.DIRECTIONS)
            .map(dir -> Shapes.or(rotate(CONNECTOR_CABLE_BB_SHAPE, dir), rotate(CONNECTOR_PLATE_BB_SHAPE, dir)))
            .toArray(VoxelShape[]::new);

    // flux controller
    public static final VoxelShape FLUX_CONTROLLER_VOXEL = Shapes.or(
            Block.box(0, 1, 0, 16, 15, 16), // bottom
            Block.box(1, 0, 1, 3, 16, 3), // strut 1
            Block.box(1, 0, 13, 3, 16, 15), // strut 2
            Block.box(13, 0, 1, 15, 16, 3), // strut 3
            Block.box(13, 0, 13, 15, 16, 15) // strut 4
    );

    @Nonnull
    public static VoxelShape rotate(@Nonnull AABB aabb, @Nonnull Direction dir) {
        return Shapes.create(rotateToDirection(aabb.move(-0.5, -0.5, -0.5), dir)
                .move(0.5, 0.5, 0.5));
    }

    @Nonnull
    public static AABB rotateToDirection(@Nonnull AABB aabb, @Nonnull Direction dir) {
        return switch (dir) {
            case UP -> new AABB(aabb.minX, -aabb.minY, -aabb.minZ, aabb.maxX,
                    -aabb.maxY, -aabb.maxZ);
            case NORTH -> new AABB(aabb.minX, -aabb.minZ, aabb.minY, aabb.maxX,
                    -aabb.maxZ, aabb.maxY);
            case SOUTH -> new AABB(-aabb.minX, aabb.minZ, -aabb.minY, -aabb.maxX,
                    aabb.maxZ, -aabb.maxY);
            case WEST -> new AABB(+aabb.minY, -aabb.minZ, -aabb.minX, +aabb.maxY,
                    -aabb.maxZ, -aabb.maxX);
            case EAST -> new AABB(-aabb.minY, aabb.minZ, aabb.minX, -aabb.maxY,
                    aabb.maxZ, aabb.maxX);
            default -> aabb;
        };
    }

    private FluxShapes() {
    }
}
