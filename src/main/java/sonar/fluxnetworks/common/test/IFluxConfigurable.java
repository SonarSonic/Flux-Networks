package sonar.fluxnetworks.common.test;

import net.minecraft.nbt.CompoundNBT;

@Deprecated
public interface IFluxConfigurable {

    CompoundNBT copyConfiguration(CompoundNBT config);

    void pasteConfiguration(CompoundNBT config);
}
