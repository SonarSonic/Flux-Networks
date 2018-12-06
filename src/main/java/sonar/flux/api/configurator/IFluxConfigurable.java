package sonar.flux.api.configurator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sonar.flux.api.tiles.IFlux;

public interface IFluxConfigurable extends IFlux {

    NBTTagCompound copyConfiguration(NBTTagCompound config, NBTTagCompound disabled, EntityPlayer player);

    void pasteConfiguration(NBTTagCompound config, NBTTagCompound disabled, EntityPlayer player);
}