package sonar.fluxnetworks.client.gui.popups;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiFocusable;
import sonar.fluxnetworks.client.gui.basic.GuiPopUpHost;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import net.minecraft.client.Minecraft;
import sonar.fluxnetworks.common.misc.ContainerConnector;

import java.util.ArrayList;
import java.util.List;

public class PopUpCore<T extends GuiPopUpHost> extends GuiFocusable<ContainerConnector<?>> {

    protected List<NormalButton> popButtons = Lists.newArrayList();
    protected List<SlidedSwitchButton> popSwitches = new ArrayList<>();

    public T host;
    public PlayerEntity player;
    public INetworkConnector connector;

    public PopUpCore(T host, PlayerEntity player, INetworkConnector connector) {
        super(host.getContainer(), player.inventory, ((INamedContainerProvider)connector).getDisplayName());
        this.host = host;
        this.player = player;
        this.connector = connector;
    }

    public void init(){
        super.init();
        popButtons.clear();
        popSwitches.clear();
    }

    public void openPopUp(){
        super.init(Minecraft.getInstance(), host.width, host.height);
    }

    public void closePopUp(){
        popButtons.clear();
        popSwitches.clear();
        FluxClientCache.setFeedback(FeedbackInfo.NONE, true);
    }

    @Override
    public int getGuiColouring(){
        return host.getGuiColouring();
    }

    public void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        popButtons.forEach(b -> b.drawButton(minecraft, matrixStack, mouseX, mouseY, guiLeft, guiTop));
    }

    @Override
    public void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY){
        fillGradient(matrixStack, -guiLeft, -guiTop, this.width, this.height, 0xa0101010, 0xb0101010);
        drawFluxDefaultBackground(matrixStack);

        //screenUtils.drawRectWithBackground(guiLeft + 8, guiTop + 13, 150, 160, 0xccffffff, 0xb0000000);
        for(SlidedSwitchButton button : popSwitches) {
            button.updateButton(partialTicks * 4, mouseX, mouseY);
        }
        for(Widget widget : buttons){
            widget.render(matrixStack, mouseX, mouseY, 1);
        }
    }

}
