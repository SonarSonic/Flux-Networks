package sonar.fluxnetworks.common.capability;

import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class SuperAdmin implements ISuperAdmin {

    public static final ResourceLocation CAP_KEY = new ResourceLocation(FluxNetworks.MODID, "super_admin");

    private byte permission;

    @Override
    public void changePermission() {
        permission ^= 1;
    }

    @Override
    public boolean hasPermission() {
        return permission != 0;
    }

    @Override
    public ByteTag writeNBT() {
        return ByteTag.valueOf(permission);
    }

    @Override
    public void readNBT(@Nonnull ByteTag nbt) {
        permission = nbt.getAsByte();
    }

    //// UTIL METHODS \\\\

    public static boolean canActivateSuperAdmin(Player player) {
        if (ServerLifecycleHooks.getCurrentServer().isSingleplayer()) {
            return true;
        }
        if (FluxConfig.enableSuperAdmin) {
            ServerOpListEntry opEntry =
                    ServerLifecycleHooks.getCurrentServer().getPlayerList().getOps().get(player.getGameProfile());
            return opEntry != null && opEntry.getLevel() >= FluxConfig.superAdminRequiredPermission;
        }
        return false;
    }

    public static boolean isPlayerSuperAdmin(@Nonnull Player player) {
        ISuperAdmin instance = FluxUtils.get(player.getCapability(FluxCapabilities.SUPER_ADMIN));
        return instance != null && instance.hasPermission();
    }
}
