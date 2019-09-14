package fluxnetworks.common.block;

import fluxnetworks.FluxTranslate;
import fluxnetworks.common.tileentity.TileFluxPoint;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluxPoint extends BlockSidedConnection {

    public BlockFluxPoint() {
        super("fluxPoint");
        bounding = new AxisAlignedBB(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(FluxTranslate.FLUX_POINT_TOOLTIP.t());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFluxPoint();
    }
}
