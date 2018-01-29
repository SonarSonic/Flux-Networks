package sonar.flux.api.configurator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sonar.flux.api.tiles.IFlux;

public interface IFluxConfigurable extends IFlux {

    NBTTagCompound addConfigs(NBTTagCompound config, EntityPlayer player);

    void readConfigs(NBTTagCompound config, EntityPlayer player);
}