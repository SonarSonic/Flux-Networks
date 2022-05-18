package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.common.device.FluxDeviceMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GuiPopupHost extends GuiFocusable {

    private GuiPopup currentPopup;

    protected GuiPopupHost(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    //// OPEN POP UP \\\\

    public final void openPopup(GuiPopup popup) {
        if (popup == null || popup.mHost != this) {
            return;
        }
        closePopup();
        currentPopup = popup;
        currentPopup.init(getMinecraft(), width, height);
        onPopupOpen(currentPopup);
    }

    protected void onPopupOpen(GuiPopup popup) {
    }

    //// CLOSE POP UP \\\\\

    public final void closePopup() {
        if (currentPopup != null) {
            onPopupClose(currentPopup);
            currentPopup.onClose();
            currentPopup = null;
        }
    }

    // used for obtaining info from popups
    protected void onPopupClose(GuiPopup popup) {
    }

    @Nullable
    public final GuiPopup getCurrentPopup() {
        return currentPopup;
    }

    //// mouse moved \\\\

    @Override
    public final void mouseMoved(double xPos, double yPos) {
        if (currentPopup != null) {
            currentPopup.mouseMoved(xPos, yPos);
            return;
        }
        if (onMouseMoved(xPos, yPos)) {
            return;
        }
        super.mouseMoved(xPos, yPos);
    }

    protected boolean onMouseMoved(double xPos, double yPos) {
        return false;
    }

    //// mouse click \\\\

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (currentPopup != null) {
            return currentPopup.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return onMouseClicked(mouseX, mouseY, mouseButton) ||
                super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    //// mouse release \\\\

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (currentPopup != null) {
            return currentPopup.mouseReleased(mouseX, mouseY, mouseButton);
        }
        return onMouseReleased(mouseX, mouseY, mouseButton) ||
                super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    //// mouse dragged \\\\

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double startX, double startY) {
        if (currentPopup != null) {
            return currentPopup.mouseDragged(mouseX, mouseY, mouseButton, startX, startY);
        }
        return onMouseDragged(mouseX, mouseY, mouseButton, startX, mouseY) ||
                super.mouseDragged(mouseX, mouseY, mouseButton, startX, mouseY);
    }

    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double startX, double startY) {
        return false;
    }

    //// mouse scrolled \\\\

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (currentPopup != null) {
            return currentPopup.mouseScrolled(mouseX, mouseY, scroll);
        }
        return onMouseScrolled(mouseX, mouseY, scroll) ||
                super.mouseScrolled(mouseX, mouseY, scroll);
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double scroll) {
        return false;
    }

    //// key pressed \\\\

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (currentPopup != null) {
            return currentPopup.keyPressed(keyCode, scanCode, modifiers);
        }
        return onKeyPressed(keyCode, scanCode, modifiers) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //// key released \\\\

    @Override
    public final boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (currentPopup != null) {
            return currentPopup.keyReleased(keyCode, scanCode, modifiers);
        }
        return onKeyReleased(keyCode, scanCode, modifiers) ||
                super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    //// char typed \\\\

    @Override
    public final boolean charTyped(char typedChar, int keyCode) {
        if (currentPopup != null) {
            return currentPopup.charTyped(typedChar, keyCode);
        }
        return onCharTypes(typedChar, keyCode) ||
                super.charTyped(typedChar, keyCode);
    }

    public boolean onCharTypes(char typedChar, int keyCode) {
        return false;
    }

    //// INIT \\\\

    @Override
    public void init() {
        super.init();
        if (currentPopup != null) {
            currentPopup.init(getMinecraft(), width, height);
        }
    }

    protected void drawForegroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
    }

    protected void drawBackgroundLayer(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
    }

    @Override
    public final void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        super.render(poseStack, mouseX, mouseY, deltaTicks);
        drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);

        if (currentPopup != null) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 450);
            currentPopup.drawBackgroundLayer(poseStack, mouseX, mouseY, deltaTicks);
            poseStack.translate(0, 0, 100);
            currentPopup.drawForegroundLayer(poseStack, mouseX, mouseY, deltaTicks);
            poseStack.popPose();
        }
    }

    @Override
    protected final void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
    }

    @Override
    protected final void renderBg(@Nonnull PoseStack poseStack, float deltaTicks, int mouseX, int mouseY) {
        renderBackground(poseStack);
        drawBackgroundLayer(poseStack, mouseX, mouseY, deltaTicks);
    }
}
