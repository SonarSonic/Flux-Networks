package sonar.fluxnetworks.common.misc;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;
import sonar.fluxnetworks.common.integration.energy.FNEnergyHandler;
import sonar.fluxnetworks.common.integration.energy.ForgeEnergyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EnergyUtils {

    private static final List<ITileEnergyHandler> TILE_ENERGY_HANDLERS = new ArrayList<>();
    private static final List<Block> BLOCK_BLACKLIST = new ArrayList<>();

    private static final List<IItemEnergyHandler> ITEM_ENERGY_HANDLERS = new ArrayList<>();
    private static final List<Item> ITEM_BLACKLIST = new ArrayList<>();

    static {
        TILE_ENERGY_HANDLERS.add(FNEnergyHandler.INSTANCE);
        ITEM_ENERGY_HANDLERS.add(FNEnergyHandler.INSTANCE);

        TILE_ENERGY_HANDLERS.add(ForgeEnergyHandler.INSTANCE);
        ITEM_ENERGY_HANDLERS.add(ForgeEnergyHandler.INSTANCE);

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
        }*/
    }

    public static void reloadBlacklist() {
        BLOCK_BLACKLIST.clear();
        for (String str : FluxConfig.blockBlacklistStrings) {
            if (str.isEmpty()) {
                continue;
            }
            if (!str.contains(":")) {
                FluxNetworks.LOGGER.warn("BLACKLIST ERROR: " + str + " has incorrect formatting");
                continue;
            }
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(str));
            if (block != null) {
                BLOCK_BLACKLIST.add(block);
            }
        }
        ITEM_BLACKLIST.clear();
        for (String str : FluxConfig.itemBlackListStrings) {
            if (str.isEmpty()) {
                continue;
            }
            if (!str.contains(":")) {
                FluxNetworks.LOGGER.warn("BLACKLIST ERROR: " + str + " has incorrect formatting");
                continue;
            }
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(str));
            if (item != null) {
                ITEM_BLACKLIST.add(item);
            }
        }
        FluxNetworks.LOGGER.info("BLACKLIST RELOADED");
    }

    @Nullable
    public static ITileEnergyHandler getEnergyHandler(@Nullable TileEntity tile, @Nonnull Direction dir) {
        if (tile == null) {
            return null;
        }
        if (tile instanceof IFluxDevice) {
            return null;
        }
        if (BLOCK_BLACKLIST.contains(tile.getBlockState().getBlock())) {
            return null;
        }
        for (ITileEnergyHandler handler : TILE_ENERGY_HANDLERS) {
            if (handler.hasCapability(tile, dir)) {
                return handler;
            }
        }
        return null;
    }

    public static boolean canRenderConnection(@Nullable TileEntity tile, @Nonnull Direction dir) {
        return getEnergyHandler(tile, dir) != null;
    }

    @Nullable
    public static IItemEnergyHandler getEnergyHandler(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (ITEM_BLACKLIST.contains(stack.getItem())) {
            return null;
        }
        for (IItemEnergyHandler handler : ITEM_ENERGY_HANDLERS) {
            if (handler.hasCapability(stack)) {
                return handler;
            }
        }
        return null;
    }
}
