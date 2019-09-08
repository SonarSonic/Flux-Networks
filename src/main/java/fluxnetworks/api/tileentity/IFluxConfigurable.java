package fluxnetworks.api.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IFluxConfigurable {

    NBTTagCompound copyConfiguration(NBTTagCompound config);

    void pasteConfiguration(NBTTagCompound config);
}
