package sonar.fluxnetworks.common.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.common.core.FluxShapes;
import sonar.fluxnetworks.common.tileentity.TileFluxController;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class FluxControllerBlock extends FluxNetworkBlock {

    public FluxControllerBlock(Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return FluxShapes.FLUX_CONTROLLER_VOXEL;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(FluxTranslate.FLUX_CONTROLLER_TOOLTIP.k()));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFluxController();
    }

}
