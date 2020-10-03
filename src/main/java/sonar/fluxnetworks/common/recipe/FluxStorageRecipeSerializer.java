package sonar.fluxnetworks.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FluxStorageRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluxStorageRecipe> {

    public static final FluxStorageRecipeSerializer INSTANCE = new FluxStorageRecipeSerializer();

    @Nonnull
    @Override
    public FluxStorageRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
        return new FluxStorageRecipe(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json));
    }

    @Override
    public FluxStorageRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
        try {
            return new FluxStorageRecipe(Objects.requireNonNull(IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer)));
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error reading Flux Storage Recipe from Packet", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketBuffer buffer, @Nonnull FluxStorageRecipe recipe) {
        try {
            IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe);
        } catch (Exception e) {
            FluxNetworks.LOGGER.error("Error writing Flux Storage Recipe to packet.", e);
            throw e;
        }
    }
}
