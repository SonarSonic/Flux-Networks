package sonar.fluxnetworks.client.gui.basic;

/*
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.client.gui.popup.PopupCore;
import sonar.fluxnetworks.common.blockentity.FluxContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GuiPopupHost extends GuiFocusable<FluxContainerMenu> {

    public GuiPopupHost(@Nonnull FluxContainerMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
    }

    public PopupCore<? extends GuiPopupHost> currentPopUp;

    public final boolean hasActivePopup() {
        return currentPopUp != null;
    }

    //// OPEN POP UP \\\\

    public final void openPopUp(PopupCore<? extends GuiPopupHost> popUp) {
        if (popUp == null) {
            return;
        }
        if (currentPopUp != null) {
            currentPopUp.closePopUp();
            currentPopUp = null;
        }
        currentPopUp = popUp;
        currentPopUp.openPopUp();
        onPopUpOpen(popUp);
    }

    public void onPopUpOpen(PopupCore<?> popUp) {

    }

    //// CLOSE POP UP \\\\\

    public final void closePopUp() {
        if (currentPopUp != null) {
            onPopUpClose(currentPopUp);
            currentPopUp.closePopUp();
            currentPopUp = null;
        }
    }

    // USED FOR OBTAINING INFO FROM POP UPS
    public void onPopUpClose(PopupCore<?> popUp) {

    }

    @Nullable
    public IGuiEventListener getPopUp() {
        return currentPopUp;
    }

    //// mouse moved \\\\

    @Override
    public final void mouseMoved(double xPos, double yPos) {
        if (getPopUp() != null) {
            getPopUp().mouseMoved(xPos, yPos);
            return;
        }
        if (mouseMovedMain(xPos, yPos)) {
            return;
        }
        super.mouseMoved(xPos, yPos);
    }

    public boolean mouseMovedMain(double xPos, double yPos) {
        return false;
    }

    //// mouse click \\\\

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (getPopUp() != null) {
            return getPopUp().mouseClicked(mouseX, mouseY, mouseButton);
        }
        return mouseClickedMain(mouseX, mouseY, mouseButton) || super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    //// mouse release \\\\

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (getPopUp() != null) {
            return getPopUp().mouseReleased(mouseX, mouseY, mouseButton);
        }
        return mouseReleasedMain(mouseX, mouseY, mouseButton) || super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public boolean mouseReleasedMain(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    //// mouse dragged \\\\

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double startX, double startY) {
        if (getPopUp() != null) {
            return getPopUp().mouseDragged(mouseX, mouseY, mouseButton, startX, startY);
        }
        return mouseDraggedMain(mouseX, mouseY, mouseButton, startX, mouseY) || super.mouseDragged(mouseX, mouseY, mouseButton, startX, mouseY);
    }

    public boolean mouseDraggedMain(double mouseX, double mouseY, int mouseButton, double startX, double startY) {
        return false;
    }

    //// mouse scrolled \\\\

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (getPopUp() != null) {
            return getPopUp().mouseScrolled(mouseX, mouseY, scroll);
        }
        return mouseScrolledMain(mouseX, mouseY, scroll) || super.mouseScrolled(mouseX, mouseY, scroll);
    }

    public boolean mouseScrolledMain(double mouseX, double mouseY, double scroll) {
        return false;
    }

    //// key pressed \\\\

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (getPopUp() != null) {
            return getPopUp().keyPressed(keyCode, scanCode, modifiers);
        }
        return keyPressedMain(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean keyPressedMain(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //// key released \\\\

    @Override
    public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (getPopUp() != null) {
            return getPopUp().keyReleased(keyCode, scanCode, modifiers);
        }
        return keyReleasedMain(keyCode, scanCode, modifiers) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean keyReleasedMain(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //// char typed \\\\

    @Override
    public final boolean charTyped(char typedChar, int keyCode) {
        if (getPopUp() != null) {
            return getPopUp().charTyped(typedChar, keyCode);
        }
        return charTypedMain(typedChar, keyCode) || super.charTyped(typedChar, keyCode);
    }

    public boolean charTypedMain(char typedChar, int keyCode) {
        return false;
    }

    //// INIT \\\\

    @Override
    public void init(@Nonnull Minecraft mc, int width, int height) {
        super.init(mc, width, height);
        if (currentPopUp != null) {
            currentPopUp.init(mc, width, height);
        }
    }

    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
    }

    protected void drawBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected final void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        drawForegroundLayer(matrixStack, mouseX, mouseY);

        float partialTicks = Minecraft.getInstance().getTickLength();

        if (currentPopUp != null) {
            RenderSystem.disableDepthTest();
            RenderSystem.pushMatrix();
            RenderSystem.translatef(-guiLeft, -guiTop, 400);
            currentPopUp.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
            RenderSystem.translatef(guiLeft, guiTop, 100);
            currentPopUp.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    protected final void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(matrixStack);
        drawBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
    }
}
*/
