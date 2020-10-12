package sonar.fluxnetworks.common.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.misc.FluxCapabilities;
import sonar.fluxnetworks.api.network.ISuperAdmin;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public ByteNBT writeNBT() {
        return ByteNBT.valueOf(permission);
    }

    @Override
    public void readNBT(@Nonnull ByteNBT nbt) {
        permission = nbt.getByte();
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ISuperAdmin.class, new SuperAdminStorage(), SuperAdmin::new);
    }

    //// UTIL METHODS \\\\

    public static boolean canActivateSuperAdmin(PlayerEntity player) {
        if (ServerLifecycleHooks.getCurrentServer().isSinglePlayer()) {
            return true;
        }
        if (FluxConfig.enableSuperAdmin) {
            OpEntry opEntry = ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
            return opEntry != null && opEntry.getPermissionLevel() >= FluxConfig.superAdminRequiredPermission;
        }
        return false;
    }

    public static boolean isPlayerSuperAdmin(@Nonnull PlayerEntity player) {
        if (!player.world.isRemote) {
            ISuperAdmin instance = FluxUtils.getCap(player, FluxCapabilities.SUPER_ADMIN);
            return instance != null && instance.hasPermission();
        }
        return FluxClientCache.superAdmin;
    }

    private static class SuperAdminStorage implements Capability.IStorage<ISuperAdmin> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<ISuperAdmin> capability, @Nonnull ISuperAdmin instance, Direction side) {
            return instance.writeNBT();
        }

        @Override
        public void readNBT(Capability<ISuperAdmin> capability, @Nonnull ISuperAdmin instance, Direction side, @Nonnull INBT nbt) {
            instance.readNBT((ByteNBT) nbt);
        }
    }
}
