package sonar.fluxnetworks.common.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.capability.SuperAdminProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;

public class CapabilityHandler {

    private static final ResourceLocation SUPER_ADMIN = new ResourceLocation(FluxNetworks.MODID, "superadmin");

    @SubscribeEvent
    public void attachCapability(@Nonnull AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(SUPER_ADMIN, new SuperAdminProvider());
        }
    }
}
