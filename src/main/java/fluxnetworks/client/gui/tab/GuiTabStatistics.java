package fluxnetworks.client.gui.tab;

import fluxnetworks.client.gui.GuiCore;
import fluxnetworks.client.gui.GuiTabCore;
import fluxnetworks.client.gui.button.NavigationButton;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.entity.player.EntityPlayer;

public class GuiTabStatistics extends GuiTabCore {

    public GuiTabStatistics(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    public void initGui() {
        super.initGui();

        for(int i = 0; i < 7; i++) {
            navigationButtons.add(new NavigationButton(width / 2 - 75 + 18 * i, height / 2 - 99, i));
        }
        navigationButtons.add(new NavigationButton(width / 2 + 59, height / 2 - 99, 7));
        navigationButtons.get(4).setMain();
    }
}
