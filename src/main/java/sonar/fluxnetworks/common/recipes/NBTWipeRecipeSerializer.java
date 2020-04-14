package sonar.fluxnetworks.common.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sonar.fluxnetworks.FluxNetworks;

public class NBTWipeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<NBTWipeRecipe> {

    public static NBTWipeRecipeSerializer INSTANCE = new NBTWipeRecipeSerializer();

    @Override
    public NBTWipeRecipe read(ResourceLocation recipeId, JsonObject json) {
        return new NBTWipeRecipe(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, json));
    }

    @Override
    public NBTWipeRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        try {
            return new NBTWipeRecipe(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, buffer));
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error reading NBT Wipe Recipe from Packet", e);
            throw e;
        }
    }

    @Override
    public void write(PacketBuffer buffer, NBTWipeRecipe recipe) {
        try {
            IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe);
        } catch(Exception e){
            FluxNetworks.LOGGER.error("Error writing NBT Wipe Recipe to packet.", e);
            throw e;
        }
    }
}
