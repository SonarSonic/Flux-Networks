package sonar.flux.common.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.helpers.FontHelper;
import sonar.flux.common.tileentity.TileEntityFlux;

public class FluxStorage extends FluxConnection {

	public FluxStorage() {
		super(-5);
	}

	public static class Herculean extends FluxStorage{

		@Override
		public TileEntity createNewTileEntity(World world, int i) {
			return new TileEntityFlux.Storage.Advanced();
		}
	}
	
	public static class Gargantuan extends FluxStorage{

		@Override
		public TileEntity createNewTileEntity(World world, int i) {
			return new TileEntityFlux.Storage.Massive();
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityFlux.Storage.Basic();
	}

	@Override
	public void standardInfo(ItemStack stack, EntityPlayer player, List list) {
		//list.add("Stores Energy");
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, EntityPlayer player, List list) {
		int energy = stack.getTagCompound().getInteger("energy");
		if (energy != 0) {
			list.add(FontHelper.translate("energy.stored") + ": " + FontHelper.formatStorage(energy));
		}
	}
}
