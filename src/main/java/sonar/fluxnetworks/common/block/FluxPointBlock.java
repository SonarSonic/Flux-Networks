package sonar.fluxnetworks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.common.misc.FluxShapes;
import sonar.fluxnetworks.common.tileentity.TileFluxPoint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FluxPointBlock extends FluxConnectorBlock {

    public FluxPointBlock(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        VoxelShape shape = FluxShapes.FLUX_POINT_CENTRE_VOXEL;
        for (Direction dir : Direction.values()) {
            if (state.get(SIDES_CONNECTED[dir.ordinal()])) {
                shape = VoxelShapes.combine(shape, FluxShapes.CONNECTORS_ROTATED_VOXELS[dir.ordinal()], IBooleanFunction.OR);
            }
        }
        return shape;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(FluxTranslate.FLUX_POINT_TOOLTIP.getTextComponent());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFluxPoint();
    }
}
