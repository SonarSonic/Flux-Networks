package sonar.flux.common.block;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxConfig;
import sonar.flux.common.tileentity.TileStorage;

public class FluxStorage extends FluxConnection {

	public FluxStorage() {
		super();
	}

	public static class Herculean extends FluxStorage {

		@Override
		public TileEntity createNewTileEntity(World world, int i) {
			return new TileStorage.Herculean();
		}

		public int getMaxStorage() {
			return FluxConfig.herculeanCapacity;
		}

		public int getMaxTransfer() {
			return FluxConfig.herculeanTransfer;
		}
	}

	public static class Gargantuan extends FluxStorage {

		@Override
		public TileEntity createNewTileEntity(World world, int i) {
			return new TileStorage.Gargantuan();
		}

		public int getMaxStorage() {
			return FluxConfig.gargantuanCapacity;
		}

		public int getMaxTransfer() {
			return FluxConfig.gargantuanTransfer;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileStorage.Basic();
	}

	public int getMaxStorage() {
		return FluxConfig.basicCapacity;
	}

	public int getMaxTransfer() {
		return FluxConfig.basicTransfer;
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, World world, List<String> list, NBTTagCompound tag) {
		int energy = tag == null ? 0 : tag.getInteger("energy");
		list.add(FontHelper.translate("network.energy.stored") + ": " + FontHelper.formatStorage(energy) + "/" + FontHelper.formatStorage(getMaxStorage()));
	}
}
