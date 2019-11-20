package fluxnetworks.system.registry;

import fluxnetworks.common.block.BlockFluxPlug;
import fluxnetworks.common.block.BlockFluxPoint;
import fluxnetworks.common.item.ItemFluxDevice;
import fluxnetworks.system.FluxItemGroup;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class RegistryBlocks {

    public static List<Block> BLOCKS = new ArrayList<>();

    public static final Block FLUX_BLOCK = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5f, 17.5f)).setRegistryName("fluxblock");
    public static final Block FLUX_PLUG = new BlockFluxPlug().setRegistryName("fluxplug");
    public static final Block FLUX_POINT = new BlockFluxPoint().setRegistryName("fluxpoint");

    static {
        BLOCKS.add(FLUX_BLOCK);
        BLOCKS.add(FLUX_PLUG);
        BLOCKS.add(FLUX_POINT);
        RegistryItems.ITEMS.add(new BlockItem(FLUX_BLOCK, new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName(FLUX_BLOCK.getRegistryName()));
        RegistryItems.ITEMS.add(new ItemFluxDevice(FLUX_PLUG, new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName(FLUX_PLUG.getRegistryName()));
        RegistryItems.ITEMS.add(new ItemFluxDevice(FLUX_POINT, new Item.Properties().group(FluxItemGroup.GROUP)).setRegistryName(FLUX_POINT.getRegistryName()));
    }
}
