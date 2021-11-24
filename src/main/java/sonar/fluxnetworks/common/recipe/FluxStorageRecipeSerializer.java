package sonar.fluxnetworks.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluxStorageRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FluxStorageRecipe> {

    public static final FluxStorageRecipeSerializer INSTANCE = new FluxStorageRecipeSerializer();

    private FluxStorageRecipeSerializer() {
    }

    @Nonnull
    @Override
    public FluxStorageRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        return new FluxStorageRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json));
    }

    @Nullable
    @Override
    public FluxStorageRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
        try {
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
            if (recipe != null) {
                return new FluxStorageRecipe(recipe);
            }
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error reading Flux Storage Recipe from Packet", e);
        }
        return null;
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull FluxStorageRecipe recipe) {
        try {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error writing Flux Storage Recipe to packet.", e);
        }
    }
}
