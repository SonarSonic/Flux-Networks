package sonar.fluxnetworks.common.registry;

import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

public class RegistryRecipes {

    public static final ResourceLocation group = new ResourceLocation(FluxNetworks.MODID);

    /*public static void registerRecipes() {
        addShapedRecipe(new ItemStack(RegistryItems.FLUX_CORE, 4), "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', RegistryItems.FLUX, 'A', Items.ENDER_EYE);
        addShapedRecipe(new ItemStack(RegistryItems.FLUX_CONFIGURATOR, 1), " CP", " OC", "O  ", 'C', RegistryItems.FLUX, 'P', Items.ENDER_PEARL, 'O', Blocks.OBSIDIAN);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_BLOCK, 1), "ACA", "CAC", "ACA", 'A', RegistryItems.FLUX, 'C', RegistryItems.FLUX_CORE);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_CONTROLLER, 1), "BCB", "R R", "BBB", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK, 'R', RegistryItems.FLUX);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_POINT, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', Blocks.REDSTONE_BLOCK);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_PLUG, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK);
    }*/

    public static void registerRecipes(IForgeRegistry<IRecipe> registry) {
        registry.register(new FluxShapedRecipe(new ItemStack(RegistryItems.FLUX_CORE, 4), "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', RegistryItems.FLUX, 'A', Items.ENDER_EYE));
        registry.register(new FluxShapedRecipe(new ItemStack(RegistryItems.FLUX_CONFIGURATOR, 1), " CP", " OC", "O  ", 'C', RegistryItems.FLUX, 'P', Items.ENDER_PEARL, 'O', Blocks.OBSIDIAN));
        registry.register(new FluxShapedRecipe(new ItemStack(RegistryBlocks.FLUX_BLOCK, 1), "ACA", "CAC", "ACA", 'A', RegistryItems.FLUX, 'C', RegistryItems.FLUX_CORE));
        registry.register(new FluxShapedRecipe(new ItemStack(RegistryBlocks.FLUX_CONTROLLER, 1), "BCB", "R R", "BBB", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK, 'R', RegistryItems.FLUX));
        registry.register(new FluxShapedRecipe(new ItemStack(RegistryBlocks.FLUX_POINT, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', Blocks.REDSTONE_BLOCK));
        registry.register(new FluxShapedRecipe(new ItemStack(RegistryBlocks.FLUX_PLUG, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK));
        registry.register(new FluxStorageRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_1, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_BLOCK, 'G', "paneGlassColorless"));
        registry.register(new FluxStorageRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_2, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_STORAGE_1, 'G', "paneGlassColorless"));
        registry.register(new FluxStorageRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_3, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_STORAGE_2, 'G', "paneGlassColorless"));
        registry.register(new FluxShapelessRecipe(new ItemStack(RegistryBlocks.FLUX_CONTROLLER, 1), RegistryBlocks.FLUX_CONTROLLER));
        registry.register(new FluxShapelessRecipe(new ItemStack(RegistryBlocks.FLUX_POINT, 1), RegistryBlocks.FLUX_POINT));
        registry.register(new FluxShapelessRecipe(new ItemStack(RegistryBlocks.FLUX_PLUG, 1), RegistryBlocks.FLUX_PLUG));
        registry.register(new FluxShapelessRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_1, 1), RegistryBlocks.FLUX_STORAGE_1));
        registry.register(new FluxShapelessRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_2, 1), RegistryBlocks.FLUX_STORAGE_2));
        registry.register(new FluxShapelessRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_3, 1), RegistryBlocks.FLUX_STORAGE_3));
    }

    /*public static void addShapedRecipe(@Nonnull ItemStack result, @Nonnull Object... input) {
        GameRegistry.addShapedRecipe(new ResourceLocation(FluxNetworks.MODID, result.getUnlocalizedName()), group, result, input);
    }*/

    public static class FluxShapedRecipe extends ShapedOreRecipe {

        public FluxShapedRecipe(@Nonnull ItemStack result, Object... recipe) {
            super(RegistryRecipes.group, result, recipe);
            setRegistryName(new ResourceLocation(FluxNetworks.MODID, result.getTranslationKey()));
        }
    }

    public static class FluxShapelessRecipe extends ShapelessOreRecipe {

        public FluxShapelessRecipe(@Nonnull ItemStack result, Object... recipe) {
            super(RegistryRecipes.group, result, recipe);
            setRegistryName(new ResourceLocation(FluxNetworks.MODID, result.getTranslationKey() + 's'));
        }
    }

    public static class FluxStorageRecipe extends FluxShapedRecipe {

        public FluxStorageRecipe(@Nonnull ItemStack result, Object... recipe) {
            super(result, recipe);
        }

        @Nonnull
        @Override
        public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafting) {
            int energyTotal = 0, networkID = 0;
            boolean firstFound = false;
            for (int i = 0; i < crafting.getSizeInventory(); i++) {
                ItemStack stack = crafting.getStackInSlot(i);
                NBTTagCompound subTag = stack.getSubCompound(FluxUtils.FLUX_DATA);
                if(subTag != null) {
                    if(!firstFound) {
                        networkID = subTag.getInteger(FluxNetworkData.NETWORK_ID);
                        firstFound = true;
                    }
                    energyTotal += subTag.getInteger("energy");
                }
            }
            if(firstFound) {
                ItemStack stack = output.copy();
                NBTTagCompound subTag = stack.getOrCreateSubCompound(FluxUtils.FLUX_DATA);
                subTag.setInteger(FluxNetworkData.NETWORK_ID, networkID);
                subTag.setInteger("energy", energyTotal);
                return stack;
            }
            return super.getCraftingResult(crafting);
        }
    }

    public static class FluxRecipe {

        public final ItemStack input;
        public final ItemStack output;

        public FluxRecipe(ItemStack input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        public ItemStack getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }
    }
}
