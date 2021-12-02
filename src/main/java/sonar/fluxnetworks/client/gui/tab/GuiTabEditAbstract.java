package sonar.fluxnetworks.client.gui.tab;

/*import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.client.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.network.SecurityLevel;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.popup.PopupCore;
import sonar.fluxnetworks.client.gui.popup.PopupCustomColor;
import sonar.fluxnetworks.common.blockentity.FluxContainerMenu;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.List;

*//**
 * the base class for settings and creation guis
 *//*
public abstract class GuiTabEditAbstract extends GuiTabCore {

    public InvisibleButton redirectButton;

    protected List<ColorButton> colorButtons = Lists.newArrayList();

    protected SecurityLevel mSecurityLevel;
    public ColorButton colorBtn;
    public FluxTextWidget nameField;
    public FluxTextWidget passwordField;

    public GuiTabEditAbstract(@Nonnull FluxContainerMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
    }

    public abstract void onEditSettingsChanged();

    @Override
    public void init() {
        super.init();
        colorButtons.clear();
        buttonLists.add(colorButtons);
        configureNavigationButtons(getNavigationTab(), navigationTabs);

        if (networkValid || getNavigationTab() == EnumNavigationTab.TAB_CREATE) {
            int l = font.getStringWidth(FluxTranslate.NETWORK_NAME.t());
            nameField = FluxTextWidget.create("", font, guiLeft + 20 + l, guiTop + 28, 140 - l, 12);
            nameField.setMaxStringLength(24);
            nameField.setResponder(string -> onEditSettingsChanged());


            l = font.getStringWidth(FluxTranslate.NETWORK_PASSWORD.t());
            passwordField = FluxTextWidget.create("", font, guiLeft + 20 + l, guiTop + 63, 140 - l, 12).setTextInvisible();
            passwordField.setMaxStringLength(16);
            passwordField.setResponder(string -> onEditSettingsChanged());
            passwordField.setVisible(mSecurityLevel == SecurityLevel.ENCRYPTED);

            addButton(nameField);
            addButton(passwordField);
        } else if (!networkValid) {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
            addButton(redirectButton);
        }
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (getNavigationTab() == EnumNavigationTab.TAB_CREATE || networkValid) {

            drawCenterText(matrixStack, getNavigationTab().getTranslatedName(), 88, 10, 0xb4b4b4);
            font.drawString(matrixStack, FluxTranslate.NETWORK_NAME.t() + ":", 14, 30, 0x606060);
            font.drawString(matrixStack, FluxTranslate.NETWORK_SECURITY.t() + ": " + TextFormatting.AQUA + mSecurityLevel.getName(), 14, 50, 0x606060);
            if (mSecurityLevel == SecurityLevel.ENCRYPTED)
                font.drawString(matrixStack, FluxTranslate.NETWORK_PASSWORD.t() + ": ", 14, 65, 0x606060);
            //font.drawString(matrixStack, FluxTranslate.NETWORK_ENERGY.t() + ": " + TextFormatting.AQUA + energyType.getName(), 14, 78, 0x606060);
            font.drawString(matrixStack, FluxTranslate.NETWORK_COLOR.t() + ":", 14, 92, 0x606060);
        }
    }

    @Override
    public boolean mouseClickedMain(double mouseX, double mouseY, int mouseButton) {
        super.mouseClickedMain(mouseX, mouseY, mouseButton);
        ////TODO MINOR replace with "text buttons
        if (mouseButton == 0) {
            if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 48 && mouseY < getGuiTop() + 60) {
                mSecurityLevel = FluxUtils.incrementEnum(mSecurityLevel, SecurityLevel.values());
                passwordField.setText("");
                passwordField.setVisible(mSecurityLevel == SecurityLevel.ENCRYPTED);
                onEditSettingsChanged();
                return true;
            }
            *//*if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() + 88) {
                energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
                onEditSettingsChanged();
                return true;
            }*//*
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
                openPopUp(new PopupCustomColor(this, player, colorBtn.color));
            }
        }
    }

    public void onPopUpClose(PopupCore<?> popUp) {
        super.onPopUpClose(popUp);
        if (popUp instanceof PopupCustomColor) {
            this.colorBtn.color = ((PopupCustomColor) popUp).currentColor;
        }
    }
}*/
