package sonar.flux.common.block;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import sonar.flux.FluxTranslate;
import sonar.flux.common.tileentity.TileFluxPoint;

import javax.annotation.Nonnull;
import java.util.List;

public class FluxPoint extends FluxSidedConnection {

	public FluxPoint() {
		super();
		this.bounding_box = new AxisAlignedBB(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileFluxPoint();
	}

    @Override
    public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
        list.add(FluxTranslate.FLUX_POINT_TOOLTIP.t());
    }
}
