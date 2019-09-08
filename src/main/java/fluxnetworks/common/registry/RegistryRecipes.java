package fluxnetworks.common.registry;

import fluxnetworks.FluxNetworks;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RegistryRecipes {

    public static final ResourceLocation group = new ResourceLocation(FluxNetworks.MODID);

    public static void registerRecipes() {
        addShapedRecipe(new ItemStack(RegistryItems.FLUX_CORE, 4), "GCG", "CAC", "GCG", 'C', Blocks.OBSIDIAN, 'G', RegistryItems.FLUX, 'A', Items.ENDER_EYE);
        addShapedRecipe(new ItemStack(RegistryItems.FLUX_CONFIGURATOR, 1), " CP", " OC", "O  ", 'C', RegistryItems.FLUX, 'P', Items.ENDER_PEARL, 'O', Blocks.OBSIDIAN);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_BLOCK, 1), "ACA", "CAC", "ACA", 'A', RegistryItems.FLUX, 'C', RegistryItems.FLUX_CORE);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_CONTROLLER, 1), "BCB", "R R", "BBB", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK, 'R', RegistryItems.FLUX);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_POINT, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', Blocks.REDSTONE_BLOCK);
        addShapedRecipe(new ItemStack(RegistryBlocks.FLUX_PLUG, 1), " C ", "CBC", " C ", 'C', RegistryItems.FLUX_CORE, 'B', RegistryBlocks.FLUX_BLOCK);
    }

    public static void registerStorageRecipes(IForgeRegistry<IRecipe> registry){
        registry.register(new FluxStorageRecipe(group, new ItemStack(RegistryBlocks.FLUX_STORAGE_1, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_BLOCK, 'G', "paneGlassColorless"));
        registry.register(new FluxStorageRecipe(group, new ItemStack(RegistryBlocks.FLUX_STORAGE_2, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_STORAGE_1, 'G', "paneGlassColorless"));
        registry.register(new FluxStorageRecipe(group, new ItemStack(RegistryBlocks.FLUX_STORAGE_3, 1), "BBB", "G G", "BBB", 'B', RegistryBlocks.FLUX_STORAGE_2, 'G', "paneGlassColorless"));
    }

    public static void addShapedRecipe(@Nonnull ItemStack result, @Nonnull Object... input) {
        GameRegistry.addShapedRecipe(new ResourceLocation(FluxNetworks.MODID, result.getUnlocalizedName()), group, result, input);
    }

    public static class FluxStorageRecipe extends ShapedOreRecipe {

        public FluxStorageRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
            super(group, result, recipe);
            setRegistryName(new ResourceLocation(FluxNetworks.MODID, result.getUnlocalizedName()));
        }

        @Nonnull
        @Override
        public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafting) {
            int energyStored = 0;
            for (int i = 0; i < crafting.getSizeInventory(); i++) {
                ItemStack stack = crafting.getStackInSlot(i);
                NBTTagCompound flux_tag = stack.getSubCompound(FluxUtils.FLUX_DATA);
                if(flux_tag != null){
                    energyStored += flux_tag.getInteger("energy");
                }
            }
            if(energyStored > 0){
                ItemStack stack = output.copy();
                NBTTagCompound flux_tag = stack.getOrCreateSubCompound(FluxUtils.FLUX_DATA);
                flux_tag.setInteger("energy", energyStored);
                return stack;
            }
            return super.getCraftingResult(crafting);
        }
    }
}
