package sonar.fluxnetworks.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NBTWipeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<NBTWipeRecipe> {

    public static NBTWipeRecipeSerializer INSTANCE = new NBTWipeRecipeSerializer();

    @Nonnull
    @Override
    public NBTWipeRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        return new NBTWipeRecipe(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, json));
    }

    @Override
    public NBTWipeRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
        try {
            return new NBTWipeRecipe(Objects.requireNonNull(IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, buffer)));
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error reading NBT Wipe Recipe from Packet", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer, @Nonnull NBTWipeRecipe recipe) {
        try {
            IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe);
        } catch(Exception e){
            FluxNetworks.LOGGER.error("Error writing NBT Wipe Recipe to packet.", e);
            throw e;
        }
    }
}
