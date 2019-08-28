package fluxnetworks.client.gui;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.GuiTextBox;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.common.core.ContainerCore;
import fluxnetworks.common.registry.RegistrySounds;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public abstract class GuiCore extends GuiContainer {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(FluxNetworks.MODID + ":textures/gui/gui_default_flux.png");
    public static final ResourceLocation GUI_BAR = new ResourceLocation(FluxNetworks.MODID + ":textures/gui/gui_bar.png");

    protected List<NormalButton> buttons = Lists.newArrayList();
    protected List<NavigationButton> navigationButtons = Lists.newArrayList();
    protected List<GuiTextBox> textBoxes = Lists.newArrayList();
    protected List<GuiTextBox> popBoxes = Lists.newArrayList();

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
        textBoxes.forEach(GuiTextBox::drawTextBox);
        for(NormalButton button : buttons) {
            button.drawButton(mc, mouseX, mouseY, guiLeft, guiTop);
        }
    }

    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        popBoxes.forEach(GuiTextBox::drawTextBox);
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
    }

    private void drawFluxDefaultBackground() {
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
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
        if(main) {
            mouseMainClicked(mouseX, mouseY, mouseButton);
        } else {
            mousePopupClicked(mouseX, mouseY, mouseButton);
        }

    }

    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton == 0) {
            for(NavigationButton button : navigationButtons) {
                if(button.isMouseHovered(mc, mouseX, mouseY)) {
                    button.switchTab(button.buttonNavigationId, player, tileEntity);
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(RegistrySounds.BUTTON_CLICK, 1.0F));
                }
            }
        }
        for(GuiTextBox text : textBoxes) {
            if(text.getVisible() == true && text.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton)) {
                text.setFocused(true);
            }
        }
    }

    protected void mousePopupClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(GuiTextBox text : popBoxes) {
            if(text.getVisible() == true && text.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton)) {
                text.setFocused(true);
            }
        }
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
        for(GuiTextBox text : textBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
                onTextBoxChanged(text);
            }
        }
    }

    protected void keyTypedPop(char c, int k) throws IOException {

    }

    public void onTextBoxChanged(GuiTextBox text) {

    }

    public void mouseScroll(int mouseX, int mouseY, int scroll) throws IOException {

    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {

        buttons.clear();
        navigationButtons.clear();
        textBoxes.clear();

        super.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

    }

}
