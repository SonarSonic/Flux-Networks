package sonar.fluxnetworks.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import sonar.fluxnetworks.FluxNetworks;

public class RegistrySounds {
    public static final ResourceLocation BUTTON_CLICK_KEY = FluxNetworks.location("button");

    public static final RegistryObject<SoundEvent> BUTTON_CLICK = RegistryObject.create(BUTTON_CLICK_KEY, ForgeRegistries.SOUND_EVENTS);

    static void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
        helper.register(BUTTON_CLICK_KEY, SoundEvent.createVariableRangeEvent(BUTTON_CLICK_KEY));
    }

    private RegistrySounds() {}
}
