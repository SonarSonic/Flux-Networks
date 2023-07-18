package sonar.fluxnetworks.client.gui.popup;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiPopupCore;
import sonar.fluxnetworks.client.gui.button.*;
import sonar.fluxnetworks.client.gui.tab.GuiTabConnections;
import sonar.fluxnetworks.common.connection.TransferHandler;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.register.ClientMessages;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class PopupConnectionEdit extends GuiPopupCore<GuiTabConnections> {

    public SimpleButton mCancel;
    public SimpleButton mApply;

    public FluxEditBox mCustomName;
    public FluxEditBox mPriority;
    public FluxEditBox mLimit;

    public SwitchButton mSurgeMode;
    public SwitchButton mDisableLimit;
    public SwitchButton mChunkLoading;

    public Checkbox mEditCustomName;
    public Checkbox mEditPriority;
    public Checkbox mEditLimit;
    public Checkbox mEditSurgeMode;
    public Checkbox mEditDisableLimit;
    public Checkbox mEditChunkLoading;

    public PopupConnectionEdit(GuiTabConnections host) {
        super(host);
    }

    @Override
    public void init() {
        super.init();

        mCancel = new SimpleButton(this, leftPos + (imageWidth / 2) - 12 - 48, topPos + 146, 48, 12,
                FluxTranslate.CANCEL.get());
        mButtons.add(mCancel);
        mApply = new SimpleButton(this, leftPos + (imageWidth / 2) + 12, topPos + 146, 48, 12,
                FluxTranslate.APPLY.get());
        mApply.setClickable(false);
        mButtons.add(mApply);

        int color = mHost.getNetwork().getNetworkColor() | 0xFF000000;
        IFluxDevice singleConnection = mHost.mSelected.size() == 1 ? mHost.mSelected.iterator().next() : null;
        //if (mHost.mBatchMode) {
        mCustomName = FluxEditBox.create(FluxTranslate.NAME.get() + ": ", font, leftPos + 20, topPos + 30, 136, 12)
                .setOutlineColor(color);
        mCustomName.setMaxLength(TileFluxDevice.MAX_CUSTOM_NAME_LENGTH);
        if (singleConnection != null) {
            mCustomName.setValue(singleConnection.getCustomName());
        }
        addRenderableWidget(mCustomName);

        mPriority = FluxEditBox.create(FluxTranslate.PRIORITY.get() + ": ", font, leftPos + 20, topPos + 47, 136,
                        12)
                .setOutlineColor(color)
                .setDigitsOnly()
                .setAllowNegatives(true);
        mPriority.setMaxLength(5);
        if (singleConnection != null) {
            mPriority.setValue(Integer.toString(singleConnection.getRawPriority()));
        } else {
            mPriority.setValue(Integer.toString(0));
        }
        addRenderableWidget(mPriority);

        mLimit = FluxEditBox.create(FluxTranslate.TRANSFER_LIMIT.get() + ": ", font, leftPos + 20, topPos + 64,
                        136, 12)
                .setOutlineColor(color)
                .setDigitsOnly()
                .setMaxValue(Long.MAX_VALUE);
        mLimit.setMaxLength(15);
        if (singleConnection != null) {
            mLimit.setValue(Long.toString(singleConnection.getRawLimit()));
        } else {
            mLimit.setValue(Integer.toString(0));
        }
        addRenderableWidget(mLimit);

        mEditCustomName = new Checkbox(this, leftPos + 10, topPos + 33);
        mEditPriority = new Checkbox(this, leftPos + 10, topPos + 50);
        mEditLimit = new Checkbox(this, leftPos + 10, topPos + 67);

        mEditSurgeMode = new Checkbox(this, leftPos + 10, topPos + 82);
        mEditDisableLimit = new Checkbox(this, leftPos + 10, topPos + 94);
        mEditChunkLoading = new Checkbox(this, leftPos + 10, topPos + 106);

        mButtons.add(mEditCustomName);
        mButtons.add(mEditPriority);
        mButtons.add(mEditLimit);

        mButtons.add(mEditSurgeMode);
        mButtons.add(mEditDisableLimit);
        mButtons.add(mEditChunkLoading);

        mSurgeMode = new SwitchButton(this, leftPos + 140, topPos + 82,
                singleConnection != null && singleConnection.getSurgeMode(), color);
        mDisableLimit = new SwitchButton(this, leftPos + 140, topPos + 94,
                singleConnection != null && singleConnection.getDisableLimit(), color);
        mChunkLoading = new SwitchButton(this, leftPos + 140, topPos + 106,
                singleConnection != null && singleConnection.isForcedLoading(), color);
        mChunkLoading.setClickable(FluxConfig.enableChunkLoading &&
                (singleConnection == null || !singleConnection.getDeviceType().isStorage()));

        mButtons.add(mSurgeMode);
        mButtons.add(mDisableLimit);
        mButtons.add(mChunkLoading);
        /*} else {
            mCustomName = FluxEditBox.create(FluxTranslate.NAME.get() + ": ", font, leftPos + 18, topPos + 30, 140, 12)
                    .setOutlineColor(color);
            mCustomName.setMaxLength(TileFluxDevice.MAX_CUSTOM_NAME_LENGTH);
            mCustomName.setValue(mHost.mSingleConnection.getCustomName());
            addRenderableWidget(mCustomName);

            mPriority = FluxEditBox.create(FluxTranslate.PRIORITY.get() + ": ", font, leftPos + 18, topPos + 47, 140,
                            12)
                    .setOutlineColor(color)
                    .setDigitsOnly()
                    .setAllowNegatives(true);
            mPriority.setMaxLength(5);
            mPriority.setValue(Integer.toString(mHost.mSingleConnection.getRawPriority()));
            addRenderableWidget(mPriority);

            mLimit = FluxEditBox.create(FluxTranslate.TRANSFER_LIMIT.get() + ": ", font, leftPos + 18, topPos + 64,
                            140, 12)
                    .setOutlineColor(color)
                    .setDigitsOnly()
                    .setMaxValue(Long.MAX_VALUE);
            mLimit.setMaxLength(15);
            mLimit.setValue(Long.toString(mHost.mSingleConnection.getRawLimit()));
            addRenderableWidget(mLimit);

            mSurgeMode = new SwitchButton(this, leftPos + 140, topPos + 82,
                    mHost.mSingleConnection.getSurgeMode());
            mDisableLimit = new SwitchButton(this, leftPos + 140, topPos + 94,
                    mHost.mSingleConnection.getDisableLimit());

            mButtons.add(mSurgeMode);
            mButtons.add(mDisableLimit);

            if (!mHost.mSingleConnection.getDeviceType().isStorage()) {
                mChunkLoading = new SwitchButton(this, leftPos + 140, topPos + 106,
                        mHost.mSingleConnection.isForcedLoading());
                mButtons.add(mChunkLoading);
            }
        }*/
    }

    @Override
    public void drawForegroundLayer(@Nonnull GuiGraphics gr, int mouseX, int mouseY, float deltaTicks) {
        super.drawForegroundLayer(gr, mouseX, mouseY, deltaTicks);
        /*if (!mHost.mBatchMode) {
            drawCenteredString(poseStack, font, FluxTranslate.SINGLE_EDIT.get(),
                    leftPos + 88, topPos + 14, 0xffffff);
            drawCenteredString(poseStack, font, FluxUtils.getDisplayPos(mHost.mSingleConnection.getGlobalPos()),
                    leftPos + 88, topPos + 121, 0xffffff);
            drawCenteredString(poseStack, font, FluxUtils.getDisplayDim(mHost.mSingleConnection.getGlobalPos()),
                    leftPos + 88, topPos + 130, 0xffffff);
        } else {*/
        gr.drawCenteredString(font, FluxTranslate.BATCH_EDIT.get(),
                leftPos + 88, topPos + 14, 0xffffff);
        gr.drawCenteredString(font,
                FluxTranslate.EDITING_CONNECTIONS.format(mHost.mSelected.size()),
                leftPos + 88, topPos + 122, 0xffffff);
        //}
        gr.drawString(font, FluxTranslate.SURGE_MODE.get(),
                leftPos + 20, topPos + 82, mHost.getNetwork().getNetworkColor());
        gr.drawString(font, FluxTranslate.DISABLE_LIMIT.get(),
                leftPos + 20, topPos + 94, mHost.getNetwork().getNetworkColor());
        if (mChunkLoading != null) {
            gr.drawString(font, FluxTranslate.CHUNK_LOADING.get(),
                    leftPos + 20, topPos + 106, mHost.getNetwork().getNetworkColor());
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }
        if (button instanceof SwitchButton switchButton) {
            switchButton.toggle();
            /*if (!mHost.mBatchMode) {
                mApply.setClickable(true);
            }*/
        } else if (button instanceof Checkbox checkbox) {
            checkbox.toggle();
            mApply.setClickable(mButtons.stream().filter(b -> b instanceof Checkbox)
                    .anyMatch(t -> ((Checkbox) t).isChecked()));
        }
        if (button == mCancel) {
            mHost.closePopup();
        } else if (button == mApply) {
            List<GlobalPos> list = mHost.mSelected.stream().map(IFluxDevice::getGlobalPos).collect(Collectors.toList());
            CompoundTag tag = new CompoundTag();
            //if (mBatchMode) {
            if (mEditCustomName.isChecked()) {
                tag.putString(FluxConstants.CUSTOM_NAME, mCustomName.getValue());
            }
            if (mEditPriority.isChecked()) {
                int priority = Mth.clamp(mPriority.getValidInt(),
                        TransferHandler.PRI_USER_MIN, TransferHandler.PRI_USER_MAX);
                tag.putInt(FluxConstants.PRIORITY, priority);
            }
            if (mEditLimit.isChecked()) {
                long limit = mLimit.getValidLong();
                tag.putLong(FluxConstants.LIMIT, limit);
            }
            if (mEditSurgeMode.isChecked()) {
                tag.putBoolean(FluxConstants.SURGE_MODE, mSurgeMode.isChecked());
            }
            if (mEditDisableLimit.isChecked()) {
                tag.putBoolean(FluxConstants.DISABLE_LIMIT, mDisableLimit.isChecked());
            }
            if (mEditChunkLoading.isChecked()) {
                tag.putBoolean(FluxConstants.FORCED_LOADING, mChunkLoading.isChecked());
            }
            ClientMessages.editConnection(mHost.getToken(), mHost.getNetwork(), list, tag);
            mApply.setClickable(false);
            /*} else {
                list = Lists.newArrayList(host.singleConnection.getGlobalPos());
                flags |= FluxConstants.FLAG_EDIT_NAME | FluxConstants.FLAG_EDIT_PRIORITY
                        | FluxConstants.FLAG_EDIT_LIMIT | FluxConstants.FLAG_EDIT_SURGE_MODE
                        | FluxConstants.FLAG_EDIT_DISABLE_LIMIT | FluxConstants.FLAG_EDIT_CHUNK_LOADING;
            }*/
            //CompoundNBT tag = FluxUtils.getBatchEditingTag(fluxName, priority, limit, surgeMode,
            // disableLimit, chunkLoading);
            /*C2SNetMsg.editConnections(host.network.getNetworkID(), list, flags,
                    fluxName.getText(), priority.getIntegerFromText(true), limit.getLongFromText(false),
                    surgeMode != null && surgeMode.toggled,
                    disableLimit != null && disableLimit.toggled,
                    chunkLoading != null && chunkLoading.toggled);*/
            //PacketHandler.CHANNEL.sendToServer(new CEditConnectionsMessage(host.network.getNetworkID(),
            // list, tag, b));
        }
    }
}
