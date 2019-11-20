package fluxnetworks.system.registry;

import fluxnetworks.system.FluxItemGroup;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RegistryItems {

    public static List<Item> ITEMS = new ArrayList<>();

    public static final Item FLUX = new Item(new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName("flux");

    static {
        ITEMS.add(FLUX);
    }
}
