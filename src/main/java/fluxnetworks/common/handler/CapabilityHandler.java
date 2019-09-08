package fluxnetworks.common.handler;

import fluxnetworks.FluxNetworks;
import fluxnetworks.common.capabilities.CapabilitySAProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {

    public static final ResourceLocation SUPER_ADMIN = new ResourceLocation(FluxNetworks.MODID, "SuperAdmin");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof EntityPlayer) {
            event.addCapability(SUPER_ADMIN, new CapabilitySAProvider());
        }
    }
}
