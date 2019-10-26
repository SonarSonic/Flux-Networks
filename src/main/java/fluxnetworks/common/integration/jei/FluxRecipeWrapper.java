package fluxnetworks.common.integration.jei;

import fluxnetworks.common.registry.RegistryRecipes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

public class FluxRecipeWrapper implements IRecipeWrapper {

    private RegistryRecipes.FluxRecipe recipe;

    public FluxRecipeWrapper(RegistryRecipes.FluxRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, recipe.getInput());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

}
