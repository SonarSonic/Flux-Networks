package sonar.flux.client.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.SonarCore;
import sonar.core.client.gui.SonarButtons;
import sonar.core.network.sync.SyncTagType;
import sonar.flux.FluxTranslate;
import sonar.flux.client.AbstractGuiTab;
import sonar.flux.client.GuiTab;
import sonar.flux.common.tileentity.TileController;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.client.renderer.GlStateManager.color;

public class GuiTabWirelessCharging extends AbstractGuiTab<TileController> {

    public static final ResourceLocation inventory_configuration = new ResourceLocation("fluxnetworks:textures/gui/inventory_configuration.png");

    public GuiTabWirelessCharging(TileController tile, List<GuiTab> tabs) {
        super(tile, tabs);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new InventoryConfigButton(this, flux -> flux.main_inventory, FluxTranslate.WIRELESS_CHARGE_INVENTORY.t(), 0, getGuiLeft() + 32, getGuiTop() + 48, 0, 0, 112, 40));
        buttonList.add(new InventoryConfigButton(this, flux -> flux.hot_bar, FluxTranslate.WIRELESS_CHARGE_HOTBAR.t(), 1, getGuiLeft() + 32, getGuiTop() + 98, 112, 0, 112, 16));
        buttonList.add(new InventoryConfigButton(this, flux -> flux.armour_slot, FluxTranslate.WIRELESS_CHARGE_ARMOUR.t(), 2, getGuiLeft() + 24, getGuiTop() + 24, 224, 0, 52, 16));
        buttonList.add(new InventoryConfigButton(this, flux -> flux.baubles_slot, FluxTranslate.WIRELESS_CHARGE_BAUBLES.t(), 3, getGuiLeft() + 100, getGuiTop() + 24, 224, 0, 52, 16));
        buttonList.add(new InventoryConfigButton(this, flux -> flux.left_hand, FluxTranslate.WIRELESS_CHARGE_LEFT_HAND.t(), 4, getGuiLeft() + 24, getGuiTop() + 128, 276, 0, 16, 16));
        buttonList.add(new InventoryConfigButton(this, flux -> flux.right_hand, FluxTranslate.WIRELESS_CHARGE_RIGHT_HAND.t(), 5, getGuiLeft() + 136, getGuiTop() + 128, 276, 0, 16, 16));
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button instanceof InventoryConfigButton){
            ((InventoryConfigButton) button).value.apply(flux).invert();
            SonarCore.sendPacketToServer(flux, 15);
        }
    }

    @SideOnly(Side.CLIENT)
    private class InventoryConfigButton extends SonarButtons.ImageButton {
        public int id;
        public GuiTabWirelessCharging gui;
        public Function<TileController, SyncTagType.BOOLEAN> value;
        public String hover;

        public InventoryConfigButton(GuiTabWirelessCharging gui, Function<TileController, SyncTagType.BOOLEAN> isActive, String hover, int id, int x, int y, int texX, int texY, int sizeX, int sizeY) {
            super(id, x, y, inventory_configuration, texX/2, texY/2, sizeX, sizeY);
            this.id = id;
            this.value = isActive;
            this.hover = hover;
            this.gui = gui;
        }

        public void drawButtonForegroundLayer(int x, int y) {
            if (!hover.isEmpty()) {
                gui.drawSonarCreativeTabHoveringText(hover + ": " + FluxTranslate.translateBoolean(value.apply(gui.flux).getObject()), x, y);
            }
        }

        public void drawButton(Minecraft mc, int x, int y, float partialTicks) {
            if (visible) {
                color(1.0F, 1.0F, 1.0F, 1.0F);
                hovered = x >= this.x && y >= this.y && x < this.x + width + 1 && y < this.y + height + 1;
                mc.getTextureManager().bindTexture(texture);
                GlStateManager.scale(2, 2, 2);
                drawTexturedModalRect(this.x/2, this.y/2, textureX, value.apply(gui.flux).getObject() ? textureY + sizeY/2 : textureY, sizeX/2, sizeY/2);
                GlStateManager.scale(0.5, 0.5, 0.5);
            }
        }
    }

    @Override
    public GuiTab getCurrentTab() {
        return GuiTab.WIRELESS_CHARGING;
    }

    @Override
    public ResourceLocation getBackground() {
        return blank_flux_gui;
    }

}
