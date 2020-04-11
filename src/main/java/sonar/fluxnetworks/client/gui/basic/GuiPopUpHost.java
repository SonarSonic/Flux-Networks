package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.popups.PopUpCore;
import sonar.fluxnetworks.common.core.ContainerCore;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

public abstract class GuiPopUpHost extends GuiFocusable<ContainerCore> {

    public INetworkConnector connector;
    public PlayerEntity player;

    public GuiPopUpHost(PlayerEntity player, INetworkConnector connector) {
        super(new ContainerCore(0, player.inventory, connector), player.inventory, ((INamedContainerProvider)connector).getDisplayName());
        this.player = player;
        this.connector = connector;
    }

    public PopUpCore<GuiPopUpHost> currentPopUp;

    public final boolean hasActivePopup(){
        return currentPopUp != null;
    }

    //// OPEN POP UP \\\\

    public final void openPopUp(PopUpCore popUp){
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
    }

    public void onPopUpOpen(PopUpCore popUp){}


    //// CLOSE POP UP \\\\\

    public final void closePopUp(){
        if(currentPopUp != null) {
            onPopUpClose(currentPopUp);
            currentPopUp.closePopUp();
            currentPopUp = null;
        }
    }

    //USED FOR OBTAINING INFO FROM POP UPS
    public void onPopUpClose(PopUpCore popUp){}

    @Nullable
    public IGuiEventListener getPopUp(){
        return currentPopUp;
    }

    //// mouse moved \\\\

    @Override
    public void mouseMoved(double xPos, double yPos) {
        if(getPopUp() != null){
            mouseMovedPopUp(xPos, yPos);
            return;
        }
        if(mouseMovedMain(xPos, yPos)){
            return;
        }
        super.mouseMoved(xPos, yPos);
    }

    public void mouseMovedPopUp(double xPos, double yPos){
        getPopUp().mouseMoved(xPos, yPos);
    }

    public boolean mouseMovedMain(double xPos, double yPos){
        return false;
    }

    //// mouse click \\\\

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(getPopUp() != null){
            return mouseClickedPopUp(mouseX, mouseY, mouseButton);
        }
        return mouseClickedMain(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseClickedPopUp(double mouseX, double mouseY, int mouseButton){
        return getPopUp().mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton){
        return false;
    }

    //// mouse release \\\\

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if(getPopUp() != null){
            return mouseReleasedPopUp(mouseX, mouseY, mouseButton);
        }
        return mouseReleasedMain(mouseX, mouseY, mouseButton) || super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public boolean mouseReleasedPopUp(double mouseX, double mouseY, int mouseButton){
        return getPopUp().mouseReleased(mouseX, mouseY, mouseButton);
    }

    public boolean mouseReleasedMain(double mouseX, double mouseY, int mouseButton){
        return false;
    }

    //// mouse dragged \\\\

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double startX, double startY) {
        if(getPopUp() != null){
            return mouseDraggedPopUp(mouseX, mouseY, mouseButton, startX, mouseY);
        }
        return mouseDraggedMain(mouseX, mouseY, mouseButton, startX, mouseY) || super.mouseDragged(mouseX, mouseY, mouseButton, startX, mouseY);
    }

    public boolean mouseDraggedPopUp(double mouseX, double mouseY, int mouseButton, double startX, double startY){
        return getPopUp().mouseDragged(mouseX, mouseY, mouseButton, startX, startY);
    }

    public boolean mouseDraggedMain(double mouseX, double mouseY, int mouseButton, double startX, double startY){
        return false;
    }

    //// mouse scrolled \\\\

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if(getPopUp() != null){
            return mouseScrolledPopUp(mouseX, mouseY, scroll);
        }
        return mouseScrolledMain(mouseX, mouseY, scroll) || super.mouseScrolled(mouseX, mouseY, scroll);
    }

    public boolean mouseScrolledPopUp(double mouseX, double mouseY, double scroll) {
        return getPopUp().mouseScrolled(mouseX, mouseY, scroll);
    }

    public boolean mouseScrolledMain(double mouseX, double mouseY, double scroll) {
        return false;
    }

    //// key pressed \\\\

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(getPopUp() != null){
            return keyPressedPopUp(keyCode, scanCode, modifiers);
        }
        return keyPressedMain(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean keyPressedPopUp(int keyCode, int scanCode, int modifiers) {
        return getPopUp().keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean keyPressedMain(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //// key released \\\\

    @Override
    public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(getPopUp() != null){
            return keyReleasedPopUp(keyCode, scanCode, modifiers);
        }
        return keyReleasedMain(keyCode, scanCode, modifiers) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean keyReleasedPopUp(int keyCode, int scanCode, int modifiers) {
        return getPopUp().keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean keyReleasedMain(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //// char typed \\\\

    @Override
    public final boolean charTyped(char typedChar, int keyCode) {
        if(getPopUp() != null){
            return charTypedPopUp(typedChar, keyCode);
        }
        return charTypedMain(typedChar, keyCode) || super.charTyped(typedChar, keyCode);
    }

    public boolean charTypedPopUp(char typedChar, int keyCode) {
        return getPopUp().charTyped(typedChar, keyCode);
    }

    public boolean charTypedMain(char typedChar, int keyCode) {
        return false;
    }

    //// INIT \\\\

    @Override
    public void init(Minecraft mc, int width, int height) {
        super.init(mc, width, height);
        if(currentPopUp!=null) {
            currentPopUp.init(mc, width, height);
        }
    }

    /// RENDER METHODS \\\\\


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        if(currentPopUp != null){
            //currentPopUp.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void renderHoveredToolTip(int mouseX, int mouseY) {
        if(currentPopUp == null){
            super.renderHoveredToolTip(mouseX, mouseY);
        }

    }

    protected void drawForegroundLayer(int mouseX, int mouseY){}

    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY){}

    protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawForegroundLayer(mouseX, mouseY);

        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();

        if(currentPopUp != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.pushMatrix();
            RenderSystem.translated(-guiLeft, -guiTop, 0);
            currentPopUp.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            RenderSystem.popMatrix();

            currentPopUp.drawGuiContainerForegroundLayer(mouseX, mouseY);
            RenderSystem.enableDepthTest();
        }

    }

    protected final void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        renderBackground();
        drawFluxDefaultBackground();
        drawBackgroundLayer(partialTicks, mouseX, mouseY);
    }
}
