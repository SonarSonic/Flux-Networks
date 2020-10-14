package sonar.fluxnetworks.register;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sonar.fluxnetworks.client.FluxClientCache;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class ClientEventHandler {

    @SubscribeEvent
    public static void onPlayerLeft(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        FluxClientCache.release();
        //FluxColorHandler.INSTANCE.reset();
    }

    @SubscribeEvent
    public static void onClientTick(@Nonnull TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            FluxClientCache.tick();
            //FluxColorHandler.INSTANCE.tick();
        }
    }
}
