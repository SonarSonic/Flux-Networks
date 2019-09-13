package fluxnetworks.common.handler;

import fluxnetworks.FluxNetworks;
import fluxnetworks.api.energy.IItemEnergyHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemEnergyHandler {

    public static List<IItemEnergyHandler> itemEnergyHandlers = new ArrayList<>();

    @Nullable
    public static IItemEnergyHandler getEnergyHandler(ItemStack stack) {
        for(IItemEnergyHandler handler : itemEnergyHandlers) {
            if(handler.canAddEnergy(stack) || handler.canRemoveEnergy(stack)) {
                return handler;
            }
        }
        return null;
    }
}
