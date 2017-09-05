package sonar.flux.client.states;

import net.minecraft.client.gui.GuiButton;
import sonar.core.client.gui.SonarTextField;
import sonar.flux.client.GuiFlux;
import sonar.flux.client.GuiFluxBase;
import sonar.flux.client.GuiState;
import sonar.flux.client.GuiTypeMessage;

public class GuiStateNetworkCreate extends GuiState {

    public GuiStateNetworkCreate() {
        super(GuiTypeMessage.NETWORK_CREATE, 176, 166, 320, "network.create");
    }

    @Override
    public void draw(GuiFlux flux, int x, int y) {
        GuiState.NETWORK_EDIT.draw(flux, x, y);
    }

    @Override
    public void init(GuiFlux flux) {
        GuiState.NETWORK_EDIT.initEditFields(flux, flux.player.getName() + "'s" + " Network", GuiFluxBase.colours[GuiState.NETWORK_EDIT.currentColour]);
        flux.getButtonList().add(new GuiButton(5, flux.getGuiLeft() + 5, flux.getGuiTop() + 140, 80, 20, "Reset"));
        flux.getButtonList().add(new GuiButton(6, flux.getGuiLeft() + 90, flux.getGuiTop() + 140, 80, 20, "Create"));
    }

    @Override
    public void button(GuiFlux flux, GuiButton button) {
        GuiState.NETWORK_EDIT.button(flux, button);
    }

    @Override
    public void click(GuiFlux flux, int x, int y, int mouseButton) {
        GuiState.NETWORK_EDIT.click(flux, x, y, mouseButton);
    }

    public boolean type(GuiFlux flux, char c, int i) {
        return GuiState.NETWORK_EDIT.type(flux, c, i);
    }

    @Override
    public SonarTextField[] getFields(GuiFlux flux) {
        return GuiState.NETWORK_EDIT.getFields(flux);
    }

    @Override
    public int getSelectionSize(GuiFlux flux) {
        return 0;
    }

}