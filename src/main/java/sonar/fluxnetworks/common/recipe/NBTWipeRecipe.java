package sonar.fluxnetworks.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import javax.annotation.Nonnull;

/**
 * save Flux Storage energy when wiping NBT
 */
public class NBTWipeRecipe extends ShapelessRecipe {

    public NBTWipeRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn,
                         NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
    }

    public NBTWipeRecipe(@Nonnull ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients());
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer container) {
        ItemStack originalStack = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                originalStack = stack;
                break;
            }
        }
        if (originalStack != null) {
            ItemStack output = getResultItem().copy();
            if (Block.byItem(output.getItem()) instanceof FluxStorageBlock) {
                CompoundTag subTag = originalStack.getTagElement(FluxConstants.TAG_FLUX_DATA);
                long energy = 0;
                if (subTag != null) {
                    energy = subTag.getLong(FluxConstants.ENERGY);
                }
                if (energy != 0) {
                    CompoundTag newTag = output.getOrCreateTagElement(FluxConstants.TAG_FLUX_DATA);
                    newTag.putLong(FluxConstants.ENERGY, energy);
                }
            }
            return output;
        }
        return super.assemble(container);
    }

    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return NBTWipeRecipeSerializer.INSTANCE;
    }
}
