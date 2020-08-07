package sonar.fluxnetworks.common.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.api.utils.Capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A class holding the super admin instance of each player
 */
public class SuperAdminProvider implements ICapabilitySerializable<INBT> {

    @Nonnull
    private final ISuperAdmin instance;

    private final LazyOptional<ISuperAdmin> handler;

    public SuperAdminProvider() {
        //noinspection ConstantConditions
        instance = Capabilities.SUPER_ADMIN.getDefaultInstance();
        handler = LazyOptional.of(this::getInstance);
    }

    @Nonnull
    public ISuperAdmin getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.SUPER_ADMIN.orEmpty(cap, handler);
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
