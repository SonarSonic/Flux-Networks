package sonar.fluxnetworks.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.network.ISuperAdmin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A class holding the super admin instance of each player
 */
public class SuperAdminProvider implements ICapabilitySerializable<Tag> {

    @Nonnull
    private final ISuperAdmin instance;

    private final LazyOptional<ISuperAdmin> handler;

    public SuperAdminProvider() {
        instance = new SuperAdmin();
        handler = LazyOptional.of(this::getInstance);
    }

    @Nonnull
    public ISuperAdmin getInstance() {
        return instance;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return FluxCapabilities.SUPER_ADMIN.orEmpty(cap, handler);
    }

    @Override
    public Tag serializeNBT() {
        return instance.writeNBT();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        instance.readNBT((ByteTag) nbt);
    }
}
