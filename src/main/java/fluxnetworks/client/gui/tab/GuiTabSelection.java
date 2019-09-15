package fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.client.gui.basic.GuiCore;
import fluxnetworks.client.gui.basic.GuiTabPages;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.*;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketTile;
import fluxnetworks.common.network.PacketTileHandler;
import fluxnetworks.common.network.PacketTileType;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class GuiTabSelection extends GuiTabPages<IFluxNetwork> {

    public TextboxButton password;
    public IFluxNetwork popSelect;

    private int timer2;

    public GuiTabSelection(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        gridStartX = 16;
        gridStartY = 22;
        gridHeight = 13;
        gridPerPage = 10;
        elementHeight = 12;
        elementWidth = 143;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(elements.size() == 0) {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_NETWORK.t(), FluxTranslate.TAB_CREATE.t());
        } else {
            String amount = FluxTranslate.TOTAL.t() + ": " + elements.size();
            fontRenderer.drawString(amount, 154 - fontRenderer.getStringWidth(amount), 10, 0xffffff);
            fontRenderer.drawString(FluxTranslate.SORT_BY.t() + ": " + TextFormatting.AQUA + sortType.name, 20, 10, 0xffffff);
            if (main) {
                drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 89, 150, 0xffffff);
            }
        }
    }

    @Override
    protected void onElementClicked(IFluxNetwork element, int mouseButton) {
        if(mouseButton == 0) {
            popSelect = element;
            PacketHandler.network.sendToServer(new PacketTile.TileMessage(PacketTileType.SET_NETWORK, PacketTileHandler.getSetNetworkPacket(element.getNetworkID(), ""), tileEntity.getPos(), tileEntity.getWorld().provider.getDimension()));
        }
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        super.drawPopupForegroundLayer(mouseX, mouseY);
        if(popSelect != null) {
            drawCenteredString(fontRenderer, FluxTranslate.CONNECTING_TO.t() + " " + popSelect.getSetting(NetworkSettings.NETWORK_NAME), 89, 50, 0xffffff);
        }
        drawCenteredString(fontRenderer, FluxTranslate.NETWORK_PASSWORD.t() + ":", 40, 68, 0xffffff);

        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 89, 110, 0xffffff);
    }

    @Override
    public void initGui() {

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(1).setMain();

        super.initGui();
    }

    @Override
    public void renderElement(IFluxNetwork element, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(GuiCore.GUI_BAR);

        int color = element.getSetting(NetworkSettings.NETWORK_COLOR);

        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        boolean selected = tileEntity.getNetworkID() == element.getNetworkID();
        boolean isEncrypted = element.getSetting(NetworkSettings.NETWORK_SECURITY).isEncrypted();

        if(isEncrypted) {
            if(selected) {
                drawTexturedModalRect(x + 129, y, 143, 16, 16, elementHeight);
            } else {
                drawTexturedModalRect(x + 129, y, 159, 16, 16, elementHeight);
            }
        }

        String text = element.getSetting(NetworkSettings.NETWORK_NAME);

        if(selected) {
            GlStateManager.color(f, f1, f2);
            drawTexturedModalRect(x, y, 0, 16, elementWidth, elementHeight);
            mc.fontRenderer.drawString(text, x + 4, y + 2, 0xffffff);
        } else {
            GlStateManager.color(f * 0.75f, f1 * 0.75f, f2 * 0.75f);
            drawTexturedModalRect(x, y, 0, 16, elementWidth, elementHeight);
            mc.fontRenderer.drawString(text, x + 4, y + 2, 0x404040);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void renderElementTooltip(IFluxNetwork element, int mouseX, int mouseY) {

    }

    private void initPopGui() {
        popButtons.clear();
        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 24, 86, 30, 12, 11));
        popButtons.add(new NormalButton(FluxTranslate.CONNECT.t(), 120, 86, 30, 12, 12));

        password = TextboxButton.create(this, "", 5, fontRenderer, 70, 66, 81, 12);
        password.setTextInvisible();
        password.setMaxStringLength(16);

        popBoxes.add(password);
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            if (mouseX > guiLeft + 45 && mouseX < guiLeft + 75 && mouseY > guiTop + 10 && mouseY < getGuiTop() + 17) {
                sortType = FluxUtils.incrementEnum(sortType, SortType.values());
                sortGrids(sortType);
            }
        }
    }

    @Override
    protected void mousePopupClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mousePopupClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(NormalButton button : popButtons) {
                if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 11) {
                        main = true;
                    }
                    if(button.id == 12) {
                        if(password.getText().length() > 0) {
                            PacketHandler.network.sendToServer(new PacketTile.TileMessage(PacketTileType.SET_NETWORK, PacketTileHandler.getSetNetworkPacket(popSelect.getNetworkID(), password.getText()), tileEntity.getPos(), tileEntity.getWorld().provider.getDimension()));
                            password.setText("");
                        }
                    }
                }
            }
            if(main) {
                backToMain();
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(timer2 == 0) {
            refreshPages(FluxNetworkCache.instance.getAllClientNetworks());
        }
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS) {
            backToMain();
        }
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.PASSWORD_REQUIRE) {
            main = false;
            initPopGui();
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
        }
        timer2++;
        timer2 %= 10;
    }

    @Override
    protected void sortGrids(SortType sortType) {
        switch (sortType) {
            case ID:
                elements.sort(Comparator.comparing(IFluxNetwork::getNetworkID));
                refreshCurrentPage();
                break;
            case NAME:
                elements.sort(Comparator.comparing(IFluxNetwork::getNetworkName));
                refreshCurrentPage();
                break;
        }
    }

    @Override
    protected void keyTypedPop(char c, int k) throws IOException {
        super.keyTypedPop(c, k);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        if(!main) {
            initPopGui();
        }
    }
}
