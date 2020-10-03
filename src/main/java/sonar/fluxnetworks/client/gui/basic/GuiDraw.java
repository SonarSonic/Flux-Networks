package sonar.fluxnetworks.client.gui.basic;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.client.gui.ScreenUtils;


////ONLY RENDER METHODS & TEXTURES \\\\
public abstract class GuiDraw<T extends Container> extends ContainerScreen<T> {

    public ScreenUtils screenUtils = ScreenUtils.INSTANCE;

    public GuiDraw(T container, PlayerInventory inventory, ITextComponent name) {
        super(container, inventory, name);
    }

    public int getGuiColouring(){
        return FluxConstants.INVALID_NETWORK_COLOR;
    }

    protected void drawFluxDefaultBackground(MatrixStack matrixStack) {
        RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.pushMatrix();
        minecraft.getTextureManager().bindTexture(ScreenUtils.BACKGROUND);
        blit(matrixStack, width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);

        screenUtils.setGuiColouring(getGuiColouring());
        minecraft.getTextureManager().bindTexture(ScreenUtils.FRAME);
        blit(matrixStack, width / 2 - 128, height / 2 - 128, 0, 0, 256, 256);
        RenderSystem.popMatrix();
        RenderSystem.disableBlend();
        RenderSystem.disableAlphaTest();
    }

}
