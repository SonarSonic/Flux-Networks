package fluxnetworks.client.gui.basic;

import fluxnetworks.api.INetworkConnector;
import fluxnetworks.client.gui.popups.GuiPopCore;
import fluxnetworks.common.core.ContainerCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public abstract class GuiPopUpHost extends GuiDraw {


    public INetworkConnector connector;
    public EntityPlayer player;

    public GuiPopUpHost(EntityPlayer player, INetworkConnector connector) {
        super(new ContainerCore(player, connector));
        this.player = player;
        this.connector = connector;
    }

    protected void drawForegroundLayer(int mouseX, int mouseY){}

    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY){}

    protected abstract void drawFluxDefaultBackground();

    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {}

    protected void mouseScroll(int mouseX, int mouseY, int scroll) throws IOException {}

    protected void keyTypedMain(char typedChar, int keyCode) throws IOException {}

    @Override
    public final void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if(hasActivePopup())
            return;

        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        int k = Integer.signum(Mouse.getEventDWheel());
        if(k != 0) {
            mouseScroll(i, j, k);
        }
    }

    //// FLUX CONFIGURATOR / CONNECTOR \\\\


    //// POP UPs \\\\

    public GuiPopCore currentPopUp;

    public final boolean hasActivePopup(){
        return currentPopUp != null;
    }

    public final void openPopUp(GuiPopCore popUp){
        if(popUp == null){
            return;
        }
        if(currentPopUp != null){
            currentPopUp.closePopUp();
            currentPopUp = null;
        }
        currentPopUp = popUp;
        currentPopUp.openPopUp();
        onPopUpOpen(popUp);
        currentPopUp.initGui();
    }

    public void onPopUpOpen(GuiPopCore popUp){

    }

    public final void closePopUp(){
        if(currentPopUp != null) {
            onPopUpClose(currentPopUp);
            currentPopUp.closePopUp();
            currentPopUp = null;
        }
    }

    //USED FOR OBTAINING INFO FROM POP UPS
    public void onPopUpClose(GuiPopCore popUp){

    }

    public void initGui() {
        super.initGui();
        if(currentPopUp != null) {
            currentPopUp.initGui();
        }
    }

    protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawForegroundLayer(mouseX, mouseY);
        if(currentPopUp!=null) {
            currentPopUp.drawGuiContainerForegroundLayer(mouseX, mouseY);
        }
    }

    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        drawDefaultBackground();
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        drawFluxDefaultBackground();
        drawBackgroundLayer(partialTicks, mouseX, mouseY);

        GlStateManager.popMatrix();


        drawBackgroundLayer(partialTicks, mouseX, mouseY);

        if(currentPopUp != null) {
            currentPopUp.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        }
    }

    protected final void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(currentPopUp != null) {
            currentPopUp.mouseClicked(mouseX, mouseY, mouseButton);
        }else{
            mouseMainClicked(mouseX, mouseY, mouseButton);
        }
    }

    protected final void keyTyped(char typedChar, int keyCode) throws IOException {
        if(currentPopUp != null) {
            currentPopUp.keyTyped(typedChar, keyCode);
        }else{
            keyTypedMain(typedChar, keyCode);
        }
    }
    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        if(currentPopUp!=null) {
            currentPopUp.setWorldAndResolution(mc, width, height);
        }
    }
}
