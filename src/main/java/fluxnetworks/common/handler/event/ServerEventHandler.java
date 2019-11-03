package fluxnetworks.common.handler.event;

import fluxnetworks.FluxNetworks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

@Mod.EventBusSubscriber
public class ServerEventHandler {

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        FluxNetworks.logger.info("server stopped");
    }
}
