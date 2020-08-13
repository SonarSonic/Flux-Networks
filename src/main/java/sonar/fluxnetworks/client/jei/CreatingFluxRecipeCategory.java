package sonar.fluxnetworks.client.jei;

//TODO JEI
public class CreatingFluxRecipeCategory {/*implements IRecipeCategory<CreatingFluxRecipeType> {

    public static final ResourceLocation CATEGORY_UUID = new ResourceLocation(FluxNetworks.MODID, "creatingflux");
    public static final ResourceLocation TEXTURES = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_creating_flux_recipe.png");

    public IGuiHelper guiHelper;
    public IDrawable background;
    public ITickTimer timer;

    public CreatingFluxRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createDrawable(TEXTURES, 0, -20 , 128, 80);
        this.timer = guiHelper.createTickTimer(60, 320, false);
    }

    public static List<CreatingFluxRecipeType> getRecipes() {
        List<CreatingFluxRecipeType> recipes = new ArrayList<>();
        recipes.add(new CreatingFluxRecipeType(Blocks.BEDROCK, Blocks.OBSIDIAN, new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX)));
        recipes.add(new CreatingFluxRecipeType(RegistryBlocks.FLUX_BLOCK, Blocks.OBSIDIAN, new ItemStack(Items.REDSTONE), new ItemStack(RegistryItems.FLUX)));
        return recipes;
    }

    public static List<ItemStack> getCatalysts(){
        return Lists.newArrayList(new ItemStack(RegistryItems.FLUX));
    }

    @Override
    public ResourceLocation getUid() {
        return CATEGORY_UUID;
    }

    @Override
    public Class<? extends CreatingFluxRecipeType> getRecipeClass() {
        return CreatingFluxRecipeType.class;
    }

    @Override
    public String getTitle() {
        return FluxTranslate.JEI_CREATING_FLUX.t();
    }

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
    public void setRecipe(IRecipeLayout iRecipeLayout, CreatingFluxRecipeType recipe, IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();

        guiItemStacks.init(0, false, 8, 24);
        guiItemStacks.init(1, false, 102, 24);

        guiItemStacks.set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
        guiItemStacks.set(1, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public List<String> getTooltipStrings(CreatingFluxRecipeType recipe, double mouseX, double mouseY) {
        if(mouseX > 40 && mouseX < 80 && mouseY < 64) {
            return Lists.newArrayList(
                    "Y+2 = " + recipe.getCrusher().getBlock().getNameTextComponent().getFormattedText(),
                    "Y+1 = " + recipe.getInput().getDisplayName().getFormattedText(),
                    "Y+0 = " + recipe.getBase().getBlock().getNameTextComponent().getFormattedText()
            );
        }
        return Collections.emptyList();
    }

    @Override
    public void draw(CreatingFluxRecipeType recipe, double mouseX, double mouseY) {
        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        MatrixStack stack = new MatrixStack();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();

        //// OBSIDIAN
        stack.push();
        int value = timer.getValue();
        double offset = (value> 160 ? 160-(value-160) : value)/10F;
        stack.translate(52, 10 + offset, 128);
        stack.scale(16, 16, 16);
        stack.rotate(new Quaternion(30, 45, 0, true));
        dispatcher.renderBlock(recipe.getCrusher().getDefaultState(), stack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        stack.pop();

        //// BEDROCK
        stack.push();
        stack.translate(52, 40, 128+ -32);
        stack.scale(16, 16, 16);
        stack.rotate(new Quaternion(30, 45, 0, true));
        dispatcher.renderBlock(recipe.getBase().getDefaultState(), stack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        stack.pop();

        //// ITEM
        stack.push();
        stack.translate(63, 36, 128+ -16);
        stack.scale(16, -16, 16);
        ItemStack toDisplay = value > 160 ? recipe.getOutput() : recipe.getInput();
        stack.rotate(new Quaternion(toDisplay.getItem() instanceof BlockItem ? 30 : 0, -90 + 180 * ((float)value/ timer.getMaxValue()), 0, true));
        itemRenderer.renderItem(toDisplay, ItemCameraTransforms.TransformType.FIXED, 0xF000F0, OverlayTexture.NO_OVERLAY, stack, buffer);
        stack.pop();

        buffer.finish();

        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        String help = FluxTranslate.JEI_LEFT_CLICK.format(recipe.getCrusher().getBlock().getNameTextComponent().getFormattedText());
        fontRenderer.drawString(help, (float)(64 - fontRenderer.getStringWidth(help) / 2), 68, 4210752);

    }*/
}