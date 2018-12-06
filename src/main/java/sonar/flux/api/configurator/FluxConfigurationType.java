package sonar.flux.api.configurator;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.translate.Localisation;
import sonar.flux.FluxTranslate;
import sonar.flux.api.ConnectionSettings;
import sonar.flux.api.EnumActivationType;
import sonar.flux.api.EnumPriorityType;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.FluxHelper;

public class FluxConfigurationType {
    public static FluxConfigurationType NETWORK = new FluxConfigurationType(0,"network", FluxTranslate.NETWORK_NAME, FluxConfigurationType::copyNetwork, FluxConfigurationType::pasteNetwork);
    public static FluxConfigurationType REDSTONE_SETTING = new FluxConfigurationType(1,"r_setting", FluxTranslate.ENABLE_LIMIT, FluxConfigurationType::copyRedstoneSetting, FluxConfigurationType::pasteRedstoneSetting);
    public static FluxConfigurationType PRIORITY = new FluxConfigurationType(2,"priority", FluxTranslate.PRIORITY, FluxConfigurationType::copyPriority, FluxConfigurationType::pastePriority);
    public static FluxConfigurationType PRIORITY_SETTING = new FluxConfigurationType(3,"p_setting", FluxTranslate.PRIORITY_MODE, FluxConfigurationType::copyPrioritySetting, FluxConfigurationType::pastePrioritySetting);
    public static FluxConfigurationType TRANSFER = new FluxConfigurationType(4,"transfer", FluxTranslate.TRANSFER_LIMIT, FluxConfigurationType::copyTransfer, FluxConfigurationType::pasteTransfer);
    public static FluxConfigurationType TRANSFER_SETTING = new FluxConfigurationType(5,"t_setting", FluxTranslate.ENABLE_LIMIT, FluxConfigurationType::copyTransferSetting, FluxConfigurationType::pasteTransferSetting);

    public static FluxConfigurationType[] VALUES = new FluxConfigurationType[]{NETWORK, REDSTONE_SETTING, PRIORITY, PRIORITY_SETTING, TRANSFER, TRANSFER_SETTING};

    public int ordinal;
    public String key;
    public Localisation translate;
    public ICopyMethod copy;
    public IPasteMethod paste;

    public FluxConfigurationType(int ordinal, String key, Localisation translate, ICopyMethod copy, IPasteMethod paste){
        this.ordinal = ordinal;
        this.key = key;
        this.translate = translate;
        this.copy = copy;
        this.paste = paste;
    }

    public String getNBTName() {
        return key;
    }

    //// NETWORK \\\\

    public static void copyNetwork(NBTTagCompound nbt, String key, TileFlux tile){
        if (!tile.getNetwork().isFakeNetwork() && tile.getNetworkID() != -1) {
            nbt.setInteger(key, tile.getNetworkID());
        }
    }

    public static void pasteNetwork(NBTTagCompound nbt, String key, TileFlux tile){
        int storedID = nbt.getInteger(key);
        if (storedID != -1) {
            FluxHelper.removeConnection(tile, null);
            tile.networkID.setValue(storedID);
            FluxHelper.addConnection(tile, null);
        }
    }

    //// REDSTONE SETTING \\\\

    public static void copyRedstoneSetting(NBTTagCompound nbt, String key, TileFlux tile){
        nbt.setInteger(key, tile.activation_type.getValue().ordinal());
    }

    public static void pasteRedstoneSetting(NBTTagCompound nbt, String key, TileFlux tile){
        tile.activation_type.setValue(EnumActivationType.values()[nbt.getInteger(key)]);
    }

    //// PRIORITY \\\\

    public static void copyPriority(NBTTagCompound nbt, String key, TileFlux tile){
        nbt.setInteger(key, tile.priority.getValue());
    }

    public static void pastePriority(NBTTagCompound nbt, String key, TileFlux tile){
        tile.priority.setValue(nbt.getInteger(key));
        tile.markSettingChanged(ConnectionSettings.PRIORITY);
    }

    //// PRIORITY SETTING \\\\

    public static void copyPrioritySetting(NBTTagCompound nbt, String key, TileFlux tile){
        nbt.setInteger(key, tile.priority_type.getValue().ordinal());
    }

    public static void pastePrioritySetting(NBTTagCompound nbt, String key, TileFlux tile){
        tile.priority_type.setValue(EnumPriorityType.values()[nbt.getInteger(key)]);
    }

    //// TRANSFER LIMIT \\\\

    public static void copyTransfer(NBTTagCompound nbt, String key, TileFlux tile){
        nbt.setLong(key, tile.limit.getValue());
    }

    public static void pasteTransfer(NBTTagCompound nbt, String key, TileFlux tile){
        tile.limit.setValue(nbt.getLong(key));
        tile.markSettingChanged(ConnectionSettings.TRANSFER_LIMIT);
    }

    //// TRANSFER SETTING \\\\

    public static void copyTransferSetting(NBTTagCompound nbt, String key, TileFlux tile){
        nbt.setBoolean(key, tile.disableLimit.getValue());
    }

    public static void pasteTransferSetting(NBTTagCompound nbt, String key, TileFlux tile){
        tile.disableLimit.setValue(nbt.getBoolean(key));
        tile.markSettingChanged(ConnectionSettings.TRANSFER_LIMIT);
    }

    public interface ICopyMethod<T>{
        void copyFromTile(NBTTagCompound tag, String key, TileFlux tile);
    }

    public interface IPasteMethod<T>{
        void pasteToTile(NBTTagCompound tag, String key, TileFlux tile);
    }


}