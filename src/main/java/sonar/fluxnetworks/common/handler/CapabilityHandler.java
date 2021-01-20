package sonar.fluxnetworks.common.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.capabilities.CapabilitySAProvider;

public class CapabilityHandler {

    private static final ResourceLocation SUPER_ADMIN = new ResourceLocation(FluxNetworks.MODID, "SuperAdmin");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(SUPER_ADMIN, new CapabilitySAProvider());
        }
    }
}
