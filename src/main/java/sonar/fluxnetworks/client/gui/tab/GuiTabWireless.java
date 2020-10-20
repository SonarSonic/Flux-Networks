package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.gui.EnumChargingType;
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

import java.util.ArrayList;
import java.util.List;

public class GuiTabWireless extends GuiTabCore {

    public InvisibleButton redirectButton;

    public List<InventoryButton> inventoryButtonList = new ArrayList<>();
    public NormalButton apply;

    public boolean[] settings = new boolean[EnumChargingType.values().length];

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

            int setting = 0;
            for (EnumChargingType types : EnumChargingType.values()) {
                settings[types.ordinal()] = types.isActivated(setting);
            }

            switches.add(new SlidedSwitchButton(140, 156, 4, guiLeft, guiTop, settings[0]));
            inventoryButtonList.add(new InventoryButton(EnumChargingType.ARMOR_SLOT, this, 24, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingType.BAUBLES, this, 100, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingType.INVENTORY, this, 32, 56, 0, 0, 112, 40));
            inventoryButtonList.add(new InventoryButton(EnumChargingType.HOT_BAR, this, 32, 104, 112, 0, 112, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingType.RIGHT_HAND, this, 136, 128, 52, 80, 16, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingType.LEFT_HAND, this, 24, 128, 52, 80, 16, 16));

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
            //TODO new wireless system
            //int wireless = (settings[0] ? 1 : 0) | (settings[1] ? 1 : 0) << 1 | (settings[2] ? 1 : 0) << 2 | (settings[3] ? 1 : 0) << 3 | (settings[4] ? 1 : 0) << 4 | (settings[5] ? 1 : 0) << 5;
            //PacketHandler.CHANNEL.sendToServer(new GeneralPacket(GeneralPacketEnum.CHANGE_WIRELESS, GeneralPacketHandler.getChangeWirelessPacket(network.getNetworkID(), wireless)));
        }
        if (button instanceof SlidedSwitchButton) {
            ((SlidedSwitchButton) button).switchButton();
            if (button.id == 4) {
                switchSetting(EnumChargingType.ENABLE_WIRELESS);
            }
        }
    }

    public void switchSetting(EnumChargingType type) {
        settings[type.ordinal()] = !settings[type.ordinal()];
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
