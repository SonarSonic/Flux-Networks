package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.BatchEditButton;
import sonar.fluxnetworks.client.gui.popups.PopUpConnectionEdit;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.BatchEditingPacket;
import sonar.fluxnetworks.common.network.ConnectionUpdateRequestPacket;
import sonar.fluxnetworks.common.network.NetworkUpdateRequestPacket;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiTabConnections extends GuiTabPages<IFluxConnector> {

    private List<BatchEditButton> editButtons = new ArrayList<>();

    public List<IFluxConnector> batchConnections = new ArrayList<>();
    public IFluxConnector singleConnection;

    public BatchEditButton clear, edit, disconnect;

    private int timer = 3;

    public GuiTabConnections(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        gridStartX = 15; gridStartY = 22;
        gridHeight = 19; gridPerPage = 7;
        elementHeight = 18; elementWidth = 146;
        PacketHandler.INSTANCE.sendToServer(new NetworkUpdateRequestPacket(network.getNetworkID(), NBTType.NETWORK_CONNECTIONS));
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_CONNECTION;
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_CONNECTION, navigationTabs);
        editButtons.clear();
        buttonLists.add(editButtons);

        if(networkValid) {
            clear = new BatchEditButton(118, 8, 0, FluxTranslate.BATCH_CLEAR_BUTTON.t()).setUnclickable();
            edit = new BatchEditButton(132, 8, 1, FluxTranslate.BATCH_EDIT_BUTTON.t()).setUnclickable();
            disconnect = new BatchEditButton(146, 8, 2, FluxTranslate.BATCH_DISCONNECT_BUTTON.t()).setUnclickable();
            editButtons.add(clear);
            editButtons.add(edit);
            editButtons.add(disconnect);
        }
    }

    @Override
    protected void onElementClicked(IFluxConnector element, int mouseButton) {
        if(mouseButton == 0 && batchConnections.size() == 0 && element.isChunkLoaded()) {
            singleConnection = element;
            openPopUp(new PopUpConnectionEdit(this, false, player, connector));
        }
        if(mouseButton == 1 || (mouseButton == 0 && batchConnections.size() > 0) ) {
            if (batchConnections.contains(element)) {
                batchConnections.remove(element);
                if(batchConnections.size() <= 0) {
                    clear.clickable = false;
                    edit.clickable = false;
                    disconnect.clickable = false;
                }
            } else if(element.isChunkLoaded()) {
                batchConnections.add(element);
                clear.clickable = true;
                edit.clickable = true;
                disconnect.clickable = true;
            }
        }
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        if(networkValid) {
            if(batchConnections.size() > 0) {
                font.drawString(FluxTranslate.SELECTED.t() + ": " + TextFormatting.AQUA + batchConnections.size(), 20, 10, 0xffffff);
            } else {
                font.drawString(FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + FluxTranslate.SORTING_SMART.t(), 19, 10, 0xffffff);
            }
            super.drawForegroundLayer(mouseX, mouseY);
            if(!hasActivePopup())
                drawCenteredString(font, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 165, 0xffffff);
        } else {
            super.drawForegroundLayer(mouseX, mouseY);
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void renderElement(IFluxConnector element, int x, int y) {
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(screenUtils.GUI_BAR);
        int fontColor = 0xffffff;
        int color = element.getConnectionType().color;

        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        if(batchConnections.size() > 0) {
            if (batchConnections.contains(element)) {
                fill(x - 5, y + 1, x - 3, y + elementHeight - 1, 0xccffffff);
                fill(x + elementWidth + 3, y + 1, x + elementWidth + 5, y + elementHeight - 1, 0xccffffff);
                RenderSystem.color3f(red, green, blue);
                blit(x, y, 0, 32, elementWidth, elementHeight);
            } else {
                fill(x - 5, y + 1, x - 3, y + elementHeight - 1, 0xaa606060);
                fill(x + elementWidth + 3, y + 1, x + elementWidth + 5, y + elementHeight - 1, 0xaa606060);
                RenderSystem.color3f(red * 0.5f, green * 0.5f, blue * 0.5f);
                blit(x, y, 0, 32, elementWidth, elementHeight);
                fontColor = 0xd0d0d0;
            }
        } else {
            RenderSystem.color3f(red, green, blue);
            blit(x, y, 0, 32, elementWidth, elementHeight);
        }
        if(element.isChunkLoaded()) {
            font.drawString(element.getCustomName(), x + 21, y + 2, fontColor);
            RenderSystem.scaled(0.625, 0.625, 0.625);
            font.drawString(FluxUtils.getTransferInfo(element.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), element.getChange()), (int) ((x + 21) * 1.6), (int) ((y + 11) * 1.6), fontColor);
            RenderSystem.scaled(1.6, 1.6, 1.6);
        } else {
            font.drawString(element.getCustomName(), x + 21, y + 5, 0x808080);
        }
        if(currentPopUp == null) { //FIXME - TEMP FIX ! - DEPTH PROBLEM WITH POPUPS DUE TO THE ITEMRENDERER OFFSET I THINK - lets disable when there is no pop up for now.
            screenUtils.renderItemStack(element.getDisplayStack(), x + 2, y + 1);
        }
    }

    @Override
    public void renderElementTooltip(IFluxConnector element, int mouseX, int mouseY) {
        if(!hasActivePopup()) {
            GlStateManager.pushMatrix();
            screenUtils.drawHoverTooltip(getFluxInfo(element), mouseX + 4, mouseY - 16);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if(button instanceof BatchEditButton){
            switch (button.id) {
                case 0:
                    batchConnections.clear();
                    clear.clickable = false;
                    edit.clickable = false;
                    disconnect.clickable = false;
                    break;
                case 1:
                    openPopUp(new PopUpConnectionEdit(this, true, player, connector));
                    break;
                case 2:
                    List<Coord4D> list = batchConnections.stream().map(IFluxConnector::getCoords).collect(Collectors.toList());
                    boolean[] b = {false, false, false, false, false, false, true};
                    PacketHandler.INSTANCE.sendToServer(new BatchEditingPacket(network.getNetworkID(), list, new CompoundNBT(), b));
                    break;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!networkValid)
            return;
        if(timer == 4) {
            refreshPages(network.getSetting(NetworkSettings.ALL_CONNECTORS));
        }
        if(timer % 5 == 0) {
            PacketHandler.INSTANCE.sendToServer(new ConnectionUpdateRequestPacket(network.getNetworkID(), current.stream().map(IFluxConnector::getCoords).collect(Collectors.toList())));
        }
        timer++;
        timer %= 20;
        if(FluxNetworks.proxy.getFeedback(true) == EnumFeedbackInfo.SUCCESS) {
            closePopUp();
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(network.getSetting(NetworkSettings.ALL_CONNECTORS));
        }
        if(FluxNetworks.proxy.getFeedback(true) == EnumFeedbackInfo.SUCCESS_2) {
            closePopUp();
            elements.removeAll(batchConnections);
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(network.getSetting(NetworkSettings.ALL_CONNECTORS));
            if(connector instanceof IFluxConnector && elements.stream().noneMatch(f -> f.getCoords().equals(((IFluxConnector)connector).getCoords()))) {
                Minecraft.getInstance().currentScreen = new GuiTabSelection(player, connector);
            }
            page = Math.min(page, pages);
        }
    }

    @Override
    protected void sortGrids(SortType sortType) {
        elements.sort(Comparator.comparing(IFluxConnector::isChunkLoaded).reversed().
                thenComparing(f -> f.getConnectionType().isStorage()).
                thenComparing(f -> f.getConnectionType().canAddEnergy()).
                thenComparing(f -> f.getConnectionType().canRemoveEnergy()).
                thenComparing(p -> -p.getPriority()));
        refreshCurrentPageInternal();
    }
}
