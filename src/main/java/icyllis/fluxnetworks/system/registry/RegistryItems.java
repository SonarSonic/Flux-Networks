package icyllis.fluxnetworks.system.registry;

import icyllis.fluxnetworks.common.item.ItemFluxTile;
import icyllis.fluxnetworks.system.FluxItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

import static icyllis.fluxnetworks.system.registry.RegistryBlocks.*;

@SuppressWarnings("ConstantConditions")
public class RegistryItems {

    public static List<Item> ITEMS = new ArrayList<>();

    public static final Item FLUX = new Item(new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName("flux");

    static {
        ITEMS.add(FLUX);
        ITEMS.add(new BlockItem(FLUX_BLOCK, new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName(FLUX_BLOCK.getRegistryName()));
        ITEMS.add(new ItemFluxTile(FLUX_PLUG, new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName(FLUX_PLUG.getRegistryName()));
        ITEMS.add(new ItemFluxTile(FLUX_POINT, new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName(FLUX_POINT.getRegistryName()));
        ITEMS.add(new ItemFluxTile(FLUX_CONTROLLER, new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName(FLUX_CONTROLLER.getRegistryName()));
    }
}
