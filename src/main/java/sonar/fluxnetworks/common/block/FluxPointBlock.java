package sonar.fluxnetworks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import sonar.fluxnetworks.common.tileentity.TileFluxPoint;

import javax.annotation.Nullable;
import java.util.List;

public class FluxPointBlock extends FluxConnectorBlock {

    public FluxPointBlock(Properties props) {
        super(props);
        //bounding = new AxisAlignedBB(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(FluxTranslate.FLUX_POINT_TOOLTIP.k()));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFluxPoint();
    }
}
