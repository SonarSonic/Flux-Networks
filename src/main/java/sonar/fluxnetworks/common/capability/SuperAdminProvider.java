package sonar.fluxnetworks.common.capability;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.api.utils.Capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class SuperAdminProvider implements ICapabilityProvider {

    @Nonnull
    private final ISuperAdmin instance;

    public SuperAdminProvider() {
        this.instance = Objects.requireNonNull(Capabilities.SUPER_ADMIN.getDefaultInstance());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.SUPER_ADMIN.orEmpty(cap, LazyOptional.of(() -> instance));
    }
}
