package sonar.fluxnetworks.common.recipes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

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
        int energyTotal = 0, networkID = -1;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            CompoundNBT subTag = stack.getChildTag(FluxUtils.FLUX_DATA);
            if (subTag != null) {
                if (networkID == -1) {
                    networkID = subTag.getInt(FluxNetworkData.NETWORK_ID);
                }
                energyTotal += subTag.getInt("energy");
            }
        }
        ItemStack stack = getRecipeOutput().copy();
        if (energyTotal > 0 || networkID != -1) {
            CompoundNBT subTag = stack.getOrCreateChildTag(FluxUtils.FLUX_DATA);
            if (networkID != -1)
                subTag.putInt(FluxNetworkData.NETWORK_ID, networkID);
            if (energyTotal > 0)
                subTag.putInt("energy", energyTotal);
        }
        return stack;
    }

    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return FluxStorageRecipeSerializer.INSTANCE;
    }
}
