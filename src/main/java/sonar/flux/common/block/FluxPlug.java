package sonar.flux.common.block;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.flux.common.tileentity.TileFluxPlug;

public class FluxPlug extends FluxSidedConnection {

	public FluxPlug() {
		super();
		this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileFluxPlug();
	}

    @Override
    public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
        list.add("Sends Energy");
    }
}
