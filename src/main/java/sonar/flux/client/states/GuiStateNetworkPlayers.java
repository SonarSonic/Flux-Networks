package sonar.flux.client.states;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import sonar.core.client.gui.SonarTextField;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.flux.api.AccessType;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.client.GuiFlux;
import sonar.flux.client.GuiFluxBase;
import sonar.flux.client.GuiState;
import sonar.flux.client.GuiTypeMessage;
import sonar.flux.network.PacketHelper;
import sonar.flux.network.PacketType;

public class GuiStateNetworkPlayers extends GuiState {

    public SonarScroller scroller;
    public SonarTextField playerName;
    public PlayerAccess playerAccess = PlayerAccess.USER;
    public FluxPlayer selectedPlayer;
    public static int listSize = 10;

    public GuiStateNetworkPlayers() {
        super(GuiTypeMessage.PLAYERS, 176, 166, 386, "network.players");
    }

    @Override
    public void init(GuiFlux flux) {
        scroller = new SonarScroller(flux.getGuiLeft() + 165, flux.getGuiTop() + 8, 146, 10);
        selectedPlayer = null;
        int networkColour = flux.common.getNetworkColour().getRGB();
        flux.getButtonList().add(flux.selectButton(1, flux.getGuiLeft() + 150, flux.getGuiTop() + 138, 136, "Add"));
        playerName = new SonarTextField(1, flux.getFontRenderer(), 14, 138, 130, 12).setBoxOutlineColour(networkColour);
        playerName.setMaxStringLength(24);
        playerName.setText("");
        if (!flux.common.isFakeNetwork() && flux.common.getAccessType() == AccessType.PRIVATE) {
            flux.disabledState = true;
        }
    }


    @Override
    public void draw(GuiFlux flux, int x, int y) {

        if (flux.common.isFakeNetwork()) {
            flux.renderNavigationPrompt("No players can be added", "Network Selection");
        } else if (flux.disabledState) {
            flux.renderNavigationPrompt("Unavailable in Private Mode", "Edit Network");
        } else {
            ArrayList<FluxPlayer> players = flux.common.getPlayers();
            FluxPlayer currentPlayer = null;
            if (!players.isEmpty()) {
                int start = (int) (players.size() * scroller.getCurrentScroll());
                int finish = Math.min(start + listSize, players.size());
                for (int i = start; i < finish; i++) {
                    FluxPlayer player = players.get(i);
                    if (players.get(i) != null) {
                        int xPos = 11;
                        int yPos = 8 + 12 * i - 12 * start;
                        PlayerAccess access = player.getAccess();
                        boolean isOwner = flux.common.getCachedPlayerName().equals(player.getCachedName());
                        Gui.drawRect(xPos, yPos, xPos + 154, yPos + 12, access.canDelete() || isOwner ? Color.lightGray.getRGB() : access.canEdit() ? GuiFluxBase.colours[7].getRGB() : !access.canConnect() ? GuiFluxBase.colours[4].getRGB() : GuiFluxBase.lightBlue);

                        flux.bindTexture(flux.getBackground());
                        flux.drawTexturedModalRect(xPos, yPos, 0, 166, 154, 12);
                        FontHelper.text(player.getCachedName(), xPos + 3, yPos + 2, Color.white.getRGB());
                        flux.bindTexture(GuiFluxBase.buttons);
                        flux.drawTexturedModalRect(xPos + 154 - 12, yPos, 112 / 2, 0, 10 + 1, 10 + 1);

                        if (x > flux.getGuiLeft() + xPos && x < flux.getGuiLeft() + xPos + 154 && y >= flux.getGuiTop() + yPos && y < flux.getGuiTop() + yPos + 12) {
                            currentPlayer = players.get(i);
                        }
                    }
                }
            }
            selectedPlayer = currentPlayer;
            if (selectedPlayer != null) {
                boolean isOwner = flux.common.getCachedPlayerName().equals(selectedPlayer.getCachedName());
                List<String> strings = Lists.newArrayList();
                if (x > flux.getGuiLeft() + 11 + 142 && x < flux.getGuiLeft() + 11 + 153) {
                    strings.add(TextFormatting.RED + "Delete: " + selectedPlayer.getCachedName());
                } else {
                    strings.add(TextFormatting.AQUA + "Config: " + FontHelper.translate(isOwner ? PlayerAccess.OWNER.getName() : selectedPlayer.access.getName()));
                    strings.add("Right click to change");
                }
                flux.drawHoveringText(strings, x - flux.getGuiLeft(), y - flux.getGuiTop());
            }
            flux.bindTexture(flux.getBackground());
        }
    }

    @Override
    public void button(GuiFlux flux, GuiButton button) {
        switch (button.id) {
            case 1:
                if (!playerName.getText().isEmpty()) {
                 	PacketHelper.sendPacketToServer(PacketType.ADD_PLAYER, flux.tile, PacketHelper.createAddPlayerPacket(flux.getNetworkID(), playerName.getText(), PlayerAccess.USER));
                    return;
                }
                break;
        }

    }

    @Override
    public void click(GuiFlux flux, int x, int y, int mouseButton) {
        if (selectedPlayer != null) {
            if (x - flux.getGuiLeft() > 11 + 142 && x - flux.getGuiLeft() < 11 + 153) {
                PacketHelper.sendPacketToServer(PacketType.REMOVE_PLAYER, flux.tile, PacketHelper.createRemovePlayerPacket(flux.getNetworkID(), selectedPlayer.id, PlayerAccess.USER));
            } else if (mouseButton == 1) {
                PacketHelper.sendPacketToServer(PacketType.CHANGE_PLAYER, flux.tile, PacketHelper.createChangePlayerPacket(flux.getNetworkID(), selectedPlayer.id, PlayerAccess.USER));
            }
        }
    }

    @Override
    public SonarTextField[] getFields(GuiFlux flux) {
        return new SonarTextField[]{playerName};
    }

    @Override
    public SonarScroller[] getScrollers() {
        return new SonarScroller[]{scroller};
    }

    @Override
    public int getSelectionSize(GuiFlux flux) {
        return flux.common.getPlayers().size();
    }

}