package fluxnetworks.common.handler;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.energy.IItemEnergyHandler;
import fluxnetworks.api.energy.ITileEnergyHandler;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.CommonProxy;
import fluxnetworks.common.handler.energy.ForgeEnergyHandler;
import fluxnetworks.common.handler.energy.GTEnergyHandler;
import fluxnetworks.common.handler.energy.IC2EnergyHandler;
import fluxnetworks.common.handler.energy.RedstoneFluxHandler;
import fluxnetworks.common.tileentity.TileFluxController;
import fluxnetworks.common.tileentity.TileFluxPlug;
import fluxnetworks.common.tileentity.TileFluxPoint;
import fluxnetworks.common.tileentity.TileFluxStorage;
import li.cil.oc.api.internal.Adapter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityHandler {

    public static List<ITileEnergyHandler> tileEnergyHandlers = Lists.newArrayList();
    public static Map<String, Integer> blockBlacklist = new HashMap<>();

    public static void registerTileEntity() {
        // SonarCore still use old method, this will be minecraft:FluxController etc.
        GameRegistry.registerTileEntity(TileFluxController.class, "FluxController");
        GameRegistry.registerTileEntity(TileFluxPoint.class, "FluxPoint");
        GameRegistry.registerTileEntity(TileFluxPlug.class, "FluxPlug");
        GameRegistry.registerTileEntity(TileFluxStorage.class, "FluxStorage");
        GameRegistry.registerTileEntity(TileFluxStorage.Herculean.class, "HerculeanFluxStorage");
        GameRegistry.registerTileEntity(TileFluxStorage.Gargantuan.class, "GargantuanFluxStorage");

        /*GameRegistry.registerTileEntity(TileFluxController.class, new ResourceLocation(FluxNetworks.MODID, "FluxController"));
        GameRegistry.registerTileEntity(TileFluxPoint.class, new ResourceLocation(FluxNetworks.MODID, "FluxPoint"));
        GameRegistry.registerTileEntity(TileFluxPlug.class, new ResourceLocation(FluxNetworks.MODID, "FluxPlug"));
        GameRegistry.registerTileEntity(TileFluxStorage.class, new ResourceLocation(FluxNetworks.MODID, "FluxStorage"));
        GameRegistry.registerTileEntity(TileFluxStorage.Herculean.class, new ResourceLocation(FluxNetworks.MODID, "HerculeanFluxStorage"));
        GameRegistry.registerTileEntity(TileFluxStorage.Gargantuan.class, new ResourceLocation(FluxNetworks.MODID, "GargantuanFluxStorage"));*/
    }

    public static void registerEnergyHandler() {
        tileEnergyHandlers.add(ForgeEnergyHandler.INSTANCE);
        ItemEnergyHandler.itemEnergyHandlers.add(ForgeEnergyHandler.INSTANCE);
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
    }

    @Nullable
    public static ITileEnergyHandler getEnergyHandler(TileEntity tile, EnumFacing side) {
        if(tile instanceof IFluxConnector) {
            return null;
        }
        String s = tile.getBlockType().getRegistryName().toString();
        if(blockBlacklist.containsKey(s)) {
            int meta = blockBlacklist.get(s);
            if(meta == -1)
                return null;
            else if(meta == tile.getBlockMetadata())
                return null;
        }
        for(ITileEnergyHandler handler : tileEnergyHandlers) {
            if(handler.canRenderConnection(tile, side)) {
                return handler;
            }
        }
        return null;
    }

    public static boolean canRenderConnection(TileEntity tile, EnumFacing side) {
        if(tile == null) {
            return false;
        }
        if(tile instanceof IFluxConnector) {
            return false;
        }
        /*if(FluxNetworks.proxy.ocLoaded) {
            if(tile instanceof Adapter) {
                return true;
            }
        }*/
        if(blockBlacklist.containsKey(tile.getBlockType().getRegistryName().toString())) {
            int meta = blockBlacklist.get(tile.getBlockType().getRegistryName().toString());
            if(meta == -1)
                return false;
            else if(meta == tile.getBlockMetadata())
                return false;
        }
        ITileEnergyHandler handler = null;
        for(ITileEnergyHandler handler1 : tileEnergyHandlers) {
            if(handler1.canRenderConnection(tile, side)) {
                handler = handler1;
            }
        }
        return handler != null;
    }
}
