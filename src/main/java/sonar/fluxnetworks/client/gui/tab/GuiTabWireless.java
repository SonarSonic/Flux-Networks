package sonar.fluxnetworks.client.gui.tab;

/*import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.network.WirelessType;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InventoryButton;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.blockentity.FluxContainerMenu;
import sonar.fluxnetworks.common.network.C2SNetMsg;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiTabWireless extends GuiTabCore {

    public InvisibleButton redirectButton;

    public List<InventoryButton> inventoryButtonList = new ArrayList<>();
    public NormalButton apply;

    public int wirelessMode;

    public GuiTabWireless(@Nonnull FluxContainerMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_WIRELESS;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (networkValid) {
            int color = network.getNetworkColor();
            drawCenterText(matrixStack, FluxTranslate.TAB_WIRELESS.t(), 88, 12, 0xb4b4b4);
            font.drawString(matrixStack, FluxTranslate.ENABLE_WIRELESS.t(), 20, 156, color);
            drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 88, 146, FluxClientCache.getFeedbackColor());
        } else {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_WIRELESS, navigationTabs);
        inventoryButtonList.clear();
        buttonLists.add(inventoryButtonList);
        if (networkValid) {

            wirelessMode = network.getWirelessMode();

            switches.add(new SlidedSwitchButton(140, 156, 4, guiLeft, guiTop, WirelessType.ENABLE_WIRELESS.isActivated(wirelessMode)));
            inventoryButtonList.add(new InventoryButton(WirelessType.ARMOR, this, 24, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(WirelessType.CURIOS, this, 100, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(WirelessType.INVENTORY, this, 32, 56, 0, 0, 112, 40));
            inventoryButtonList.add(new InventoryButton(WirelessType.HOT_BAR, this, 32, 104, 112, 0, 112, 16));
            inventoryButtonList.add(new InventoryButton(WirelessType.MAIN_HAND, this, 136, 128, 52, 80, 16, 16));
            inventoryButtonList.add(new InventoryButton(WirelessType.OFF_HAND, this, 24, 128, 52, 80, 16, 16));

            apply = new NormalButton(FluxTranslate.APPLY.t(), 73, 130, 32, 12, 0).setUnclickable();
            buttons.add(apply);
        } else {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
            addButton(redirectButton);
        }
    }


    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton != 0) {
            return;
        }
        if (button instanceof InventoryButton) {
            switchSetting(((InventoryButton) button).type);
        }
        if (button instanceof NormalButton && button.id == 0) {
            C2SNetMsg.editWireless(network.getNetworkID(), wirelessMode);
        }
        if (button instanceof SlidedSwitchButton) {
            ((SlidedSwitchButton) button).switchButton();
            if (button.id == 4) {
                switchSetting(WirelessType.ENABLE_WIRELESS);
            }
        }
    }

    public void switchSetting(WirelessType type) {
        if (type != WirelessType.INVENTORY) {
            wirelessMode ^= 1 << type.ordinal();
            apply.clickable = true;
        }
    }

    @Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (apply != null && info == FeedbackInfo.SUCCESS) {
            apply.clickable = false;
        }
    }
}*/
