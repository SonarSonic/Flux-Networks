package sonar.fluxnetworks.common.handler;

import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemEnergyHandler {

    public static List<IItemEnergyHandler> itemEnergyHandlers = new ArrayList<>();
    public static Map<String, Integer> itemBlackList = new HashMap<>();

    @Nullable
    public static IItemEnergyHandler getEnergyHandler(ItemStack stack) {
        String s = stack.getItem().getRegistryName().toString();
        if(itemBlackList.containsKey(s)) {
            int meta = itemBlackList.get(s);
            if(meta == -1)
                return null;
            else if(meta == stack.getItemDamage())
                return null;
        }
        for(IItemEnergyHandler handler : itemEnergyHandlers) {
            if(handler.canAddEnergy(stack) || handler.canRemoveEnergy(stack)) {
                return handler;
            }
        }
        return null;
    }
}
