package fluxnetworks.common.handler;

import fluxnetworks.FluxNetworks;
import fluxnetworks.common.block.BlockCore;
import fluxnetworks.common.item.ItemCore;
import fluxnetworks.common.registry.RegistryBlocks;
import fluxnetworks.common.registry.RegistryItems;
import fluxnetworks.common.registry.RegistryRecipes;
import fluxnetworks.common.registry.RegistrySounds;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod.EventBusSubscriber
public class RegistryEventHandler {

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {

        event.getRegistry().registerAll(RegistryItems.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {

        event.getRegistry().registerAll(RegistryBlocks.BLOCKS.toArray(new Block[0]));
        TileEntityHandler.registerTileEntity();
        NetworkRegistry.INSTANCE.registerGuiHandler(FluxNetworks.instance, new GuiHandler());
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {

        RegistryRecipes.registerStorageRecipes(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {

        for(Item item : RegistryItems.ITEMS) {
            if(item instanceof ItemCore)
                ((ItemCore) item).registerModels();
        }

        for(Block block : RegistryBlocks.BLOCKS) {
            if(block instanceof BlockCore)
                ((BlockCore) block).registerModels();
        }
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        RegistrySounds.registerSounds(event.getRegistry());
    }
}
