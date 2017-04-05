package sonar.flux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class FluxConfiguration {
	
	public boolean hasConfig;
	public NBTTagCompound toWrite = new NBTTagCompound();
	public FluxConfigurationType type;

	public FluxConfiguration(FluxConfigurationType type) {
		this.type = type;
	}
}
