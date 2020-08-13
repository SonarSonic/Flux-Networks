package sonar.fluxnetworks.client.gui.popups;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.utils.Coord4D;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SimpleToggleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.tab.GuiTabConnections;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.BatchEditingPacket;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PopUpConnectionEdit extends PopUpCore<GuiTabConnections> {

    public NormalButton apply;
    public FluxTextWidget fluxName, priority, limit;
    public SlidedSwitchButton surge, unlimited, chunkLoad;
    public SimpleToggleButton editName, editPriority, editLimit, editSurge, editUnlimited, editChunkLoad;

    public boolean batchMode;

    private List<SimpleToggleButton> toggleButtons = new ArrayList<>();

    public PopUpConnectionEdit(GuiTabConnections host, boolean batchMode, PlayerEntity player, INetworkConnector connector) {
        super(host, player, connector);
        this.batchMode = batchMode;
    }

    @Override
    public void init() {
        super.init();
        toggleButtons.clear();

        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 140, 36, 12, 11));
        apply = new NormalButton(FluxTranslate.APPLY.t(), 100, 140, 36, 12, 12).setUnclickable();
        popButtons.add(apply);

        int color = host.network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
        if(batchMode) {
            fluxName = FluxTextWidget.create(FluxTranslate.NAME.t() + ": ", font, guiLeft + 20, guiTop + 30, 136, 12).setOutlineColor(color);
            fluxName.setMaxStringLength(24);
            addButton(fluxName);


            priority = FluxTextWidget.create(FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 20, guiTop + 47, 136, 12).setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
            priority.setMaxStringLength(5);
            priority.setText(String.valueOf(0));
            addButton(priority);

            limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 20, guiTop + 64, 136, 12).setOutlineColor(color).setDigitsOnly().setMaxValue(Long.MAX_VALUE);
            limit.setMaxStringLength(9);
            limit.setText(String.valueOf(0));
            addButton(limit);

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
        } else {
            fluxName = FluxTextWidget.create( FluxTranslate.NAME.t() + ": ", font, guiLeft + 18, guiTop + 30, 140, 12).setOutlineColor(color);
            fluxName.setMaxStringLength(24);
            fluxName.setText(host.singleConnection.getCustomName());
            fluxName.setResponder(string -> apply.clickable = true);
            addButton(fluxName);

            priority = FluxTextWidget.create( FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 18, guiTop + 47, 140, 12).setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
            priority.setMaxStringLength(5);
            priority.setText(String.valueOf(host.singleConnection.getPriority()));
            priority.setResponder(string -> apply.clickable = true);
            addButton(priority);

            limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 18, guiTop + 64, 140, 12).setOutlineColor(color).setDigitsOnly().setMaxValue(Long.MAX_VALUE);
            limit.setMaxStringLength(9);
            limit.setText(String.valueOf(host.singleConnection.getCurrentLimit()));
            limit.setResponder(string -> apply.clickable = true);
            addButton(limit);

            surge = new SlidedSwitchButton(140, 82, 1, guiLeft, guiTop, host.singleConnection.getSurgeMode());
            unlimited = new SlidedSwitchButton(140, 94, 2, guiLeft, guiTop, host.singleConnection.getDisableLimit());

            popSwitches.add(surge);
            popSwitches.add(unlimited);

            if(!host.singleConnection.getConnectionType().isStorage()) {
                chunkLoad = new SlidedSwitchButton(140, 106, 3, guiLeft, guiTop, host.singleConnection.isForcedLoading());
                popSwitches.add(chunkLoad);
            }
        }

    }

    @Override
    public void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        for(SlidedSwitchButton button : popSwitches) {
            button.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop);
        }
        for(SimpleToggleButton button : toggleButtons) {
            button.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop);
        }

        if(!batchMode) {
            drawCenteredString(matrixStack, font, FluxTranslate.SINGLE_EDIT.t(), 88, 14, 0xffffff);
            drawCenteredString(matrixStack, font, host.singleConnection.getCoords().getStringInfo(), 88, 122, 0xffffff);
        } else {
            drawCenteredString(matrixStack, font, FluxTranslate.BATCH_EDIT.t(), 88, 14, 0xffffff);
            drawCenteredString(matrixStack, font, FluxTranslate.EDITING.t() + " " + host.batchConnections.size() + " " + FluxTranslate.CONNECTIONS.t(), 88, 122, 0xffffff);
        }
        font.drawString(matrixStack, FluxTranslate.SURGE_MODE.t(), 20, 82, host.network.getSetting(NetworkSettings.NETWORK_COLOR));
        font.drawString(matrixStack, FluxTranslate.DISABLE_LIMIT.t(), 20, 94, host.network.getSetting(NetworkSettings.NETWORK_COLOR));
        if(batchMode || !host.singleConnection.getConnectionType().isStorage()) {
            font.drawString(matrixStack, FluxTranslate.CHUNK_LOADING.t(), 20, 106, host.network.getSetting(NetworkSettings.NETWORK_COLOR));
        }
        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxNetworks.PROXY.getFeedback(false).getInfo(), 88, 155, 0xffffff);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.clickable && button.isMouseHovered(minecraft, (int)mouseX - guiLeft, (int)mouseY - guiTop)) {
                    if(button.id == 11) {
                        host.closePopUp();
                        return true;
                    }
                    if(button.id == 12) {
                        List<Coord4D> list;
                        boolean[] b = {true, true, true, true, true, true, false};
                        if(batchMode) {
                            list = host.batchConnections.stream().map(IFluxConnector::getCoords).collect(Collectors.toList());
                            b[0] = editName.on;
                            b[1] = editPriority.on;
                            b[2] = editLimit.on;
                            b[3] = editSurge.on;
                            b[4] = editUnlimited.on;
                            b[5] = editChunkLoad.on; // Do!
                        } else {
                            list = Lists.newArrayList(host.singleConnection.getCoords());
                        }
                        CompoundNBT tag = FluxUtils.getBatchEditingTag(fluxName, priority, limit, surge, unlimited, chunkLoad);
                        PacketHandler.CHANNEL.sendToServer(new BatchEditingPacket(host.network.getNetworkID(), list, tag, b));
                        return true;
                    }
                }
            }
            for(SlidedSwitchButton s : popSwitches) {
                if (s.isMouseHovered(minecraft, (int)mouseX - guiLeft, (int)mouseY - guiTop)) {
                    s.switchButton();
                    if(!batchMode) {
                        apply.clickable = true;
                    }
                    return true;
                }
            }
            for(SimpleToggleButton s : toggleButtons) {
                if(s.isMouseHovered(minecraft, (int)mouseX - guiLeft, (int)mouseY - guiTop)) {
                    s.on = !s.on;
                    apply.clickable = toggleButtons.stream().anyMatch(b -> b.on);
                    return true;
                }
            }
        }
        return false;
    }
}
