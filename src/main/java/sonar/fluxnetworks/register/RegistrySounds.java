package sonar.fluxnetworks.register;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ObjectHolder;
import sonar.fluxnetworks.FluxNetworks;

@ObjectHolder(FluxNetworks.MODID)
public class RegistrySounds {

    @ObjectHolder("button")
    public static SoundEvent BUTTON_CLICK;
}
