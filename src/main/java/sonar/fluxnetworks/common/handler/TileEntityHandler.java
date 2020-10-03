package sonar.fluxnetworks.common.handler;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.common.handler.energy.FNEnergyHandler;
import sonar.fluxnetworks.common.handler.energy.ForgeEnergyHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityHandler {

    public static final List<ITileEnergyHandler> TILE_ENERGY_HANDLERS = new ArrayList<>();
    public static final Map<String, Integer> BLOCK_BLACKLIST = new HashMap<>();

    public static void registerEnergyHandler() {
        TILE_ENERGY_HANDLERS.add(FNEnergyHandler.INSTANCE);
        ItemEnergyHandler.ITEM_ENERGY_HANDLERS.add(FNEnergyHandler.INSTANCE);

        TILE_ENERGY_HANDLERS.add(ForgeEnergyHandler.INSTANCE);
        ItemEnergyHandler.ITEM_ENERGY_HANDLERS.add(ForgeEnergyHandler.INSTANCE);

        /* TODO PORT OTHER MOD ENERGY HANDLERS.
        if(Loader.isModLoaded("gregtech")) {
            tileEnergyHandlers.add(GTEnergyHandler.INSTANCE);
            ItemEnergyHandler.itemEnergyHandlers.add(GTEnergyHandler.INSTANCE);
        }
        if(Loader.isModLoaded("redstoneflux")) {
            tileEnergyHandlers.add(RedstoneFluxHandler.INSTANCE);
            ItemEnergyHandler.itemEnergyHandlers.add(RedstoneFluxHandler.INSTANCE);
        }
        if(Loader.isModLoaded("ic2")) {
            tileEnergyHandlers.add(IC2EnergyHandler.INSTANCE);
            ItemEnergyHandler.itemEnergyHandlers.add(IC2EnergyHandler.INSTANCE);
        }
        */
    }

    @Nullable
    public static ITileEnergyHandler getEnergyHandler(TileEntity tile, Direction dir) {
        if (tile instanceof IFluxDevice) {
            return null;
        }

        ResourceLocation registryName = tile.getBlockState().getBlock().getRegistryName();
        if (registryName != null && BLOCK_BLACKLIST.containsKey(registryName.toString())) {
            return null; //TODO REDO META CHECKS WITH BLOCK STATE CHECKS
        }
        for (ITileEnergyHandler handler : TILE_ENERGY_HANDLERS) {
            if (handler.canRenderConnection(tile, dir)) {
                return handler;
            }
        }
        return null;
    }

    public static boolean canRenderConnection(@Nullable TileEntity tile, Direction dir) {
        if (tile == null) {
            return false;
        }
        if (tile instanceof IFluxDevice) {
            return false;
        }
        ResourceLocation registryName = tile.getBlockState().getBlock().getRegistryName();
        if (registryName != null && BLOCK_BLACKLIST.containsKey(registryName.toString())) {
            return false; //TODO REDO META CHECKS WITH BLOCK STATE CHECKS
        }
        ITileEnergyHandler handler = null;
        for (ITileEnergyHandler handler1 : TILE_ENERGY_HANDLERS) {
            if (handler1.canRenderConnection(tile, dir)) {
                handler = handler1;
            }
        }
        return handler != null;
    }
}
