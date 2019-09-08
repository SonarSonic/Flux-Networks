package fluxnetworks.common.registry;

import fluxnetworks.FluxNetworks;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;

public class RegistryRecipes {

    public static final ResourceLocation group = new ResourceLocation(FluxNetworks.MODID);

    public static void registerRecipes() {
        addShapedRecipe(new ItemStack(RegistryItems.FLUX_CORE, 4), "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', RegistryItems.FLUX, 'A', Items.ENDER_EYE);
        addShapedRecipe(new ItemStack(RegistryItems.FLUX_CONFIGURATOR, 1), " CP", " OC", "O  ", 'C', RegistryItems.FLUX, 'P', Items.ENDER_PEARL, 'O', Blocks.OBSIDIAN);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_BLOCK, 1), "ACA", "CAC", "ACA", 'A', RegistryItems.FLUX, 'C', RegistryItems.FLUX_CORE);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_CONTROLLER, 1), "BCB", "R R", "BBB", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK, 'R', RegistryItems.FLUX);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_POINT, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', Blocks.REDSTONE_BLOCK);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_PLUG, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_1, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_BLOCK, 'G', "paneGlassColorless");
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_2, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_STORAGE_1, 'G', "paneGlassColorless");
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_STORAGE_3, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_STORAGE_2, 'G', "paneGlassColorless");
    }

    public static void addShapedRecipe(@Nonnull ItemStack result, @Nonnull Object... input) {
        GameRegistry.addShapedRecipe(new ResourceLocation(FluxNetworks.MODID, result.getUnlocalizedName()), group, result, input);
    }

    /*public static void addStorageRecipe(ItemStack result, Object... input) {
        if (!result.isEmpty() && input != null) {
            try {
                StorageCrafting storageRecipe = new StorageCrafting(result, input);
                ForgeRegistries.RECIPES.register(storageRecipe);
            } catch (Exception e) {

            }
        }
    }

    public static class StorageCrafting extends ShapedOreRecipe {

        public StorageCrafting(ItemStack result, Object... recipe) {
            super(new ResourceLocation(FluxNetworks.MODID, result.getUnlocalizedName()), result, recipe);
            setRegistryName(new ResourceLocation(FluxNetworks.MODID, result.getUnlocalizedName()));
        }

        @Nonnull
        @Override
        public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafting) {
            int energyStored = 0;
            for (int i = 0; i < crafting.getSizeInventory(); i++) {
                ItemStack stack = crafting.getStackInSlot(i);
                if (stack.hasTagCompound()) {
                    NBTTagCompound tag = stack.getSubCompound(FluxUtils.FLUX_DATA);
                    if (tag != null)
                        energyStored += tag.getInteger("energy");
                }
            }
            ItemStack stack = output.copy();
            NBTTagCompound newTag = stack.getOrCreateSubCompound(FluxUtils.FLUX_DATA);
            newTag.setInteger("energy", energyStored);
            return stack;
        }
    }*/
}
