package sonar.fluxnetworks.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

/**
 * Flux Networks capability to {@link net.minecraft.world.entity.player.Player}.
 */
public class FluxPlayer {

    public static final Capability<FluxPlayer> FLUX_PLAYER = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final String SUPER_ADMIN_KEY = "superAdmin";

    private boolean mSuperAdmin;
    private int mWirelessMode;
    private int mWirelessNetwork;

    public boolean isSuperAdmin() {
        return mSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        mSuperAdmin = superAdmin;
    }

    public int getWirelessMode() {
        return mWirelessMode;
    }

    public void setWirelessMode(int wirelessMode) {
        mWirelessMode = wirelessMode;
    }

    public int getWirelessNetwork() {
        return mWirelessNetwork;
    }

    public void setWirelessNetwork(int wirelessNetwork) {
        mWirelessNetwork = wirelessNetwork;
    }

    public void writeNBT(@Nonnull CompoundTag tag) {
        tag.putBoolean(SUPER_ADMIN_KEY, mSuperAdmin);
        tag.putInt("wirelessMode", mWirelessMode);
        tag.putInt("wirelessNetwork", mWirelessNetwork);
    }

    public void readNBT(@Nonnull CompoundTag tag) {
        mSuperAdmin = tag.getBoolean(SUPER_ADMIN_KEY);
        mWirelessMode = tag.getInt("wirelessMode");
        mWirelessNetwork = tag.getInt("wirelessNetwork");
    }

    //// UTIL METHODS \\\\

    // server side only
    public static boolean canActivateSuperAdmin(Player player) {
        return FluxConfig.enableSuperAdmin && player.hasPermissions(FluxConfig.superAdminRequiredPermission);
    }

    // server side only
    public static boolean isPlayerSuperAdmin(@Nonnull Player player) {
        if (FluxConfig.enableSuperAdmin) {
            FluxPlayer fluxPlayer = FluxUtils.get(player, FLUX_PLAYER);
            return fluxPlayer != null && fluxPlayer.isSuperAdmin();
        }
        return false;
    }
}
