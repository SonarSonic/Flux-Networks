package sonar.fluxnetworks.common.core;

import com.sun.javafx.geom.Vec3d;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class FluxShapes {

    //// flux connectors

    public static final VoxelShape FLUX_POINT_CENTRE_VOXEL = VoxelShapes.create(getBBFromPixels(5.5, 5.5, 5.5, 10.5, 10.5, 10.5));
    public static final VoxelShape FLUX_PLUG_CENTRE_VOXEL  = VoxelShapes.create(getBBFromPixels(4, 4, 4, 12, 12, 12));

    public static final AxisAlignedBB CONNECTOR_CABLE_BB_SHAPE                = getBBFromPixels(7, 1, 7, 9, 6, 9);
    public static final VoxelShape[]  CONNECTOR_CABLE_BB_SHAPE_ROTATED_VOXELS = getRotatedVoxels(CONNECTOR_CABLE_BB_SHAPE, Direction.values());

    public static final AxisAlignedBB CONNECTOR_PLATE_BB_SHAPE                = getBBFromPixels(5, 0, 5, 11, 1, 11);
    public static final VoxelShape[]  CONNECTOR_PLATE_BB_SHAPE_ROTATED_VOXELS = getRotatedVoxels(CONNECTOR_PLATE_BB_SHAPE, Direction.values());

    public static final VoxelShape[] CONNECTORS_ROTATED_VOXELS = new VoxelShape[]{
            VoxelShapes.combine(CONNECTOR_CABLE_BB_SHAPE_ROTATED_VOXELS[0], CONNECTOR_PLATE_BB_SHAPE_ROTATED_VOXELS[0], IBooleanFunction.OR),
            VoxelShapes.combine(CONNECTOR_CABLE_BB_SHAPE_ROTATED_VOXELS[1], CONNECTOR_PLATE_BB_SHAPE_ROTATED_VOXELS[1], IBooleanFunction.OR),
            VoxelShapes.combine(CONNECTOR_CABLE_BB_SHAPE_ROTATED_VOXELS[2], CONNECTOR_PLATE_BB_SHAPE_ROTATED_VOXELS[2], IBooleanFunction.OR),
            VoxelShapes.combine(CONNECTOR_CABLE_BB_SHAPE_ROTATED_VOXELS[3], CONNECTOR_PLATE_BB_SHAPE_ROTATED_VOXELS[3], IBooleanFunction.OR),
            VoxelShapes.combine(CONNECTOR_CABLE_BB_SHAPE_ROTATED_VOXELS[4], CONNECTOR_PLATE_BB_SHAPE_ROTATED_VOXELS[4], IBooleanFunction.OR),
            VoxelShapes.combine(CONNECTOR_CABLE_BB_SHAPE_ROTATED_VOXELS[5], CONNECTOR_PLATE_BB_SHAPE_ROTATED_VOXELS[5], IBooleanFunction.OR)
    };

    /// flux controller

    public static VoxelShape FLUX_CONTROLLER_VOXEL = combineAll(
            VoxelShapes.create(getBBFromPixels(0, 1, 0, 16, 15, 16)), // bottom
            VoxelShapes.create(getBBFromPixels(1, 0, 1, 3, 16, 3)), // strut 1
            VoxelShapes.create(getBBFromPixels(1, 0, 13, 3, 16, 15)), // strut 2
            VoxelShapes.create(getBBFromPixels(13, 0, 1, 15, 16, 3)), // strut 3
            VoxelShapes.create(getBBFromPixels(13, 0, 13, 15, 16, 15)) // strut 4
    );


    public static final double PIXEL = 0.0625;

    public static Vec3d getVec3DFromPixels(double x, double y, double z) {
        return new Vec3d(x * PIXEL, y * PIXEL, z * PIXEL);
    }

    public static AxisAlignedBB getBBFromPixels(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX * PIXEL, minY * PIXEL, minZ * PIXEL, maxX * PIXEL, maxY * PIXEL, maxZ * PIXEL);
    }

    public static VoxelShape[] getRotatedVoxels(AxisAlignedBB downShape, Direction[] dirs) {
        VoxelShape[] rotated = new VoxelShape[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            rotated[i] = VoxelShapes.create(rotate(downShape, dirs[i]));
        }
        return rotated;
    }

    public static AxisAlignedBB rotate(AxisAlignedBB shape, Direction dir) {
        return rotateToDirection(shape.offset(-0.5, -0.5, -0.5), dir).offset(0.5, 0.5, 0.5);
    }

    private static AxisAlignedBB rotateToDirection(AxisAlignedBB boundingBox, Direction side) {
        switch (side) {
            case DOWN:
                return boundingBox;
            case UP:
                return new AxisAlignedBB(boundingBox.minX, -boundingBox.minY, -boundingBox.minZ, boundingBox.maxX, -boundingBox.maxY, -boundingBox.maxZ);
            case NORTH:
                return new AxisAlignedBB(boundingBox.minX, -boundingBox.minZ, boundingBox.minY, boundingBox.maxX, -boundingBox.maxZ, boundingBox.maxY);
            case SOUTH:
                return new AxisAlignedBB(-boundingBox.minX, boundingBox.minZ, -boundingBox.minY, -boundingBox.maxX, boundingBox.maxZ, -boundingBox.maxY);
            case WEST:
                return new AxisAlignedBB(boundingBox.minY, -boundingBox.minZ, -boundingBox.minX, boundingBox.maxY, -boundingBox.maxZ, -boundingBox.maxX);
            case EAST:
                return new AxisAlignedBB(-boundingBox.minY, boundingBox.minZ, boundingBox.minX, -boundingBox.maxY, boundingBox.maxZ, boundingBox.maxX);
        }
        return boundingBox;
    }

    public static AxisAlignedBB rotate(AxisAlignedBB box, Rotation rotation) {
        switch (rotation) {
            case NONE:
                return box;
            case CLOCKWISE_90:
                return new AxisAlignedBB(-box.minZ, box.minY, box.minX, -box.maxZ, box.maxY, box.maxX);
            case CLOCKWISE_180:
                return new AxisAlignedBB(-box.minX, box.minY, -box.minZ, -box.maxX, box.maxY, -box.maxZ);
            case COUNTERCLOCKWISE_90:
                return new AxisAlignedBB(box.minZ, box.minY, -box.minX, box.maxZ, box.maxY, -box.maxX);
        }
        return box;
    }

    public static VoxelShape combineAll(VoxelShape... shapes) {
        VoxelShape shape = null;
        for (VoxelShape nextShape : shapes) {
            if (shape == null) {
                shape = nextShape;
            } else {
                shape = VoxelShapes.combine(shape, nextShape, IBooleanFunction.OR);
            }
        }
        return shape;
    }


}
