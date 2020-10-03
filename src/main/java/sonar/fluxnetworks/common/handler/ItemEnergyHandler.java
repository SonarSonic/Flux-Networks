package sonar.fluxnetworks.common.handler;

import net.minecraft.item.ItemStack;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEnergyHandler {

    public static final List<IItemEnergyHandler> ITEM_ENERGY_HANDLERS = new ArrayList<>();
    public static final Map<String, Integer> ITEM_BLACKLIST = new HashMap<>();

    @Nullable
    public static IItemEnergyHandler getEnergyHandler(ItemStack stack) {
        String s = stack.getItem().getRegistryName().toString();
        if (ITEM_BLACKLIST.containsKey(s)) {
            return null;
        }
        for (IItemEnergyHandler handler : ITEM_ENERGY_HANDLERS) {
            if (handler.canAddEnergy(stack) || handler.canRemoveEnergy(stack)) {
                return handler;
            }
        }
        return null;
    }
}
