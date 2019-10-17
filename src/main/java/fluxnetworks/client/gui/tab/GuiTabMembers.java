package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.EnumNavigationTabs;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.client.gui.basic.GuiDraw;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.popups.GuiPopUserEdit;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketNetworkUpdateRequest;
import fluxnetworks.common.network.PacketPermissionRequest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GuiTabMembers extends GuiTabPages<NetworkMember> {

    public NetworkMember selectedPlayer;

    private int timer;

    public GuiTabMembers(EntityPlayer player, INetworkConnector connector) {
        super(player, connector);
        gridStartX = 15;
        gridStartY = 22;
        gridHeight = 13;
        gridPerPage = 10;
        elementHeight = 12;
        elementWidth = 146;
        PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_PLAYERS));
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_MEMBER;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        if(networkValid) {
            String str2 = accessPermission.getName();
            fontRenderer.drawString(str2, 158 - fontRenderer.getStringWidth(str2), 10, 0xffffff);
            fontRenderer.drawString(FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + FluxTranslate.SORTING_SMART.t(), 19, 10, 0xffffff);
            super.drawForegroundLayer(mouseX, mouseY);
        } else {
            super.drawForegroundLayer(mouseX, mouseY);
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void initGui() {
        /*if(networkValid) {

            buttons.add(new NormalButton("+", 152, 150, 12, 12, 1));

            player = TextboxButton.create(this, "", 1, fontRenderer, 14, 150, 130, 12);
            player.setMaxStringLength(32);

            textBoxes.add(player);
        }*/

        super.initGui();
        configureNavigationButtons(EnumNavigationTabs.TAB_MEMBER, navigationTabs);
    }

    @Override
    protected void onElementClicked(NetworkMember element, int mouseButton) {
        /*if(mouseButton == 0) {
            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_PERMISSION, PacketGeneralHandler.getChangePermissionPacket(network.getNetworkID(), element.getPlayerUUID())));
        } else if(mouseButton == 1) {
            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.REMOVE_MEMBER, PacketGeneralHandler.getRemoveMemberPacket(network.getNetworkID(), element.getPlayerUUID())));
        }*/
        if(mouseButton == 0) {
            selectedPlayer = element;
            openPopUp(new GuiPopUserEdit(this, player, connector));
        }
    }

    @Override
    public void renderElement(NetworkMember element, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        int color = element.getAccessPermission().color;

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        GlStateManager.color(f, f1, f2, 0.8f);

        mc.getTextureManager().bindTexture(GuiDraw.GUI_BAR);
        drawTexturedModalRect(x, y, 0, 16, elementWidth, elementHeight);

        if(element.getPlayerUUID().equals(player.getUniqueID())) {
            drawRect(x - 4, y + 1, x - 2, y + elementHeight - 1, 0xccffffff);
            drawRect(x + elementWidth + 2, y + 1, x + elementWidth + 4, y + elementHeight - 1, 0xccffffff);
        }

        fontRenderer.drawString(TextFormatting.WHITE + element.getCachedName(), x + 4, y + 2, 0xffffff);

        String p = element.getAccessPermission().getName();
        fontRenderer.drawString(p, x + 142 - fontRenderer.getStringWidth(p), y + 2, 0xffffff);

        GlStateManager.popMatrix();
    }

    @Override
    public void renderElementTooltip(NetworkMember element, int mouseX, int mouseY) {
        if(hasActivePopup())
            return;
        GlStateManager.pushMatrix();
        List<String> strings = new ArrayList<>();
        strings.add(FluxTranslate.USERNAME.t() + ": " + TextFormatting.AQUA + element.getCachedName());
        String permission = element.getAccessPermission().getName() + (element.getPlayerUUID().equals(player.getUniqueID()) ? " (" + FluxTranslate.YOU.t() + ")" : "");
        strings.add(FluxTranslate.ACCESS.t() + ": " + TextFormatting.RESET + permission);
        //strings.add(TextFormatting.GRAY + "UUID: " + TextFormatting.RESET + element.getPlayerUUID().toString());
        /*if(element.getPlayerUUID().equals(player.getUniqueID())) {
            strings.add(TextFormatting.WHITE + "You");
        }*/
        drawHoverTooltip(strings, mouseX + 4, mouseY - 8);
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        /*for(NormalButton button : buttons) {
            if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                if(button.id == 1 && !player.getText().isEmpty()) {
                    PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.ADD_MEMBER, PacketGeneralHandler.getAddMemberPacket(network.getNetworkID(), player.getText())));
                    player.setText("");
                }
            }
        }*/
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer == 0) {
            PacketHandler.network.sendToServer(new PacketPermissionRequest.PermissionRequestMessage(network.getNetworkID(), player.getUniqueID()));
        }
        if(timer % 2 == 0) {
            refreshPages(network.getSetting(NetworkSettings.NETWORK_PLAYERS));
            if(FluxNetworks.proxy.getFeedback(true) == FeedbackInfo.SUCCESS) {
                if(hasActivePopup()) {
                    Optional<NetworkMember> n = elements.stream().filter(f -> f.getPlayerUUID().equals(selectedPlayer.getPlayerUUID())).findFirst();
                    if (n.isPresent()) {
                        selectedPlayer = n.get();
                        openPopUp(new GuiPopUserEdit(this, player, connector));
                    } else {
                        closePopUp();
                    }
                }
                FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE, true);
            }
        }
        timer++;
        timer %= 40;
    }

    @Override
    protected void sortGrids(SortType sortType) {
        elements.sort(Comparator.comparing(NetworkMember::getAccessPermission).thenComparing(NetworkMember::getCachedName));
        refreshCurrentPageInternal();
    }
}
