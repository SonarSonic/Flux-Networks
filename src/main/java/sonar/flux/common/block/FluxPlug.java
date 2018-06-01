package sonar.flux.common.block;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import sonar.flux.FluxTranslate;
import sonar.flux.common.tileentity.TileFluxPlug;

import javax.annotation.Nonnull;
import java.util.List;

public class FluxPlug extends FluxSidedConnection {

	public FluxPlug() {
		super();
		this.bounding_box = new AxisAlignedBB(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
	}
	
	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileFluxPlug();
	}

    @Override
    public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
        list.add(FluxTranslate.FLUX_PLUG_TOOLTIP.t());
    }
}
