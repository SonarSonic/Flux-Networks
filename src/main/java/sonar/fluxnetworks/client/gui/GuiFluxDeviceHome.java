package sonar.fluxnetworks.client.gui;

/*
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.blockentity.FluxContainerMenu;
import sonar.fluxnetworks.common.test.C2SNetMsg;
import sonar.fluxnetworks.common.blockentity.FluxDeviceEntity;

import javax.annotation.Nonnull;

*/
/**
 * The home page.
 *//*

public class GuiFluxDeviceHome extends GuiTabCore {

    public InvisibleButton redirectButton;
    public FluxTextWidget fluxName, priority, limit;

    public SlidedSwitchButton surge, disableLimit, chunkLoading;

    private final FluxDeviceEntity tileEntity;
    private int timer;

    public GuiFluxDeviceHome(@Nonnull FluxContainerMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
        this.tileEntity = (FluxDeviceEntity) container.bridge;
    }

    @Override
    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_HOME;
    }


    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12,
                EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
        addButton(redirectButton);

        int color = network.getNetworkColor() | 0xff000000;
        fluxName = FluxTextWidget.create(FluxTranslate.NAME.t() + ": ", font, guiLeft + 16, guiTop + 28, 144, 12).setOutlineColor(color);
        fluxName.setMaxStringLength(24);
        fluxName.setText(tileEntity.getCustomName());
        fluxName.setResponder(string -> {
            tileEntity.setCustomName(fluxName.getText());
            C2SNetMsg.tileEntity(tileEntity, FluxConstants.C2S_CUSTOM_NAME);
        });
        addButton(fluxName);

        priority = FluxTextWidget.create(FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 16, guiTop + 45, 144, 12).setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
        priority.setMaxStringLength(5);
        priority.setText(String.valueOf(tileEntity.getRawPriority()));
        priority.setResponder(string -> {
            tileEntity.setPriority(priority.getValidInt());
            C2SNetMsg.tileEntity(tileEntity, FluxConstants.C2S_PRIORITY);
        });
        addButton(priority);

        limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 16, guiTop + 62, 144, 12).setOutlineColor(color).setDigitsOnly().setMaxValue(tileEntity.getMaxTransferLimit());
        limit.setMaxStringLength(9);
        limit.setText(String.valueOf(tileEntity.getRawLimit()));
        limit.setResponder(string -> {
            tileEntity.setTransferLimit(limit.getValidLong());
            C2SNetMsg.tileEntity(tileEntity, FluxConstants.C2S_LIMIT);
        });
        addButton(limit);

        surge = new SlidedSwitchButton(140, 120, 1, guiLeft, guiTop, tileEntity.getSurgeMode());
        disableLimit = new SlidedSwitchButton(140, 132, 2, guiLeft, guiTop, tileEntity.getDisableLimit());
        switches.add(surge);
        switches.add(disableLimit);

        if (!tileEntity.getDeviceType().isStorage()) {
            chunkLoading = new SlidedSwitchButton(140, 144, 3, guiLeft, guiTop, tileEntity.isForcedLoading());
            switches.add(chunkLoading);
        }
    }


    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        screenUtils.renderNetwork(matrixStack, network.getNetworkName(), network.getNetworkColor(), 20, 8);
        renderTransfer(matrixStack, tileEntity);
        drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 89, 150, FluxClientCache.getFeedbackColor());

        font.drawString(matrixStack, FluxTranslate.SURGE_MODE.t(), 20, 120, network.getNetworkColor());
        font.drawString(matrixStack, FluxTranslate.DISABLE_LIMIT.t(), 20, 132, network.getNetworkColor());
        if (!tileEntity.getDeviceType().isStorage()) {
            font.drawString(matrixStack, FluxTranslate.CHUNK_LOADING.t(), 20, 144, network.getNetworkColor());
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && button instanceof SlidedSwitchButton) {
            SlidedSwitchButton switchButton = (SlidedSwitchButton) button;
            switch (switchButton.id) {
                case 1:
                    switchButton.switchButton();
                    tileEntity.setSurgeMode(switchButton.toggled);
                    C2SNetMsg.tileEntity(tileEntity, FluxConstants.C2S_SURGE_MODE);
                    break;
                case 2:
                    switchButton.switchButton();
                    tileEntity.setDisableLimit(switchButton.toggled);
                    C2SNetMsg.tileEntity(tileEntity, FluxConstants.C2S_DISABLE_LIMIT);
                    break;
                case 3:
                    tileEntity.setForcedLoading(!switchButton.toggled); // delayed updating value
                    C2SNetMsg.tileEntity(tileEntity, FluxConstants.C2S_CHUNK_LOADING);
                    break;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            C2SNetMsg.requestNetworkUpdate(network, FluxConstants.TYPE_NET_BASIC);
        }
        if (chunkLoading != null) {
            chunkLoading.toggled = tileEntity.isForcedLoading();
        }
        timer++;
        timer %= 100;
    }
}
*/
