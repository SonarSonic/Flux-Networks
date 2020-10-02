package sonar.fluxnetworks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.common.misc.FluxShapes;
import sonar.fluxnetworks.common.tileentity.TileFluxController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FluxControllerBlock extends FluxDeviceBlock {

    public FluxControllerBlock(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return FluxShapes.FLUX_CONTROLLER_VOXEL;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, @Nonnull List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(FluxTranslate.FLUX_CONTROLLER_TOOLTIP.getTextComponent());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFluxController();
    }
}
