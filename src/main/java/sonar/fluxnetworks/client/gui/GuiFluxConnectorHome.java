package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.NetworkUpdateRequestPacket;
import sonar.fluxnetworks.common.network.TileMessage;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

/**
 * The home page.
 */
public class GuiFluxConnectorHome extends GuiTabCore {

    public InvisibleButton redirectButton;
    public FluxTextWidget fluxName, priority, limit;

    public SlidedSwitchButton surge, disableLimit, chunkLoading;

    private final TileFluxDevice tileEntity;
    private int timer;

    public GuiFluxConnectorHome(PlayerEntity player, TileFluxDevice tileEntity) {
        super(player, tileEntity);
        this.tileEntity = tileEntity;
    }

    @Override
    public EnumNavigationTabs getNavigationTab() {
        return EnumNavigationTabs.TAB_HOME;
    }


    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
        addButton(redirectButton);

        int color = network.getNetworkColor() | 0xff000000;
        fluxName = FluxTextWidget.create(FluxTranslate.NAME.t() + ": ", font, guiLeft + 16, guiTop + 28, 144, 12).setOutlineColor(color);
        fluxName.setMaxStringLength(24);
        fluxName.setText(tileEntity.getCustomName());
        fluxName.setResponder(string -> {
            tileEntity.setCustomName(fluxName.getText());
            NetworkHandler.INSTANCE.sendToServer(new TileMessage(tileEntity, TileMessage.C2S_CUSTOM_NAME));
        });
        addButton(fluxName);

        priority = FluxTextWidget.create(FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 16, guiTop + 45, 144, 12).setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
        priority.setMaxStringLength(5);
        priority.setText(String.valueOf(tileEntity.getRawPriority()));
        priority.setResponder(string -> {
            tileEntity.setPriority(priority.getValidInt());
            NetworkHandler.INSTANCE.sendToServer(new TileMessage(tileEntity, TileMessage.C2S_PRIORITY));
        });
        addButton(priority);

        limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 16, guiTop + 62, 144, 12).setOutlineColor(color).setDigitsOnly().setMaxValue(tileEntity.getMaxTransferLimit());
        limit.setMaxStringLength(9);
        limit.setText(String.valueOf(tileEntity.getRawLimit()));
        limit.setResponder(string -> {
            tileEntity.setLimit(limit.getValidLong());
            NetworkHandler.INSTANCE.sendToServer(new TileMessage(tileEntity, TileMessage.C2S_LIMIT));
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
        renderTransfer(matrixStack, tileEntity, 0xffffff, 30, 90);
        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxNetworks.PROXY.getFeedback(false).getInfo(), 89, 150, 0xffffff);

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
                    tileEntity.setSurgeMode(switchButton.slideControl);
                    NetworkHandler.INSTANCE.sendToServer(new TileMessage(tileEntity, TileMessage.C2S_SURGE_MODE));
                    break;
                case 2:
                    switchButton.switchButton();
                    tileEntity.setDisableLimit(switchButton.slideControl);
                    NetworkHandler.INSTANCE.sendToServer(new TileMessage(tileEntity, TileMessage.C2S_DISABLE_LIMIT));
                    break;
                case 3:
                    tileEntity.setForcedLoading(!switchButton.slideControl); // delayed updating value
                    NetworkHandler.INSTANCE.sendToServer(new TileMessage(tileEntity, TileMessage.C2S_CHUNK_LOADING));
                    break;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (timer == 0) {
            PacketHandler.CHANNEL.sendToServer(new NetworkUpdateRequestPacket(network.getNetworkID(), NBTType.NETWORK_GENERAL));
        }
        if (timer % 4 == 0) {
            if (chunkLoading != null) {
                chunkLoading.slideControl = tileEntity.isForcedLoading();
            }
        }
        timer++;
        timer %= 100;
    }
}
