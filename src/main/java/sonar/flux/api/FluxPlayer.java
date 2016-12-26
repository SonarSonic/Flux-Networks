package sonar.flux.api;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;

public class FluxPlayer implements INBTSyncable {

	public UUID id;
	public String cachedName = "";
	public PlayerAccess access;

	public FluxPlayer(UUID id, PlayerAccess access) {
		this.id = id;
		this.access = access;
	}

	public FluxPlayer(NBTTagCompound tag) {
		readData(tag, SyncType.SAVE);
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			this.cachedName = SonarHelper.getProfileByUUID(id).getName();
		}
	}

	public UUID getUUID() {
		return id;
	}

	public String getCachedName() {
		return cachedName;
	}

	public PlayerAccess getAccess() {
		return access;
	}

	public void setAccess(PlayerAccess access) {
		this.access = access;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		id = nbt.getUniqueId("playerUUID");
		cachedName = nbt.getString("cachedName");
		access = PlayerAccess.values()[nbt.getByte("playerAccess")];
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setUniqueId("playerUUID", id);
		nbt.setString("cachedName", cachedName);
		nbt.setByte("playerAccess", (byte) access.ordinal());
		return nbt;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FluxPlayer) {
			FluxPlayer player = (FluxPlayer) obj;
			return player.id.equals(this.id) && player.access.equals(access);
		}
		return false;
	}
}
