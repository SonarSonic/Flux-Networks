package sonar.flux.api.configurator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import sonar.flux.api.tiles.IFlux;

public interface IFluxConfigurable extends IFlux {

	public NBTTagCompound addConfigs(NBTTagCompound config, EntityPlayer player);
	
	public void readConfigs(NBTTagCompound config, EntityPlayer player);
	
	public boolean canAccess(EntityPlayer player);
}
