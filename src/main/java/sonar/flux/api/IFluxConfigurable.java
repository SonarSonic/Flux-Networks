package sonar.flux.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IFluxConfigurable extends IFlux {

	public NBTTagCompound addConfigs(NBTTagCompound config, EntityPlayer player);
	
	public void readConfigs(NBTTagCompound config, EntityPlayer player);
	
	public boolean canAccess(EntityPlayer player);
}
