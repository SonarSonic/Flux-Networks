package sonar.flux.client.states;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.ClientFlux;
import sonar.flux.client.GUI;
import sonar.flux.client.GuiFlux;
import sonar.flux.client.GuiFluxBase;
import sonar.flux.client.GuiFluxBase.NetworkButton;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.network.PacketFluxButton;
import sonar.flux.network.PacketFluxButton.Type;

import java.util.ArrayList;

public class GuiStateNetworkConnections extends GuiStateScrollable {

    public SonarScroller scroller;
    public ClientFlux selected;
    public boolean toRemove;
    public static int listSize = 10;

    public GuiStateNetworkConnections() {
        super(GuiTypeMessage.CONNECTIONS, 176, 166, 64, "network.nav.config");
    }

    @Override
    public void draw(GuiFlux flux, int x, int y) {
        if (flux.common.isFakeNetwork()) {
            flux.renderNavigationPrompt("No Connections Available", "Network Selection");
            return;
        }
        ArrayList<ClientFlux> connections = flux.common.getClientFluxConnection();
        int start = (int) (connections.size() * scroller.getCurrentScroll());
        int finish = Math.min(start + listSize + 2, connections.size());
        selected = null;
        for (int i = start; i < finish; i++) {
            ClientFlux clientFlux = connections.get(i);
            if (clientFlux != null) {
                int posX = 11;
                int posY = 8 + 12 * i - 12 * start;
                if (x > flux.getGuiLeft() + posX && x < flux.getGuiLeft() + posX + 154 && y >= flux.getGuiTop() + posY && y < flux.getGuiTop() + posY + 12) {
                    toRemove = x > flux.getGuiLeft() + posX + 144;
                    selected = clientFlux;
                    flux.renderFlux(clientFlux, true, posX, posY);
                } else {
                    flux.renderFlux(clientFlux, false, posX, posY);
                }
                flux.bindTexture(GuiFluxBase.buttons);
                flux.drawTexturedModalRect(154, posY, 56, 0, 12, 12);
            }
        }
        flux.bindTexture(flux.getBackground());
        if (selected != null) {
            boolean isCurrent = selected.coords.getBlockPos().equals(flux.tile.getPos());
            ArrayList<String> strings = new ArrayList<>();
            if (toRemove) {
                strings.add(TextFormatting.RED + "REMOVE");
            } else {
                if (isCurrent)
                    strings.add(TextFormatting.GREEN + "THIS CONNECTION!");
                strings.add(FontHelper.translate("flux.type") + ": " + TextFormatting.AQUA + selected.getConnectionType().toString());
                strings.add(TextFormatting.GRAY + selected.getCoords().toString());
                strings.add(GUI.MAX + ": " + TextFormatting.AQUA + (selected.getTransferLimit() == Long.MAX_VALUE ? "NO LIMIT" : selected.getTransferLimit()));
                strings.add(GUI.PRIORITY + ": " + TextFormatting.AQUA + selected.getCurrentPriority());
            }
            flux.drawHoveringText(strings, x - flux.getGuiLeft(), y - flux.getGuiTop());
        } else {
            toRemove = false;
        }
    }

    @Override
    public void init(GuiFlux flux) {
        scroller = new SonarScroller(flux.getGuiLeft() + 165, flux.getGuiTop() + 8, 146, 10);
        for (int i = 0; i < listSize + 2; i++) {
            flux.getButtonList().add(new NetworkButton(10 + i, flux.getGuiLeft() + 7, flux.getGuiTop() + 8 + i * 12));
        }
    }

    @Override
    public void button(GuiFlux flux, GuiButton button) {
    }

    @Override
    public void click(GuiFlux flux, int x, int y, int mouseButton) {
        if (toRemove && selected != null) {
            FluxNetworks.network.sendToServer(new PacketFluxButton(selected.coords.getDimension(), Type.REMOVE_CONNECTION, selected.coords.getBlockPos(), flux.getNetworkID()));
        }
    }

    @Override
    public SonarTextField[] getFields(GuiFlux flux) {
        return new SonarTextField[0];
    }

    @Override
    public SonarScroller[] getScrollers() {
        return new SonarScroller[]{scroller};
    }

    @Override
    public int getSelectionSize(GuiFlux flux) {
        if (flux.common == null)
            return 0;
        return flux.common.getClientFluxConnection().size();
    }

}