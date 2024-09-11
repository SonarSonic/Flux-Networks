package sonar.fluxnetworks.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.crafting.FluxStorageRecipe;
import sonar.fluxnetworks.common.crafting.FluxStorageRecipeSerializer;
import sonar.fluxnetworks.common.crafting.NBTWipeRecipe;
import sonar.fluxnetworks.common.crafting.NBTWipeRecipeSerializer;

import java.util.function.Supplier;

public class RegistryRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, FluxNetworks.MODID);

    public static final ResourceLocation FLUX_STORAGE_RECIPE_KEY = FluxNetworks.location("flux_storage_recipe");
    public static final ResourceLocation NBT_WIPE_RECIPE_KEY = FluxNetworks.location("nbt_wipe_recipe");

    public static final Supplier<RecipeSerializer<FluxStorageRecipe>> FLUX_STORAGE_RECIPE = RECIPES.register(
            "flux_storage_recipe", () -> FluxStorageRecipeSerializer.INSTANCE
    );
    public static final Supplier<RecipeSerializer<NBTWipeRecipe>> NBT_WIPE_RECIPE = RECIPES.register(
            "nbt_wipe_recipe", () -> NBTWipeRecipeSerializer.INSTANCE
    );

    private RegistryRecipes() {}
}
