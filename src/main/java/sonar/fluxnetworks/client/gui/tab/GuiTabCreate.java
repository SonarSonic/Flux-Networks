package sonar.fluxnetworks.client.gui.tab;

import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.EnumSecurityType;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.GeneralPacket;
import sonar.fluxnetworks.common.network.GeneralPacketHandler;
import sonar.fluxnetworks.common.network.GeneralPacketEnum;
import net.minecraft.util.text.TextFormatting;

public class GuiTabCreate extends GuiTabEditAbstract {

    public NormalButton apply, create;

    public GuiTabCreate(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        securityType = EnumSecurityType.ENCRYPTED;
        energyType = EnergyType.FE;
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_CREATE;
    }

    @Override
    public void init() {
        super.init();
        name.setText(minecraft.player.getDisplayName().getFormattedText() + "'s Network");
        int i = 0;
        for(EnumNetworkColor color : EnumNetworkColor.values()) {
            colorButtons.add(new ColorButton(48 + (i >= 7 ? i - 7 : i) * 16, 96 + (i >= 7 ? 1 : 0) * 16, color.color));
            i++;
        }
        color = colorButtons.get(0);
        color.selected = true;

        buttons.add(create = new NormalButton(FluxTranslate.CREATE.t(), 70, 150, 36, 12, 3).setUnclickable());
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);

        screenUtils.renderNetwork(name.getText(), color.color, 20, 129);
        drawCenteredString(font, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 150, 0xffffff);
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if(button instanceof NormalButton){
            if (mouseButton == 0 && button.id == 3) {
                PacketHandler.INSTANCE.sendToServer(new GeneralPacket(GeneralPacketEnum.CREATE_NETWORK, GeneralPacketHandler.getCreateNetworkPacket(name.getText(), color.color, securityType, energyType, password.getText())));
            }
        }
    }

    @Override
    public void onEditSettingsChanged() {
        if(create != null) {
            create.clickable = (!securityType.isEncrypted() || password.getText().length() != 0) && name.getText().length() != 0;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(FluxNetworks.proxy.getFeedback(true) == EnumFeedbackInfo.SUCCESS) {
            switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector);
            FluxNetworks.proxy.setFeedback(EnumFeedbackInfo.NONE, true);
        }
    }

}
