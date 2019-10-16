package fluxnetworks.client.gui.popups;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.INetworkConnector;
import fluxnetworks.client.gui.basic.GuiDraw;
import fluxnetworks.client.gui.basic.GuiPopUpHost;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.SlidedSwitchButton;
import fluxnetworks.client.gui.button.TextboxButton;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiPopCore<T extends GuiPopUpHost> extends GuiDraw {

    protected List<NormalButton> popButtons = Lists.newArrayList();
    protected List<TextboxButton> popBoxes = Lists.newArrayList();
    protected List<SlidedSwitchButton> popSwitches = new ArrayList<>();

    public T host;
    public EntityPlayer player;
    public INetworkConnector connector;

    public GuiPopCore(T host, EntityPlayer player, INetworkConnector connector) {
        super(host.inventorySlots);
        this.host = host;
        this.player = player;
        this.connector = connector;
    }

    public void initGui(){
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        popBoxes.clear();
        popButtons.clear();
        popSwitches.clear();
    }

    public void openPopUp(){
        setWorldAndResolution(Minecraft.getMinecraft(), host.width, host.height);
    }

    public void closePopUp(){
        popButtons.clear();
        popBoxes.clear();
        popSwitches.clear();
        FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE, true);
    }

    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawGradientRect(0 - guiLeft, 0 - guiTop, this.width, this.height, 0xa0101010, 0xb0101010);
        popBoxes.forEach(TextboxButton::drawTextBox);
        popButtons.forEach(b -> b.drawButton(mc, mouseX, mouseY, guiLeft, guiTop));

    }

    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        for(SlidedSwitchButton button : popSwitches) {
            button.updateButton(partialTicks * 4, mouseX, mouseY);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
         popBoxes.forEach(button -> button.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton));
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
            if(popBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                host.closePopUp();
            }
        }
        for(TextboxButton text : popBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }
}
