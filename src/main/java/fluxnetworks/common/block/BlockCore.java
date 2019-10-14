package fluxnetworks.common.block;

import fluxnetworks.common.core.registry.RegistryBlocks;
import fluxnetworks.common.core.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BlockCore extends Block {

    public BlockCore(String name, Properties properties) {
        super(properties);
        setRegistryName(name.toLowerCase());
        RegistryBlocks.BLOCKS.add(this);
        RegistryItems.ITEMS.add(new BlockItem(this, new Item.Properties()).setRegistryName(this.getRegistryName()));
    }

}
