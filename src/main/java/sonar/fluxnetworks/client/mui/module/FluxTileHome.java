package sonar.fluxnetworks.client.mui.module;

import icyllis.modernui.gui.master.Module;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

public class FluxTileHome extends Module {

    private final TileFluxCore tileEntity;

    public FluxTileHome(TileFluxCore tileEntity) {
        this.tileEntity = tileEntity;
    }
}
