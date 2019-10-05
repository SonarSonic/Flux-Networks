package fluxnetworks.client.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class JEIIntegration implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        FluxCraftingCategory.register(registry);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new FluxCraftingCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
