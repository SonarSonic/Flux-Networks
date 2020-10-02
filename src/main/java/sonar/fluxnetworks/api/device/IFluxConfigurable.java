package sonar.fluxnetworks.api.device;

import net.minecraft.nbt.CompoundNBT;

public interface IFluxConfigurable {

    CompoundNBT copyConfiguration(CompoundNBT config);

    void pasteConfiguration(CompoundNBT config);
}
