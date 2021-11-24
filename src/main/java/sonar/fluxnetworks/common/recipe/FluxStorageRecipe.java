package sonar.fluxnetworks.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import sonar.fluxnetworks.api.misc.FluxConstants;

import javax.annotation.Nonnull;

public class FluxStorageRecipe extends ShapedRecipe {

    public FluxStorageRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
                             NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
    }

    public FluxStorageRecipe(@Nonnull ShapedRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(),
                recipe.getIngredients(), recipe.getResultItem());
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer container) {
        long totalEnergy = 0;
        int networkID = -1;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            CompoundTag subTag = stack.getTagElement(FluxConstants.TAG_FLUX_DATA);
            if (subTag != null) {
                if (networkID == -1) {
                    networkID = subTag.getInt(FluxConstants.NETWORK_ID);
                }
                totalEnergy += subTag.getLong(FluxConstants.ENERGY);
            }
        }
        ItemStack stack = getResultItem().copy();
        if (totalEnergy > 0 || networkID != -1) {
            CompoundTag subTag = stack.getOrCreateTagElement(FluxConstants.TAG_FLUX_DATA);
            if (networkID != -1)
                subTag.putInt(FluxConstants.NETWORK_ID, networkID);
            if (totalEnergy > 0)
                subTag.putLong(FluxConstants.ENERGY, totalEnergy);
        }
        return stack;
    }

    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return FluxStorageRecipeSerializer.INSTANCE;
    }
}
