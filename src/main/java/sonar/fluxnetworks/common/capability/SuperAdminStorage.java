package sonar.fluxnetworks.common.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SuperAdminStorage implements Capability.IStorage<ISuperAdmin> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<ISuperAdmin> capability, @Nonnull ISuperAdmin instance, Direction side) {
        return instance.writeToNBT(new CompoundNBT());
    }

    @Override
    public void readNBT(Capability<ISuperAdmin> capability, @Nonnull ISuperAdmin instance, Direction side, @Nonnull INBT nbt) {
        if (nbt.getType() == CompoundNBT.TYPE) {
            instance.readFromNBT((CompoundNBT) nbt);
        }
    }
}
