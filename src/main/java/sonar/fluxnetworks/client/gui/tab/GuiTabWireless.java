package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.network.ChargingType;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InventoryButton;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.network.CEditMemberMessage;
import sonar.fluxnetworks.common.network.CEditWirelessMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;

import java.util.ArrayList;
import java.util.List;

public class GuiTabWireless extends GuiTabCore {

    public InvisibleButton redirectButton;

    public List<InventoryButton> inventoryButtonList = new ArrayList<>();
    public NormalButton apply;

    public int settings;

    public GuiTabWireless(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
    }

    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_WIRELESS;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (networkValid) {
            int colour = network.getNetworkColor();
            drawCenteredString(matrixStack, font, FluxTranslate.TAB_WIRELESS.t(), 88, 12, 0xb4b4b4);
            font.drawString(matrixStack, FluxTranslate.ENABLE_WIRELESS.t(), 20, 156, colour);
            drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getInfo(), 88, 146, 0xffffff);
        } else {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_WIRELESS, navigationTabs);
        inventoryButtonList.clear();
        buttonLists.add(inventoryButtonList);
        if (networkValid) {

            settings = network.getWirelessMode();

            switches.add(new SlidedSwitchButton(140, 156, 4, guiLeft, guiTop, ChargingType.ENABLE_WIRELESS.isActivated(settings)));
            inventoryButtonList.add(new InventoryButton(ChargingType.ARMOR, this, 24, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(ChargingType.CURIOS, this, 100, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(ChargingType.INVENTORY, this, 32, 56, 0, 0, 112, 40));
            inventoryButtonList.add(new InventoryButton(ChargingType.HOT_BAR, this, 32, 104, 112, 0, 112, 16));
            inventoryButtonList.add(new InventoryButton(ChargingType.MAIN_HAND, this, 136, 128, 52, 80, 16, 16));
            inventoryButtonList.add(new InventoryButton(ChargingType.OFF_HAND, this, 24, 128, 52, 80, 16, 16));

            apply = new NormalButton(FluxTranslate.APPLY.t(), 73, 130, 32, 12, 0).setUnclickable();
            buttons.add(apply);
        } else {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
            addButton(redirectButton);
        }
    }


    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton != 0) {
            return;
        }
        if (button instanceof InventoryButton) {
            switchSetting(((InventoryButton) button).chargeType);
        }
        if (button instanceof NormalButton && button.id == 0) {
            NetworkHandler.INSTANCE.sendToServer(new CEditWirelessMessage(network.getNetworkID(), settings));
        }
        if (button instanceof SlidedSwitchButton) {
            ((SlidedSwitchButton) button).switchButton();
            if (button.id == 4) {
                switchSetting(ChargingType.ENABLE_WIRELESS);
            }
        }
    }

    public void switchSetting(ChargingType type) {
        settings ^= 1 << type.ordinal();
        apply.clickable = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (apply != null && FluxClientCache.getFeedback(true) == FeedbackInfo.SUCCESS) {
            apply.clickable = false;
            FluxClientCache.setFeedback(FeedbackInfo.NONE, true);
        }
    }
}
