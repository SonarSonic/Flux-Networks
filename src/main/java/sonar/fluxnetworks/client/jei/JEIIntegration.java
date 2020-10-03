package sonar.fluxnetworks.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.util.ResourceLocation;
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
    public void registerRecipes(IRecipeRegistration registration) {
        if (FluxConfig.enableFluxRecipe) {
            registration.addRecipes(CreatingFluxRecipeCategory.getRecipes(), CreatingFluxRecipeCategory.CATEGORY_UUID);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (FluxConfig.enableFluxRecipe) {
            CreatingFluxRecipeCategory.getCatalysts().forEach(c -> registration.addRecipeCatalyst(c, CreatingFluxRecipeCategory.CATEGORY_UUID));
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (FluxConfig.enableFluxRecipe) {
            registration.addRecipeCategories(new CreatingFluxRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        }
    }
}