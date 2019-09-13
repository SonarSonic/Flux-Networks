package fluxnetworks.client.gui.tab;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.button.InventoryButton;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketGeneral;
import fluxnetworks.common.network.PacketGeneralHandler;
import fluxnetworks.common.network.PacketGeneralType;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiTabWireless extends GuiTabCore {

    public List<InventoryButton> inventoryButtonList = new ArrayList<>();

    public int enableWireless, rightHand, leftHand, hotBar, armorSlot, baublesSlot;

    public GuiTabWireless(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
        int a = network.getSetting(NetworkSettings.NETWORK_WIRELESS);
        enableWireless = a & 1;
        rightHand = a >> 1 & 1;
        leftHand = a >> 2 & 1;
        hotBar = a >> 3 & 1;
        armorSlot = a >> 4 & 1;
        baublesSlot = a >> 5 & 1;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        if(networkValid) {
            drawCenteredString(fontRenderer, FluxTranslate.TAB_WIRELESS, 89, 12, 0xb4b4b4);
            fontRenderer.drawString(FluxTranslate.ENABLE_WIRELESS, 20, 156, network.getSetting(NetworkSettings.NETWORK_COLOR));
        } else {
            renderNavigationPrompt(FluxTranslate.ERROR_NO_SELECTED, FluxTranslate.TAB_SELECTION);
        }

        for(InventoryButton button : inventoryButtonList) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(2).setMain();

        if(networkValid) {
            switches.add(new SlidedSwitchButton(140, 156, 4, guiLeft, guiTop, enableWireless != 0));

            inventoryButtonList.add(new InventoryButton(24, 32, 0, 80, 52, 16, guiLeft, guiTop, 0, armorSlot != 0, "Armor Slots"));
            inventoryButtonList.add(new InventoryButton(100, 32, 0, 80, 52, 16, guiLeft, guiTop, 1, baublesSlot != 0, "Baubles Slots"));
            inventoryButtonList.add(new InventoryButton(32, 56, 0, 0, 112, 40, guiLeft, guiTop, 2, false, "Main Inventory"));
            inventoryButtonList.add(new InventoryButton(32, 104, 112, 0, 112, 16, guiLeft, guiTop, 3, hotBar != 0, "HotBar"));
            inventoryButtonList.add(new InventoryButton(136, 128, 52, 80, 16, 16, guiLeft, guiTop, 4, rightHand != 0, "Right Hand"));
            inventoryButtonList.add(new InventoryButton(24, 128, 52, 80, 16, 16, guiLeft, guiTop, 5, leftHand != 0, "Left Hand"));

            buttons.add(new NormalButton("Apply", 73, 130, 32, 12, 0));
        }
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0) {
            for(InventoryButton button : inventoryButtonList) {
                if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    switch(button.id) {
                        case 0:
                            button.selected = !button.selected;
                            armorSlot = armorSlot == 0 ? 1 : 0;
                            break;
                        case 1:
                            button.selected = !button.selected;
                            baublesSlot = baublesSlot == 0 ? 1 : 0;
                            break;
                        case 3:
                            button.selected = !button.selected;
                            hotBar = hotBar == 0 ? 1 : 0;
                            break;
                        case 4:
                            button.selected = !button.selected;
                            rightHand = rightHand == 0 ? 1 : 0;
                            break;
                        case 5:
                            button.selected = !button.selected;
                            leftHand = leftHand == 0 ? 1 : 0;
                            break;
                        default:
                            button.selected = !button.selected;
                            break;
                    }
                }
            }
            for(NormalButton button : buttons) {
                if(button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    if(button.id == 0) {
                        int wireless = enableWireless | rightHand << 1 | leftHand << 2 | hotBar << 3 | armorSlot << 4 | baublesSlot << 5;
                        PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_WIRELESS, PacketGeneralHandler.getChangeWirelessPacket(network.getNetworkID(), wireless)));
                    }
                }
            }
            for(SlidedSwitchButton s : switches) {
                if (s.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                    s.switchButton();
                    if(s.id == 4) {
                        enableWireless = enableWireless == 0 ? 1 : 0;
                        int wireless = enableWireless | rightHand << 1 | leftHand << 2 | hotBar << 3 | armorSlot << 4 | baublesSlot << 5;
                        PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CHANGE_WIRELESS, PacketGeneralHandler.getChangeWirelessPacket(network.getNetworkID(), wireless)));
                    }
                }
            }
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        inventoryButtonList.clear();
        super.setWorldAndResolution(mc, width, height);
    }
}
