package sonar.flux.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.flux.common.tileentity.TileEntityPoint;

import java.util.List;

public class FluxPoint extends FluxSidedConnection {

	public FluxPoint() {
		super();
		this.setBlockBounds(0.375F, 0.375F, 0.375F, 0.625F, 0.625F, 0.625F);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityPoint();
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
    public void standardInfo(ItemStack stack, EntityPlayer player, List<String> list) {
		list.add("Receives Energy");
	}

    @Override
    public void standardInfo(ItemStack stack, World world, List<String> list) {
        list.add("Receives Energy");
    }
}
