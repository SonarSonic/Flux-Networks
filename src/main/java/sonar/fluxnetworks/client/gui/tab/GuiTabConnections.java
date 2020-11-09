package sonar.fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.ScreenUtils;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabPages;
import sonar.fluxnetworks.client.gui.button.BatchEditButton;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.popup.PopupConnectionEdit;
import sonar.fluxnetworks.common.misc.FluxMenu;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.CConnectionUpdateMessage;
import sonar.fluxnetworks.common.network.CEditConnectionsMessage;
import sonar.fluxnetworks.common.network.CNetworkUpdateMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GuiTabConnections extends GuiTabPages<IFluxDevice> {

    public InvisibleButton redirectButton;

    private final List<BatchEditButton> editButtons = new ArrayList<>();

    public List<IFluxDevice> batchConnections = new ArrayList<>();
    public IFluxDevice singleConnection;

    public BatchEditButton clear, edit, disconnect;

    private int timer = 3;

    public GuiTabConnections(@Nonnull FluxMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
        gridStartX = 15;
        gridStartY = 22;
        gridHeight = 19;
        gridPerPage = 7;
        elementHeight = 18;
        elementWidth = 146;
        NetworkHandler.INSTANCE.sendToServer(new CNetworkUpdateMessage(network.getNetworkID(), FluxConstants.TYPE_NET_CONNECTIONS));
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_CONNECTION;
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_CONNECTION, navigationTabs);
        editButtons.clear();
        buttonLists.add(editButtons);

        if (!networkValid) {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
            addButton(redirectButton);
        } else {
            clear = new BatchEditButton(118, 8, 0, FluxTranslate.BATCH_CLEAR_BUTTON.t()).setUnclickable();
            edit = new BatchEditButton(132, 8, 1, FluxTranslate.BATCH_EDIT_BUTTON.t()).setUnclickable();
            disconnect = new BatchEditButton(146, 8, 2, FluxTranslate.BATCH_DISCONNECT_BUTTON.t()).setUnclickable();
            editButtons.add(clear);
            editButtons.add(edit);
            editButtons.add(disconnect);
        }
        refreshPages(Lists.newArrayList(network.getAllConnections()));
    }

    @Override
    protected void onElementClicked(IFluxDevice element, int mouseButton) {
        if (mouseButton == 0 && batchConnections.size() == 0 && element.isChunkLoaded()) {
            singleConnection = element;
            openPopUp(new PopupConnectionEdit(this, player, false));
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
                drawCenterText(matrixStack, TextFormatting.RED + FluxClientCache.getFeedbackText(), 88, 165, 0xffffff);
        } else {
            super.drawForegroundLayer(matrixStack, mouseX, mouseY);
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void renderElement(MatrixStack matrixStack, @Nonnull IFluxDevice element, int x, int y) {
        RenderSystem.color3f(1.0f, 1.0f, 1.0f);
        minecraft.getTextureManager().bindTexture(ScreenUtils.GUI_BAR);
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
            font.drawString(matrixStack, FluxUtils.getTransferInfo(element, EnergyType.FE), (x + 21) * 1.6f, (y + 11) * 1.6f, fontColor);
            RenderSystem.scaled(1.6, 1.6, 1.6);
        } else {
            font.drawString(matrixStack, element.getCustomName(), x + 21, y + 5, 0x808080);
        }
        screenUtils.renderItemStack(element.getDisplayStack(), x + 2, y + 1);
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
                    openPopUp(new PopupConnectionEdit(this, player, true));
                    break;
                case 2:
                    List<GlobalPos> list = batchConnections.stream().map(IFluxDevice::getGlobalPos).collect(Collectors.toList());
                    NetworkHandler.INSTANCE.sendToServer(new CEditConnectionsMessage(network.getNetworkID(), list,
                            FluxConstants.FLAG_EDIT_DISCONNECT));
                    break;
            }
        }
    }

    @Override
    public void onOperationalFeedback(@Nonnull FeedbackInfo info) {
        super.onOperationalFeedback(info);
        if (info == FeedbackInfo.SUCCESS) {
            closePopUp();
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(Lists.newArrayList(network.getAllConnections()));
        } else if (info == FeedbackInfo.SUCCESS_2) {
            closePopUp();
            //elements.removeAll(batchConnections);
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(Lists.newArrayList(network.getAllConnections()));
            if (container.bridge instanceof IFluxDevice) {
                GlobalPos g = ((IFluxDevice) container.bridge).getGlobalPos();
                if (elements.stream().noneMatch(f -> f.getGlobalPos().equals(g))) {
                    switchTab(EnumNavigationTab.TAB_SELECTION);
                }
            }
            page = Math.min(page, pages);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!networkValid)
            return;
        if (timer == 4 || timer == 14) {
            refreshPages(Lists.newArrayList(network.getAllConnections()));
        }
        if (timer % 5 == 0) {
            NetworkHandler.INSTANCE.sendToServer(new CConnectionUpdateMessage(network.getNetworkID(), current.stream().map(IFluxDevice::getGlobalPos).collect(Collectors.toList())));
        }
        timer++;
        timer %= 20;
    }

    @Override
    protected void sortGrids(SortType sortType) {
        Comparator<IFluxDevice> comparator = Comparator.comparing((Function<IFluxDevice, Boolean>) f -> !f.isChunkLoaded())
                .thenComparing(f -> f.getDeviceType().isStorage())
                .thenComparing(f -> f.getDeviceType().isPlug())
                .thenComparing(f -> f.getDeviceType().isPoint())
                .thenComparingInt(p -> -p.getRawPriority());
        elements.sort(comparator);
        refreshCurrentPageInternal();
    }
}
