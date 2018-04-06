package sonar.flux.common.block;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.flux.FluxTranslate;
import sonar.flux.common.tileentity.TileFluxPoint;

public class FluxPoint extends FluxSidedConnection {

	public FluxPoint() {
		super();
		this.setBlockBounds(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileFluxPoint();
	}

    @Override
    public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
        list.add(FluxTranslate.FLUX_POINT_TOOLTIP.t());
    }
}
