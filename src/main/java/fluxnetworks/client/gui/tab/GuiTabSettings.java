package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.*;
import fluxnetworks.client.gui.basic.GuiButtonCore;
import fluxnetworks.client.gui.button.ColorButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.network.PacketGeneralType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class GuiTabSettings extends GuiTabEditAbstract {

    public NormalButton apply, delete;
    public int deleteCount;

    public GuiTabSettings(EntityPlayer player, INetworkConnector connector) {
        super(player, connector);
        if(networkValid) {
            securityType = network.getSetting(NetworkSettings.NETWORK_SECURITY);
            energyType = network.getSetting(NetworkSettings.NETWORK_ENERGY);
        }
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_SETTING;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(getNavigationTab() == EnumNavigationTabs.TAB_CREATE || networkValid) {
            if(mouseX > 30 + guiLeft && mouseX < 66 + guiLeft && mouseY > 140 + guiTop && mouseY < 152 + guiTop) {
                if(delete.clickable) {
                    drawCenteredString(fontRenderer, TextFormatting.BOLD + FluxTranslate.DELETE_NETWORK.t(), 48, 128, 0xff0000);
                } else {
                    drawCenteredString(fontRenderer, FluxTranslate.DOUBLE_SHIFT.t(), 48, 128, 0xffffff);
                }
            }
            drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 156, 0xffffff);
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        if(networkValid) {
            name.setText(network.getNetworkName());

            password.setText(network.getSetting(NetworkSettings.NETWORK_PASSWORD));
            password.setVisible(network.getSetting(NetworkSettings.NETWORK_SECURITY).isEncrypted());

            buttons.add(apply = new NormalButton(FluxTranslate.APPLY.t(), 112, 140, 36, 12, 3).setUnclickable());
            buttons.add(delete = new NormalButton(FluxTranslate.DELETE.t(), 30, 140, 36, 12, 4).setUnclickable());

            int i = 0;
            boolean colorSet = false;
            for (NetworkColor color : NetworkColor.values()) {
                ColorButton b = new ColorButton(48 + ((i >= 7 ? i - 7 : i) * 16), 96 + ((i >= 7 ? 1 : 0) * 16), color.color);
                colorButtons.add(b);
                if(!colorSet && color.color == network.getSetting(NetworkSettings.NETWORK_COLOR)) {
                    this.color = b;
                    this.color.selected = true;
                    colorSet = true;
                }
                i++;
            }
            if(!colorSet) {
                ColorButton c = new ColorButton(width / 2 - 56, height / 2 + 29, network.getSetting(NetworkSettings.NETWORK_COLOR));
                colorButtons.add(c);
                this.color = c;
                this.color.selected = true;
            }
        }

    }


    @Override
    public void onEditSettingsChanged() {
        if(networkValid) {
            apply.clickable = ((!securityType.isEncrypted() || password.getText().length() != 0) && name.getText().length() != 0);
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (networkValid && button instanceof NormalButton) {
            switch (button.id){
                case 3:
                    PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.EDIT_NETWORK, PacketGeneralHandler.getNetworkEditPacket(network.getNetworkID(), name.getText(), color.color, securityType, energyType, password.getText())));
                    break;
                case 4:
                    PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.DELETE_NETWORK, PacketGeneralHandler.getDeleteNetworkPacket(connector.getNetworkID())));
                    break;
            }
        }
    }

    @Override
    protected void keyTypedMain(char c, int k) throws IOException {
        super.keyTypedMain(c, k);
        if(delete != null) {
            if (k == 42) {
                deleteCount++;
                if (deleteCount > 1) {
                    delete.clickable = true;
                }
            } else {
                deleteCount = 0;
                delete.clickable = false;
            }
        }
    }
    @Override
    public void updateScreen() {
        super.updateScreen();
        if(FluxNetworks.proxy.getFeedback(true) == FeedbackInfo.SUCCESS) {
            switchTab(EnumNavigationTabs.TAB_HOME, player, connector);
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE, true);
        }
        if(FluxNetworks.proxy.getFeedback(true) == FeedbackInfo.SUCCESS_2) {
            apply.clickable = false;
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE, true);
        }
    }
}
