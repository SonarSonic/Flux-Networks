package icyllis.fluxnetworks.common.block;

import icyllis.fluxnetworks.system.misc.FluxTranslate;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluxPoint extends BlockSidedConnection {

    public BlockFluxPoint() {
        bounding = VoxelShapes.create(0.34375F, 0.34375F, 0.34375F, 0.65625F, 0.65625F, 0.65625F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(FluxTranslate.FLUX_POINT_TOOLTIP);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null;
    }
}
