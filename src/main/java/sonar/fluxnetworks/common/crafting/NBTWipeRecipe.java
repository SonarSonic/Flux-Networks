package sonar.fluxnetworks.common.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.block.FluxStorageBlock;

import javax.annotation.Nonnull;

/**
 * save Flux Storage energy when wiping NBT
 */
public class NBTWipeRecipe extends ShapelessRecipe {

    public NBTWipeRecipe(ResourceLocation idIn, String groupIn, CraftingBookCategory category, ItemStack recipeOutputIn,
                         NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, groupIn, category, recipeOutputIn, recipeItemsIn);
    }

    public NBTWipeRecipe(@Nonnull ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getResultItem(RegistryAccess.EMPTY), recipe.getIngredients());
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingContainer container, @Nonnull RegistryAccess registryAccess) {
        ItemStack originalStack = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                originalStack = stack;
                break;
            }
        }
        if (originalStack != null) {
            ItemStack output = getResultItem(registryAccess).copy();
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
        return super.assemble(container, registryAccess);
    }

    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return NBTWipeRecipeSerializer.INSTANCE;
    }
}
