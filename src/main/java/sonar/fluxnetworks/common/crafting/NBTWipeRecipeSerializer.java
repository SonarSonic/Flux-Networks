package sonar.fluxnetworks.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NBTWipeRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<NBTWipeRecipe> {

    public static final NBTWipeRecipeSerializer INSTANCE = new NBTWipeRecipeSerializer();

    private NBTWipeRecipeSerializer() {
    }

    @Nonnull
    @Override
    public NBTWipeRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        return new NBTWipeRecipe(RecipeSerializer.SHAPELESS_RECIPE.fromJson(recipeId, json));
    }

    @Nullable
    @Override
    public NBTWipeRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
        try {
            ShapelessRecipe recipe = RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(recipeId, buffer);
            if (recipe != null) {
                return new NBTWipeRecipe(recipe);
            }
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error reading NBT Wipe Recipe from Packet", e);
        }
        return null;
    }

    @Override
    public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull NBTWipeRecipe recipe) {
        try {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe);
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error writing NBT Wipe Recipe to packet.", e);
        }
    }
}
