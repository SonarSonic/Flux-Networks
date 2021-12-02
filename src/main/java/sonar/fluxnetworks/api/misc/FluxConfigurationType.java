package sonar.fluxnetworks.api.misc;

import net.minecraft.nbt.CompoundTag;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;

public enum FluxConfigurationType {
    NETWORK(FluxConstants.NETWORK_ID),
    PRIORITY(FluxConstants.PRIORITY),
    PRIORITY_SETTING(FluxConstants.SURGE_MODE),
    TRANSFER(FluxConstants.LIMIT),
    TRANSFER_SETTING(FluxConstants.DISABLE_LIMIT);

    private final String key;

    FluxConfigurationType(String key) {
        this.key = key;
    }

    public void copy(CompoundTag nbt, @Nonnull TileFluxDevice tile) {
        switch (this) {
            case NETWORK:
                nbt.putInt(key, tile.getNetwork().getNetworkID());
                break;
            case PRIORITY:
                nbt.putInt(key, tile.getRawPriority());
                break;
            case PRIORITY_SETTING:
                nbt.putBoolean(key, tile.getSurgeMode());
                break;
            case TRANSFER:
                nbt.putLong(key, tile.getRawLimit());
                break;
            case TRANSFER_SETTING:
                nbt.putBoolean(key, tile.getDisableLimit());
                break;
        }
    }

    public void paste(@Nonnull CompoundTag nbt, @Nonnull TileFluxDevice tile) {
        if (!nbt.contains(key)) {
            return;
        }
        //FIXME
        /*switch (this) {
            case NETWORK:
                IFluxNetwork network = FluxNetworkData.getNetwork(nbt.getInt(key));
                tile.connect(network);
                break;
            case PRIORITY:
                tile.setPriority(nbt.getInt(key));
                break;
            case PRIORITY_SETTING:
                tile.setSurgeMode(nbt.getBoolean(key));
                break;
            case TRANSFER:
                tile.setTransferLimit(nbt.getLong(key));
                break;
            case TRANSFER_SETTING:
                tile.setDisableLimit(nbt.getBoolean(key));
                break;
        }*/
    }

    //// NETWORK \\\\

    /*public static void copyNetwork(CompoundNBT nbt, String key, @Nonnull IFluxDevice tile) {
        if (tile.getNetwork().isValid()) {
            nbt.putInt(key, tile.getNetwork().getNetworkID());
        }
    }

    public static void pasteNetwork(@Nonnull CompoundNBT nbt, String key, IFluxDevice tile) {
        int storedID = nbt.getInt(key);
        if (storedID > 0) {
            IFluxNetwork newNetwork = FluxNetworkData.getNetwork(storedID);
            newNetwork.enqueueConnectionAddition(tile);
        }
    }

    //// PRIORITY \\\\

    public static void copyPriority(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putInt(key, tile.getRawPriority());
    }

    public static void pastePriority(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.setPriority(nbt.getInt(key));
    }

    //// PRIORITY SETTING \\\\

    public static void copyPrioritySetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putBoolean(key, tile.getSurgeMode());
    }

    public static void pastePrioritySetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.setSurgeMode(nbt.getBoolean(key));
    }

    //// TRANSFER LIMIT \\\\

    public static void copyTransfer(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putLong(key, tile.getRawLimit());
    }

    public static void pasteTransfer(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.setLimit(nbt.getLong(key));
    }

    //// TRANSFER SETTING \\\\

    public static void copyTransferSetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        nbt.putBoolean(key, tile.getDisableLimit());
    }

    public static void pasteTransferSetting(CompoundNBT nbt, String key, TileFluxDevice tile) {
        tile.setDisableLimit(nbt.getBoolean(key));
    }

    @FunctionalInterface
    public interface ICopyMethod {
        void copyFromTile(CompoundNBT tag, String key, TileFluxDevice tile);
    }

    @FunctionalInterface
    public interface IPasteMethod {
        void pasteToTile(CompoundNBT tag, String key, TileFluxDevice tile);
    }*/
}