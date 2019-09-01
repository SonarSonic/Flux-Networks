package fluxnetworks.client.gui.tab;

import fluxnetworks.api.tileentity.ILiteConnector;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GuiTabConnections extends GuiTabPages<ILiteConnector> {

    private int timer;

    public GuiTabConnections(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 18;
        gridHeight = 19;
        gridPerPage = 7;
        elementHeight = 12;
        elementWidth = 143;
    }

    @Override
    protected void onElementClicked(ILiteConnector element, int mouseButton) {

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
    public void updateScreen() {
        if(timer == 0) {
            refreshPages(network.getSetting(NetworkSettings.NETWORKS_CONNECTIONS));
        }
        timer++;
        timer %= 100;
    }

    @Override
    public void renderElement(ILiteConnector element, int x, int y) {
        drawColorRect(x, y, 16, 144, network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000);
        fontRenderer.drawString(element.getCustomName(), x + 20, y + 4, 0xffffff);
        renderItemStack(element.getDisplayStack(), x, y);
    }
}
