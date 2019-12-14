package fluxnetworks.client.jei;

import fluxnetworks.FluxConfig;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class JEIIntegration implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        if(FluxConfig.enableFluxRecipe)
            FluxCraftingCategory.register(registry);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if(FluxConfig.enableFluxRecipe)
            registry.addRecipeCategories(new FluxCraftingCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
