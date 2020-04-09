package sonar.fluxnetworks.common.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.capabilities.CapabilitySAProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class CapabilityHandler {

    private static final ResourceLocation SUPER_ADMIN = new ResourceLocation(FluxNetworks.MODID, "superadmin");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity) {
            event.addCapability(SUPER_ADMIN, new CapabilitySAProvider());
        }
    }
}
