package sonar.flux.connection;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import sonar.core.api.energy.EnergyType;
import sonar.core.handlers.energy.IEnergyHandler;
import sonar.core.handlers.energy.IEnergyTransferProxy;
import sonar.core.utils.Pair;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;

import java.util.ArrayList;
import java.util.List;

public class FNEnergyTransferProxy implements IEnergyTransferProxy {

    @Override
    public double getRFConversion(EnergyType type) {
        switch(type){
            case FE:
                return FluxConfig.FORGE_ENERGY_RF_CONVERSION;
            case TESLA:
                return FluxConfig.TESLA_RF_CONVERSION;
            case RF:
                return FluxConfig.REDSTONE_FLUX_RF_CONVERSION;
            case EU:
                return FluxConfig.ENERGY_UNITS_RF_CONVERSION;
            case MJ:
                return FluxConfig.MINECRAFT_JOULES_RF_CONVERSION;
            case AE:
                return FluxConfig.APPLIED_ENERGISTICS_RF_CONVERSION;
        }
        return FluxConfig.FORGE_ENERGY_RF_CONVERSION;
    }

    @Override
    public boolean isItemEnergyTypeEnabled(EnergyType type){
        Pair<Boolean, Boolean> canTransfer = FluxConfig.transfer_types.get(type);
        return canTransfer != null && canTransfer.a;
    }

    @Override
    public boolean isTileEnergyTypeEnabled(EnergyType type){
        Pair<Boolean, Boolean> canTransfer = FluxConfig.transfer_types.get(type);
        return canTransfer != null && canTransfer.a;
    }

    @Override
    public boolean canConnectTile(TileEntity tile, EnumFacing face){
        return !FluxNetworks.block_connection_blacklist.contains(tile.getBlockType());
    }

    @Override
    public boolean canConnectItem(ItemStack stack){
        return !FluxNetworks.item_connection_blacklist.contains(stack.getItem());
    }

    @Override
    public boolean canConvert(IEnergyHandler to, IEnergyHandler from){
        return FluxConfig.conversion.get(from).contains(to);
    }

    public static boolean checkOverride(EnergyType to, EnergyType from){
        return FluxConfig.conversion_override.get(from).contains(to);
    }

    public static <T extends IForgeRegistryEntry<T>> List<T> getBlackListedValues(IForgeRegistry<T> registry, String[] strings){
        List<T> blacklisted = new ArrayList<>();

        for(String s : strings){
            String[] split = s.split(":");
            if(split.length != 2){
                FluxNetworks.logger.error("BLACKLIST ERROR: " + s + " has incorrect formatting, please use 'modid:name'");
                continue;
            }
            ResourceLocation loc = new ResourceLocation(split[0], split[1]);
            T block = registry.getValue(loc);
            if(block == null){
                FluxNetworks.logger.info("BLACKLIST ISSUE: " + s + " no matching block/item was found");
                continue;
            }
            blacklisted.add(block);
            FluxNetworks.logger.info("BLACKLIST: " + s + " successfully black listed");
        }

        return blacklisted;
    }
}
