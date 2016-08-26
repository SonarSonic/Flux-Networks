package sonar.flux.network;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
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
		FluxNetworks.getServerCache().uniqueID = nbt.getInteger("uniqueID");
		if (nbt.hasKey("networks")) {
			NBTTagList list = nbt.getTagList("networks", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound tag = list.getCompoundTagAt(i);
				CustomColour colour = new CustomColour(0, 0, 0);
				colour.readData(tag.getCompoundTag("colour"), SyncType.SAVE);
				UUID id = null;
				//legacy support
				if(tag.hasKey("owner")){
					id = SonarHelper.getGameProfileForUsername(tag.getString("owner")).getId();
				}
				if(id==null && tag.hasUniqueId("ownerUUID")){
					id = tag.getUniqueId("ownerUUID");
				}			
				if(id==null){
					FluxNetworks.logger.info("[ERROR] CAN'T LOAD NETWORK WITHOUT PLAYER UUID, aborting");
					continue;
				}
				BasicFluxNetwork loaded = new BasicFluxNetwork(tag.getInteger("id"), id, tag.getString("name"), colour, AccessType.valueOf(tag.getString("access")));
				loaded.cachedOwnerName.setObject(SonarHelper.getProfileByUUID(loaded.getOwnerUUID()).getName());
				loaded.getPlayers().readData(tag.getCompoundTag("playerList"), SyncType.SAVE);
				FluxNetworks.getServerCache().addNetwork(loaded);
				FluxNetworks.logger.info("[LOADED NETWORK] '" + loaded.getNetworkName() + "' with ID '" + loaded.getNetworkID());
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("uniqueID", FluxNetworks.getServerCache().uniqueID);
		NBTTagList list = new NBTTagList();
		for (IFluxNetwork network : FluxNetworks.getServerCache().getAllNetworks()) {
			if (network != null) {				
				NBTTagCompound tag = new NBTTagCompound();		
				tag.setInteger("id", network.getNetworkID());
				tag.setUniqueId("ownerUUID", network.getOwnerUUID());
				tag.setString("name", network.getNetworkName());
				tag.setTag("colour", network.getNetworkColour().writeData(new NBTTagCompound(), SyncType.SAVE));
				tag.setString("access", network.getAccessType().name());
				tag.setTag("playerList", network.getPlayers().writeData(new NBTTagCompound(), SyncType.SAVE));
				list.appendTag(tag);
			}
		}
		if (!list.hasNoTags()) {
			nbt.setTag("networks", list);
			FluxNetworks.logger.debug("All " + list.tagCount() + " Networks were saved successfully");
		}

		return nbt;
	}

	public boolean isDirty() {
		return !FluxNetworks.getServerCache().getAllNetworks().isEmpty();
	}
}
