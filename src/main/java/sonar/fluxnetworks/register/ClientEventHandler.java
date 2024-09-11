package sonar.fluxnetworks.register;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.client.ClientCache;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = FluxNetworks.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCache.release();
        //FluxColorHandler.INSTANCE.reset();
    }

    /*@SubscribeEvent
    public static void onClientTick(@Nonnull TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            //FluxClientCache.tick();
            //FluxColorHandler.INSTANCE.tick();
        }
    }*/
}
