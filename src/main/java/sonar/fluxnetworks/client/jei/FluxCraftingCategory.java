package sonar.fluxnetworks.client.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FluxCraftingCategory implements IRecipeCategory<FluxRecipeWrapper> {

    public static final ResourceLocation TEXTURES = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_creating_flux_recipe.png");

    private final IDrawable background;
    private final IDrawable icon;

    static ITickTimer timer;

    public FluxCraftingCategory(@Nonnull IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(TEXTURES, 0, -20, 128, 80);
        icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryItems.FLUX));
        timer = guiHelper.createTickTimer(60, 320, false);
    }

    public static void register(@Nonnull IModRegistry registry) {
        registry.addRecipes(getRecipes(), "flux");
        registry.addRecipeCatalyst(new ItemStack(RegistryItems.FLUX), "flux");
    }

    @Nonnull
    public static List<FluxRecipeWrapper> getRecipes() {
        List<FluxRecipeWrapper> recipes = new ArrayList<>();
        recipes.add(new FluxRecipeWrapper(new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX)));
        return recipes;
    }

    @Nonnull
    @Override
    public String getUid() {
        return "flux";
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("info.fluxnetworks.jei.creatingfluxrecipe");
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public String getModName() {
        return FluxNetworks.NAME;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull FluxRecipeWrapper fluxRecipeWrapper, @Nonnull IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();

        guiItemStacks.init(0, false, 8, 24);
        guiItemStacks.init(1, false, 102, 24);

        guiItemStacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
        guiItemStacks.set(1, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
