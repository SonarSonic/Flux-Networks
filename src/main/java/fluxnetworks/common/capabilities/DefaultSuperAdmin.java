package fluxnetworks.common.capabilities;

import fluxnetworks.api.network.ISuperAdmin;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class DefaultSuperAdmin implements ISuperAdmin {

    public boolean SA = false;

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
}
