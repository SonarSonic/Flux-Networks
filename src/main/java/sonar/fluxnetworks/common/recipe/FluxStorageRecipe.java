package sonar.fluxnetworks.common.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import sonar.fluxnetworks.api.misc.FluxConstants;

import javax.annotation.Nonnull;

public class FluxStorageRecipe extends ShapedRecipe {

    public FluxStorageRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
    }

    public FluxStorageRecipe(@Nonnull ShapedRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getRecipeOutput());
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull CraftingInventory inventory) {
        long totalEnergy = 0;
        int networkID = -1;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            CompoundNBT subTag = stack.getChildTag(FluxConstants.TAG_FLUX_DATA);
            if (subTag != null) {
                if (networkID == -1) {
                    networkID = subTag.getInt(FluxConstants.NETWORK_ID);
                }
                totalEnergy += subTag.getLong(FluxConstants.ENERGY);
            }
        }
        ItemStack stack = getRecipeOutput().copy();
        if (totalEnergy > 0 || networkID != -1) {
            CompoundNBT subTag = stack.getOrCreateChildTag(FluxConstants.TAG_FLUX_DATA);
            if (networkID != -1)
                subTag.putInt(FluxConstants.NETWORK_ID, networkID);
            if (totalEnergy > 0)
                subTag.putLong(FluxConstants.ENERGY, totalEnergy);
        }
        return stack;
    }

    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return FluxStorageRecipeSerializer.INSTANCE;
    }
}
