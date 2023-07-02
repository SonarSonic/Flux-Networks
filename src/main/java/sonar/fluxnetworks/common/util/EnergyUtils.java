package sonar.fluxnetworks.common.util;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.IBlockEnergyConnector;
import sonar.fluxnetworks.api.energy.IItemEnergyConnector;
import sonar.fluxnetworks.common.integration.energy.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public final class EnergyUtils {

    private static final Marker MARKER = MarkerManager.getMarker("Energy");

    private static final List<IBlockEnergyConnector> BLOCK_ENERGY_CONNECTORS = new ArrayList<>();
    private static final Set<Block> BLOCK_BLACKLIST = new HashSet<>();

    private static final List<IItemEnergyConnector> ITEM_ENERGY_CONNECTORS = new ArrayList<>();
    private static final Set<Item> ITEM_BLACKLIST = new HashSet<>();

    static {
        BLOCK_ENERGY_CONNECTORS.add(FNEnergyConnector.INSTANCE);
        ITEM_ENERGY_CONNECTORS.add(FNEnergyConnector.INSTANCE);

        BLOCK_ENERGY_CONNECTORS.add(ForgeEnergyConnector.INSTANCE);
        ITEM_ENERGY_CONNECTORS.add(ForgeEnergyConnector.INSTANCE);
    }

    private EnergyUtils() {
    }

    public static void register() {
        /* TODO PORT OTHER MOD ENERGY HANDLERS.
        if(Loader.isModLoaded("gregtech")) {
            tileEnergyHandlers.add(GTEnergyHandler.INSTANCE);
            ItemEnergyHandler.itemEnergyHandlers.add(GTEnergyHandler.INSTANCE);
        }*/

        // disable because of imbalance
        /*if (ModList.get().isLoaded("ic2")) {
            BLOCK_ENERGY_CONNECTORS.add(IC2EnergyHandler.INSTANCE);
            ITEM_ENERGY_CONNECTORS.add(IC2EnergyHandler.INSTANCE);
        }*/
    }

    public static void reloadBlacklist(@Nonnull List<String> blockBlacklist, @Nonnull List<String> itemBlacklist) {
        BLOCK_BLACKLIST.clear();
        for (String s : blockBlacklist) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            try {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
                if (block != null) {
                    BLOCK_BLACKLIST.add(block);
                }
            } catch (Exception e) {
                FluxNetworks.LOGGER.warn(MARKER, "Block blacklist error: {} has incorrect formatting", s, e);
            }
        }
        ITEM_BLACKLIST.clear();
        for (String s : itemBlacklist) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            try {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                if (item != null) {
                    ITEM_BLACKLIST.add(item);
                }
            } catch (Exception e) {
                FluxNetworks.LOGGER.warn(MARKER, "Item blacklist error: {} has incorrect formatting", s, e);
            }
        }
        FluxNetworks.LOGGER.info(MARKER, "Energy blacklist loaded: {} block entries, {} item entries",
                BLOCK_BLACKLIST.size(), ITEM_BLACKLIST.size());
    }

    @Nullable
    public static IBlockEnergyConnector getConnector(@Nullable BlockEntity target, @Nonnull Direction side) {
        if (target == null) {
            return null;
        }
        if (target.isRemoved()) {
            return null;
        }
        if (target instanceof IFluxDevice) {
            return null;
        }
        if (BLOCK_BLACKLIST.contains(target.getBlockState().getBlock())) {
            return null;
        }
        for (IBlockEnergyConnector connector : BLOCK_ENERGY_CONNECTORS) {
            if (connector.hasCapability(target, side)) {
                return connector;
            }
        }
        return null;
    }

    @Nullable
    public static IItemEnergyConnector getConnector(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.isEmpty()) {
            return null;
        }
        if (ITEM_BLACKLIST.contains(stack.getItem())) {
            return null;
        }
        for (IItemEnergyConnector connector : ITEM_ENERGY_CONNECTORS) {
            if (connector.hasCapability(stack)) {
                return connector;
            }
        }
        return null;
    }
}
