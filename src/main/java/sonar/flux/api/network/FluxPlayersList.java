package sonar.flux.api.network;

/* TODO REMOVE ME
public class FluxPlayersList extends SonarControlledList<FluxPlayer> implements INBTSyncable {

    public FluxPlayersList() {
        super(FluxPlayer.class, new ArrayList<>());
    }

    @Override
    public void readData(NBTTagCompound nbt, SyncType type) {
        if (nbt.hasKey("playerList")) {
            NBTTagList list = nbt.getTagList("playerList", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                value.add(new FluxPlayer(tag));
            }
        }
    }

    @Override
    public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
        if (this.isDirty || type.mustSync()) {
            NBTTagList list = new NBTTagList();
            value.forEach(player -> list.appendTag(player.writeData(new NBTTagCompound(), type)));
            nbt.setTag("playerList", list);
        }
        return nbt;
    }
}
*/