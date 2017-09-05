package sonar.flux.client.states;

import net.minecraft.client.gui.GuiButton;
import sonar.core.SonarCore;
import sonar.core.client.gui.SonarTextField;
import sonar.flux.client.GuiFlux;
import sonar.flux.client.GuiState;
import sonar.flux.client.GuiTypeMessage;

public class GuiStateIndex extends GuiState {

    public SonarTextField fluxName;
    public SonarTextField priority;
    public SonarTextField limit;

    public GuiStateIndex() {
        super(GuiTypeMessage.INDEX, 176, 166, 0, "network.nav.home");
    }

    @Override
    public void draw(GuiFlux flux, int x, int y) {
        flux.drawScreen(flux.tile, flux.tile.getConnectionType());
    }

    @Override
    public void init(GuiFlux flux) {
        int networkColour = flux.common.getNetworkColour().getRGB();
        priority = new SonarTextField(0, flux.getFontRenderer(), 50, 46, 30, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        priority.setMaxStringLength(3);
        priority.setText(String.valueOf(flux.tile.getCurrentPriority()));

        limit = new SonarTextField(1, flux.getFontRenderer(), 110, 46, 58, 12).setBoxOutlineColour(networkColour).setDigitsOnly(true);
        limit.setMaxStringLength(8);
        limit.setText(String.valueOf(flux.tile.limit.getObject()));

        fluxName = new SonarTextField(1, flux.getFontRenderer(), 38, 28, 130, 12).setBoxOutlineColour(networkColour);
        fluxName.setMaxStringLength(24);
        fluxName.setText(flux.tile.getCustomName());
    }

    @Override
    public void button(GuiFlux flux, GuiButton button) {
        if (button.id == 0) {
            flux.switchState(GuiState.NETWORK_SELECT);
            flux.reset();
            return;
        }
        if (button.id == 5) {
            flux.switchState(GuiState.CONNECTIONS);
            SonarCore.sendPacketToServer(flux.tile, 4);
        }

    }

    @Override
    public void click(GuiFlux flux, int x, int y, int mouseButton) {
        if (x - flux.getGuiLeft() > 5 && x - flux.getGuiLeft() < 165 && y - flux.getGuiTop() > 66 && y - flux.getGuiTop() < 80) {
            flux.tile.disableLimit.invert();
            SonarCore.sendPacketToServer(flux.tile, -1);
        } else if (x - flux.getGuiLeft() > 5 && x - flux.getGuiLeft() < 165 && y - flux.getGuiTop() > 10 && y - flux.getGuiTop() < 20) {
            flux.switchState(GuiState.NETWORK_SELECT);
        }
    }

    public boolean type(GuiFlux flux, char c, int i) {
        return true;
    }

    public void textboxKeyTyped(GuiFlux flux, SonarTextField field, char c, int i) {
        if (field == priority) {
            flux.tile.priority.setObject(priority.getIntegerFromText());
            SonarCore.sendPacketToServer(flux.tile, 1);
        } else if (field == limit) {
            flux.tile.limit.setObject(limit.getLongFromText());
            SonarCore.sendPacketToServer(flux.tile, 2);
        } else if (field == fluxName) {
            flux.tile.customName.setObject(fluxName.getText());
            SonarCore.sendPacketToServer(flux.tile, 3);
        }
    }

    @Override
    public SonarTextField[] getFields(GuiFlux flux) {
        return new SonarTextField[]{priority, limit, fluxName};
    }

    @Override
    public int getSelectionSize(GuiFlux flux) {
        return 0;
    }

}