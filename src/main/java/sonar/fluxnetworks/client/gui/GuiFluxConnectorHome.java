package sonar.fluxnetworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.common.network.*;

/**
 * The home page.
 */
public class GuiFluxConnectorHome extends GuiTabCore {

    public InvisibleButton redirectButton;
    public FluxTextWidget fluxName, priority, limit;

    public SlidedSwitchButton surge, disableLimit, chunkLoad;

    private TileFluxDevice tileEntity;
    private int            timer;

    public GuiFluxConnectorHome(PlayerEntity player, TileFluxDevice tileEntity) {
        super(player, tileEntity);
        this.tileEntity = tileEntity;
    }

    @Override
    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_HOME;
    }


    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
        addButton(redirectButton);

        int color = network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
        fluxName = FluxTextWidget.create(FluxTranslate.NAME.t() + ": ", font, guiLeft + 16, guiTop + 28, 144, 12).setOutlineColor(color);
        fluxName.setMaxStringLength(24);
        fluxName.setText(tileEntity.getCustomName());
        fluxName.setResponder(string -> {
            tileEntity.customName = fluxName.getText();
            tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_CUSTOM_NAME);
        });
        addButton(fluxName);

        priority = FluxTextWidget.create(FluxTranslate.PRIORITY.t() + ": ", font, guiLeft + 16, guiTop + 45, 144, 12).setOutlineColor(color).setDigitsOnly().setAllowNegatives(true);
        priority.setMaxStringLength(5);
        priority.setText(String.valueOf(tileEntity.priority));
        priority.setResponder(string -> {
            tileEntity.priority = priority.getValidInt();
            tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_PRIORITY);
        });
        addButton(priority);

        limit = FluxTextWidget.create(FluxTranslate.TRANSFER_LIMIT.t() + ": ", font, guiLeft + 16, guiTop + 62, 144, 12).setOutlineColor(color).setDigitsOnly().setMaxValue(tileEntity.getMaxTransferLimit());
        limit.setMaxStringLength(9);
        limit.setText(String.valueOf(tileEntity.limit));
        limit.setResponder(string -> {
            tileEntity.limit = limit.getValidLong();
            tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_LIMIT);
        });
        addButton(limit);

        surge = new SlidedSwitchButton(140, 120, 1, guiLeft, guiTop, tileEntity.surgeMode);
        disableLimit = new SlidedSwitchButton(140, 132, 2, guiLeft, guiTop, tileEntity.disableLimit);
        switches.add(surge);
        switches.add(disableLimit);

        if(!tileEntity.getDeviceType().isStorage()) {
            chunkLoad = new SlidedSwitchButton(140, 144, 3, guiLeft, guiTop, tileEntity.chunkLoading);
            switches.add(chunkLoad);
        }

    }


    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        screenUtils.renderNetwork(matrixStack, network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        renderTransfer(matrixStack, tileEntity, 0xffffff, 30, 90);
        drawCenteredString(matrixStack, font, TextFormatting.RED + FluxNetworks.PROXY.getFeedback(false).getInfo(), 89, 150, 0xffffff);

        font.drawString(matrixStack, FluxTranslate.SURGE_MODE.t(), 20, 120, network.getSetting(NetworkSettings.NETWORK_COLOR));
        font.drawString(matrixStack, FluxTranslate.DISABLE_LIMIT.t(), 20, 132, network.getSetting(NetworkSettings.NETWORK_COLOR));
        if(!tileEntity.getDeviceType().isStorage()) {
            font.drawString(matrixStack, FluxTranslate.CHUNK_LOADING.t(), 20, 144, network.getSetting(NetworkSettings.NETWORK_COLOR));
        }
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if(mouseButton == 0 && button instanceof SlidedSwitchButton){
            SlidedSwitchButton switchButton = (SlidedSwitchButton)button;
            switch (switchButton.id) {
                case 1:
                    switchButton.switchButton();
                    tileEntity.surgeMode = switchButton.slideControl;
                    tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_SURGE_MODE);
                    break;
                case 2:
                    switchButton.switchButton();
                    tileEntity.disableLimit = switchButton.slideControl;
                    tileEntity.sendTilePacketToServer(TilePacketBufferConstants.FLUX_DISABLE_LIMIT);
                    break;
                case 3:
                    PacketHandler.CHANNEL.sendToServer(new TilePacket(TilePacketEnum.CHUNK_LOADING, TilePacketHandler.getChunkLoadPacket(!switchButton.slideControl), tileEntity.getCoords()));
                    break;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(timer == 0) {
            PacketHandler.CHANNEL.sendToServer(new NetworkUpdateRequestPacket(network.getNetworkID(), NBTType.NETWORK_GENERAL));
        }
        if(timer % 4 == 0) {
            if (chunkLoad != null) {
                chunkLoad.slideControl = tileEntity.chunkLoading;
            }
        }
        timer++;
        timer %= 100;
    }
}
