package sonar.fluxnetworks.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import net.neoforged.neoforge.registries.DeferredRegister;
import sonar.fluxnetworks.FluxNetworks;

import java.util.function.Supplier;

public class RegistrySounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, FluxNetworks.MODID);

    public static final ResourceLocation BUTTON_CLICK_KEY = FluxNetworks.location("button");

    public static final Supplier<SoundEvent> BUTTON_CLICK = SOUNDS.register("button", () -> SoundEvent.createVariableRangeEvent(BUTTON_CLICK_KEY));

//    public static final RegistryObject<SoundEvent> BUTTON_CLICK = RegistryObject.create(BUTTON_CLICK_KEY, ForgeRegistries.SOUND_EVENTS);

    private RegistrySounds() {}
}
