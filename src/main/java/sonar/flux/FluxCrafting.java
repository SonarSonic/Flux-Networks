package sonar.flux;

import sonar.core.common.block.SonarBlockTip;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class FluxCrafting extends FluxNetworks {

	public static class StorageCrafting extends ShapedOreRecipe {

		public StorageCrafting(Block result, Object... recipe) {
			this(new ItemStack(result), recipe);
		}

		public StorageCrafting(Item result, Object... recipe) {
			this(new ItemStack(result), recipe);
		}

		public StorageCrafting(ItemStack result, Object... recipe) {
			super(result, recipe);
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting crafting) {
			int energyStored = 0;
			for (int i = 0; i < crafting.getSizeInventory(); i++) {
				ItemStack stack = crafting.getStackInSlot(i);
				if (stack != null && stack.hasTagCompound() && stack.getItem() instanceof SonarBlockTip) {
					NBTTagCompound tag = stack.getTagCompound();
					energyStored += tag.getInteger("energy");
				}
			}
			//System.out.print(energyStored);
			ItemStack stack = output.copy();
			if (output != null) {
				NBTTagCompound newTag = stack.getTagCompound();
				if (newTag == null) {
					newTag = new NBTTagCompound();
				}
				newTag.setInteger("energy", energyStored);
				stack.setTagCompound(newTag);
			}
			return stack;
		}
	}

	public static void addRecipes() {

		//addShaped(new ItemStack(burntRedstoneBlock, 1), new Object[] { "AAA", "AAA", "AAA", 'A', burntRedstone });
		//addShapeless(new ItemStack(burntRedstone, 9), new Object[] { new ItemStack(burntRedstoneBlock, 1) });
		addShaped(new ItemStack(fluxBlock, 1), new Object[] { "ACA", "CAC", "ACA", 'A', flux, 'C', fluxCore });
		addShaped(new ItemStack(fluxCore, 4), new Object[] { "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', flux, 'A', Items.ENDER_EYE });
		addShaped(new ItemStack(fluxController, 1), new Object[] { "BCB", "R R", "BBB", 'C', fluxCore, 'B', fluxBlock, 'R', flux });
		addShaped(new ItemStack(fluxPlug, 1), new Object[] { " C ", "CBC", " C ", 'C', fluxCore, 'B', fluxBlock });
		addShaped(new ItemStack(fluxPoint, 1), new Object[] { " C ", "CBC", " C ", 'C', fluxCore, 'B', Blocks.REDSTONE_BLOCK });
		addStorageRecipe(new ItemStack(fluxStorage, 1), new Object[] { "BBB", "G G", "BBB", 'B', fluxBlock, 'G', "paneGlassColorless" });
		addStorageRecipe(new ItemStack(largeFluxStorage, 1), new Object[] { "BBB", "G G", "BBB", 'B', fluxStorage, 'G', "paneGlassColorless" });
		addStorageRecipe(new ItemStack(massiveFluxStorage, 1), new Object[] { "BBB", "G G", "BBB", 'B', largeFluxStorage, 'G', "paneGlassColorless" });
	}

	public static void addShaped(ItemStack result, Object... input) {
		if (result != null && result.getItem() != null && input != null) {
			try {
				GameRegistry.addRecipe(result, input);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPED RECIPE: " + result);
			}
		}
	}

	public static void addStorageRecipe(ItemStack result, Object... input) {
		if (result != null && result.getItem() != null && input != null) {
			try {
				StorageCrafting oreRecipe = new StorageCrafting(result, input);
				GameRegistry.addRecipe(oreRecipe);
			} catch (Exception exception) {
				logger.error("ERROR ADDING FLUX STORAGE RECIPE: " + result);
			}
		}
	}

	public static void addShapedOre(ItemStack result, Object... input) {
		if (result != null && result.getItem() != null && input != null) {
			try {
				ShapedOreRecipe oreRecipe = new ShapedOreRecipe(result, input);
				GameRegistry.addRecipe(oreRecipe);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPED ORE RECIPE: " + result);
			}
		}
	}

	public static void addShapeless(ItemStack result, Object... input) {
		if (result != null && result.getItem() != null && input != null) {
			try {
				GameRegistry.addShapelessRecipe(result, input);
			} catch (Exception exception) {
				logger.error("ERROR ADDING SHAPELESS RECIPE: " + result);
			}
		}
	}
}
