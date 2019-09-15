package fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import fluxnetworks.FluxConfig;
import fluxnetworks.FluxNetworks;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.common.core.ContainerCore;
import fluxnetworks.common.registry.RegistrySounds;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GuiCore extends GuiContainer implements ITextBoxButton {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_background.png");
    public static final ResourceLocation FRAME = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_default_frame.png");
    public static final ResourceLocation GUI_BAR = new ResourceLocation(FluxNetworks.MODID, "textures/gui/gui_bar.png");
    public static final ResourceLocation INVENTORY = new ResourceLocation(FluxNetworks.MODID, "textures/gui/inventory_configuration.png");
    public static final ResourceLocation BUTTONS = new ResourceLocation(FluxNetworks.MODID , "textures/gui/gui_button.png");

    protected List<NormalButton> buttons = Lists.newArrayList();
    protected List<NavigationButton> navigationButtons = Lists.newArrayList();
    protected List<TextboxButton> textBoxes = Lists.newArrayList();
    protected List<TextboxButton> popBoxes = Lists.newArrayList();
    protected List<SlidedSwitchButton> switches = Lists.newArrayList();

    public TileFluxCore tileEntity;
    public EntityPlayer player;

    // for popup
    protected boolean main = true;

    public GuiCore(EntityPlayer player, TileFluxCore tileEntity) {
        super(new ContainerCore(player, tileEntity));
        this.player = player;
        this.tileEntity = tileEntity;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawForegroundLayer(mouseX, mouseY);
        if(!main) {
            drawGradientRect(0 - guiLeft, 0 - guiTop, this.width, this.height, 0xa0101010, 0xb0101010);
            drawPopupForegroundLayer(mouseX, mouseY);
        }
    }

    protected void drawForegroundLayer(int mouseX, int mouseY) {
        textBoxes.forEach(TextboxButton::drawTextBox);
        for(NormalButton button : buttons) {
            button.drawButton(mc, mouseX, mouseY, guiLeft, guiTop);
        }
        for(SlidedSwitchButton button : switches) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        popBoxes.forEach(TextboxButton::drawTextBox);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        /*GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(10, 70, 1);
        GL11.glVertex3d(60, 170, 1);
        GL11.glVertex3d(110, 90, 1);
        GL11.glVertex3d(160, 120, 1);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();*/

        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        drawFluxDefaultBackground();
        drawBackgroundLayer(partialTicks, mouseX, mouseY);

        GlStateManager.popMatrix();
    }

    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        for(NavigationButton button : navigationButtons) {
            button.drawButton(mc, mouseX, mouseY);
        }
        for(SlidedSwitchButton button : switches) {
            button.updatePosition(partialTicks * 4);
        }
    }

    protected void drawFluxDefaultBackground() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(TextboxButton text : textBoxes) {
            text.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
        }

        if(main) {
            mouseMainClicked(mouseX, mouseY, mouseButton);
        } else {
            mousePopupClicked(mouseX, mouseY, mouseButton);
        }

    }

    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

    }

    protected void mousePopupClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if(!main)
            return;

        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        int k = Integer.signum(Mouse.getEventDWheel());
        if(k != 0) {
            mouseScroll(i, j, k);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(main) {
            keyTypedMain(typedChar, keyCode);
        } else {
            keyTypedPop(typedChar, keyCode);
        }
    }

    protected void keyTypedMain(char c, int k) throws IOException {
        for(TextboxButton text : textBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
            }
        }
    }

    protected void keyTypedPop(char c, int k) throws IOException {

    }

    public void mouseScroll(int mouseX, int mouseY, int scroll) throws IOException {

    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {

        buttons.clear();
        switches.clear();
        navigationButtons.clear();
        textBoxes.clear();

        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public void triggerSoundEffect(SoundEvent soundEvent, float f){
        if(FluxConfig.enableGuiSoundEffects) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(soundEvent, f));
        }
    }

    public void drawColorRect(int x, int y, int height, int width, int color) {
        drawRect(x - 1, y - 1, x + width + 1, y, color);
        drawRect(x - 1, y + height, x + width + 1, y + height + 1, color);
        drawRect(x - 1, y, x, y + height, color);
        drawRect(x + width, y, x + width + 1, y + height, color);
    }

    public void drawRectWithBackground(int x, int y, int height, int width, int frameColor, int backColor) {
        drawRect(x - 1, y - 1, x + width + 1, y, frameColor);
        drawRect(x - 1, y + height, x + width + 1, y + height + 1, frameColor);
        //drawRect(x - 1, y, x, y + height, frameColor);
        //drawRect(x + width, y, x + width + 1, y + height, frameColor);
        drawRect(x, y, x + width, y + height, backColor);
    }

    protected void drawHoverTooltip(List<String> strings, int x, int y) {
        AtomicInteger maxLength = new AtomicInteger();
        strings.forEach(a -> maxLength.set(Math.max(fontRenderer.getStringWidth(a), maxLength.get())));
        drawRectWithBackground(x, y, strings.size() * 9 + 3, maxLength.get() + 4, 0x80ffffff, 0xc0000000);
        int i = 0;
        for(String s : strings) {
            fontRenderer.drawString(s, x + 2, y + 2 + 9 * i, 0xffffff);
            i++;
        }
    }

    protected void drawHoverTooltip(String text, int x, int y) {
        int maxLength = fontRenderer.getStringWidth(text);
        drawRect(x, y, x + maxLength + 4, y + 12, 0x80000000);
        fontRenderer.drawString(text, x + 2, y + 2, Color.GREEN.getRGB());
    }

}
