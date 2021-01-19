package sonar.fluxnetworks.common.block;

import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluxPlug extends BlockSidedConnection {

    public BlockFluxPlug() {
        super("FluxPlug");
        bounding = new AxisAlignedBB(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(FluxTranslate.FLUX_PLUG_TOOLTIP.t());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFluxPlug();
    }

}
