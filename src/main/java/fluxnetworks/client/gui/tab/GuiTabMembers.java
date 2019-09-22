package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.client.gui.basic.GuiCore;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.*;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
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
    public NormalButton transferOwnership;
    public int transferOwnershipCount;

    private int timer;

    public GuiTabMembers(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 15;
        gridStartY = 22;
        gridHeight = 13;
        gridPerPage = 10;
        elementHeight = 12;
        elementWidth = 146;
        PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_PLAYERS));
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        if(networkValid) {
            String str2 = accessPermission.getName();
            fontRenderer.drawString(str2, 158 - fontRenderer.getStringWidth(str2), 10, 0xffffff);
            fontRenderer.drawString(FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + "Smart", 19, 10, 0xffffff);
            super.drawForegroundLayer(mouseX, mouseY);
        } else {
            super.drawForegroundLayer(mouseX, mouseY);
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        drawRectWithBackground(20, 34, 100, 138, 0xccffffff, 0x80000000);
        super.drawPopupForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 88, 162, 0xffffff);
        drawCenteredString(fontRenderer, TextFormatting.AQUA + selectedPlayer.getCachedName(), 88, 38, 0xffffff);
        drawCenteredString(fontRenderer, selectedPlayer.getAccessPermission().getName(), 88, 48, 0xffffff);
        String text = selectedPlayer.getPlayerUUID().toString();
        GlStateManager.scale(0.625, 0.625, 0.625);
        drawCenteredString(fontRenderer, "UUID: " + text.substring(0, 16), (int) (88 * 1.6), (int) (60 * 1.6), 0xffffff);
        drawCenteredString(fontRenderer, text.substring(16), (int) (88 * 1.6), (int) (66 * 1.6), 0xffffff);
        GlStateManager.scale(1.6, 1.6, 1.6);
    }

    @Override
    public void initGui() {

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(5).setMain();

        /*if(networkValid) {

            buttons.add(new NormalButton("+", 152, 150, 12, 12, 1));

            player = TextboxButton.create(this, "", 1, fontRenderer, 14, 150, 130, 12);
            player.setMaxStringLength(32);

            textBoxes.add(player);
        }*/

        super.initGui();
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
            main = false;
            initPopGui();
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        if(!main) {
            initPopGui();
        }
    }

    private void initPopGui() {
        popButtons.clear();
        boolean editPermission = accessPermission.canEdit();
        boolean ownerPermission = accessPermission.canDelete();
        if(selectedPlayer.getAccessPermission() != AccessPermission.OWNER && editPermission) {
            String text;
            int length;
            int i = 0;
            if (selectedPlayer.getAccessPermission() == AccessPermission.NONE || selectedPlayer.getAccessPermission() == AccessPermission.SUPER_ADMIN) {
                text = FluxTranslate.SET_USER.t();
                length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 0));
                if(selectedPlayer.getAccessPermission() == AccessPermission.SUPER_ADMIN && ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            } else {
                if(ownerPermission) {
                    if (selectedPlayer.getAccessPermission() == AccessPermission.USER) {
                        text = FluxTranslate.SET_ADMIN.t();
                        length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 1));
                    } else if(selectedPlayer.getAccessPermission() == AccessPermission.ADMIN) {
                        text = FluxTranslate.SET_USER.t();
                        length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                        popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 2));
                    }
                }
                if(!selectedPlayer.getAccessPermission().canEdit() || ownerPermission) {
                    text = FluxTranslate.CANCEL_MEMBERSHIP.t();
                    length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                    popButtons.add(new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 3).setTextColor(0xffff5555));
                }
                if(ownerPermission) {
                    text = FluxTranslate.TRANSFER_OWNERSHIP.t();
                    length = Math.max(64, fontRenderer.getStringWidth(text) + 4);
                    transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i++, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
                    popButtons.add(transferOwnership);
                }
            }
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

        mc.getTextureManager().bindTexture(GuiCore.GUI_BAR);
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
        if(!main)
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
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
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
    protected void mousePopupClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mousePopupClicked(mouseX, mouseY, mouseButton);
        for(NormalButton button : popButtons) {
            if(button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_PERMISSION, PacketGeneralHandler.getChangePermissionPacket(network.getNetworkID(), selectedPlayer.getPlayerUUID(), button.id)));
            }
        }
    }

    @Override
    protected void keyTypedPop(char c, int k) throws IOException {
        super.keyTypedPop(c, k);
        if(transferOwnership != null) {
            if (k == 42) {
                transferOwnershipCount++;
                if (transferOwnershipCount > 1) {
                    transferOwnership.clickable = true;
                }
            } else {
                transferOwnershipCount = 0;
                transferOwnership.clickable = false;
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer == 0) {
            PacketHandler.network.sendToServer(new PacketPermissionRequest.PermissionRequestMessage(network.getNetworkID(), player.getUniqueID()));
        }
        if(timer % 2 == 0) {
            refreshPages(network.getSetting(NetworkSettings.NETWORK_PLAYERS));
            if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS) {
                if(!main) {
                    Optional<NetworkMember> n = elements.stream().filter(f -> f.getPlayerUUID().equals(selectedPlayer.getPlayerUUID())).findFirst();
                    if (n.isPresent()) {
                        selectedPlayer = n.get();
                        initPopGui();
                    } else {
                        backToMain();
                    }
                }
                FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
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
