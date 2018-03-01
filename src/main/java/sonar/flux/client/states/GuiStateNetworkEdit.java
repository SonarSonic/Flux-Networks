package sonar.flux.client.states;

import java.awt.Color;

//import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SonarTextField;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.CustomColour;
import sonar.flux.api.AccessType;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiFlux;
import sonar.flux.client.GuiFluxBase;
import sonar.flux.client.GuiState;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;
import static net.minecraft.client.renderer.GlStateManager.*;

public class GuiStateNetworkEdit extends GuiState {

    public SonarTextField name, r, g, b;
    public int currentColour;
    public AccessType currentAccess = AccessType.PRIVATE;
    public boolean previewSelected = true, showFullPreview = true;

    public GuiStateNetworkEdit() {
        super(GuiTypeMessage.NETWORK_EDIT, 176, 166, 256, "network.edit");
    }

    @Override
    public void draw(GuiFlux flux, int x, int y) {

        if (flux.disabledState) {
            flux.renderNavigationPrompt("No network to edit", "Network Selection");
        } else {
            pushMatrix();
            if (GuiFluxBase.state == GuiState.NETWORK_CREATE)
                FontHelper.textCentre(GUI.CREATE_NETWORK.toString(), flux.getXSize(), 8, Color.GRAY.getRGB());
            else
                FontHelper.textCentre(GUI.EDIT_NETWORK.toString(), flux.getXSize(), 8, Color.GRAY.getRGB());
            FontHelper.text(GUI.NETWORK_NAME + ": ", 8, 24, 0);
            FontHelper.text("Colour" + ": ", 8, 80, 0);

            FontHelper.text(TextFormatting.RED + "R:", 46, 80, -1);
            FontHelper.text(TextFormatting.GREEN + "G:", 86, 80, -1);
            FontHelper.text(TextFormatting.BLUE + "B:", 126, 80, -1);

            CustomColour colour = getCurrentColour();
            Gui.drawRect(55, 63 + 32, 165, 68 + 32 + 4, colour.getRGB());

            FontHelper.text(GUI.ACCESS_SETTING + ": " + TextFormatting.AQUA + FontHelper.translate(currentAccess.getName()), 8, 40, 0);
            FontHelper.text(FontHelper.translate("Preview") + ": ", 8, 96, 0);
            String networkName = name.getText().isEmpty() ? "Network Name" : name.getText();
            if (showFullPreview) {
                flux.renderNetworkInFull(networkName, currentAccess, colour.getRGB(), previewSelected, 11, 110);
            } else {
                flux.renderNetwork(networkName, currentAccess, colour.getRGB(), previewSelected, 11, 110);
            }

            if (x - flux.getGuiLeft() > 55 && x - flux.getGuiLeft() < 165 && y - flux.getGuiTop() > 63 + 32 && y - flux.getGuiTop() < 68 + 32 + 4) {
                flux.drawCreativeTabHoveringText(GUI.NEXT_COLOUR.toString(), x - flux.getGuiLeft(), y - flux.getGuiTop());
            }
            if (x - flux.getGuiLeft() > 5 && x - flux.getGuiLeft() < 165 && y - flux.getGuiTop() > 38 && y - flux.getGuiTop() < 52) {
                flux.drawCreativeTabHoveringText(GUI.CHANGE_SETTING.toString(), x - flux.getGuiLeft(), y - flux.getGuiTop());
            }
            popMatrix();
        }
    }

    @Override
    public void init(GuiFlux flux) {

        if (!flux.common.isFakeNetwork()) {
            initEditFields(flux, flux.common.getNetworkName(), flux.common.getNetworkColour());
            flux.getButtonList().add(new GuiButton(5, flux.getGuiLeft() + 5, flux.getGuiTop() + 140, 80, 20, "Reset"));
            flux.getButtonList().add(new GuiButton(6, flux.getGuiLeft() + 90, flux.getGuiTop() + 140, 80, 20, "Save Changes"));
            currentAccess = flux.common.getAccessType();
        } else {
            flux.disabledState = true;
        }
    }

    @Override
    public void button(GuiFlux flux, GuiButton button) {
        switch (button.id) {
            case 5:
                resetCreateTab(flux);
                break;
            case 6:
                if (!name.getText().isEmpty()) {
                    if (GuiFluxBase.state == GuiState.NETWORK_CREATE) {
						PacketHelper.sendPacketToServer(PacketType.CREATE_NETWORK, flux.tile, PacketHelper.createNetworkCreationPacket(name.getText(), getCurrentColour(), currentAccess));
                    } else {
                    	PacketHelper.sendPacketToServer(PacketType.EDIT_NETWORK, flux.tile, PacketHelper.createNetworkEditPacket(flux.getNetworkID(), name.getText(), getCurrentColour(), currentAccess));
                    }
                    flux.switchState(GuiState.NETWORK_SELECT);
                    resetCreateTab(flux);
                    return;
                }
                break;
        }

    }

    @Override
    public void click(GuiFlux flux, int x, int y, int mouseButton) {
        if (mouseButton == 1) {
            name.setText("");
        }
        if (x - flux.getGuiLeft() > 55 && x - flux.getGuiLeft() < 165 && y - flux.getGuiTop() > 63 + 32 && y - flux.getGuiTop() < 68 + 32 + 4) {
            currentColour++;
            if (currentColour >= GuiFluxBase.colours.length) {
                currentColour = 0;
            }
            CustomColour colour = GuiFluxBase.colours[currentColour];
            r.setText(String.valueOf(colour.red));
            g.setText(String.valueOf(colour.green));
            b.setText(String.valueOf(colour.blue));
        }
        if (x - flux.getGuiLeft() > 5 && x - flux.getGuiLeft() < 165 && y - flux.getGuiTop() > 38 && y - flux.getGuiTop() < 52) {
            currentAccess = AccessType.values()[currentAccess.ordinal() + 1 < AccessType.values().length ? currentAccess.ordinal() + 1 : 0];
        }
        if (x - flux.getGuiLeft() > 11 && x - flux.getGuiLeft() < 165 && y - flux.getGuiTop() > 108 && y - flux.getGuiTop() < 134) {
            showFullPreview = !showFullPreview;
        }

    }

    public boolean type(GuiFlux flux, char c, int i) {
        if (i == 1) {
            flux.switchState(GuiState.INDEX);
            return false;
        }
        return true;
    }

    public void resetCreateTab(GuiFlux flux) {
        name.setText("");
        currentColour = 0;
        currentAccess = AccessType.PRIVATE;
        flux.reset();
    }

    public void initEditFields(GuiFlux flux, String networkName, CustomColour colour) {
        name = new SonarTextField(1, flux.getFontRenderer(), 38, 22, 130, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB());
        name.setMaxStringLength(24);
        name.setText(networkName);

        r = new SonarTextField(2, flux.getFontRenderer(), 56, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
        r.setMaxStringLength(3);
        r.setText(String.valueOf(colour.red));

        g = new SonarTextField(3, flux.getFontRenderer(), 96, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
        g.setMaxStringLength(3);
        g.setText(String.valueOf(colour.green));

        b = new SonarTextField(4, flux.getFontRenderer(), 136, 78, 28, 12).setBoxOutlineColour(Color.DARK_GRAY.getRGB()).setDigitsOnly(true);
        b.setMaxStringLength(3);
        b.setText(String.valueOf(colour.blue));
    }

    public CustomColour getCurrentColour() {
        return new CustomColour(r.getIntegerFromText(), g.getIntegerFromText(), b.getIntegerFromText());
    }

    @Override
    public SonarTextField[] getFields(GuiFlux flux) {
        return new SonarTextField[]{name, r, g, b};
    }

    @Override
    public int getSelectionSize(GuiFlux flux) {
        return 0;
    }

}