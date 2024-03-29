package sonar.fluxnetworks.client.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.EmptyModelData;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.register.RegistryBlocks;
import sonar.fluxnetworks.register.RegistryItems;

import javax.annotation.Nonnull;
import java.util.*;

public class CreatingFluxRecipeCategory implements IRecipeCategory<CreatingFluxRecipe> {

    public static final ResourceLocation TEXTURES = new ResourceLocation(FluxNetworks.MODID,
            "textures/gui/gui_creating_flux_recipe.png");

    public static final RecipeType<CreatingFluxRecipe> RECIPE_TYPE =
            RecipeType.create(FluxNetworks.MODID, "creating_flux", CreatingFluxRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final ITickTimer timer;

    public CreatingFluxRecipeCategory(@Nonnull IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURES, 0, -20, 128, 80);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryItems.FLUX_DUST));
        this.timer = guiHelper.createTickTimer(60, 320, false);
    }

    @Nonnull
    public static List<CreatingFluxRecipe> getRecipes() {
        List<CreatingFluxRecipe> recipes = new ArrayList<>();
        recipes.add(new CreatingFluxRecipe(Blocks.BEDROCK, Blocks.OBSIDIAN,
                new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX_DUST)));
        recipes.add(new CreatingFluxRecipe(RegistryBlocks.FLUX_BLOCK, Blocks.OBSIDIAN,
                new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX_DUST)));
        return recipes;
    }

    @Nonnull
    public static List<ItemStack> getCatalysts() {
        return List.of(new ItemStack(RegistryItems.FLUX_DUST));
    }

    @Nonnull
    @Override
    public RecipeType<CreatingFluxRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @SuppressWarnings("removal")
    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return getRecipeType().getUid();
    }

    @SuppressWarnings("removal")
    @Nonnull
    @Override
    public Class<? extends CreatingFluxRecipe> getRecipeClass() {
        return getRecipeType().getRecipeClass();
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
                    new TextComponent("Y+2 = ").append(recipe.crusher().getName()),
                    new TextComponent("Y+1 = ").append(recipe.input().getHoverName()),
                    new TextComponent("Y+0 = ").append(recipe.base().getName())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public void draw(@Nonnull CreatingFluxRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView,
                     @Nonnull PoseStack poseStack, double mouseX, double mouseY) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();

        //// OBSIDIAN
        poseStack.pushPose();
        int value = timer.getValue();
        double offset = (value > 160 ? 160 - (value - 160) : value) / 10F;
        poseStack.translate(52, 10 + offset, 128);
        poseStack.scale(16, 16, 16);
        poseStack.mulPose(new Quaternion(30, 45, 0, true));
        dispatcher.renderSingleBlock(recipe.crusher().defaultBlockState(), poseStack, bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        poseStack.popPose();

        //// BEDROCK
        poseStack.pushPose();
        poseStack.translate(52, 40, 128 - 32);
        poseStack.scale(16, 16, 16);
        poseStack.mulPose(new Quaternion(30, 45, 0, true));
        dispatcher.renderSingleBlock(recipe.base().defaultBlockState(), poseStack, bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        poseStack.popPose();

        //// ITEM
        poseStack.pushPose();
        poseStack.translate(63, 36, 128 - 16);
        poseStack.scale(16, -16, 16);
        ItemStack toDisplay = value > 160 ? recipe.output() : recipe.input();
        poseStack.mulPose(new Quaternion(toDisplay.getItem() instanceof BlockItem ? 30 : 0,
                -90 + 180 * ((float) value / timer.getMaxValue()), 0, true));
        itemRenderer.renderStatic(toDisplay, ItemTransforms.TransformType.FIXED, LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource, 0);
        poseStack.popPose();

        bufferSource.endBatch();

        Font fontRenderer = Minecraft.getInstance().font;
        String help = FluxTranslate.JEI_LEFT_CLICK.format(recipe.crusher().getName().getString());
        fontRenderer.draw(poseStack, help, 64 - fontRenderer.width(help) / 2f, 68, 0xff404040);
    }
}
