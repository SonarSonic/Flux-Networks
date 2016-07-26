package sonar.flux.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.CustomColour;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxCommon.AccessType;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.connection.BasicFluxNetwork;

public class NetworkData extends WorldSavedData {

	public static final String tag = "sonar.flux.networks.configurations";

	public NetworkData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		FluxNetworks.cache.uniqueID = nbt.getInteger("uniqueID");
		NBTTagList list = nbt.getTagList("networks", 10);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			CustomColour colour =new CustomColour(0,0,0);
			colour.readData(tag.getCompoundTag("colour"), SyncType.SAVE);
			BasicFluxNetwork loaded = new BasicFluxNetwork(tag.getInteger("id"), tag.getString("owner"), tag.getString("name"), colour, AccessType.valueOf(tag.getString("access")));
			FluxNetworks.cache.addNetwork(loaded);
			//FluxNetworks.cache.reloadNetwork(tag.getInteger("id"), tag.getString("owner"), tag.getString("name"), colour, AccessType.valueOf(tag.getString("access")));
			FluxNetworks.logger.info("[LOADED NETWORK] '" + loaded.getNetworkName() + "' with ID '" + loaded.getNetworkID());
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("uniqueID", FluxNetworks.cache.uniqueID);
		NBTTagList list = new NBTTagList();
		for (IFluxNetwork network : FluxNetworks.cache.getAllNetworks()) {
			NBTTagCompound tag = new NBTTagCompound();
			//network.writeData(nbt, SyncType.SAVE);			
			tag.setInteger("id", network.getNetworkID());
			tag.setString("owner", network.getOwnerName());
			tag.setString("name", network.getNetworkName());
			tag.setTag("colour", network.getNetworkColour().writeData(new NBTTagCompound(), SyncType.SAVE));
			tag.setString("access", network.getAccessType().name());
			
			list.appendTag(tag);
		}
		nbt.setTag("networks", list);
		FluxNetworks.logger.debug("All " + list.tagCount() + " Networks were saved successfully");
		return nbt;
	}

	public boolean isDirty() {
		return !FluxNetworks.cache.getAllNetworks().isEmpty();
	}
}
