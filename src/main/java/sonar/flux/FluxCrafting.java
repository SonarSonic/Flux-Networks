package sonar.flux;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class FluxCrafting extends FluxNetworks {

	public static void addRecipes() {

		//addShaped(new ItemStack(burntRedstoneBlock, 1), new Object[] { "AAA", "AAA", "AAA", 'A', burntRedstone });
		//addShapeless(new ItemStack(burntRedstone, 9), new Object[] { new ItemStack(burntRedstoneBlock, 1) });
		addShaped(new ItemStack(fluxBlock, 1), new Object[] { "ACA", "CAC", "ACA", 'A', flux, 'C', fluxCore });
		addShaped(new ItemStack(fluxCore, 4), new Object[] { "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', flux, 'A', Items.ENDER_EYE });
		addShaped(new ItemStack(fluxController, 1), new Object[] { "BCB", "R R", "BBB", 'C', fluxCore, 'B', fluxBlock, 'R', flux });
		addShaped(new ItemStack(fluxPlug, 1), new Object[] { " C ", "CBC", " C ", 'C', fluxCore, 'B', fluxBlock });
		addShaped(new ItemStack(fluxPoint, 1), new Object[] { " C ", "CBC", " C ", 'C', fluxCore, 'B', Blocks.REDSTONE_BLOCK });
		addShapedOre(new ItemStack(fluxStorage, 1), new Object[] { "BBB", "G G", "BBB", 'B', fluxBlock, 'G', "paneGlassColorless" });
		addShapedOre(new ItemStack(largeFluxStorage, 1), new Object[] { "BBB", "G G", "BBB", 'B', fluxStorage, 'G', "paneGlassColorless" });
		addShapedOre(new ItemStack(massiveFluxStorage, 1), new Object[] { "BBB", "G G", "BBB", 'B', largeFluxStorage, 'G', "paneGlassColorless" });
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
