package sonar.fluxnetworks.client.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.register.RegistryBlocks;
import sonar.fluxnetworks.register.RegistryItems;

import javax.annotation.Nonnull;
import java.util.*;

public class CreatingFluxRecipeCategory implements IRecipeCategory<CreatingFluxRecipe> {

    public static final ResourceLocation TEXTURES = FluxNetworks.location(
            "textures/gui/gui_creating_flux_recipe.png");

    public static final RecipeType<CreatingFluxRecipe> RECIPE_TYPE =
            RecipeType.create(FluxNetworks.MODID, "creating_flux", CreatingFluxRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final ITickTimer timer;

    public CreatingFluxRecipeCategory(@Nonnull IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURES, 0, -20, 128, 80);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(RegistryItems.FLUX_DUST.get()));
        this.timer = guiHelper.createTickTimer(60, 320, false);
    }

    @Nonnull
    public static List<CreatingFluxRecipe> getRecipes() {
        List<CreatingFluxRecipe> recipes = new ArrayList<>();
        recipes.add(new CreatingFluxRecipe(Blocks.BEDROCK, Blocks.OBSIDIAN,
                new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX_DUST.get())));
        recipes.add(new CreatingFluxRecipe(RegistryBlocks.FLUX_BLOCK.get(), Blocks.OBSIDIAN,
                new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX_DUST.get())));
        return recipes;
    }

    @Nonnull
    public static List<ItemStack> getCatalysts() {
        return List.of(new ItemStack(RegistryItems.FLUX_DUST.get()));
    }

    @Nonnull
    @Override
    public RecipeType<CreatingFluxRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return FluxTranslate.JEI_CREATING_FLUX.getComponent();
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CreatingFluxRecipe recipe,
                          @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8 + 1, 24 + 1)
                .addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 102 + 1, 24 + 1)
                .addItemStack(recipe.output());
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(@Nonnull CreatingFluxRecipe recipe,
                                             @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 40 && mouseX < 80 && mouseY >= 10 && mouseY < 64) {
            return List.of(
                    Component.literal("Y+2 = ").append(recipe.crusher().getName()),
                    Component.literal("Y+1 = ").append(recipe.input().getHoverName()),
                    Component.literal("Y+0 = ").append(recipe.base().getName())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public void draw(@Nonnull CreatingFluxRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView,
                     @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        Quaternionf quat = new Quaternionf();
        quat.rotationXYZ(30 * Mth.DEG_TO_RAD, 45 * Mth.DEG_TO_RAD, 0);

        //// OBSIDIAN
        guiGraphics.pose().pushPose();
        int value = timer.getValue();
        double offset = (value > 160 ? 160 - (value - 160) : value) / 10F;
        guiGraphics.pose().translate(52, 10 + offset, 128);
        guiGraphics.pose().scale(16, 16, 16);
        guiGraphics.pose().mulPose(quat);
        dispatcher.renderSingleBlock(recipe.crusher().defaultBlockState(), guiGraphics.pose(), bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        guiGraphics.pose().popPose();

        //// BEDROCK
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(52, 40, 128 - 32);
        guiGraphics.pose().scale(16, 16, 16);
        guiGraphics.pose().mulPose(quat);
        dispatcher.renderSingleBlock(recipe.base().defaultBlockState(), guiGraphics.pose(), bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
        guiGraphics.pose().popPose();

        //// ITEM
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(63, 36, 128 - 16);
        guiGraphics.pose().scale(16, -16, 16);
        ItemStack toDisplay = value > 160 ? recipe.output() : recipe.input();
        guiGraphics.pose().mulPose(quat.rotationXYZ(toDisplay.getItem() instanceof BlockItem ? 30 * Mth.DEG_TO_RAD : 0,
                (-90 + 180 * ((float) value / timer.getMaxValue())) * Mth.DEG_TO_RAD, 0));
        itemRenderer.renderStatic(toDisplay, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, guiGraphics.pose(), bufferSource, null, 0);
        guiGraphics.pose().popPose();

        bufferSource.endBatch();

        Font fontRenderer = Minecraft.getInstance().font;
        String help = FluxTranslate.JEI_LEFT_CLICK.format(recipe.crusher().getName().getString());
        guiGraphics.drawString(fontRenderer, help, 64 - fontRenderer.width(help) / 2, 68, 0xff404040, false);
    }
}
