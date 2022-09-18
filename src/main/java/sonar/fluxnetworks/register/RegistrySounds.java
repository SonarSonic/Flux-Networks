package sonar.fluxnetworks.register;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;

public class RegistrySounds {
    private static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FluxNetworks.MODID);

    public static final RegistryObject<SoundEvent> BUTTON_CLICK = registerSoundEvent("button");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        var resourceLocation = FluxNetworks.rl(name);
        return REGISTRY.register(name, () -> new SoundEvent(resourceLocation));
    }

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
    private RegistrySounds() {}
}
