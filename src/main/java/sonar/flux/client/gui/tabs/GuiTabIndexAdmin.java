package sonar.flux.client.gui.tabs;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.flux.FluxTranslate;
import sonar.flux.client.gui.EnumGuiTab;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.client.gui.buttons.FluxTextButton;

import java.io.IOException;
import java.util.List;

import static sonar.flux.connection.NetworkSettings.*;

public class GuiTabIndexAdmin extends GuiTabAbstract {

    public GuiTabIndexAdmin(List<EnumGuiTab> tabs) {
        super(tabs);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new FluxTextButton(0, getGuiLeft() + 12, getGuiTop() + 30, 152, 20, FluxTranslate.TRANSFER_OWNERSHIP.t(), NETWORK_COLOUR.getValue(common).getRGB()));
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button instanceof FluxTextButton){
            switch(button.id){
                case 0:
                    if(this.getNetworkID() != -1){
                        FMLCommonHandler.instance().showGuiScreen(new GuiTabTransferOwnership(this, tabs, common));
                    }
                    break;
            }
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        super.drawGuiContainerForegroundLayer(x, y);
        renderNetwork(NETWORK_NAME.getValue(common), NETWORK_ACCESS.getValue(common), NETWORK_COLOUR.getValue(common).getRGB(), true, 11, 8);
    }

    @Override
    public EnumGuiTab getCurrentTab() {
        return EnumGuiTab.INDEX;
    }

    @Override
    public ResourceLocation getBackground() {
        return scroller_flux_gui;
    }
}
