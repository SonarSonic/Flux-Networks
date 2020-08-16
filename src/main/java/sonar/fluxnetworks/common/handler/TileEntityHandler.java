package sonar.fluxnetworks.common.handler;

import com.google.common.collect.Lists;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.api.tiles.IFluxDevice;
import sonar.fluxnetworks.common.handler.energy.FNEnergyHandler;
import sonar.fluxnetworks.common.handler.energy.ForgeEnergyHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityHandler {

    public static List<ITileEnergyHandler> tileEnergyHandlers = Lists.newArrayList();
    public static Map<String, Integer>     blockBlacklist     = new HashMap<>();

    public static void registerEnergyHandler() {
        tileEnergyHandlers.add(FNEnergyHandler.INSTANCE);
        ItemEnergyHandler.itemEnergyHandlers.add(FNEnergyHandler.INSTANCE);

        tileEnergyHandlers.add(ForgeEnergyHandler.INSTANCE);
        ItemEnergyHandler.itemEnergyHandlers.add(ForgeEnergyHandler.INSTANCE);

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
        if (registryName != null && blockBlacklist.containsKey(registryName.toString())) {
            return null; //TODO REDO META CHECKS WITH BLOCK STATE CHECKS
        }
        for (ITileEnergyHandler handler : tileEnergyHandlers) {
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
        if (registryName != null && blockBlacklist.containsKey(registryName.toString())) {
            return false; //TODO REDO META CHECKS WITH BLOCK STATE CHECKS
        }
        ITileEnergyHandler handler = null;
        for (ITileEnergyHandler handler1 : tileEnergyHandlers) {
            if (handler1.canRenderConnection(tile, dir)) {
                handler = handler1;
            }
        }
        return handler != null;
    }
}
