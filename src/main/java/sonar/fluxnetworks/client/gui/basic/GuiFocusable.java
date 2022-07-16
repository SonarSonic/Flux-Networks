package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.client.gui.button.FluxEditBox;
import sonar.fluxnetworks.common.connection.FluxMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Fixes the focus of popup host and dialog popup.
 */
public abstract class GuiFocusable extends AbstractContainerScreen<FluxMenu> {

    public static final int TEXTURE_SIZE = 512;

    public static final ResourceLocation BACKGROUND = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_background.png");
    public static final ResourceLocation FRAME = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_frame.png");
    public static final ResourceLocation ICON = new ResourceLocation(
            FluxNetworks.MODID, "textures/gui/gui_icon.png");

    public GuiFocusable(FluxMenu menu, @Nonnull Player player) {
        super(menu, player.getInventory(), TextComponent.EMPTY);
    }

    /**
     * Un-focus other text elements
     */
    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        super.setFocused(listener);
        for (GuiEventListener child : children()) {
            if (child != listener && child instanceof FluxEditBox editBox) {
                if (editBox.isFocused()) {
                    editBox.setFocused(false);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (getFocused() != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setFocused(null);
                return true;
            }
            if (getMinecraft().options.keyInventory.isActiveAndMatches(key)) {
                return false; // allows the typing of "E"
            }
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || getMinecraft().options.keyInventory.isActiveAndMatches(key)) {
            if (this instanceof GuiPopupCore core) {
                core.mHost.closePopup();
                return true;
            }
            if (this instanceof GuiTabCore core) {
                if (core.getNavigationTab() == EnumNavigationTab.TAB_HOME) {
                    onClose();
                } else {
                    core.switchTab(EnumNavigationTab.TAB_HOME, true);
                }
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (GuiEventListener child : children()) {
            if (child instanceof FluxEditBox editBox) {
                editBox.tick();
            }
        }
    }

    protected void blitBackground(PoseStack poseStack) {
        blitF(poseStack, width / 2f - 85, height / 2f - 80, 85, 85,
                4, 4, 340, 340, false, false);
        blitF(poseStack, width / 2f - 85, height / 2f + 5, 85, 85,
                4, 4, 340, 340, false, true);
        blitF(poseStack, width / 2f, height / 2f + 5, 85, 85,
                4, 4, 340, 340, true, true);
        blitF(poseStack, width / 2f, height / 2f - 80, 85, 85,
                4, 4, 340, 340, true, false);
    }

    protected void blitFrame(PoseStack poseStack) {
        blitF(poseStack, width / 2f - 86, height / 2f - 81, 86, 86,
                0, 0, 344, 344, false, false);
        blitF(poseStack, width / 2f - 86, height / 2f + 5, 86, 86,
                0, 0, 344, 344, false, true);
        blitF(poseStack, width / 2f, height / 2f + 5, 86, 86,
                0, 0, 344, 344, true, true);
        blitF(poseStack, width / 2f, height / 2f - 81, 86, 86,
                0, 0, 344, 344, true, false);
    }

    public void blitF(@Nonnull PoseStack poseStack, float x, float y, float width, float height,
                      float uOffset, float vOffset, float uWidth, float vHeight) {
        float minU = uOffset / TEXTURE_SIZE;
        float minV = vOffset / TEXTURE_SIZE;
        float maxU = (uOffset + uWidth) / TEXTURE_SIZE;
        float maxV = (vOffset + vHeight) / TEXTURE_SIZE;
        blitF(poseStack.last().pose(), x, y, getBlitOffset(), width, height, minU, minV, maxU, maxV);
    }

    public void blitF(@Nonnull PoseStack poseStack, float x, float y, float width, float height,
                      float uOffset, float vOffset, float uWidth, float vHeight,
                      boolean flipX, boolean flipY) {
        float minU = uOffset / TEXTURE_SIZE;
        float minV = vOffset / TEXTURE_SIZE;
        float maxU = (uOffset + uWidth) / TEXTURE_SIZE;
        float maxV = (vOffset + vHeight) / TEXTURE_SIZE;
        float t;
        if (flipX) {
            t = minU;
            minU = maxU;
            maxU = t;
        }
        if (flipY) {
            t = minV;
            minV = maxV;
            maxV = t;
        }
        blitF(poseStack.last().pose(), x, y, getBlitOffset(), width, height, minU, minV, maxU, maxV);
    }

    public static void blitF(Matrix4f matrix, float x, float y, float z, float width, float height,
                              float minU, float minV, float maxU, float maxV) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, x + width, y, z).uv(maxU, minV).endVertex();
        builder.vertex(matrix, x, y, z).uv(minU, minV).endVertex();
        builder.vertex(matrix, x, y + height, z).uv(minU, maxV).endVertex();
        builder.vertex(matrix, x + width, y + height, z).uv(maxU, maxV).endVertex();
        builder.end();
        BufferUploader.end(builder);
    }
}
