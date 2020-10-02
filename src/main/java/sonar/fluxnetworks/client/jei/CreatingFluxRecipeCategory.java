package sonar.fluxnetworks.client.jei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.model.data.EmptyModelData;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreatingFluxRecipeCategory implements IRecipeCategory<CreatingFluxRecipeType> {

    public static final ResourceLocation CATEGORY_UUID = new ResourceLocation(FluxNetworks.MODID, "creatingflux");

    public static final ResourceLocation TEXTURES = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_creating_flux_recipe.png");

    public IGuiHelper guiHelper;
    public IDrawable  background;
    public ITickTimer timer;

    public CreatingFluxRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createDrawable(TEXTURES, 0, -20, 128, 80);
        this.timer = guiHelper.createTickTimer(60, 320, false);
    }

    public static List<CreatingFluxRecipeType> getRecipes() {
        List<CreatingFluxRecipeType> recipes = new ArrayList<>();
        recipes.add(new CreatingFluxRecipeType(Blocks.BEDROCK, Blocks.OBSIDIAN, new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX_DUST)));
        recipes.add(new CreatingFluxRecipeType(RegistryBlocks.FLUX_BLOCK, Blocks.OBSIDIAN, new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX_DUST)));
        return recipes;
    }

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
    public String getTitle() {
        return FluxTranslate.JEI_CREATING_FLUX.t();
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(CreatingFluxRecipeType recipe, IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, recipe.getInput());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull CreatingFluxRecipeType recipe, IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();

        guiItemStacks.init(0, false, 8, 24);
        guiItemStacks.init(1, false, 102, 24);

        guiItemStacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
        guiItemStacks.set(1, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Nonnull
    @Override
    public List<ITextComponent> getTooltipStrings(CreatingFluxRecipeType recipe, double mouseX, double mouseY) {
        if (mouseX > 40 && mouseX < 80 && mouseY < 64) {
            return Lists.newArrayList(
                    new StringTextComponent("Y+2 = ").append(recipe.getCrusher().getBlock().getTranslatedName()),
                    new StringTextComponent("Y+1 = ").append(recipe.getInput().getDisplayName()),
                    new StringTextComponent("Y+0 = ").append(recipe.getBase().getBlock().getTranslatedName())
            );
        }
        return Collections.emptyList();
    }

    @Override
    public void draw(CreatingFluxRecipeType recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

        //// OBSIDIAN
        matrixStack.push();
        int value = timer.getValue();
        double offset = (value > 160 ? 160 - (value - 160) : value) / 10F;
        matrixStack.translate(52, 10 + offset, 128);
        matrixStack.scale(16, 16, 16);
        matrixStack.rotate(new Quaternion(30, 45, 0, true));
        dispatcher.renderBlock(recipe.getCrusher().getDefaultState(), matrixStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        matrixStack.pop();

        //// BEDROCK
        matrixStack.push();
        matrixStack.translate(52, 40, 128 + -32);
        matrixStack.scale(16, 16, 16);
        matrixStack.rotate(new Quaternion(30, 45, 0, true));
        dispatcher.renderBlock(recipe.getBase().getDefaultState(), matrixStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        matrixStack.pop();

        //// ITEM
        matrixStack.push();
        matrixStack.translate(63, 36, 128 + -16);
        matrixStack.scale(16, -16, 16);
        ItemStack toDisplay = value > 160 ? recipe.getOutput() : recipe.getInput();
        matrixStack.rotate(new Quaternion(toDisplay.getItem() instanceof BlockItem ? 30 : 0, -90 + 180 * ((float) value / timer.getMaxValue()), 0, true));
        itemRenderer.renderItem(toDisplay, ItemCameraTransforms.TransformType.FIXED, 0xF000F0, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
        matrixStack.pop();

        buffer.finish();

        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        String help = FluxTranslate.JEI_LEFT_CLICK.format(recipe.getCrusher().getBlock().getTranslatedName().getString());
        fontRenderer.drawString(matrixStack, help, (float) (64 - fontRenderer.getStringWidth(help) / 2), 68, 4210752);

    }
}