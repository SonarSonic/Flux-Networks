package sonar.fluxnetworks.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

/**
 * Flux Networks capability to {@link net.minecraft.world.entity.player.Player}.
 */
public class FluxPlayer {

    public static final Capability<FluxPlayer> FLUX_PLAYER = CapabilityManager.get(new CapabilityToken<>() {
    });

    private boolean mSuperAdmin;

    public boolean isSuperAdmin() {
        return mSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        mSuperAdmin = superAdmin;
    }

    public void writeNBT(@Nonnull CompoundTag tag) {
        tag.putBoolean("superAdmin", mSuperAdmin);
    }

    public void readNBT(@Nonnull CompoundTag tag) {
        mSuperAdmin = tag.getBoolean("superAdmin");
    }

    //// UTIL METHODS \\\\

    // server side only
    public static boolean canActivateSuperAdmin(Player player) {
        return FluxConfig.enableSuperAdmin && player.hasPermissions(FluxConfig.superAdminRequiredPermission);
    }

    // server side only
    public static boolean isPlayerSuperAdmin(@Nonnull Player player) {
        if (FluxConfig.enableSuperAdmin) {
            FluxPlayer instance = FluxUtils.get(player.getCapability(FLUX_PLAYER));
            return instance != null && instance.isSuperAdmin();
        }
        return false;
    }
}
