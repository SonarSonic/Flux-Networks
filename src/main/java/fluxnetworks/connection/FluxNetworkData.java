package fluxnetworks.connection;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.utils.NBTType;
import fluxnetworks.system.FluxNetworks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class FluxNetworkData extends WorldSavedData {

    static final String NETWORK_DATA = FluxNetworks.MODID + "data";

    public static String NETWORKS = "networks";
    public static String LOADED_CHUNKS = "loadedChunks";
    public static String UNIQUE_ID = "uniqueID";

    public static String NETWORK_ID = "networkID";
    public static String NETWORK_NAME = "networkName";
    public static String NETWORK_COLOR = "networkColor";
    public static String NETWORK_PASSWORD = "networkPassword";
    public static String SECURITY_TYPE = "networkSecurity";
    public static String OWNER_UUID = "ownerUUID";
    public static String WIRELESS_MODE = "wirelessMode";

    public static String PLAYER_LIST = "playerList";
    public static String NETWORK_FOLDERS = "folders";
    public static String UNLOADED_CONNECTIONS = "unloaded";

    Map<Integer, IFluxNetwork> networks = new HashMap<>();

    int uniqueID = 1;

    FluxNetworkData() {
        super(NETWORK_DATA);
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        uniqueID = nbt.getInt(UNIQUE_ID);
        if(nbt.contains(NETWORKS)) {
            ListNBT list = nbt.getList(NETWORKS, Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                FluxNetworkServer network = new FluxNetworkServer();
                network.readNetworkNBT(tag, NBTType.ALL_SAVE);
                networks.putIfAbsent(network.getNetworkID(), network);
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        compound.putInt(UNIQUE_ID, uniqueID);

        ListNBT list = new ListNBT();
        for(IFluxNetwork network : FluxDataHandler.INSTANCE.getAllNetworks()) {
            CompoundNBT tag = new CompoundNBT();
            network.writeNetworkNBT(tag, NBTType.ALL_SAVE);
            list.add(tag);
        }
        compound.put(NETWORKS, list);

        return compound;
    }
}
