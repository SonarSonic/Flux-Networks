package sonar.fluxnetworks.common.recipe;

import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import javax.annotation.Nonnull;

/**
 * save Flux Storage energy when wiping NBT
 */
public class NBTWipeRecipe extends ShapelessRecipe {

    public NBTWipeRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
    }

    public NBTWipeRecipe(@Nonnull ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeOutput(), recipe.getIngredients());
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inventory) {
        ItemStack originalStack = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                originalStack = stack;
                break;
            }
        }
        if (originalStack != null) {
            ItemStack output = getRecipeOutput().copy();
            if (Block.getBlockFromItem(output.getItem()) instanceof FluxStorageBlock) {
                CompoundNBT fluxData = originalStack.getChildTag(FluxConstants.TAG_FLUX_DATA);
                long energy = 0;
                if (fluxData != null) {
                    energy = fluxData.getLong(FluxConstants.ENERGY);
                }
                if (energy != 0) {
                    CompoundNBT newTag = output.getOrCreateChildTag(FluxConstants.TAG_FLUX_DATA);
                    newTag.putLong(FluxConstants.ENERGY, energy);
                }
            }
            return output;
        }
        return super.getCraftingResult(inventory);
    }

    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World worldIn) {
        return super.matches(inv, worldIn);
    }

    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return NBTWipeRecipeSerializer.INSTANCE;
    }
}
