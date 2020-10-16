package sonar.fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.misc.Coord4D;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.BatchEditButton;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.popups.PopUpConnectionEdit;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.BatchEditingPacket;
import sonar.fluxnetworks.common.network.NetworkUpdateRequestPacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiTabConnections extends GuiTabPages<IFluxDevice> {

    public InvisibleButton redirectButton;

    private List<BatchEditButton> editButtons = new ArrayList<>();

    public List<IFluxDevice> batchConnections = new ArrayList<>();
    public IFluxDevice singleConnection;

    public BatchEditButton clear, edit, disconnect;

    private int timer = 3;

    public GuiTabConnections(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
        gridStartX = 15;
        gridStartY = 22;
        gridHeight = 19;
        gridPerPage = 7;
        elementHeight = 18;
        elementWidth = 146;
        PacketHandler.CHANNEL.sendToServer(new NetworkUpdateRequestPacket(network.getNetworkID(), NBTType.NETWORK_CONNECTIONS));
    }

    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_CONNECTION;
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_CONNECTION, navigationTabs);
        editButtons.clear();
        buttonLists.add(editButtons);

        if (!networkValid) {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
            addButton(redirectButton);
        } else {
            clear = new BatchEditButton(118, 8, 0, FluxTranslate.BATCH_CLEAR_BUTTON.t()).setUnclickable();
            edit = new BatchEditButton(132, 8, 1, FluxTranslate.BATCH_EDIT_BUTTON.t()).setUnclickable();
            disconnect = new BatchEditButton(146, 8, 2, FluxTranslate.BATCH_DISCONNECT_BUTTON.t()).setUnclickable();
            editButtons.add(clear);
            editButtons.add(edit);
            editButtons.add(disconnect);
        }
    }

    @Override
    protected void onElementClicked(IFluxDevice element, int mouseButton) {
        if (mouseButton == 0 && batchConnections.size() == 0 && element.isChunkLoaded()) {
            singleConnection = element;
            openPopUp(new PopUpConnectionEdit(this, false, player, connector));
        }
        if (mouseButton == 1 || (mouseButton == 0 && batchConnections.size() > 0)) {
            if (batchConnections.contains(element)) {
                batchConnections.remove(element);
                if (batchConnections.size() <= 0) {
                    clear.clickable = false;
                    edit.clickable = false;
                    disconnect.clickable = false;
                }
            } else if (element.isChunkLoaded()) {
                batchConnections.add(element);
                clear.clickable = true;
                edit.clickable = true;
                disconnect.clickable = true;
            }
        }
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (networkValid) {
            if (batchConnections.size() > 0) {
                font.drawString(matrixStack, FluxTranslate.SELECTED.t() + ": " + TextFormatting.AQUA + batchConnections.size(), 20, 10, 0xffffff);
            } else {
                font.drawString(matrixStack, FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + FluxTranslate.SORTING_SMART.t(), 19, 10, 0xffffff);
            }
            super.drawForegroundLayer(matrixStack, mouseX, mouseY);
            if (!hasActivePopup())
                drawCenteredString(matrixStack, font, TextFormatting.RED + FluxClientCache.getFeedback(false).getInfo(), 88, 165, 0xffffff);
        } else {
            super.drawForegroundLayer(matrixStack, mouseX, mouseY);
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void renderElement(MatrixStack matrixStack, IFluxDevice element, int x, int y) {
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(screenUtils.GUI_BAR);
        int fontColor = 0xffffff;
        int color = element.getDeviceType().color;

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        if (batchConnections.size() > 0) {
            if (batchConnections.contains(element)) {
                fill(matrixStack, x - 5, y + 1, x - 3, y + elementHeight - 1, 0xccffffff);
                fill(matrixStack, x + elementWidth + 3, y + 1, x + elementWidth + 5, y + elementHeight - 1, 0xccffffff);
                RenderSystem.color3f(red, green, blue);
                blit(matrixStack, x, y, 0, 32, elementWidth, elementHeight);
            } else {
                fill(matrixStack, x - 5, y + 1, x - 3, y + elementHeight - 1, 0xaa606060);
                fill(matrixStack, x + elementWidth + 3, y + 1, x + elementWidth + 5, y + elementHeight - 1, 0xaa606060);
                RenderSystem.color3f(red * 0.5f, green * 0.5f, blue * 0.5f);
                blit(matrixStack, x, y, 0, 32, elementWidth, elementHeight);
                fontColor = 0xd0d0d0;
            }
        } else {
            RenderSystem.color3f(red, green, blue);
            blit(matrixStack, x, y, 0, 32, elementWidth, elementHeight);
        }
        if (element.isChunkLoaded()) {
            font.drawString(matrixStack, element.getCustomName(), x + 21, y + 2, fontColor);
            RenderSystem.scaled(0.625, 0.625, 0.625);
            font.drawString(matrixStack, FluxUtils.getTransferInfo(element.getDeviceType(), EnergyType.FE, element.getChange()), (int) ((x + 21) * 1.6), (int) ((y + 11) * 1.6), fontColor);
            RenderSystem.scaled(1.6, 1.6, 1.6);
        } else {
            font.drawString(matrixStack, element.getCustomName(), x + 21, y + 5, 0x808080);
        }
        if (currentPopUp == null) { //TODO MINOR - TEMP FIX ! - DEPTH PROBLEM WITH POPUPS DUE TO THE ITEMRENDERER OFFSET I THINK - lets disable when there is no pop up for now.
            screenUtils.renderItemStack(element.getDisplayStack(), x + 2, y + 1);
        }
    }

    @Override
    public void renderElementTooltip(MatrixStack matrixStack, IFluxDevice element, int mouseX, int mouseY) {
        if (!hasActivePopup()) {
            screenUtils.drawHoverTooltip(matrixStack, getFluxInfo(element), mouseX + 4, mouseY - 16);
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (button instanceof BatchEditButton) {
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
                    //TODO
                    List<Coord4D> list = new ArrayList<>();/*batchConnections.stream().map(IFluxDevice::getCoords).collect(Collectors.toList());*/
                    boolean[] b = {false, false, false, false, false, false, true};
                    PacketHandler.CHANNEL.sendToServer(new BatchEditingPacket(network.getNetworkID(), list, new CompoundNBT(), b));
                    break;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!networkValid)
            return;
        if (timer == 4) {
            refreshPages(Lists.newArrayList(network.getAllConnections()));
        }
        if (timer % 5 == 0) {
            //TODO
            //PacketHandler.CHANNEL.sendToServer(new ConnectionUpdateRequestPacket(network.getNetworkID(), current.stream().map(IFluxDevice::getCoords).collect(Collectors.toList())));
        }
        timer++;
        timer %= 20;
        if (FluxClientCache.getFeedback(true) == EnumFeedbackInfo.SUCCESS) {
            closePopUp();
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(Lists.newArrayList(network.getAllConnections()));
        }
        if (FluxClientCache.getFeedback(true) == EnumFeedbackInfo.SUCCESS_2) {
            closePopUp();
            elements.removeAll(batchConnections);
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(Lists.newArrayList(network.getAllConnections()));
            //TODO
            /*if(connector instanceof IFluxDevice && elements.stream().noneMatch(f -> f.getCoords().equals(((IFluxDevice)connector).getCoords()))) {
                Minecraft.getInstance().currentScreen = new GuiTabSelection(player, connector);
            }*/
            page = Math.min(page, pages);
        }
    }

    @Override
    protected void sortGrids(SortType sortType) {
        elements.sort(Comparator.comparing(IFluxDevice::isChunkLoaded).reversed().
                thenComparing(f -> f.getDeviceType().isStorage()).
                thenComparing(f -> f.getDeviceType().isPlug()).
                thenComparing(f -> f.getDeviceType().isPoint()).
                thenComparing(p -> -p.getRawPriority()));
        refreshCurrentPageInternal();
    }
}
