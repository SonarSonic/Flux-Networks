package sonar.fluxnetworks.common.capabilities;

import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.utils.Capabilities;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("SA", SA);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        SA = nbt.getBoolean("SA");
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ISuperAdmin.class, new DefaultSAStorage(), DefaultSuperAdmin::new);
    }

    //// UTIL METHODS \\\\

    public static boolean canActivateSuperAdmin(EntityPlayer player){
        if(FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()){
            return true;
        }
        int permissionLevel = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().getPermissionLevel(player.getGameProfile());
        return permissionLevel >= FluxConfig.superAdminRequiredPermission;
    }

    public static boolean isPlayerSuperAdmin(EntityPlayer player){
        if(!player.world.isRemote) {
            ISuperAdmin iSuperAdmin = player.getCapability(Capabilities.SUPER_ADMIN, null);
            return iSuperAdmin != null && iSuperAdmin.getPermission();
        }
        return FluxNetworkCache.instance.superAdminClient;
    }

}
