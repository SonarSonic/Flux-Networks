package fluxnetworks.common.handler.event;

import fluxnetworks.FluxNetworks;
import fluxnetworks.common.core.registry.RegistryBlocks;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Inventory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;

@Mod.EventBusSubscriber
public class TickEventHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {

    }
}
