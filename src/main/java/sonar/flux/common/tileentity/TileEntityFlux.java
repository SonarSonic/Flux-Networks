package sonar.flux.common.tileentity;

import cofh.redstoneflux.api.IEnergyHandler;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.Optional;
import sonar.core.SonarCore;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.SonarHelper;
import sonar.core.listener.ISonarListenable;
import sonar.core.listener.ListenableList;
import sonar.core.listener.ListenerTally;
import sonar.core.listener.PlayerListener;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.sync.SyncTagType.LONG;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.sync.SyncTagTypeList;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.flux.FluxConfig;
import sonar.flux.api.FluxError;
import sonar.flux.api.FluxListener;
import sonar.flux.api.configurator.FluxConfigurationType;
import sonar.flux.api.configurator.IFluxConfigurable;
import sonar.flux.api.network.FluxCache;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.common.block.FluxConnection;
import sonar.flux.connection.EmptyFluxNetwork;
import sonar.flux.connection.FluxHelper;

import java.util.UUID;

@Optional.InterfaceList({@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyHandler", modid = "redstoneflux")})
public class TileEntityFlux extends TileEntitySonar implements IFluxListenable, IEnergyHandler, IByteBufTile, IFluxConfigurable {
	// shared
	public SyncTagType.INT priority = new SyncTagType.INT(0);
	public SyncTagType.LONG limit = (LONG) new SyncTagType.LONG(1).setDefault(FluxConfig.defaultLimit);
	public SyncTagType.BOOLEAN disableLimit = new SyncTagType.BOOLEAN(2);
	public SyncTagType.INT networkID = (INT) new SyncTagType.INT(4).setDefault(-1);
	public SyncUUID playerUUID = new SyncUUID(5);
	public SyncTagType.STRING customName = (STRING) new SyncTagType.STRING(6).setDefault("Flux Connection");
	public SyncTagType.INT colour = new SyncTagType.INT(7);
    public ListenableList<PlayerListener> listeners = new ListenableList(this, FluxListener.values().length);
	public TileEntity[] cachedTiles = new TileEntity[6];
	public boolean hasTiles;
    public SyncTagTypeList<Boolean> connections = new SyncTagTypeList<>(NBT.TAG_END, 8);
	private final ConnectionType type;
    public long toReceive; // is reset after each tick, the network calculates the max accept based upon priorities and sorting etc.
    public long toSend;
    public long totalTransferMax; // may need to be changed
    public long[] currentTransfer = new long[6];

	{
        syncList.addParts(priority, limit, disableLimit, networkID, playerUUID, customName, colour, connections); // , colour, connections);
		connections.setObjects(Lists.newArrayList(false, false, false, false, false, false));
	}

	// server only
	public IFluxNetwork network = EmptyFluxNetwork.INSTANCE;
    private int checkTicks;

	// client only
	public FluxError error = FluxError.NONE;

	public TileEntityFlux(ConnectionType type) {
		super();
		this.type = type;
	}

	public void setPlayerUUID(UUID name) {
		this.playerUUID.setObject(name);
	}

    @Override
    public void connect(IFluxNetwork network) {
				this.network = network;
        this.networkID.setObject(network.getNetworkID());
				colour.setObject(network.getNetworkColour().getRGB());
        setState(true);
        markBlockForUpdate();
    }

    @Override
    public void disconnect(IFluxNetwork network) {
        if (network.getNetworkID() == this.networkID.getObject()) {
            this.network = EmptyFluxNetwork.INSTANCE;
            this.networkID.setObject(-1);
            colour.setObject(EmptyFluxNetwork.colour.getRGB());
            setState(false);
				markBlockForUpdate();
			}
		}

    public void setState(boolean bool) {
        World world = getWorld();
        IBlockState state = getWorld().getBlockState(getPos());
        if (state.getBlock() instanceof FluxConnection) // sanity check
            world.setBlockState(getPos(), state.withProperty(FluxConnection.CONNECTED, bool), 2);
    }

    public boolean canAccess(EntityPlayer player) {
        return playerUUID.getUUID() == null || playerUUID.getUUID().equals(FluxHelper.getOwnerUUID(player)) || !getNetwork().isFakeNetwork() && getNetwork().getPlayerAccess(player).canEdit();
	}

    public UUID getConnectionOwner() {
        return playerUUID.getUUID();
	}

	public void update() {
		super.update();
		if (isServer()) {
            totalTransferMax = limit.getObject();
            for (int i = 0; i < 6; i++) {
                currentTransfer[i] = limit.getObject();
            }
			if (checkTicks >= 20) {
                updateNeighbours(false);
				checkTicks = 0;
			} else {
				checkTicks++;
			}
            listeners.forEach(tally -> FluxHelper.sendPacket(getNetwork(), tally));
		}
	}

	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void onSyncPacketRequested(EntityPlayer player) {
        updateNeighbours(true);
		super.onSyncPacketRequested(player);
	}

    @Override
    public void updateNeighbours(boolean full) {
			boolean changed = false;
			hasTiles = false;
        for (EnumFacing face : getValidFaces()) {
				int ordinal = face.getIndex();
            BlockPos pos = getPos().offset(face);
            TileEntity tile = getWorld().getTileEntity(pos);
            boolean canConnect = tile != null && FluxHelper.canConnect(tile, face);
            if (full || canConnect != connections.getObjects().get(ordinal)) {
                if (setNeighbour(face, canConnect ? tile : null))
							changed = true;
						}
            if (canConnect)
						hasTiles = true;
			}
			if (changed) {
            connections.markChanged();
				SonarCore.sendFullSyncAroundWithRenderUpdate(this, 128);
			}
		}

    public boolean setNeighbour(EnumFacing face, TileEntity tile) {
        TileEntity prev = cachedTiles[face.getIndex()];
        boolean changed = tile != null;
        connections.getObjects().set(face.getIndex(), changed);
        cachedTiles[face.getIndex()] = tile;
        return prev != tile;
	}

	public TileEntity[] cachedTiles() {
		return cachedTiles;
	}

    public EnumFacing[] getValidFaces() {
        return EnumFacing.values();
    }

	public boolean canTransfer() {
		return hasTiles;
	}

	public void onFirstTick() {
		super.onFirstTick();
		addConnection();
        if (isClient())
            requestSyncPacket();
	}

	public void invalidate() {
		super.invalidate();
		removeConnection();
	}

	public void onChunkUnload() {
		super.onChunkUnload();
		removeConnection();
	}

	public void addConnection() {
        if (isServer()) {
            FluxHelper.addConnection(this);
            updateNeighbours(true);
			SonarCore.sendPacketAround(this, 128, 0);
		}
	}

	public void removeConnection() {
        if (isServer()) {
            FluxHelper.removeConnection(this);
			}
		}

    @Override
    public int getNetworkID() {
        return networkID.getObject();
    }

    public NBTTagCompound addConfigs(NBTTagCompound config, EntityPlayer player) {
        if (!this.getNetwork().isFakeNetwork() && network.getNetworkID() != -1) {
            config.setInteger(FluxConfigurationType.NETWORK.getNBTName(), getNetwork().getNetworkID());
        }
        config.setInteger(FluxConfigurationType.PRIORITY.getNBTName(), this.getCurrentPriority());
        config.setLong(FluxConfigurationType.TRANSFER.getNBTName(), this.getTransferLimit());
        config.setBoolean(FluxConfigurationType.DISABLE_LIMIT.getNBTName(), disableLimit.getObject());
        return config;
    }

    public void readConfigs(NBTTagCompound config, EntityPlayer player) {
        if (config.hasKey(FluxConfigurationType.NETWORK.getNBTName())) {
            int storedID = config.getInteger(FluxConfigurationType.NETWORK.getNBTName());
            if (storedID != -1) {
                FluxHelper.removeConnection(this);
                FluxHelper.addConnection(this);
            }
        }
        if (config.hasKey(FluxConfigurationType.PRIORITY.getNBTName())) {
            this.priority.setObject(config.getInteger(FluxConfigurationType.PRIORITY.getNBTName()));
        }
        if (config.hasKey(FluxConfigurationType.TRANSFER.getNBTName())) {
            this.limit.setObject(config.getLong(FluxConfigurationType.TRANSFER.getNBTName()));
        }
        if (config.hasKey(FluxConfigurationType.DISABLE_LIMIT.getNBTName())) {
            this.disableLimit.setObject(config.getBoolean(FluxConfigurationType.DISABLE_LIMIT.getNBTName()));
        }
	}

	@Override
	public IFluxNetwork getNetwork() {
		return network;
	}

	@Override
	public ConnectionType getConnectionType() {
		return type;
	}

	@Override
	public long getTransferLimit() {
		return disableLimit.getObject() ? Long.MAX_VALUE : limit.getObject();
	}

	@Override
	public long getCurrentTransferLimit() {
        return disableLimit.getObject() ? Long.MAX_VALUE : totalTransferMax;
    }

    @Override
    public long getCurrentTransfer(EnumFacing face) {
        return face == null ? getCurrentTransferLimit() : Math.min(getCurrentTransferLimit(), currentTransfer[face.ordinal()]);
	}

	@Override
    public long getValidTransfer(long valid, EnumFacing face) {
        return Math.min(valid, getCurrentTransferLimit());//getCurrentTransfer(face));
	}

	@Override
    public void onEnergyRemoved(EnumFacing face, long remove) {
        totalTransferMax -= remove;
    }

    @Override
    public void onEnergyAdded(EnumFacing face, long added) {
        totalTransferMax -= added;
	}

	@Override
	public int getCurrentPriority() {
		return priority.getObject();
	}

	@Override
	public World getDimension() {
		return world;
	}

	@Override
	public String getCustomName() {
		return customName.getObject();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int getEnergyStored(EnumFacing from) {
		return 0;
	}

	@Override
    @Optional.Method(modid = "redstoneflux")
	public int getMaxEnergyStored(EnumFacing from) {
		return Integer.MAX_VALUE;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case -1:
			disableLimit.writeToBuf(buf);
			break;
		case 1:
			priority.writeToBuf(buf);
			break;
		case 2:
			limit.writeToBuf(buf);
			break;
		case 3:
			customName.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case -1:
			disableLimit.readFromBuf(buf);
			break;
		case 1:
			priority.readFromBuf(buf);
                getNetwork().markTypeDirty(FluxCache.flux);
			break;
		case 2:
			limit.readFromBuf(buf);
			break;
		case 3:
			customName.readFromBuf(buf);
			break;
		case 4:
			EntityPlayer player = SonarHelper.getPlayerFromUUID(playerUUID.getUUID());
			if (player != null) {
                    listeners.addListener(player, FluxListener.CONNECTIONS);
			}
			break;
		case 5:
			player = SonarHelper.getPlayerFromUUID(playerUUID.getUUID());
			if (player != null) {
                    listeners.clearListener(listeners.findListener(player));
                    listeners.addListener(player, FluxListener.SYNC_NETWORK);
			}
			break;
		}
	}

    @Override
    public ListenableList<PlayerListener> getListenerList() {
        return listeners;
    }

    @Override
    public void onListenerAdded(ListenerTally<PlayerListener> tally) {
    }

    @Override
    public void onListenerRemoved(ListenerTally<PlayerListener> tally) {
    }

    @Override
    public void onSubListenableAdded(ISonarListenable<PlayerListener> listen) {
    }

    @Override
    public void onSubListenableRemoved(ISonarListenable<PlayerListener> listen) {
    }

    @Override
    public boolean isValid() {
        return !isInvalid();
    }

    @Override
    public void setMaxSend(long send) {
        toSend = send;
    }

    @Override
    public void setMaxReceive(long receive) {
        toReceive = receive;
    }
}
