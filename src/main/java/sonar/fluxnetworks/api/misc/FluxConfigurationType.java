package sonar.fluxnetworks.api.misc;

import net.minecraft.nbt.CompoundNBT;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;

public class FluxConfigurationType {
    public static FluxConfigurationType NETWORK = new FluxConfigurationType(0,"network", FluxConfigurationType::copyNetwork, FluxConfigurationType::pasteNetwork);
    public static FluxConfigurationType PRIORITY = new FluxConfigurationType(2,"priority", FluxConfigurationType::copyPriority, FluxConfigurationType::pastePriority);
    public static FluxConfigurationType PRIORITY_SETTING = new FluxConfigurationType(3,"p_setting", FluxConfigurationType::copyPrioritySetting, FluxConfigurationType::pastePrioritySetting);
    public static FluxConfigurationType TRANSFER = new FluxConfigurationType(4,"transfer", FluxConfigurationType::copyTransfer, FluxConfigurationType::pasteTransfer);
    public static FluxConfigurationType TRANSFER_SETTING = new FluxConfigurationType(5,"t_setting", FluxConfigurationType::copyTransferSetting, FluxConfigurationType::pasteTransferSetting);

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

    public static void copyNetwork(CompoundNBT nbt, String key, @Nonnull IFluxDevice tile) {
        if (tile.getNetwork().isValid()) {
            nbt.putInt(key, tile.getNetwork().getNetworkID());
        }
    }

    public static void pasteNetwork(@Nonnull CompoundNBT nbt, String key, IFluxDevice tile) {
        int storedID = nbt.getInt(key);
        if (storedID > 0) {
            IFluxNetwork newNetwork = FluxNetworkCache.INSTANCE.getNetwork(storedID);
            newNetwork.enqueueConnectionAddition(tile);
        }
    }

    //// PRIORITY \\\\

    public static void copyPriority(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putInt(key, tile.priority);
    }

    public static void pastePriority(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.priority = nbt.getInt(key);
    }

    //// PRIORITY SETTING \\\\

    public static void copyPrioritySetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putBoolean(key, tile.surgeMode);
    }

    public static void pastePrioritySetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.surgeMode = nbt.getBoolean(key);
    }

    //// TRANSFER LIMIT \\\\

    public static void copyTransfer(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putLong(key, tile.limit);
    }

    public static void pasteTransfer(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.limit = nbt.getLong(key);
    }

    //// TRANSFER SETTING \\\\

    public static void copyTransferSetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putBoolean(key, tile.disableLimit);
    }

    public static void pasteTransferSetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.disableLimit = nbt.getBoolean(key);
    }

    @FunctionalInterface
    public interface ICopyMethod {
        void copyFromTile(CompoundNBT tag, String key, TileFluxDevice tile);
    }

    @FunctionalInterface
    public interface IPasteMethod {
        void pasteToTile(CompoundNBT tag, String key, TileFluxDevice tile);
    }

}