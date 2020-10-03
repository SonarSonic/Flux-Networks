package sonar.fluxnetworks.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.device.IFluxDevice;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.misc.NBTType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkInvalid;
import sonar.fluxnetworks.common.connection.SimpleFluxNetwork;
import sonar.fluxnetworks.common.misc.FluxUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class FluxClientCache {

    public static final FluxClientCache INSTANCE = new FluxClientCache();

    private Int2ObjectMap<IFluxNetwork> networks = new Int2ObjectArrayMap<>();

    public void reset() {
        networks.clear();
    }

    public void updateNetworks(@Nonnull Map<Integer, CompoundNBT> serverSideNetworks, int flags) {
        serverSideNetworks.forEach((integer, nbt) -> {
            int id = integer; // unpack
            IFluxNetwork network = networks.get(id);
            if (flags == FluxConstants.FLAG_NET_REMOVE) {
                if (network != null) {
                    networks.remove(id);
                    return;
                }
            }
            if (network == null) {
                network = new SimpleFluxNetwork();
                network.readCustomNBT(nbt, flags);
                networks.put(id, network);
            } else {
                network.readCustomNBT(nbt, flags);
            }
        });
    }

    public void updateDevices(int networkID, List<CompoundNBT> tags) {
        IFluxNetwork network = networks.get(networkID);
        if (network != null) {
            List<IFluxDevice> devices = network.getAllDevices();
            tags.forEach(t -> {
                GlobalPos globalPos = FluxUtils.readGlobalPos(t);
                devices.stream().filter(f -> f.getGlobalPos().equals(globalPos)).findFirst().ifPresent(f -> f.readCustomNBT(t, NBTType.DEFAULT));
            });
        }
    }

    public IFluxNetwork getNetwork(int id) {
        return networks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    public List<IFluxNetwork> getAllNetworks() {
        return new ArrayList<>(networks.values());
    }
}
