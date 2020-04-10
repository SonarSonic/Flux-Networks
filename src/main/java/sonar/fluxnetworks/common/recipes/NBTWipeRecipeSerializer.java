package sonar.fluxnetworks.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class NBTWipeRecipeSerializer extends ShapelessRecipe.Serializer {

    public static final NBTWipeRecipeSerializer INSTANCE = new NBTWipeRecipeSerializer();

    public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json) {
        ShapelessRecipe recipe = super.read(recipeId, json);
        return new NBTWipeRecipe(recipe);
    }

    public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapelessRecipe recipe = super.read(recipeId, buffer);
        return recipe != null ? new NBTWipeRecipe(recipe) : null;
    }
}
