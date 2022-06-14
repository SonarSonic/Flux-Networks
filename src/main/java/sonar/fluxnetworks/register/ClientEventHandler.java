package sonar.fluxnetworks.register;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.client.ClientCache;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = FluxNetworks.MODID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
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
