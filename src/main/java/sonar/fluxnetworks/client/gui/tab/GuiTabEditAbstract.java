package sonar.fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.SecurityType;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.TextboxButton;
import sonar.fluxnetworks.client.gui.popups.GuiPopCore;
import sonar.fluxnetworks.client.gui.popups.GuiPopCustomColour;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.List;

/**the base class for settings and creation guis*/
public abstract class GuiTabEditAbstract extends GuiTabCore {

    protected List<ColorButton> colorButtons = Lists.newArrayList();

    protected SecurityType securityType;
    public EnergyType energyType;
    public ColorButton color;
    public TextboxButton name;
    public TextboxButton password;

    public GuiTabEditAbstract(EntityPlayer player, INetworkConnector connector) {
        super(player, connector);
    }

    public abstract void onEditSettingsChanged();


    @Override
    public void initGui() {
        super.initGui();
        colorButtons.clear();
        buttonLists.add(colorButtons);
        configureNavigationButtons(getNavigationTab(), navigationTabs);

        if(networkValid || getNavigationTab() == EnumNavigationTabs.TAB_CREATE){
            int l = fontRenderer.getStringWidth(FluxTranslate.NETWORK_NAME.t());
            name = TextboxButton.create(this, "", 1, fontRenderer, 20 + l, 28, 140 - l, 12);
            name.setMaxStringLength(24);


            l = fontRenderer.getStringWidth(FluxTranslate.NETWORK_PASSWORD.t());
            password = TextboxButton.create(this, "", 2, fontRenderer, 20 + l, 62, 140 - l, 12).setTextInvisible();
            password.setMaxStringLength(16);

            textBoxes.add(name);
            textBoxes.add(password);
        }

    }


    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if (getNavigationTab() == EnumNavigationTabs.TAB_CREATE || networkValid) {

            drawCenteredString(fontRenderer, getNavigationTab().getTranslatedName(), 88, 10, 0xb4b4b4);
            fontRenderer.drawString(FluxTranslate.NETWORK_NAME.t() + ":", 14, 30, 0x606060);
            fontRenderer.drawString(FluxTranslate.NETWORK_SECURITY.t() + ": " + TextFormatting.AQUA + securityType.getName(), 14, 50, 0x606060);
            if (securityType == SecurityType.ENCRYPTED)
                fontRenderer.drawString(FluxTranslate.NETWORK_PASSWORD.t() + ": ", 14, 64, 0x606060);
            fontRenderer.drawString(FluxTranslate.NETWORK_ENERGY.t() + ": " + TextFormatting.AQUA + energyType.getName(), 14, 78, 0x606060);
            fontRenderer.drawString(FluxTranslate.NETWORK_COLOR.t() + ":", 14, 97, 0x606060);
        }
    }

    @Override
    public void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        ////TODO replace with "text buttons
        if(mouseButton == 0) {
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 48 && mouseY < getGuiTop() + 60) {
                securityType = FluxUtils.incrementEnum(securityType, SecurityType.values());
                password.setText("");
                password.setVisible(!password.getVisible());
                onEditSettingsChanged();
            }
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() + 88) {
                energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
                onEditSettingsChanged();
            }
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if(button instanceof ColorButton){
            color.selected = false;
            color = (ColorButton)button;
            color.selected = true;
            onEditSettingsChanged();
            if(mouseButton == 1) {
                openPopUp(new GuiPopCustomColour(this, color.color, player, connector));
            }
        }
    }

    public void onPopUpClose(GuiPopCore popUp){
        super.onPopUpClose(popUp);
        if(popUp instanceof GuiPopCustomColour){
            this.color.color = ((GuiPopCustomColour) popUp).currentColour;
        }
    }

    @Override
    protected void keyTypedMain(char c, int k) throws IOException {
        super.keyTypedMain(c, k);
        onEditSettingsChanged();
    }
}
