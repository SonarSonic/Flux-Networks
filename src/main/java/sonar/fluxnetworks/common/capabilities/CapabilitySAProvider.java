package sonar.fluxnetworks.common.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.api.utils.Capabilities;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySAProvider implements ICapabilitySerializable<INBT> {

    private ISuperAdmin instance = Capabilities.SUPER_ADMIN.getDefaultInstance();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == Capabilities.SUPER_ADMIN ? LazyOptional.of(() -> instance).cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return Capabilities.SUPER_ADMIN.writeNBT(instance, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        Capabilities.SUPER_ADMIN.readNBT(instance, null, nbt);
    }
}
