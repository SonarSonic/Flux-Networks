package sonar.flux.api;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

/** by default the energy stats are synced every tick, this may change to make stuff more efficient in the future*/
public class EnergyStats implements INBTSyncable{
	public long transfer, maxSent,maxReceived;
	public String tagName = "stats";		
	
	public EnergyStats(long transfer, long maxSent, long maxReceived) {
		this.transfer=transfer;
		this.maxSent=maxSent;
		this.maxReceived=maxReceived;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = nbt.getCompoundTag(tagName);
		transfer = tag.getLong("t");
		maxSent = tag.getLong("ms");
		maxReceived = tag.getLong("mr");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong("t", transfer);
		tag.setLong("ms", maxSent);
		tag.setLong("mr", maxReceived);
		nbt.setTag(tagName, tag);
		return nbt;
	}	
}