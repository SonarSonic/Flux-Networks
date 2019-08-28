package fluxnetworks.common.registry;

import fluxnetworks.FluxNetworks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

public class RegistryRecipes {

    public static final ResourceLocation group = new ResourceLocation("FluxNetworksX");
    public static int id;

    public static void registerRecipes() {
        addShapedRecipe(new ItemStack(RegistryItems.FLUX_CORE, 4), "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', RegistryItems.FLUX, 'A', Items.ENDER_EYE);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_BLOCK, 1), "ACA", "CAC", "ACA", 'A', RegistryItems.FLUX, 'C', RegistryItems.FLUX_CORE);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_CONTROLLER, 1), "BCB", "R R", "BBB", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK, 'R', RegistryItems.FLUX);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_POINT, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', Blocks.REDSTONE_BLOCK);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_PLUG, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK);
    }

    public static void addShapedRecipe(@Nonnull ItemStack result, @Nonnull Object... input) {
        GameRegistry.addShapedRecipe(getRecipeResourceLocation(result), group, result, input);
    }

    public static ResourceLocation getRecipeResourceLocation(ItemStack result) {
        return new ResourceLocation(FluxNetworks.MODID, result.getUnlocalizedName() + id++);
    }
}
