package sonar.fluxnetworks.register;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientPlayerLeft(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        FluxNetworkCache.INSTANCE.clearClientCache();
        FluxColorHandler.reset();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        FluxColorHandler.sendRequests();
    }
}
