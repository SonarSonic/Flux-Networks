package sonar.fluxnetworks.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class DefaultSAStorage implements Capability.IStorage<ISuperAdmin> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<ISuperAdmin> capability, ISuperAdmin instance, Direction side) {
        return instance.writeToNBT(new CompoundNBT());
    }

    @Override
    public void readNBT(Capability<ISuperAdmin> capability, ISuperAdmin instance, Direction side, INBT nbt) {
        instance.readFromNBT((CompoundNBT) nbt);
    }
}
