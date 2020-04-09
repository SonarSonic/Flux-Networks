package sonar.fluxnetworks.api.tiles;

import net.minecraft.nbt.CompoundNBT;

public interface IFluxConfigurable {

    CompoundNBT copyConfiguration(CompoundNBT config);

    void pasteConfiguration(CompoundNBT config);
}
