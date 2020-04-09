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

public class FluxPlugBlock extends FluxConnectorBlock {

    public FluxPlugBlock(Properties props) {
        super(props);
        //bounding = new AxisAlignedBB(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(FluxTranslate.FLUX_PLUG_TOOLTIP.t()));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileFluxPlug();
    }
}
