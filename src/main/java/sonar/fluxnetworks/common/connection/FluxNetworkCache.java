package sonar.fluxnetworks.common.connection;

/**
 * Cached all flux networks
 */
@Deprecated
public class FluxNetworkCache {

    //public static final FluxNetworkCache INSTANCE = new FluxNetworkCache();

    //public Map<Integer, IFluxNetwork> networks = new HashMap<>();
    //public boolean superAdminClient = false;

    /*public void clearClientCache() {
        networks.clear();
        superAdminClient = false;
    }

    public boolean hasSpaceLeft(PlayerEntity player) {
        if (FluxConfig.maximumPerPlayer == -1) {
            return true;
        }
        UUID uuid = PlayerEntity.getUUID(player.getGameProfile());
        long created = getAllNetworks().stream().filter(n -> n.getNetworkOwner().equals(uuid)).count();
        return created < FluxConfig.maximumPerPlayer;
    }

    public void createdNetwork(@Nonnull PlayerEntity player, String name, int color, SecurityType securityType, EnergyType energyType, String password) {
        UUID uuid = PlayerEntity.getUUID(player.getGameProfile());

        NetworkMember owner = NetworkMember.createNetworkMember(player, AccessType.OWNER);
        FluxNetworkServer network = new FluxNetworkServer(nextUID(), name, securityType, color, uuid, password);
        network.getNetworkMembers().add(owner);

        FluxNetworkData.get().addNetwork(network);
    }

    private int nextUID() {
        return FluxNetworkData.get().uniqueID++;
    }

    public void updateClientFromPacket(Map<Integer, CompoundNBT> serverSideNetworks, NBTType type) {
        serverSideNetworks.forEach((i, n) -> {
            IFluxNetwork network = networks.get(i);
            if (type == NBTType.NETWORK_CLEAR) {
                if (network != null) {
                    networks.remove(i);
                    return;
                }
            }
            if (network == null) {
                network = new FluxLiteNetwork();
                network.readCustomNBT(n, type);
                networks.put(i, network);
            } else {
                network.readCustomNBT(n, type);
            }
        });
    }*/

    /*public void updateClientConnections(int networkID, List<CompoundNBT> tags) {
        IFluxNetwork network = networks.get(networkID);
        if (network != null) {
            List<IFluxDevice> connectors = network.getSetting(NetworkSettings.ALL_CONNECTORS);
            tags.forEach(t -> {
                Coord4D coord4D = new Coord4D(t);
                connectors.stream().filter(f -> f.getCoords().equals(coord4D)).findFirst().ifPresent(f -> f.readCustomNBT(t, NBTType.DEFAULT));
            });
        }
    }*/

    /*public IFluxNetwork getNetwork(int id) {
        return FluxNetworkData.get().networks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }*/

    /*public Collection<IFluxNetwork> getAllNetworks() {
        return FluxNetworkData.get().networks.values();
    }*/

    /*public IFluxNetwork getClientNetwork(int id) {
        return networks.getOrDefault(id, FluxNetworkInvalid.INSTANCE);
    }

    public List<IFluxNetwork> getAllClientNetworks() {
        return new ArrayList<>(networks.values());
    }*/
}
