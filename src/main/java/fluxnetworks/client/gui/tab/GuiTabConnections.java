package fluxnetworks.client.gui.tab;

import fluxnetworks.api.network.FluxType;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.api.tileentity.ILiteConnector;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.FluxNetworkServer;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiTabConnections extends GuiTabPages<IFluxConnector> {

    private int timer;

    public GuiTabConnections(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 18;
        gridHeight = 19;
        gridPerPage = 7;
        elementHeight = 16;
        elementWidth = 144;
    }

    @Override
    protected void onElementClicked(IFluxConnector element, int mouseButton) {

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);

    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(3).setMain();
    }

    @Override
    public void renderElement(IFluxConnector element, int x, int y) {
        GlStateManager.pushMatrix();
        drawColorRect(x, y, elementHeight, elementWidth, network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        renderItemStack(element.getDisplayStack(), x, y);
        if(element.isChunkLoaded()) {
            fontRenderer.drawString(element.getCustomName(), x + 20, y + 1, 0xffffff);
            GlStateManager.scale(0.625, 0.625, 0.625);
            fontRenderer.drawString(getTransferInfo(element.getConnectionType(), network.getSetting(NetworkSettings.NETWORK_ENERGY), element.getTransferHandler().getChange()), (int) ((x + 20) * 1.6), (int) ((y + 10) * 1.6), 0xffffff);
            GlStateManager.scale(1.6, 1.6, 1.6);
        } else {
            fontRenderer.drawString(element.getCustomName(), x + 20, y + 4, 0x808080);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderElementTooltip(IFluxConnector element, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        drawHoverTooltip(getFluxInfo(element), mouseX + 4, mouseY - 16);
        GlStateManager.popMatrix();
    }

    @Override
    public void updateScreen() {
        if(timer == 0) {
            List<IFluxConnector> list = new ArrayList<>();
            list.addAll(network.getConnections(FluxType.flux));
            list.addAll(network.getSetting(NetworkSettings.UNLOADED_CONNECTORS));
            refreshPages(list);
        }
        timer++;
        timer %= 100;
    }

    @Override
    protected void sortGrids(SortType sortType) {
        elements.sort(Comparator.comparing(IFluxConnector::isChunkLoaded).reversed().thenComparing(IFluxConnector::getConnectionType).thenComparing(IFluxConnector::getPriority));
        refreshCurrentPage();
    }
}
