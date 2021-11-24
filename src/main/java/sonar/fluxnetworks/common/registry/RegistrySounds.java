package sonar.fluxnetworks.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

public class RegistrySounds {

    public static SoundEvent BUTTON_CLICK;

    public static void registerSounds(IForgeRegistry<SoundEvent> registry) {
        BUTTON_CLICK = registerSound(registry, "button");
    }

    @Nonnull
    public static SoundEvent registerSound(@Nonnull IForgeRegistry<SoundEvent> registry, String soundName) {
        ResourceLocation soundID = new ResourceLocation(FluxNetworks.MODID, soundName);
        SoundEvent event = new SoundEvent(soundID).setRegistryName(soundID);
        registry.register(event);
        return event;
    }
}
