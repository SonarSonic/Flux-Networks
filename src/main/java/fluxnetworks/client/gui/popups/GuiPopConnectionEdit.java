package fluxnetworks.client.gui.popups;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.Coord4D;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.SimpleToggleButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.client.gui.tab.GuiTabConnections;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketBatchEditing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiPopConnectionEdit extends GuiPopCore<GuiTabConnections> {

    public NormalButton apply;
    public TextboxButton fluxName, priority, limit;
    public SlidedSwitchButton surge, unlimited, chunkLoad;
    public SimpleToggleButton editName, editPriority, editLimit, editSurge, editUnlimited, editChunkLoad;

    public boolean batchMode;

    private List<SimpleToggleButton> toggleButtons = new ArrayList<>();

    public GuiPopConnectionEdit(GuiTabConnections host, boolean batchMode, EntityPlayer player, INetworkConnector connector) {
        super(host, player, connector);
        this.batchMode = batchMode;
    }

    @Override
    public void initGui() {
        super.initGui();
        toggleButtons.clear();

        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 140, 36, 12, 11));
        apply = new NormalButton(FluxTranslate.APPLY.t(), 100, 140, 36, 12, 12).setUnclickable();
        popButtons.add(apply);

        int color = host.network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
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
            fluxName.setText(host.singleConnection.getCustomName());
            priority.setText(String.valueOf(host.singleConnection.getPriority()));
            limit.setText(String.valueOf(host.singleConnection.getCurrentLimit()));

            surge = new SlidedSwitchButton(140, 82, 1, guiLeft, guiTop, host.singleConnection.getSurgeMode());
            unlimited = new SlidedSwitchButton(140, 94, 2, guiLeft, guiTop, host.singleConnection.getDisableLimit());

            popSwitches.add(surge);
            popSwitches.add(unlimited);

            if(!host.singleConnection.getConnectionType().isStorage()) {
                chunkLoad = new SlidedSwitchButton(140, 106, 3, guiLeft, guiTop, host.singleConnection.isForcedLoading());
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
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawRectWithBackground(8, 13, 150, 160, 0xccffffff, 0xb0000000);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        for(SlidedSwitchButton button : popSwitches) {
            button.drawButton(mc, mouseX, mouseY, guiLeft, guiTop);
        }
        for(SimpleToggleButton button : toggleButtons) {
            button.drawButton(mc, mouseX, mouseY, guiLeft, guiTop);
        }
        if(!batchMode) {
            drawCenteredString(fontRenderer, FluxTranslate.SINGLE_EDIT.t(), 88, 17, 0xffffff);
            drawCenteredString(fontRenderer, host.singleConnection.getCoords().getStringInfo(), 88, 122, 0xffffff);
        } else {
            drawCenteredString(fontRenderer, FluxTranslate.BATCH_EDIT.t(), 88, 17, 0xffffff);
            drawCenteredString(fontRenderer, FluxTranslate.EDITING.t() + " " + host.batchConnections.size() + " " + FluxTranslate.CONNECTIONS.t(), 88, 122, 0xffffff);
        }
        fontRenderer.drawString(FluxTranslate.SURGE_MODE.t(), 20, 82, host.network.getSetting(NetworkSettings.NETWORK_COLOR));
        fontRenderer.drawString(FluxTranslate.DISABLE_LIMIT.t(), 20, 94, host.network.getSetting(NetworkSettings.NETWORK_COLOR));
        if(batchMode || !host.singleConnection.getConnectionType().isStorage()) {
            fontRenderer.drawString(FluxTranslate.CHUNK_LOADING.t(), 20, 106, host.network.getSetting(NetworkSettings.NETWORK_COLOR));
        }
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback(false).getInfo(), 88, 155, 0xffffff);
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 11) {
                        host.closePopUp();
                        return;
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
                        NBTTagCompound tag = FluxUtils.getBatchEditingTag(fluxName, priority, limit, surge, unlimited, chunkLoad);
                        PacketHandler.network.sendToServer(new PacketBatchEditing.BatchEditingMessage(host.network.getNetworkID(), list, tag, b));
                        return;
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
        }
    }



    @Override
    public void keyTyped(char c, int k) throws IOException {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(popBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                host.closePopUp();
            }else{
                popBoxes.stream().forEach(f -> f.setFocused(false));
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
}
