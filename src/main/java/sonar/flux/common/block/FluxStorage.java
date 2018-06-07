package sonar.flux.common.block;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.common.block.SonarBlock;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxConfig;
import sonar.flux.FluxTranslate;
import sonar.flux.common.tileentity.TileStorage;

import javax.annotation.Nonnull;
import java.util.List;

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
	public TileEntity createNewTileEntity(@Nonnull World world, int i) {
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
        list.add(FluxTranslate.FLUX_STORAGE_TOOLTIP.t());
        NBTTagCompound subTag = stack.getSubCompound(SonarBlock.DROP_TAG_NAME);
		int energy = subTag == null ? 0 : subTag.getInteger("energy");
		list.add(FluxTranslate.STORED.t() + ": " + FontHelper.formatStorage(energy) + "/" + FontHelper.formatStorage(getMaxStorage()));
	}
}
