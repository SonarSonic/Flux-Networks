package sonar.fluxnetworks.client.jei;

import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.registry.RegistryItems;
import sonar.fluxnetworks.common.registry.RegistryRecipes;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class FluxCraftingCategory implements IRecipeCategory<FluxRecipeWrapper> {

    public static final ResourceLocation TEXTURES = new ResourceLocation(FluxNetworks.MODID, "textures/gui/blockhead.png");

    public IDrawable background;

    public FluxCraftingCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(TEXTURES, 0, 0, 128, 54).build();
    }

    public static void register(IModRegistry registry) {
        registry.addRecipes(getRecipes(), "flux");
        registry.addRecipeCatalyst(new ItemStack(RegistryItems.FLUX), "flux");
    }

    public static List<FluxRecipeWrapper> getRecipes() {
        List<FluxRecipeWrapper> recipes = new ArrayList<>();

        recipes.add(new FluxRecipeWrapper(new RegistryRecipes.FluxRecipe(new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX))));

        return recipes;
    }

    @Override
    public String getUid() {
        return "flux";
    }

    @Override
    public String getTitle() {
        return I18n.format("item.fluxnetworks.flux.name");
    }

    @Override
    public String getModName() {
        return FluxNetworks.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, FluxRecipeWrapper fluxRecipeWrapper, IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();

        guiItemStacks.init(0, false, 20, 2);
        guiItemStacks.init(1, false, 85, 19);

        guiItemStacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
        guiItemStacks.set(1, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
