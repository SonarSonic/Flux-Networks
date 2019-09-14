package fluxnetworks.common.block;

import fluxnetworks.FluxTranslate;
import fluxnetworks.common.tileentity.TileFluxController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluxController extends BlockFluxCore {

    public BlockFluxController() {
        super("FluxController");
        bounding = new AxisAlignedBB(0.0625F, 0.0625F, 0.0625F, 1 - 0.0625F, 1 - 0.0625F, 1 - 0.0625F);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(FluxTranslate.FLUX_CONTROLLER_TOOLTIP.t());
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFluxController();
    }
}
