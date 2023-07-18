package sonar.fluxnetworks.client.gui.basic;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.common.connection.FluxMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class GuiPopupHost extends GuiFocusable {

    private GuiPopupCore<?> mCurrentPopup;

    protected GuiPopupHost(@Nonnull FluxMenu menu, @Nonnull Player player) {
        super(menu, player);
    }

    //// OPEN POP UP \\\\

    public final void openPopup(GuiPopupCore<?> popup) {
        if (popup == null || popup.mHost != this) {
            return;
        }
        closePopup();
        mCurrentPopup = popup;
        mCurrentPopup.init(getMinecraft(), width, height);
        onPopupOpen(mCurrentPopup);
    }

    protected void onPopupOpen(GuiPopupCore<?> popup) {
    }

    //// CLOSE POP UP \\\\\

    public final void closePopup() {
        if (mCurrentPopup != null) {
            onPopupClose(mCurrentPopup);
            mCurrentPopup.onClose();
            mCurrentPopup = null;
        }
    }

    // used for obtaining info from popups
    protected void onPopupClose(GuiPopupCore<?> popup) {
    }

    @Nullable
    public final GuiPopupCore<?> getCurrentPopup() {
        return mCurrentPopup;
    }

    //// mouse moved \\\\

    @Override
    public final void mouseMoved(double mouseX, double mouseY) {
        if (mCurrentPopup != null) {
            mCurrentPopup.mouseMoved(mouseX, mouseY);
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
        if (mCurrentPopup != null) {
            return mCurrentPopup.mouseClicked(mouseX, mouseY, mouseButton);
        }
        boolean result = onMouseClicked(mouseX, mouseY, mouseButton);
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
        return result;
    }

    protected boolean onMouseClicked(double mouseX, double mouseY, int mouseButton) {
        return false;
    }

    //// mouse release \\\\

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mCurrentPopup != null) {
            return mCurrentPopup.mouseReleased(mouseX, mouseY, mouseButton);
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
        if (mCurrentPopup != null) {
            return mCurrentPopup.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
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
        if (mCurrentPopup != null) {
            return mCurrentPopup.mouseScrolled(mouseX, mouseY, vScroll);
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
        if (mCurrentPopup != null) {
            return mCurrentPopup.keyPressed(keyCode, scanCode, modifiers);
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
        if (mCurrentPopup != null) {
            return mCurrentPopup.keyReleased(keyCode, scanCode, modifiers);
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
        if (mCurrentPopup != null) {
            return mCurrentPopup.charTyped(c, modifiers);
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
        if (mCurrentPopup != null) {
            mCurrentPopup.init(getMinecraft(), width, height);
        }
    }

    protected void drawForegroundLayer(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
    }

    protected void drawBackgroundLayer(GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
    }

    @Override
    public final void render(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        super.render(gr, mouseX, mouseY, deltaTicks);
        drawForegroundLayer(gr, mouseX, mouseY, deltaTicks);

        if (mCurrentPopup != null) {
            gr.pose().pushPose();
            gr.pose().translate(0, 0, 300);
            mCurrentPopup.drawBackgroundLayer(gr, mouseX, mouseY, deltaTicks);
            gr.pose().translate(0, 0, 10);
            mCurrentPopup.drawForegroundLayer(gr, mouseX, mouseY, deltaTicks);
            gr.pose().popPose();
        }
    }

    @Override
    protected final void renderLabels(@Nonnull GuiGraphics gr, int mouseX, int mouseY) {
    }

    @Override
    protected final void renderBg(@Nonnull GuiGraphics gr, float deltaTicks, int mouseX, int mouseY) {
        renderBackground(gr);
        drawBackgroundLayer(gr, mouseX, mouseY, deltaTicks);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (mCurrentPopup != null) {
            mCurrentPopup.containerTick();
        }
    }
}
