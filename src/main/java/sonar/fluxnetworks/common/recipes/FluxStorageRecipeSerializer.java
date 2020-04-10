package sonar.fluxnetworks.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class FluxStorageRecipeSerializer extends ShapedRecipe.Serializer {

    public static final FluxStorageRecipeSerializer INSTANCE = new FluxStorageRecipeSerializer();

    public ShapedRecipe read(ResourceLocation recipeId, JsonObject json) {
        ShapedRecipe recipe = super.read(recipeId, json);
        return new FluxStorageRecipe(recipe);
    }

    public ShapedRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapedRecipe recipe = super.read(recipeId, buffer);
        return recipe != null ? new FluxStorageRecipe(recipe) : null;
    }

}
