package sonar.fluxnetworks.register;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.crafting.FluxStorageRecipe;
import sonar.fluxnetworks.common.crafting.FluxStorageRecipeSerializer;
import sonar.fluxnetworks.common.crafting.NBTWipeRecipe;
import sonar.fluxnetworks.common.crafting.NBTWipeRecipeSerializer;

public class RegistryRecipes {
    private static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FluxNetworks.MODID);

    public static final RegistryObject<RecipeSerializer<FluxStorageRecipe>> FLUX_STORAGE_RECIPE = registerRecipeSerializer("flux_storage_recipe", FluxStorageRecipeSerializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<NBTWipeRecipe>> NBT_WIPE_RECIPE = registerRecipeSerializer("nbt_wipe_recipe", NBTWipeRecipeSerializer.INSTANCE);

    private static <R extends Recipe<?>> RegistryObject<RecipeSerializer<R>> registerRecipeSerializer(String name, RecipeSerializer<R> recipeSerializer) {
        return REGISTRY.register(name, () -> recipeSerializer);
    }

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }

    private RegistryRecipes() {}
}
