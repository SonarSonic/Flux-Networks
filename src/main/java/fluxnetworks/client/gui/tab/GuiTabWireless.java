package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.EnumChargingTypes;
import fluxnetworks.api.EnumNavigationTabs;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.client.gui.basic.GuiButtonCore;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.button.InventoryButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.network.PacketGeneralType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class GuiTabWireless extends GuiTabCore {

    public List<InventoryButton> inventoryButtonList = new ArrayList<>();
    public NormalButton apply;

    public boolean[] settings = new boolean[EnumChargingTypes.values().length];

    public GuiTabWireless(EntityPlayer player, INetworkConnector connector) {
        super(player, connector);
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_WIRELESS;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(networkValid) {
            int colour = network.getSetting(NetworkSettings.NETWORK_COLOR);
            drawCenteredString(fontRenderer, FluxTranslate.TAB_WIRELESS.t(), 88, 12, 0xb4b4b4);
            fontRenderer.drawString(FluxTranslate.ENABLE_WIRELESS.t(), 20, 156, colour);
            drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 146, 0xffffff);
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        configureNavigationButtons(EnumNavigationTabs.TAB_WIRELESS, navigationTabs);
        inventoryButtonList.clear();
        buttonLists.add(inventoryButtonList);
        if(networkValid) {

            int setting = network.getSetting(NetworkSettings.NETWORK_WIRELESS);
            for(EnumChargingTypes types : EnumChargingTypes.values()){
                settings[types.ordinal()] = types.isActivated(setting);
            }

            switches.add(new SlidedSwitchButton(140, 156, 4, guiLeft, guiTop, settings[0]));
            inventoryButtonList.add(new InventoryButton(EnumChargingTypes.ARMOR_SLOT, this, 24, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingTypes.BAUBLES, this,100, 32, 0, 80, 52, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingTypes.INVENTORY, this,32, 56, 0, 0, 112, 40));
            inventoryButtonList.add(new InventoryButton(EnumChargingTypes.HOT_BAR, this,32, 104, 112, 0, 112, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingTypes.RIGHT_HAND, this,136, 128, 52, 80, 16, 16));
            inventoryButtonList.add(new InventoryButton(EnumChargingTypes.LEFT_HAND, this,24, 128, 52, 80, 16, 16));

            apply = new NormalButton(FluxTranslate.APPLY.t(), 73, 130, 32, 12, 0).setUnclickable();
            buttons.add(apply);
        }
    }


    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if(mouseButton != 0) {
            return;
        }
        if(button instanceof InventoryButton){
            switchSetting(((InventoryButton)button).chargeType);
        }
        if(button instanceof NormalButton && button.id == 0){
            int wireless = (settings[0]?1:0) | (settings[1]?1:0) << 1 | (settings[2]?1:0) << 2 | (settings[3]?1:0) << 3 | (settings[4]?1:0) << 4 | (settings[5]?1:0) << 5;
            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_WIRELESS, PacketGeneralHandler.getChangeWirelessPacket(network.getNetworkID(), wireless)));
        }
        if(button instanceof SlidedSwitchButton){
            ((SlidedSwitchButton)button).switchButton();
            if(((SlidedSwitchButton)button).id == 4) {
                switchSetting(EnumChargingTypes.ENABLE_WIRELESS);
            }
        }
    }

    public void switchSetting(EnumChargingTypes type){
        settings[type.ordinal()] = !settings[type.ordinal()];
        apply.clickable = true;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(FluxNetworks.proxy.getFeedback(true) == FeedbackInfo.SUCCESS) {
            apply.clickable = false;
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE, true);
        }
    }
}
