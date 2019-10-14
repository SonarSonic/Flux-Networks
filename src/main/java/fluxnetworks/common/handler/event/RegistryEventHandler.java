package fluxnetworks.common.handler.event;

import fluxnetworks.FluxNetworks;
import fluxnetworks.common.core.registry.RegistryBlocks;
import fluxnetworks.common.core.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEventHandler {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        RegistryBlocks.BLOCKS.forEach(b -> event.getRegistry().register(b));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        RegistryItems.ITEMS.forEach(i -> event.getRegistry().register(i));
    }
}
