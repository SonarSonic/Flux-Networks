package sonar.fluxnetworks.common.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.OpEntry;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.utils.Capabilities;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class DefaultSuperAdmin implements ISuperAdmin {

    private boolean SA = false;

    @Override
    public void changePermission() {
        SA = !SA;
    }

    @Override
    public boolean getPermission() {
        return SA;
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        nbt.putBoolean("SA", SA);
        return nbt;
    }

    @Override
    public void readFromNBT(CompoundNBT nbt) {
        SA = nbt.getBoolean("SA");
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ISuperAdmin.class, new DefaultSAStorage(), DefaultSuperAdmin::new);
    }

    //// UTIL METHODS \\\\

    public static boolean canActivateSuperAdmin(PlayerEntity player){
        if(ServerLifecycleHooks.getCurrentServer().isSinglePlayer()){
            return true;
        }
        OpEntry opEntry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
        return opEntry != null && opEntry.getPermissionLevel() >= FluxConfig.superAdminRequiredPermission;
    }

    public static boolean isPlayerSuperAdmin(PlayerEntity player){
        if(!player.world.isRemote) {
            ISuperAdmin iSuperAdmin = player.getCapability(Capabilities.SUPER_ADMIN, null).orElse(null);
            return iSuperAdmin != null && iSuperAdmin.getPermission();
        }
        return FluxNetworkCache.instance.superAdminClient;
    }

}
