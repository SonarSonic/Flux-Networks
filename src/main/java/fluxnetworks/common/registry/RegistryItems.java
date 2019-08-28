package fluxnetworks.common.registry;

import fluxnetworks.common.item.ItemCore;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class RegistryItems {

    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final Item FLUX = new ItemCore("Flux");
    public static final Item FLUX_CORE = new ItemCore("FluxCore");

}
