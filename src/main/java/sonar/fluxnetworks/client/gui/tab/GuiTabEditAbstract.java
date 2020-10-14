package sonar.fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.network.SecurityType;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.popups.PopUpCore;
import sonar.fluxnetworks.client.gui.popups.PopUpCustomColour;
import sonar.fluxnetworks.common.misc.FluxUtils;

import java.util.List;

/**
 * the base class for settings and creation guis
 */
public abstract class GuiTabEditAbstract extends GuiTabCore {

    public InvisibleButton redirectButton;

    protected List<ColorButton> colorButtons = Lists.newArrayList();

    protected SecurityType securityType;
    public EnergyType energyType;
    public ColorButton colorBtn;
    public FluxTextWidget nameField;
    public FluxTextWidget passwordField;

    public GuiTabEditAbstract(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
    }

    public abstract void onEditSettingsChanged();

    @Override
    public void init() {
        super.init();
        colorButtons.clear();
        buttonLists.add(colorButtons);
        configureNavigationButtons(getNavigationTab(), navigationTabs);

        if (networkValid || getNavigationTab() == EnumNavigationTabs.TAB_CREATE) {
            int l = font.getStringWidth(FluxTranslate.NETWORK_NAME.t());
            nameField = FluxTextWidget.create("", font, guiLeft + 20 + l, guiTop + 28, 140 - l, 12);
            nameField.setMaxStringLength(24);
            nameField.setResponder(string -> onEditSettingsChanged());


            l = font.getStringWidth(FluxTranslate.NETWORK_PASSWORD.t());
            passwordField = FluxTextWidget.create("", font, guiLeft + 20 + l, guiTop + 62, 140 - l, 12).setTextInvisible();
            passwordField.setMaxStringLength(16);
            passwordField.setResponder(string -> onEditSettingsChanged());

            addButton(nameField);
            addButton(passwordField);
        } else if (!networkValid) {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
            addButton(redirectButton);
        }
    }


    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (getNavigationTab() == EnumNavigationTabs.TAB_CREATE || networkValid) {

            drawCenteredString(matrixStack, font, getNavigationTab().getTranslatedName(), 88, 10, 0xb4b4b4);
            font.drawString(matrixStack, FluxTranslate.NETWORK_NAME.t() + ":", 14, 30, 0x606060);
            font.drawString(matrixStack, FluxTranslate.NETWORK_SECURITY.t() + ": " + TextFormatting.AQUA + securityType.getName(), 14, 50, 0x606060);
            if (securityType == SecurityType.ENCRYPTED)
                font.drawString(matrixStack, FluxTranslate.NETWORK_PASSWORD.t() + ": ", 14, 64, 0x606060);
            font.drawString(matrixStack, FluxTranslate.NETWORK_ENERGY.t() + ": " + TextFormatting.AQUA + energyType.getName(), 14, 78, 0x606060);
            font.drawString(matrixStack, FluxTranslate.NETWORK_COLOR.t() + ":", 14, 97, 0x606060);
        }
    }

    @Override
    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        super.mouseClickedMain(mouseX, mouseY, mouseButton);
        ////TODO MINOR replace with "text buttons
        if (mouseButton == 0) {
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 48 && mouseY < getGuiTop() + 60) {
                securityType = FluxUtils.incrementEnum(securityType, SecurityType.values());
                passwordField.setText("");
                passwordField.setVisible(!passwordField.getVisible());
                onEditSettingsChanged();
                return true;
            }
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() + 88) {
                energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
                onEditSettingsChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (button instanceof ColorButton) {
            colorBtn.selected = false;
            colorBtn = (ColorButton) button;
            colorBtn.selected = true;
            onEditSettingsChanged();
            if (mouseButton == 1) {
                openPopUp(new PopUpCustomColour(this, colorBtn.color, player, connector));
            }
        }
    }

    public void onPopUpClose(PopUpCore popUp) {
        super.onPopUpClose(popUp);
        if (popUp instanceof PopUpCustomColour) {
            this.colorBtn.color = ((PopUpCustomColour) popUp).currentColour;
        }
    }

}
