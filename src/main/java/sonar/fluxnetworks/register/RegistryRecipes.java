package sonar.fluxnetworks.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.crafting.FluxStorageRecipe;
import sonar.fluxnetworks.common.crafting.FluxStorageRecipeSerializer;
import sonar.fluxnetworks.common.crafting.NBTWipeRecipe;
import sonar.fluxnetworks.common.crafting.NBTWipeRecipeSerializer;

public class RegistryRecipes {
    public static final ResourceLocation FLUX_STORAGE_RECIPE_KEY = FluxNetworks.location("flux_storage_recipe");
    public static final ResourceLocation NBT_WIPE_RECIPE_KEY = FluxNetworks.location("nbt_wipe_recipe");

    public static final RegistryObject<RecipeSerializer<FluxStorageRecipe>> FLUX_STORAGE_RECIPE = RegistryObject.create(FLUX_STORAGE_RECIPE_KEY, ForgeRegistries.RECIPE_SERIALIZERS);
    public static final RegistryObject<RecipeSerializer<NBTWipeRecipe>> NBT_WIPE_RECIPE = RegistryObject.create(NBT_WIPE_RECIPE_KEY, ForgeRegistries.RECIPE_SERIALIZERS);

    static void register(RegisterEvent.RegisterHelper<RecipeSerializer<?>> helper) {
        helper.register(FLUX_STORAGE_RECIPE_KEY, FluxStorageRecipeSerializer.INSTANCE);
        helper.register(NBT_WIPE_RECIPE_KEY, NBTWipeRecipeSerializer.INSTANCE);
    }

    private RegistryRecipes() {}
}
