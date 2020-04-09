package sonar.fluxnetworks.api.utils;

import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

///TODO remove common references
public class FluxConfigurationType {
    public static FluxConfigurationType NETWORK = new FluxConfigurationType(0,"network", (nbt, key1, tile) -> copyNetwork(nbt, key1, tile), (nbt, key1, tile) -> pasteNetwork(nbt, key1, tile));
    public static FluxConfigurationType PRIORITY = new FluxConfigurationType(2,"priority", (nbt, key1, tile) -> copyPriority(nbt, key1, tile), (nbt, key1, tile) -> pastePriority(nbt, key1, tile));
    public static FluxConfigurationType PRIORITY_SETTING = new FluxConfigurationType(3,"p_setting", (nbt, key1, tile) -> copyPrioritySetting(nbt, key1, tile), (nbt, key1, tile) -> pastePrioritySetting(nbt, key1, tile));
    public static FluxConfigurationType TRANSFER = new FluxConfigurationType(4,"transfer", (nbt, key1, tile) -> copyTransfer(nbt, key1, tile), (nbt, key1, tile) -> pasteTransfer(nbt, key1, tile));
    public static FluxConfigurationType TRANSFER_SETTING = new FluxConfigurationType(5,"t_setting", (nbt, key1, tile) -> copyTransferSetting(nbt, key1, tile), (nbt, key1, tile) -> pasteTransferSetting(nbt, key1, tile));

    public static FluxConfigurationType[] VALUES = new FluxConfigurationType[]{NETWORK, PRIORITY, PRIORITY_SETTING, TRANSFER, TRANSFER_SETTING};

    public int ordinal;
    public String key;
    public ICopyMethod copy;
    public IPasteMethod paste;

    public FluxConfigurationType(int ordinal, String key, ICopyMethod copy, IPasteMethod paste){
        this.ordinal = ordinal;
        this.key = key;
        this.copy = copy;
        this.paste = paste;
    }

    public String getNBTName() {
        return key;
    }

    //// NETWORK \\\\

    public static void copyNetwork(CompoundNBT nbt, String key, TileFluxCore tile) {
        if (!tile.getNetwork().isInvalid() && tile.getNetworkID() != -1) {
            nbt.putInt(key, tile.getNetworkID());
        }
    }

    public static void pasteNetwork(CompoundNBT nbt, String key, TileFluxCore tile) {
        int storedID = nbt.getInt(key);
        if (storedID != -1) {
            IFluxNetwork newNetwork = FluxNetworkCache.instance.getNetwork(storedID);
            tile.getNetwork().queueConnectionRemoval(tile, false);
            newNetwork.queueConnectionAddition(tile);
        }
    }

    //// PRIORITY \\\\

    public static void copyPriority(CompoundNBT nbt, String key, TileFluxCore tile) {
        nbt.putInt(key, tile.priority);
    }

    public static void pastePriority(CompoundNBT nbt, String key, TileFluxCore tile) {
        tile.priority = nbt.getInt(key);
    }

    //// PRIORITY SETTING \\\\

    public static void copyPrioritySetting(CompoundNBT nbt, String key, TileFluxCore tile) {
        nbt.putBoolean(key, tile.surgeMode);
    }

    public static void pastePrioritySetting(CompoundNBT nbt, String key, TileFluxCore tile) {
        tile.surgeMode = nbt.getBoolean(key);
    }

    //// TRANSFER LIMIT \\\\

    public static void copyTransfer(CompoundNBT nbt, String key, TileFluxCore tile) {
        nbt.putLong(key, tile.limit);
    }

    public static void pasteTransfer(CompoundNBT nbt, String key, TileFluxCore tile) {
        tile.limit = nbt.getLong(key);
    }

    //// TRANSFER SETTING \\\\

    public static void copyTransferSetting(CompoundNBT nbt, String key, TileFluxCore tile) {
        nbt.putBoolean(key, tile.disableLimit);
    }

    public static void pasteTransferSetting(CompoundNBT nbt, String key, TileFluxCore tile) {
        tile.disableLimit = nbt.getBoolean(key);
    }

    public interface ICopyMethod<T> {
        void copyFromTile(CompoundNBT tag, String key, TileFluxCore tile);
    }

    public interface IPasteMethod<T> {
        void pasteToTile(CompoundNBT tag, String key, TileFluxCore tile);
    }

}