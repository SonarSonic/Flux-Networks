package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.FluxConfig;
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

    public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            FluxNetworks.MODID, "textures/gui/gui_background.png");
    public static final ResourceLocation FRAME = ResourceLocation.fromNamespaceAndPath(
            FluxNetworks.MODID, "textures/gui/gui_frame.png");
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            FluxNetworks.MODID, "textures/gui/gui_icon.png");

    public GuiFocusable(FluxMenu menu, @Nonnull Player player) {
        super(menu, player.getInventory(), CommonComponents.EMPTY);
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
            if (keyCode == GLFW.GLFW_KEY_ESCAPE ||
                    keyCode == GLFW.GLFW_KEY_ENTER ||
                    keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                setFocused(null);
                return true;
            }
            if (getMinecraft().options.keyInventory.isActiveAndMatches(key)) {
                // consume the event if there's a focus
                return true;
            }
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE || getMinecraft().options.keyInventory.isActiveAndMatches(key)) {
            if (this instanceof GuiPopupCore<?> core) {
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
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        // consume the event if there's a focus
        return result || getFocused() != null;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
//        for (GuiEventListener child : children()) {
//            if (child instanceof FluxEditBox editBox) {
//                editBox.tick();
//            }
//        }
    }

    protected void blitBackgroundOrFrame(@Nonnull GuiGraphics gr) {
        float cx = width / 2f;
        float cy = height / 2f + 5;
        blitF(gr.pose().last().pose(), cx - 86, cy - 86, 0, 172, 172,
                0, 0, 1, 1);
    }

    public static void blitF(@Nonnull GuiGraphics gr, float x, float y, float width, float height,
                             float uOffset, float vOffset, float uWidth, float vHeight) {
        float minU = uOffset / TEXTURE_SIZE;
        float minV = vOffset / TEXTURE_SIZE;
        float maxU = (uOffset + uWidth) / TEXTURE_SIZE;
        float maxV = (vOffset + vHeight) / TEXTURE_SIZE;
        blitF(gr.pose().last().pose(), x, y, 0, width, height, minU, minV, maxU, maxV);
    }

    public static void blitF(Matrix4f matrix, float x, float y, float z, float width, float height,
                             float minU, float minV, float maxU, float maxV) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.addVertex(matrix, x + width, y, z).setUv(maxU, minV);
        builder.addVertex(matrix, x, y, z).setUv(minU, minV);
        builder.addVertex(matrix, x, y + height, z).setUv(minU, maxV);
        builder.addVertex(matrix, x + width, y + height, z).setUv(maxU, maxV);
        BufferUploader.drawWithShader(builder.buildOrThrow());
    }

    private static final boolean sHasExtendedGuiGraphics;

    static {
        boolean found = false;
        try {
            // this is added in 3.11.1
            Class.forName("icyllis.modernui.mc.ExtendedGuiGraphics", false, GuiFocusable.class.getClassLoader());
            found = true;
        } catch (ClassNotFoundException ignored) {
        } catch (Exception e) {
            FluxNetworks.LOGGER.error(e);
        }
        sHasExtendedGuiGraphics = found;
    }

    public static boolean useModernDesign() {
        return sHasExtendedGuiGraphics && FluxConfig.enableModernDesign;
    }
}
