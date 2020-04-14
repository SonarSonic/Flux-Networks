package sonar.fluxnetworks.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sonar.fluxnetworks.FluxNetworks;

public class FluxStorageRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluxStorageRecipe>{

    public static final FluxStorageRecipeSerializer INSTANCE = new FluxStorageRecipeSerializer();

    public FluxStorageRecipe read(ResourceLocation recipeId, JsonObject json) {
        return new FluxStorageRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json));
    }

    public FluxStorageRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        try {
            return new FluxStorageRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer));
        }
        catch (Exception e) {
            FluxNetworks.LOGGER.error("Error reading Flux Storage Recipe from Packet", e);
            throw e;
        }
    }

    @Override
    public void write(PacketBuffer buffer, FluxStorageRecipe recipe) {
        try {
            IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe);
        }
        catch(Exception e){
            FluxNetworks.LOGGER.error("Error writing Flux Storage Recipe to packet.", e);
            throw e;
        }
    }
}
