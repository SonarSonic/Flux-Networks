package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GuiPopupHost extends GuiFocusable {

    private GuiPopupCore currentPopup;

    protected GuiPopupHost(@Nonnull FluxDeviceMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    //// OPEN POP UP \\\\

    public final void openPopup(GuiPopupCore popup) {
        if (popup == null || popup.mHost != this) {
            return;
        }
        closePopup();
        currentPopup = popup;
        currentPopup.init(getMinecraft(), width, height);
        onPopupOpen(currentPopup);
    }

    protected void onPopupOpen(GuiPopupCore popup) {
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
    protected void onPopupClose(GuiPopupCore popup) {
    }

    @Nullable
    public final GuiPopupCore getCurrentPopup() {
        return currentPopup;
    }

    //// mouse moved \\\\

    @Override
    public final void mouseMoved(double mouseX, double mouseY) {
        if (currentPopup != null) {
            currentPopup.mouseMoved(mouseX, mouseY);
            return;
        }
        if (onMouseMoved(mouseX, mouseY)) {
            return;
        }
        super.mouseMoved(mouseX, mouseY);
    }

    protected boolean onMouseMoved(double mouseX, double mouseY) {
        return false;
    }

    //// mouse click \\\\

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (currentPopup != null) {
            return currentPopup.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (onMouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        for (GuiEventListener child : children()) {
            if (child.mouseClicked(mouseX, mouseY, mouseButton)) {
                setFocused(child);
                if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                    setDragging(true);
                }
                return true;
            }
        }
        boolean focused = false;
        for (GuiEventListener child : children()) {
            if (child instanceof EditBox editBox && editBox.isFocused()) {
                focused = true;
                break;
            }
        }
        if (!focused) {
            setFocused(null);
            return true;
        }
        return false;
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
    public final boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        if (currentPopup != null) {
            return currentPopup.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
        }
        return onMouseDragged(mouseX, mouseY, mouseButton, deltaX, mouseY) ||
                super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, mouseY);
    }

    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
        return false;
    }

    //// mouse scrolled \\\\

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double vScroll) {
        if (currentPopup != null) {
            return currentPopup.mouseScrolled(mouseX, mouseY, vScroll);
        }
        return onMouseScrolled(mouseX, mouseY, vScroll) ||
                super.mouseScrolled(mouseX, mouseY, vScroll);
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double vScroll) {
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
    public final boolean charTyped(char c, int modifiers) {
        if (currentPopup != null) {
            return currentPopup.charTyped(c, modifiers);
        }
        return onCharTypes(c, modifiers) ||
                super.charTyped(c, modifiers);
    }

    public boolean onCharTypes(char c, int modifiers) {
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
