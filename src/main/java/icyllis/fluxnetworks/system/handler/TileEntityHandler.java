package icyllis.fluxnetworks.system.handler;

import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.api.util.ITileEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public enum TileEntityHandler {
    INSTANCE;

    public List<ITileEnergyHandler> energyHandlers = new ArrayList<>();
    {
        energyHandlers.add(ForgeEnergyHandler.INSTANCE);
    }

    @Nullable
    public ITileEnergyHandler getEnergyHandler(@Nullable TileEntity tile, Direction side) {
        if(tile == null) {
            return null;
        }
        if(tile instanceof IFluxTile) {
            return null;
        }
        for(ITileEnergyHandler handler : energyHandlers) {
            if(handler.match(tile, side)) {
                return handler;
            }
        }
        return null;
    }

}
