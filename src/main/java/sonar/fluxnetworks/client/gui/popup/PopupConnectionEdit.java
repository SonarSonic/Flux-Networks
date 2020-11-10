package sonar.fluxnetworks.client.gui.popup;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SimpleToggleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabConnections;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.CEditConnectionsMessage;
import sonar.fluxnetworks.common.network.NetworkHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PopupConnectionEdit extends PopupCore<GuiTabConnections> {

    public NormalButton apply;
    public FluxTextWidget fluxName, priority, limit;
    public SlidedSwitchButton surgeMode, disableLimit, chunkLoading;
    public SimpleToggleButton editName, editPriority, editLimit, editSurgeMode, editDisableLimit, editChunkLoading;

    public boolean batchMode;

    private final List<SimpleToggleButton> toggleButtons = new ArrayList<>();

    public PopupConnectionEdit(GuiTabConnections host, PlayerEntity player, boolean batchMode) {
        super(host, player);
        this.batchMode = batchMode;
    }

    @Override
    public void init() {
        super.init();
        toggleButtons.clear();

        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 146, 36, 12, 11));
        apply = new NormalButton(FluxTranslate.APPLY.t(), 100, 146, 36, 12, 12).setUnclickable();
        popButtons.add(apply);

        int color = host.network.getNetworkColor() | 0xff000000;
        if (batchMode) {
            fluxName = FluxTextWidget.create(FluxTranslate.NAME.t() + ": ", font, guiLeft + 20, guiTop + 30, 136, 12)
                    .setOutlineColor(color);
            fluxName.setMaxStringLength(24);
            addButton(fluxName);


            priority = FluxTextWidget.create(FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 20, guiTop + 47, 136, 12)
                    .setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
            priority.setMaxStringLength(5);
            priority.setText(String.valueOf(0));
            addButton(priority);

            limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 20, guiTop + 64, 136, 12)
                    .setOutlineColor(color).setDigitsOnly().setMaxValue(Long.MAX_VALUE);
            limit.setMaxStringLength(9);
            limit.setText(String.valueOf(0));
            addButton(limit);

            editName = new SimpleToggleButton(10, 33, 0);
            editPriority = new SimpleToggleButton(10, 50, 1);
            editLimit = new SimpleToggleButton(10, 67, 2);

            editSurgeMode = new SimpleToggleButton(10, 82, 3);
            editDisableLimit = new SimpleToggleButton(10, 94, 4);
            editChunkLoading = new SimpleToggleButton(10, 106, 5);

            toggleButtons.add(editName);
            toggleButtons.add(editPriority);
            toggleButtons.add(editLimit);

            toggleButtons.add(editSurgeMode);
            toggleButtons.add(editDisableLimit);
            toggleButtons.add(editChunkLoading);

            surgeMode = new SlidedSwitchButton(140, 82, 1, guiLeft, guiTop, false);
            disableLimit = new SlidedSwitchButton(140, 94, 2, guiLeft, guiTop, false);
            chunkLoading = new SlidedSwitchButton(140, 106, 3, guiLeft, guiTop, false);
            popSwitches.add(surgeMode);
            popSwitches.add(disableLimit);
            popSwitches.add(chunkLoading);
        } else {
            fluxName = FluxTextWidget.create(FluxTranslate.NAME.t() + ": ", font, guiLeft + 18, guiTop + 30, 140, 12)
                    .setOutlineColor(color);
            fluxName.setMaxStringLength(24);
            fluxName.setText(host.singleConnection.getCustomName());
            fluxName.setResponder(string -> apply.clickable = true);
            addButton(fluxName);

            priority = FluxTextWidget.create(FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 18, guiTop + 47, 140, 12)
                    .setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
            priority.setMaxStringLength(5);
            priority.setText(String.valueOf(host.singleConnection.getRawPriority()));
            priority.setResponder(string -> apply.clickable = true);
            addButton(priority);

            limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 18, guiTop + 64, 140, 12)
                    .setOutlineColor(color).setDigitsOnly().setMaxValue(Long.MAX_VALUE);
            limit.setMaxStringLength(9);
            limit.setText(String.valueOf(host.singleConnection.getRawLimit()));
            limit.setResponder(string -> apply.clickable = true);
            addButton(limit);

            surgeMode = new SlidedSwitchButton(140, 82, 1, guiLeft, guiTop, host.singleConnection.getSurgeMode());
            disableLimit = new SlidedSwitchButton(140, 94, 2, guiLeft, guiTop, host.singleConnection.getDisableLimit());

            popSwitches.add(surgeMode);
            popSwitches.add(disableLimit);

            if (!host.singleConnection.getDeviceType().isStorage()) {
                chunkLoading = new SlidedSwitchButton(140, 106, 3, guiLeft, guiTop, host.singleConnection.isForcedLoading());
                popSwitches.add(chunkLoading);
            }
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        for (SlidedSwitchButton button : popSwitches) {
            button.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop);
        }
        for (SimpleToggleButton button : toggleButtons) {
            button.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop);
        }

        if (!batchMode) {
            drawCenterText(matrixStack, FluxTranslate.SINGLE_EDIT.t(), 88, 14, 0xffffff);
            drawCenterText(matrixStack, FluxUtils.getDisplayPos(host.singleConnection.getGlobalPos()), 88, 121, 0xffffff);
            drawCenterText(matrixStack, FluxUtils.getDisplayDim(host.singleConnection.getGlobalPos()), 88, 130, 0xffffff);
        } else {
            drawCenterText(matrixStack, FluxTranslate.BATCH_EDIT.t(), 88, 14, 0xffffff);
            drawCenterText(matrixStack, FluxTranslate.EDITING_CONNECTIONS.format(host.batchConnections.size()), 88, 122, 0xffffff);
        }
        font.drawString(matrixStack, FluxTranslate.SURGE_MODE.t(), 20, 82, host.network.getNetworkColor());
        font.drawString(matrixStack, FluxTranslate.DISABLE_LIMIT.t(), 20, 94, host.network.getNetworkColor());
        if (batchMode || !host.singleConnection.getDeviceType().isStorage()) {
            font.drawString(matrixStack, FluxTranslate.CHUNK_LOADING.t(), 20, 106, host.network.getNetworkColor());
        }
        drawCenterText(matrixStack, TextFormatting.RED + FluxClientCache.getFeedbackText(), 88, 155, 0xffffff);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (NormalButton button : popButtons) {
                if (button.clickable && button.isMouseHovered(minecraft, (int) mouseX - guiLeft, (int) mouseY - guiTop)) {
                    if (button.id == 11) {
                        host.closePopUp();
                        return true;
                    }
                    if (button.id == 12) {
                        List<GlobalPos> list;
                        int flags = 0;
                        if (batchMode) {
                            list = host.batchConnections.stream().map(IFluxDevice::getGlobalPos).collect(Collectors.toList());
                            if (editName.checked) {
                                flags |= FluxConstants.FLAG_EDIT_NAME;
                            }
                            if (editPriority.checked) {
                                flags |= FluxConstants.FLAG_EDIT_PRIORITY;
                            }
                            if (editLimit.checked) {
                                flags |= FluxConstants.FLAG_EDIT_LIMIT;
                            }
                            if (editSurgeMode.checked) {
                                flags |= FluxConstants.FLAG_EDIT_SURGE_MODE;
                            }
                            if (editDisableLimit.checked) {
                                flags |= FluxConstants.FLAG_EDIT_DISABLE_LIMIT;
                            }
                            if (editChunkLoading.checked) {
                                flags |= FluxConstants.FLAG_EDIT_CHUNK_LOADING;
                            }
                        } else {
                            list = Lists.newArrayList(host.singleConnection.getGlobalPos());
                            flags |= FluxConstants.FLAG_EDIT_NAME | FluxConstants.FLAG_EDIT_PRIORITY
                                    | FluxConstants.FLAG_EDIT_LIMIT | FluxConstants.FLAG_EDIT_SURGE_MODE
                                    | FluxConstants.FLAG_EDIT_DISABLE_LIMIT | FluxConstants.FLAG_EDIT_CHUNK_LOADING;
                        }
                        //CompoundNBT tag = FluxUtils.getBatchEditingTag(fluxName, priority, limit, surgeMode, disableLimit, chunkLoading);
                        NetworkHandler.INSTANCE.sendToServer(new CEditConnectionsMessage(host.network.getNetworkID(), list, flags,
                                fluxName.getText(), priority.getIntegerFromText(true), limit.getLongFromText(false),
                                surgeMode != null && surgeMode.toggled,
                                disableLimit != null && disableLimit.toggled,
                                chunkLoading != null && chunkLoading.toggled));
                        //PacketHandler.CHANNEL.sendToServer(new CEditConnectionsMessage(host.network.getNetworkID(), list, tag, b));
                        return true;
                    }
                }
            }
            for (SlidedSwitchButton s : popSwitches) {
                if (s.isMouseHovered(minecraft, (int) mouseX - guiLeft, (int) mouseY - guiTop)) {
                    s.switchButton();
                    if (!batchMode) {
                        apply.clickable = true;
                    }
                    return true;
                }
            }
            for (SimpleToggleButton s : toggleButtons) {
                if (s.isMouseHovered(minecraft, (int) mouseX - guiLeft, (int) mouseY - guiTop)) {
                    s.checked = !s.checked;
                    apply.clickable = toggleButtons.stream().anyMatch(b -> b.checked);
                    return true;
                }
            }
        }
        return false;
    }
}
