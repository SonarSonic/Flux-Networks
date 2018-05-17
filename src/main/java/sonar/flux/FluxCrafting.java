package sonar.flux;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import sonar.core.SonarCrafting;
import sonar.core.common.block.SonarBlockTip;

import javax.annotation.Nonnull;

public class FluxCrafting extends FluxNetworks {

	public static class StorageCrafting extends ShapedOreRecipe {

		public StorageCrafting(ItemStack result, Object... recipe) {
			super(new ResourceLocation(FluxConstants.MODID, result.getUnlocalizedName()), result, recipe);
			setRegistryName(new ResourceLocation(FluxConstants.MODID, result.getUnlocalizedName()));
		}

		@Nonnull
        @Override
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafting) {
			int energyStored = 0;
			for (int i = 0; i < crafting.getSizeInventory(); i++) {
				ItemStack stack = crafting.getStackInSlot(i);
				if (stack.hasTagCompound() && stack.getItem() instanceof SonarBlockTip) {
					NBTTagCompound tag = stack.getTagCompound();
					if (tag != null)
						energyStored += tag.getInteger("energy");
				}
			}
			ItemStack stack = output.copy();
			NBTTagCompound newTag = stack.getTagCompound();
			if (newTag == null)
				newTag = new NBTTagCompound();
			newTag.setInteger("energy", energyStored);
			stack.setTagCompound(newTag);
			return stack;
		}
	}

	public static class FluxRecipe extends ShapelessOreRecipe {

		public FluxRecipe(ItemStack result, Object[] recipe) {
			super(new ResourceLocation(FluxConstants.MODID, result.getUnlocalizedName() + "flint"), result, recipe);
		}

		@Nonnull
        @Override
		public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafting) {
			return super.getCraftingResult(crafting);
		}

	}

	public static void addRecipes() {
		ResourceLocation group = new ResourceLocation("FluxNetworks");
		// RecipeSorter.register("fluxnetworks:storage",
		// FluxCrafting.StorageCrafting.class, RecipeSorter.Category.SHAPED,
		// "after:forge:shapedore");
		SonarCrafting.addShaped(FluxConstants.MODID, group, new ItemStack(fluxConfigurator, 1), " CP", " OC", "O  ", 'C', fluxCore, 'P', Items.ENDER_PEARL, 'O', Blocks.OBSIDIAN);
		SonarCrafting.addShaped(FluxConstants.MODID, group, new ItemStack(fluxBlock, 1), "ACA", "CAC", "ACA", 'A', flux, 'C', fluxCore);
		SonarCrafting.addShaped(FluxConstants.MODID, group, new ItemStack(fluxCore, 4), "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', flux, 'A', Items.ENDER_EYE);
		SonarCrafting.addShaped(FluxConstants.MODID, group, new ItemStack(fluxController, 1), "BCB", "R R", "BBB", 'C', fluxCore, 'B', fluxBlock, 'R', flux);
		SonarCrafting.addShaped(FluxConstants.MODID, group, new ItemStack(fluxPlug, 1), " C ", "CBC", " C ", 'C', fluxCore, 'B', fluxBlock);
		SonarCrafting.addShaped(FluxConstants.MODID, group, new ItemStack(fluxPoint, 1), " C ", "CBC", " C ", 'C', fluxCore, 'B', Blocks.REDSTONE_BLOCK);
		addStorageRecipe(new ItemStack(fluxStorage, 1), "BBB", "G G", "BBB", 'B', fluxBlock, 'G', "paneGlassColorless");
		addStorageRecipe(new ItemStack(largeFluxStorage, 1), "BBB", "G G", "BBB", 'B', fluxStorage, 'G', "paneGlassColorless");
		addStorageRecipe(new ItemStack(massiveFluxStorage, 1), "BBB", "G G", "BBB", 'B', largeFluxStorage, 'G', "paneGlassColorless");
	}

	public static void addStorageRecipe(ItemStack result, Object... input) {
		if (!result.isEmpty() && input != null) {
			try {
				StorageCrafting storageRecipe = new StorageCrafting(result, input);
				ForgeRegistries.RECIPES.register(storageRecipe);
			} catch (Exception exception) {
				logger.error("ERROR ADDING FLUX STORAGE RECIPE: " + result);
			}
		}
	}
}
