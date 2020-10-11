package sonar.fluxnetworks.api.misc;

import net.minecraft.nbt.CompoundNBT;

@Deprecated
public interface IFluxConfigurable {

    CompoundNBT copyConfiguration(CompoundNBT config);

    void pasteConfiguration(CompoundNBT config);
}
