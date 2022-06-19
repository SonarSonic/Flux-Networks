package sonar.fluxnetworks.api.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.api.device.FluxDeviceType;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkData;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;

public enum FluxConfigurationType {
    NETWORK(FluxConstants.NETWORK_ID),
    PRIORITY(FluxConstants.PRIORITY),
    PRIORITY_SETTING(FluxConstants.SURGE_MODE),
    TRANSFER(FluxConstants.LIMIT),
    TRANSFER_SETTING(FluxConstants.DISABLE_LIMIT);

    public static final FluxConfigurationType[] VALUES = values();

    private final String key;

    FluxConfigurationType(String key) {
        this.key = key;
    }

    public void copy(@Nonnull Player player, @Nonnull CompoundTag tag, @Nonnull TileFluxDevice device) {
        switch (this) {
            case NETWORK -> tag.putInt(key, device.getNetworkID());
            case PRIORITY -> tag.putInt(key, device.getLiteralPriority());
            case PRIORITY_SETTING -> tag.putBoolean(key, device.getSurgeMode());
            case TRANSFER -> tag.putLong(key, device.getLiteralLimit());
            case TRANSFER_SETTING -> tag.putBoolean(key, device.getDisableLimit());
        }
    }

    public void paste(@Nonnull Player player, @Nonnull CompoundTag tag, @Nonnull TileFluxDevice device) {
        if (!tag.contains(key)) {
            return;
        }
        if (this == NETWORK) {
            if (device.getDeviceType() != FluxDeviceType.CONTROLLER) {
                FluxNetwork network = FluxNetworkData.getNetwork(tag.getInt(key));
                // we can connect to an invalid network (i.e. disconnect)
                if (!network.isValid() || network.canPlayerAccess(player, "")) {
                    if (network.isValid()) {
                        device.setConnectionOwner(player.getUUID());
                    }
                    device.connect(network);
                }
            }
        } else {
            device.readCustomTag(tag, FluxConstants.NBT_TILE_SETTING);
        }
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