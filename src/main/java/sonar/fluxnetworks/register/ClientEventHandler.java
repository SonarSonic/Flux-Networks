package sonar.fluxnetworks.register;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        FluxNetworkCache.INSTANCE.clearClientCache();
        FluxColorHandler.INSTANCE.reset();
    }

    @SubscribeEvent
    public static void onClientTick(@Nonnull TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            FluxColorHandler.INSTANCE.tick();
        }
    }
}
