package sonar.fluxnetworks.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

@JeiPlugin
public class JEIIntegration implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(FluxNetworks.MODID, "jei");
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        if (FluxConfig.enableFluxRecipe) {
            registration.addRecipes(CreatingFluxRecipeCategory.RECIPE_TYPE, CreatingFluxRecipeCategory.getRecipes());
        }
    }

    @Override
    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        if (FluxConfig.enableFluxRecipe) {
            CreatingFluxRecipeCategory.getCatalysts().forEach(itemStack -> registration.addRecipeCatalyst(itemStack,
                    CreatingFluxRecipeCategory.RECIPE_TYPE));
        }
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        if (FluxConfig.enableFluxRecipe) {
            registration.addRecipeCategories(new CreatingFluxRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        }
    }
}
