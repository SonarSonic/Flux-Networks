package sonar.fluxnetworks.common.util;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.energy.IBlockEnergyBridge;
import sonar.fluxnetworks.api.energy.IItemEnergyBridge;
import sonar.fluxnetworks.common.integration.energy.FNEnergyBridge;
import sonar.fluxnetworks.common.integration.energy.ForgeEnergyBridge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnergyUtils {

    private static final List<IBlockEnergyBridge> sBlockEnergyBridges = new ArrayList<>();
    private static final Set<Block> sBlockBlacklist = new HashSet<>();

    private static final List<IItemEnergyBridge> sItemEnergyBridges = new ArrayList<>();
    private static final Set<Item> sItemBlacklist = new HashSet<>();

    static {
        sBlockEnergyBridges.add(FNEnergyBridge.INSTANCE);
        sItemEnergyBridges.add(FNEnergyBridge.INSTANCE);

        sBlockEnergyBridges.add(ForgeEnergyBridge.INSTANCE);
        sItemEnergyBridges.add(ForgeEnergyBridge.INSTANCE);

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
        sBlockBlacklist.clear();
        for (String s : FluxConfig.blockBlacklistStrings) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            try {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
                if (block != null) {
                    sBlockBlacklist.add(block);
                }
            } catch (Exception e) {
                FluxNetworks.LOGGER.warn("BLACKLIST ERROR: " + s + " has incorrect formatting", e);
            }
        }
        sItemBlacklist.clear();
        for (String s : FluxConfig.itemBlackListStrings) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            try {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
                if (item != null) {
                    sItemBlacklist.add(item);
                }
            } catch (Exception e) {
                FluxNetworks.LOGGER.warn("BLACKLIST ERROR: " + s + " has incorrect formatting", e);
            }
        }
        FluxNetworks.LOGGER.info("BLACKLIST RELOADED");
    }

    @Nullable
    public static IBlockEnergyBridge getBridge(@Nullable BlockEntity target, @Nonnull Direction side) {
        if (target == null) {
            return null;
        }
        if (target instanceof IFluxDevice) {
            return null;
        }
        if (sBlockBlacklist.contains(target.getBlockState().getBlock())) {
            return null;
        }
        for (IBlockEnergyBridge handler : sBlockEnergyBridges) {
            if (handler.hasCapability(target, side)) {
                return handler;
            }
        }
        return null;
    }

    @Nullable
    public static IItemEnergyBridge getBridge(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }
        if (stack.isEmpty()) {
            return null;
        }
        if (sItemBlacklist.contains(stack.getItem())) {
            return null;
        }
        for (IItemEnergyBridge handler : sItemEnergyBridges) {
            if (handler.hasCapability(stack)) {
                return handler;
            }
        }
        return null;
    }
}
