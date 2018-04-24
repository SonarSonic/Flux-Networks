package sonar.flux.common.block;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.flux.FluxTranslate;
import sonar.flux.common.tileentity.TileController;

import javax.annotation.Nonnull;

public class FluxController extends FluxConnection {

	public FluxController() {
		super();
		this.setBlockBounds(0.0625F, 0.0625F, 0.0625F, 1 - 0.0625F, 1 - 0.0625F, 1 - 0.0625F);
	}
	
	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileController();
	}
	
    @Override
    public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
		list.add(FluxTranslate.FLUX_CONTROLLER_TOOLTIP.t());
	}
}
