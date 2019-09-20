package fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.Coord4D;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.*;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketBatchEditing;
import fluxnetworks.common.network.PacketConnectionUpdateRequest;
import fluxnetworks.common.network.PacketNetworkUpdateRequest;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiTabConnections extends GuiTabPages<IFluxConnector> {

    private List<BatchEditButton> editButtons = new ArrayList<>();
    private List<SlidedSwitchButton> popSwitches = new ArrayList<>();
    private List<SimpleToggleButton> toggleButtons = new ArrayList<>();

    public List<IFluxConnector> batchConnections = new ArrayList<>();
    public IFluxConnector singleConnection;

    public boolean batchMode = false;

    public BatchEditButton clear, edit, disconnect;
    public NormalButton apply;
    public TextboxButton fluxName, priority, limit;
    public SlidedSwitchButton surge, unlimited, chunkLoad;
    public SimpleToggleButton editName, editPriority, editLimit, editSurge, editUnlimited, editChunkLoad;

    private int timer = 3;

    public GuiTabConnections(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 22;
        gridHeight = 19;
        gridPerPage = 7;
        elementHeight = 16;
        elementWidth = 144;
        PacketHandler.network.sendToServer(new PacketNetworkUpdateRequest.UpdateRequestMessage(network.getNetworkID(), NBTType.NETWORK_CONNECTIONS));
    }

    @Override
    protected void onElementClicked(IFluxConnector element, int mouseButton) {
        if(mouseButton == 0 && batchConnections.size() == 0 && element.isChunkLoaded()) {
            singleConnection = element;
            batchMode = false;
            main = false;
            initPopGui();
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
        for(BatchEditButton button : editButtons) {
            button.drawButton(mc, mouseX, mouseY);
        }
        super.drawForegroundLayer(mouseX, mouseY);
        if(networkValid) {
            if(batchConnections.size() > 0) {
                fontRenderer.drawString(FluxTranslate.SELECTED.t() + ": " + TextFormatting.AQUA + batchConnections.size(), 20, 10, 0xffffff);
            } else {
                fontRenderer.drawString(FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + "Smart", 20, 10, 0xffffff);
            }
            drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 88, 165, 0xffffff);
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        drawRectWithBackground(8, 13, 150, 160, 0xccffffff, 0xb0000000);
        super.drawPopupForegroundLayer(mouseX, mouseY);
        for(SlidedSwitchButton button : popSwitches) {
            button.drawButton(mc, mouseX, mouseY);
        }
        for(SimpleToggleButton button : toggleButtons) {
            button.drawButton(mc, mouseX, mouseY);
        }
        if(!batchMode) {
            drawCenteredString(fontRenderer, FluxTranslate.SINGLE_EDIT.t(), 88, 17, 0xffffff);
            drawCenteredString(fontRenderer, singleConnection.getCoords().getStringInfo(), 88, 122, 0xffffff);
        } else {
            drawCenteredString(fontRenderer, FluxTranslate.BATCH_EDIT.t(), 88, 17, 0xffffff);
            drawCenteredString(fontRenderer, FluxTranslate.EDITING.t() + " " + batchConnections.size() + " " + FluxTranslate.CONNECTIONS.t(), 88, 122, 0xffffff);
        }
        if(batchMode || !singleConnection.getConnectionType().isStorage()) {
            fontRenderer.drawString(FluxTranslate.SURGE_MODE.t(), 20, 82, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(FluxTranslate.DISABLE_LIMIT.t(), 20, 94, network.getSetting(NetworkSettings.NETWORK_COLOR));
            fontRenderer.drawString(FluxTranslate.CHUNK_LOADING.t(), 20, 106, network.getSetting(NetworkSettings.NETWORK_COLOR));
        }
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 88, 155, 0xffffff);
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
        for(SlidedSwitchButton button : popSwitches) {
            button.updatePosition(partialTicks * 4);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(3).setMain();

        if(networkValid) {
            clear = new BatchEditButton(118, 8, guiLeft, guiTop, 0, FluxTranslate.BATCH_CLEAR_BUTTON.t()).setUnclickable();
            edit = new BatchEditButton(132, 8, guiLeft, guiTop, 1, FluxTranslate.BATCH_EDIT_BUTTON.t()).setUnclickable();
            disconnect = new BatchEditButton(146, 8, guiLeft, guiTop, 2, FluxTranslate.BATCH_DISCONNECT_BUTTON.t()).setUnclickable();
            editButtons.add(clear);
            editButtons.add(edit);
            editButtons.add(disconnect);
        }
    }

    public void initPopGui() {
        popBoxes.clear();
        popButtons.clear();
        popSwitches.clear();
        toggleButtons.clear();

        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 140, 36, 12, 11));
        apply = new NormalButton(FluxTranslate.APPLY.t(), 100, 140, 36, 12, 12).setUnclickable();
        popButtons.add(apply);

        int color = network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
        if(batchMode) {
            fluxName = TextboxButton.create(this, FluxTranslate.NAME.t() + ": ", 0, fontRenderer, 20, 30, 136, 12).setOutlineColor(color);
            fluxName.setMaxStringLength(24);

            priority = TextboxButton.create(this, FluxTranslate.PRIORITY.t() + ": ", 1, fontRenderer, 20, 47, 136, 12).setOutlineColor(color).setDigitsOnly();
            priority.setMaxStringLength(5);

            limit = TextboxButton.create(this, FluxTranslate.TRANSFER_LIMIT.t() + ": ", 2, fontRenderer, 20, 64, 136, 12).setOutlineColor(color).setDigitsOnly();
            limit.setMaxStringLength(9);
        } else {
            fluxName = TextboxButton.create(this, FluxTranslate.NAME.t() + ": ", 0, fontRenderer, 18, 30, 140, 12).setOutlineColor(color);
            fluxName.setMaxStringLength(24);

            priority = TextboxButton.create(this, FluxTranslate.PRIORITY.t() + ": ", 1, fontRenderer, 18, 47, 140, 12).setOutlineColor(color).setDigitsOnly();
            priority.setMaxStringLength(5);

            limit = TextboxButton.create(this, FluxTranslate.TRANSFER_LIMIT.t() + ": ", 2, fontRenderer, 18, 64, 140, 12).setOutlineColor(color).setDigitsOnly();
            limit.setMaxStringLength(9);
        }

        if(!batchMode) {
            fluxName.setText(singleConnection.getCustomName());
            priority.setText(String.valueOf(singleConnection.getPriority()));
            limit.setText(String.valueOf(singleConnection.getCurrentLimit()));

            if(!singleConnection.getConnectionType().isStorage()) {
                surge = new SlidedSwitchButton(140, 82, 1, guiLeft, guiTop, singleConnection.getSurgeMode());
                unlimited = new SlidedSwitchButton(140, 94, 2, guiLeft, guiTop, singleConnection.getDisableLimit());
                chunkLoad = new SlidedSwitchButton(140, 106, 3, guiLeft, guiTop, singleConnection.isForcedLoading());
                popSwitches.add(surge);
                popSwitches.add(unlimited);
                popSwitches.add(chunkLoad);
            }
        } else {
            priority.setText(String.valueOf(0));
            limit.setText(String.valueOf(0));

            editName = new SimpleToggleButton(10, 33, guiLeft, guiTop, 0);
            editPriority = new SimpleToggleButton(10, 50, guiLeft, guiTop, 1);
            editLimit = new SimpleToggleButton(10, 67, guiLeft, guiTop, 2);

            editSurge = new SimpleToggleButton(10, 82, guiLeft, guiTop, 3);
            editUnlimited = new SimpleToggleButton(10, 94, guiLeft, guiTop, 4);
            editChunkLoad = new SimpleToggleButton(10, 106, guiLeft, guiTop, 5);

            toggleButtons.add(editName);
            toggleButtons.add(editPriority);
            toggleButtons.add(editLimit);

            toggleButtons.add(editSurge);
            toggleButtons.add(editUnlimited);
            toggleButtons.add(editChunkLoad);

            surge = new SlidedSwitchButton(140, 82, 1, guiLeft, guiTop, false);
            unlimited = new SlidedSwitchButton(140, 94, 2, guiLeft, guiTop, false);
            chunkLoad = new SlidedSwitchButton(140, 106, 3, guiLeft, guiTop, false);
            popSwitches.add(surge);
            popSwitches.add(unlimited);
            popSwitches.add(chunkLoad);
        }

        popBoxes.add(fluxName);
        popBoxes.add(priority);
        popBoxes.add(limit);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        editButtons.clear();
        super.setWorldAndResolution(mc, width, height);
        if(!main) {
            initPopGui();
        }
    }

    @Override
    public void renderElement(IFluxConnector element, int x, int y) {
        GlStateManager.pushMatrix();
        int fontColor = 0xffffff;
        if(batchConnections.size() > 0) {
            if (batchConnections.contains(element)) {
                drawRect(x - 5, y + 1, x - 3, y + elementHeight - 1, 0xccffffff);
                drawRect(x + elementWidth + 3, y + 1, x + elementWidth + 5, y + elementHeight - 1, 0xccffffff);
                drawColorRect(x, y, elementHeight, elementWidth, element.getConnectionType().color | 0xff000000);
            } else {
                drawRect(x - 5, y + 1, x - 3, y + elementHeight - 1, 0xaa606060);
                drawRect(x + elementWidth + 3, y + 1, x + elementWidth + 5, y + elementHeight - 1, 0xaa606060);
                drawColorRect(x, y, elementHeight, elementWidth, element.getConnectionType().color | 0x60000000);
                fontColor = 0xd0d0d0;
            }
        } else {
            drawColorRect(x, y, elementHeight, elementWidth, element.getConnectionType().color | 0xff000000);
        }
        renderItemStack(element.getDisplayStack(), x, y);
        if(element.isChunkLoaded()) {
            fontRenderer.drawString(element.getCustomName(), x + 20, y + 1, fontColor);
            GlStateManager.scale(0.625, 0.625, 0.625);
            fontRenderer.drawString(getTransferInfo(element.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), element.getChange()), (int) ((x + 20) * 1.6), (int) ((y + 10) * 1.6), fontColor);
            GlStateManager.scale(1.6, 1.6, 1.6);
        } else {
            fontRenderer.drawString(element.getCustomName(), x + 20, y + 4, 0x808080);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderElementTooltip(IFluxConnector element, int mouseX, int mouseY) {
        if(main) {
            GlStateManager.pushMatrix();
            drawHoverTooltip(getFluxInfo(element), mouseX + 4, mouseY - 16);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        for(BatchEditButton button : editButtons) {
            if(button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                switch (button.id) {
                    case 0:
                        batchConnections.clear();
                        clear.clickable = false;
                        edit.clickable = false;
                        disconnect.clickable = false;
                        break;
                    case 1:
                        batchMode = true;
                        main = false;
                        initPopGui();
                        break;
                    case 2:
                        List<Coord4D> list = batchConnections.stream().map(IFluxConnector::getCoords).collect(Collectors.toList());
                        boolean[] b = {false, false, false, false, false, false, true};
                        PacketHandler.network.sendToServer(new PacketBatchEditing.BatchEditingMessage(network.getNetworkID(), list, new NBTTagCompound(), b));
                        break;
                }
            }
        }
    }

    @Override
    protected void mousePopupClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mousePopupClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 11) {
                        main = true;
                    }
                    if(button.id == 12) {
                        List<Coord4D> list;
                        boolean[] b = {true, true, true, true, true, true, false};
                        if(batchMode) {
                            list = batchConnections.stream().map(IFluxConnector::getCoords).collect(Collectors.toList());
                            b[0] = editName.on;
                            b[1] = editPriority.on;
                            b[2] = editLimit.on;
                            b[3] = editSurge.on;
                            b[4] = editUnlimited.on;
                            b[5] = editChunkLoad.on; // Do!
                        } else {
                            list = Lists.newArrayList(singleConnection.getCoords());
                        }
                        NBTTagCompound tag = FluxUtils.getBatchEditingTag(fluxName, priority, limit, surge, unlimited, chunkLoad);
                        PacketHandler.network.sendToServer(new PacketBatchEditing.BatchEditingMessage(network.getNetworkID(), list, tag, b));
                    }
                }
            }
            for(SlidedSwitchButton s : popSwitches) {
                if (s.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    s.switchButton();
                    if(!batchMode) {
                        apply.clickable = true;
                    }
                }
            }
            for(SimpleToggleButton s : toggleButtons) {
                if(s.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    s.on = !s.on;
                    apply.clickable = toggleButtons.stream().anyMatch(b -> b.on);
                }
            }
            if(main) {
                backToMain();
            }
        }
    }

    @Override
    protected void keyTypedPop(char c, int k) throws IOException {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(popBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                backToMain();
            }
        }
        for(TextboxButton text : popBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
                if(!batchMode) {
                    apply.clickable = true;
                }
            }
        }
    }

    @Override
    public void updateScreen() {
        if(timer == 4) {
            refreshPages(network.getSetting(NetworkSettings.ALL_CONNECTORS));
        }
        if(timer % 5 == 0) {
            PacketHandler.network.sendToServer(new PacketConnectionUpdateRequest.ConnectionRequestMessage(network.getNetworkID(), current.stream().map(IFluxConnector::getCoords).collect(Collectors.toList())));
        }
        timer++;
        timer %= 100;
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS) {
            backToMain();
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(network.getSetting(NetworkSettings.ALL_CONNECTORS));
        }
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS_2) {
            backToMain();
            elements.removeAll(batchConnections);
            batchConnections.clear();
            clear.clickable = false;
            edit.clickable = false;
            disconnect.clickable = false;
            refreshPages(network.getSetting(NetworkSettings.ALL_CONNECTORS));
            if(elements.stream().noneMatch(f -> f.getCoords().equals(tileEntity.getCoords()))) {
                FMLCommonHandler.instance().showGuiScreen(new GuiTabSelection(player, tileEntity));
            }
        }
    }

    @Override
    protected void sortGrids(SortType sortType) {
        elements.sort(Comparator.comparing(IFluxConnector::isChunkLoaded).reversed().
                thenComparing(f -> f.getConnectionType().isStorage()).
                thenComparing(f -> f.getConnectionType().canAddEnergy()).
                thenComparing(f -> f.getConnectionType().canRemoveEnergy()).
                thenComparing(p -> -p.getPriority()));
        refreshCurrentPage();
    }
}
