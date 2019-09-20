package fluxnetworks.client.gui.basic;

import com.google.common.collect.Lists;
import fluxnetworks.FluxConfig;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.AccessPermission;
import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.client.gui.GuiFluxHome;
import fluxnetworks.client.gui.button.NormalButton;
import fluxnetworks.client.gui.button.TextboxButton;
import fluxnetworks.common.registry.RegistrySounds;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;
import java.util.List;

public abstract class GuiTabCore extends GuiFluxCore {

    protected List<NormalButton> popButtons = Lists.newArrayList();

    public GuiTabCore(EntityPlayer player, TileFluxCore tileEntity, AccessPermission accessPermission) {
        super(player, tileEntity, accessPermission);
    }

    public GuiTabCore(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void drawPopupForegroundLayer(int mouseX, int mouseY) {
        super.drawPopupForegroundLayer(mouseX, mouseY);
        for(NormalButton button : popButtons) {
            button.drawButton(mc, mouseX, mouseY, guiLeft, guiTop);
        }
    }

    @Override
    protected void keyTypedMain(char c, int k) throws IOException {
        super.keyTypedMain(c, k);
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(textBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                FMLCommonHandler.instance().showGuiScreen(new GuiFluxHome(player, tileEntity));
                FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
                if(FluxConfig.enableButtonSound)
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(RegistrySounds.BUTTON_CLICK, 1.0F));
            }
        }
    }

    @Override
    protected void keyTypedPop(char c, int k) throws IOException {
        super.keyTypedPop(c, k);
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(popBoxes.stream().noneMatch(GuiTextField::isFocused)) {
                backToMain();
            }
        }
        for(TextboxButton text : popBoxes) {
            if(text.isFocused()) {
                text.textboxKeyTyped(c, k);
            }
        }
    }

    @Override
    protected void mousePopupClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mousePopupClicked(mouseX, mouseY, mouseButton);
        for(TextboxButton text : popBoxes) {
            text.mouseClicked(mouseX - guiLeft, mouseY - guiTop, mouseButton);
        }
    }

    protected void backToMain() {
        main = true;
        popButtons.clear();
        popBoxes.clear();
        FluxNetworks.proxy.setFeedback(FeedbackInfo.NONE);
    }
}
