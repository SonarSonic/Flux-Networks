package sonar.flux.api;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;

public class NetworkFluxFolder implements INBTSyncable {

    public String name;
    public int folderID;
    public int rgb;

    public NetworkFluxFolder(String name, int folderID, int rgb){
        this.name = name;
        this.folderID = folderID;
        this.rgb = rgb;
    }

    public NetworkFluxFolder(NBTTagCompound tag){
        readData(tag, NBTHelper.SyncType.SAVE);
    }

    @Override
    public void readData(NBTTagCompound nbt, NBTHelper.SyncType type) {
        name = nbt.getString("name");
        folderID = nbt.getInteger("id");
        rgb = nbt.getInteger("rgb");
    }

    @Override
    public NBTTagCompound writeData(NBTTagCompound nbt, NBTHelper.SyncType type) {
        nbt.setString("name", name);
        nbt.setInteger("id", folderID);
        nbt.setInteger("rgb", rgb);
        return nbt;
    }
}
