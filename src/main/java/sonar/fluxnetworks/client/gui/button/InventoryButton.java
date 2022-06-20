package sonar.fluxnetworks.client.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.tab.GuiTabWireless;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class InventoryButton extends GuiButtonCore {

    public static final ResourceLocation INVENTORY = new ResourceLocation(FluxNetworks.MODID,
            "textures/gui/inventory_configuration.png");

    public WirelessType mType;
    private final int mTexX;
    private final int mTexY;
    public GuiTabWireless mHost;
    private final String mText;

    public InventoryButton(Minecraft mc, int x, int y, int width, int height, @Nonnull WirelessType type,
                           int texX, int texY, GuiTabWireless host) {
        super(mc, x, y, width, height);
        this.mType = type;
        this.mTexX = texX;
        this.mTexY = texY;
        this.mHost = host;
        this.mText = type.getTranslatedName();
    }

    @Override
    protected void drawButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTicks) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int color = mHost.getNetwork().getNetworkColor();
        RenderSystem.setShaderColor(FluxUtils.getRed(color), FluxUtils.getGreen(color), FluxUtils.getBlue(color), 1.0f);
        RenderSystem.setShaderTexture(0, INVENTORY);
        boolean hovered = isMouseHovered(mouseX, mouseY);

        blit(poseStack, x, y, mTexX, mTexY + height * (mType.isActivated(mHost.mWirelessMode) ? 1 : 0), width, height);

        if (hovered) {
            mc.font.draw(poseStack, mText, x + (width - mc.font.width(mText)) / 2f + 1, y - 9, 0xFFFFFF);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
