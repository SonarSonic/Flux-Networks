package fluxnetworks.common.handler.event;

import fluxnetworks.FluxNetworks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber()
public class SetupEventHandler {

    @SubscribeEvent
    public static void setupCommon(FMLCommonSetupEvent event) {
        FluxNetworks.logger.info("Common setup");
    }

    @SubscribeEvent
    public static void setupClient(FMLClientSetupEvent event) {
        FluxNetworks.logger.info("Client setup");
    }
}
