package fluxnetworks.client.gui.tab;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.api.SecurityType;
import fluxnetworks.api.EnergyType;
import fluxnetworks.client.gui.basic.GuiTabCore;
import fluxnetworks.client.gui.basic.GuiTextField;
import fluxnetworks.client.gui.button.*;
import fluxnetworks.common.network.*;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.api.NetworkColor;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.handler.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.List;

public class GuiTabCreate extends GuiTabCore {

    private List<ColorButton> colorButtons = Lists.newArrayList();

    public SecurityType securityType = SecurityType.ENCRYPTED;
    public EnergyType energyType = EnergyType.RF;
    public ColorButton color;
    public TextboxButton name;
    public TextboxButton password;
    public TextboxButton customColor;
    public NormalButton apply, create;

    public GuiTabCreate(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);

        drawCenteredString(fontRenderer, FluxTranslate.TAB_CREATE.t(), 89, 10, 0xb4b4b4);
        fontRenderer.drawString(FluxTranslate.NETWORK_NAME.t() + ":", 14, 30, 0x606060);
        fontRenderer.drawString(FluxTranslate.NETWORK_SECURITY.t() + ": " + TextFormatting.AQUA + securityType.getName(), 14, 50, 0x606060);
        if(securityType == SecurityType.ENCRYPTED)
            fontRenderer.drawString(FluxTranslate.NETWORK_PASSWORD.t() + ": ", 14, 64, 0x606060);
        fontRenderer.drawString(FluxTranslate.NETWORK_ENERGY.t() + ": " + TextFormatting.AQUA + energyType.getName(), 14, 78, 0x606060);
        fontRenderer.drawString(FluxTranslate.NETWORK_COLOR.t() + ":", 14, 97, 0x606060);

        renderNetwork(name.getText(), color.color, 20, 129);
        drawCenteredString(fontRenderer, TextFormatting.RED + FluxNetworks.proxy.getFeedback().getInfo(), 89, 150, 0xffffff);
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        drawRectWithBackground(30, 44, 60, 118, 0xccffffff, 0x80000000);
        super.drawPopupForegroundLayer(mouseX, mouseY);
        drawCenteredString(fontRenderer, FluxTranslate.CUSTOM_COLOR.t(), 89, 48, 0xffffff);
    }

    @Override
    protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawBackgroundLayer(partialTicks, mouseX, mouseY);
        for(ColorButton button : colorButtons) {
            button.drawButton(mc, mouseX, mouseY);
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        colorButtons.clear();
        super.setWorldAndResolution(mc, width, height);
        if(!main) {
            initPopGui();
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7).setMain());

        int l = fontRenderer.getStringWidth(FluxTranslate.NETWORK_NAME.t());
        name = TextboxButton.create(this, "", 1, fontRenderer, 20 + l, 28, 140 - l, 12);
        name.setMaxStringLength(24);
        name.setText(mc.player.getName() + "'s Network");

        l = fontRenderer.getStringWidth(FluxTranslate.NETWORK_PASSWORD.t());
        password = TextboxButton.create(this, "", 2, fontRenderer, 20 + l, 62, 140 - l, 12).setTextInvisible();
        password.setMaxStringLength(16);

        int x = 0, y = 0;
        for(NetworkColor color : NetworkColor.values()) {
            colorButtons.add(new ColorButton(width / 2 - 40 + x * 16, height / 2 + 13 + y * 16, color.color));
            x++;
            if(x == 7) {
                x = 0;
                y++;
            }
        }
        color = colorButtons.get(0);
        color.selected = true;

        create = new NormalButton(FluxTranslate.CREATE.t(), 70, 150, 36, 12, 3).setUnclickable();
        buttons.add(create);

        textBoxes.add(name);
        textBoxes.add(password);
    }

    @Override
    protected void mouseMainClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseMainClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 0 || mouseButton == 1) {
            if(mouseButton == 0) {
                if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 48 && mouseY < getGuiTop() + 60) {
                    securityType = FluxUtils.incrementEnum(securityType, SecurityType.values());
                    password.setText("");
                    password.setVisible(!password.getVisible());
                    create.clickable = !securityType.isEncrypted();
                }
                if (mouseX > guiLeft + 50 && mouseX < guiLeft + 150 && mouseY > guiTop + 76 && mouseY < getGuiTop() + 88) {
                    energyType = FluxUtils.incrementEnum(energyType, EnergyType.values());
                }

                for (NormalButton button : buttons) {
                    if (button.clickable && button.isMouseHovered(mc, mouseX - guiLeft, mouseY - guiTop)) {
                        if (button.id == 3) {
                            PacketHandler.network.sendToServer(new PacketGeneral.GeneralMessage(PacketGeneralType.CREATE_NETWORK, PacketGeneralHandler.getCreateNetworkPacket(name.getText(), color.color, securityType, energyType, password.getText())));
                        }
                    }
                }
            }
            for(ColorButton button : colorButtons) {
                if(button.isMouseHovered(mc, mouseX, mouseY)) {
                    color.selected = false;
                    color = button;
                    color.selected = true;
                    if(mouseButton == 1) {
                        main = false;
                        initPopGui();
                    }
                }
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
                        color.color = customColor.getIntegerFromHex();
                        main = true;
                    }
                }
            }
            if(main) {
                backToMain();
            }
        }
    }

    @Override
    protected void keyTypedMain(char c, int k) {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(textBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                mc.player.closeScreen();
            }
        }
        for(TextboxButton text : textBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
                create.clickable = (!securityType.isEncrypted() || password.getText().length() != 0) && name.getText().length() !=0;
            }
        }
    }

    @Override
    protected void keyTypedPop(char c, int k) {
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(popBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                backToMain();
            }
        }
        for(TextboxButton text : popBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
                apply.clickable = text.getText().length() == 6;
            }
        }
    }

    private void initPopGui() {
        popBoxes.clear();
        popButtons.clear();
        popButtons.add(new NormalButton(FluxTranslate.CANCEL.t(), 40, 86, 36, 12, 11));
        apply = new NormalButton(FluxTranslate.APPLY.t(), 100, 86, 36, 12, 12);
        popButtons.add(apply);

        customColor = TextboxButton.create(this, "0x", 7, fontRenderer, 57, 64, 64, 12).setHexOnly();
        customColor.setMaxStringLength(6);
        customColor.setText(Integer.toHexString(color.color));

        popBoxes.add(customColor);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if(FluxNetworks.proxy.getFeedback() == FeedbackInfo.SUCCESS) {
            FMLCommonHandler.instance().showGuiScreen(new GuiTabSelection(player, tileEntity));
            FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
        }
    }

}
