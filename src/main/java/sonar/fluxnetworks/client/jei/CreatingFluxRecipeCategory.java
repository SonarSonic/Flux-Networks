package sonar.fluxnetworks.client.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.data.EmptyModelData;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.register.RegistryBlocks;
import sonar.fluxnetworks.register.RegistryItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatingFluxRecipeCategory implements IRecipeCategory<CreatingFluxRecipeType> {

    public static final ResourceLocation CATEGORY_UUID = new ResourceLocation(FluxNetworks.MODID, "creating_flux");

    public static final ResourceLocation TEXTURES = new ResourceLocation(FluxNetworks.MODID, "textures/gui" +
            "/gui_creating_flux_recipe.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final ITickTimer timer;

    public CreatingFluxRecipeCategory(@Nonnull IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURES, 0, -20, 128, 80);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(RegistryItems.FLUX_DUST));
        this.timer = guiHelper.createTickTimer(60, 320, false);
    }

    @Nonnull
    public static List<CreatingFluxRecipeType> getRecipes() {
        List<CreatingFluxRecipeType> recipes = new ArrayList<>();
        recipes.add(new CreatingFluxRecipeType(Blocks.BEDROCK, Blocks.OBSIDIAN, new ItemStack(Items.REDSTONE),
                new ItemStack(RegistryItems.FLUX_DUST)));
        recipes.add(new CreatingFluxRecipeType(RegistryBlocks.FLUX_BLOCK, Blocks.OBSIDIAN,
                new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX_DUST)));
        return recipes;
    }

    @Nonnull
    public static List<ItemStack> getCatalysts() {
        return Lists.newArrayList(new ItemStack(RegistryItems.FLUX_DUST));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return CATEGORY_UUID;
    }

    @Nonnull
    @Override
    public Class<? extends CreatingFluxRecipeType> getRecipeClass() {
        return CreatingFluxRecipeType.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return FluxTranslate.JEI_CREATING_FLUX.getTextComponent();
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
    public void setIngredients(@Nonnull CreatingFluxRecipeType recipe, @Nonnull IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, recipe.getInput());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout iRecipeLayout, @Nonnull CreatingFluxRecipeType recipe,
                          @Nonnull IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();

        guiItemStacks.init(0, false, 8, 24);
        guiItemStacks.init(1, false, 102, 24);

        guiItemStacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
        guiItemStacks.set(1, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Nonnull
    @Override
    public List<Component> getTooltipStrings(@Nonnull CreatingFluxRecipeType recipe, double mouseX, double mouseY) {
        if (mouseX > 40 && mouseX < 80 && mouseY < 64) {
            return Lists.newArrayList(
                    new TextComponent("Y+2 = ").append(recipe.getCrusher().getName()),
                    new TextComponent("Y+1 = ").append(recipe.getInput().getDisplayName()),
                    new TextComponent("Y+0 = ").append(recipe.getBase().getName())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public void draw(@Nonnull CreatingFluxRecipeType recipe, @Nonnull PoseStack poseStack, double mouseX,
                     double mouseY) {
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
        dispatcher.renderSingleBlock(recipe.getCrusher().defaultBlockState(), poseStack, bufferSource, 0xF000F0,
                OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        poseStack.popPose();

        //// BEDROCK
        poseStack.pushPose();
        poseStack.translate(52, 40, 128 + -32);
        poseStack.scale(16, 16, 16);
        poseStack.mulPose(new Quaternion(30, 45, 0, true));
        dispatcher.renderSingleBlock(recipe.getBase().defaultBlockState(), poseStack, bufferSource, 0xF000F0,
                OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        poseStack.popPose();

        //// ITEM
        poseStack.pushPose();
        poseStack.translate(63, 36, 128 + -16);
        poseStack.scale(16, -16, 16);
        ItemStack toDisplay = value > 160 ? recipe.getOutput() : recipe.getInput();
        poseStack.mulPose(new Quaternion(toDisplay.getItem() instanceof BlockItem ? 30 : 0,
                -90 + 180 * ((float) value / timer.getMaxValue()), 0, true));
        itemRenderer.renderStatic(toDisplay, ItemTransforms.TransformType.FIXED, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, 0);
        poseStack.popPose();

        bufferSource.endBatch();

        Font fontRenderer = Minecraft.getInstance().font;
        String help = FluxTranslate.JEI_LEFT_CLICK.format(recipe.getCrusher().getName().getString());
        fontRenderer.draw(poseStack, help, 64 - fontRenderer.width(help) / 2f, 68, 0xff404040);
    }
}