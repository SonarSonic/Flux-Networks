package sonar.flux.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.flux.common.tileentity.TileEntityController;

import java.util.List;

public class FluxController extends FluxConnection {

	public FluxController() {
		super();
		this.setBlockBounds(0.0625F, 0.0625F, 0.0625F, 1 - 0.0625F, 1 - 0.0625F, 1 - 0.0625F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityController();
	}

	@Override
    public void standardInfo(ItemStack stack, EntityPlayer player, List<String> list) {
        list.add("Manages Energy");
    }

    @Override
    public void standardInfo(ItemStack stack, World world, List<String> list) {
		list.add("Manages Energy");
	}
}
