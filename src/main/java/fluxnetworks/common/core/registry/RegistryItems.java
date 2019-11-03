package fluxnetworks.common.core.registry;

import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RegistryItems {

    public static List<Item> ITEMS = new ArrayList<>();

    public static final Item FLUX = new Item(new Item.Properties()).setRegistryName("Flux".toLowerCase());
}
