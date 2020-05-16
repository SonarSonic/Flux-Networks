package sonar.fluxnetworks.common.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.OpEntry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.api.utils.Capabilities;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;

import javax.annotation.Nonnull;

public class SuperAdminInstance implements ISuperAdmin {

    public static final SuperAdminInstance DEFAULT = new SuperAdminInstance();

    private boolean superAdmin = false;

    @Override
    public void iterateSuperAdmin() {
        superAdmin = !superAdmin;
    }

    @Override
    public boolean isSuperAdmin() {
        return superAdmin;
    }

    @Override
    public CompoundNBT writeToNBT(@Nonnull CompoundNBT nbt) {
        nbt.putBoolean("b", superAdmin);
        return nbt;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundNBT nbt) {
        superAdmin = nbt.getBoolean("b");
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ISuperAdmin.class, new SuperAdminStorage(), SuperAdminInstance::new);
    }

    //// UTIL METHODS \\\\

    public static boolean canActivateSuperAdmin(PlayerEntity player) {
        if (ServerLifecycleHooks.getCurrentServer().isSinglePlayer()) {
            return true;
        }
        OpEntry opEntry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
        return opEntry != null && opEntry.getPermissionLevel() >= FluxConfig.superAdminRequiredPermission;
    }

    public static boolean isPlayerSuperAdmin(@Nonnull PlayerEntity player) {
        if (!player.world.isRemote) {
            return player.getCapability(Capabilities.SUPER_ADMIN).orElse(SuperAdminInstance.DEFAULT).isSuperAdmin();
        }
        return FluxNetworkCache.INSTANCE.superAdminClient;
    }

}
