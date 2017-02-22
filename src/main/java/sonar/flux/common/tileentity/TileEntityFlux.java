package sonar.flux.common.tileentity;

import java.util.UUID;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.SonarAPI;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.SonarHelper;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.sync.SyncTagType.LONG;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.flux.FluxConfig;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxError;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.IFluxController;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.common.block.FluxConnection;
import sonar.flux.connection.EmptyFluxNetwork;
import sonar.flux.network.FluxNetworkCache.ViewingType;
import sonar.flux.network.PacketFluxError;

public abstract class TileEntityFlux extends TileEntitySonar implements IFlux, IEnergyHandler, IByteBufTile {
	// shared
	public SyncTagType.INT priority = new SyncTagType.INT(0);
	public SyncTagType.LONG limit = (LONG) new SyncTagType.LONG(1).setDefault(FluxConfig.defaultLimit);
	public SyncTagType.BOOLEAN disableLimit = new SyncTagType.BOOLEAN(2);
	public SyncTagType.INT networkID = (INT) new SyncTagType.INT(4).setDefault(-1);
	public SyncUUID playerUUID = new SyncUUID(5);
	public SyncTagType.STRING customName = (STRING) new SyncTagType.STRING(6).setDefault("Flux Connection");
	public SyncTagType.INT colour = new SyncTagType.INT(7);
	public TileEntity[] cachedTiles = new TileEntity[6];
	public boolean hasTiles;
	public boolean[] connections = new boolean[6];
	private final ConnectionType type;
	public long currentTransfer;

	{
		syncList.addParts(priority, limit, disableLimit, networkID, playerUUID, customName, colour);
	}

	// server only
	private IFluxNetwork network = EmptyFluxNetwork.INSTANCE;
	private int checkTicks = 0;

	// client only
	public FluxError error = FluxError.NONE;

	public TileEntityFlux(ConnectionType type) {
		super();
		this.type = type;
	}

	public void setPlayerUUID(UUID name) {
		this.playerUUID.setObject(name);
	}

	public void changeNetwork(IFluxNetwork network) {
		changeNetwork(network, null);
	}

	public void changeNetwork(IFluxNetwork network, EntityPlayer player) {
		if (isServer()) {
			this.network = network;
			if (player != null && !network.isFakeNetwork() && this instanceof IFluxController && network.hasController()) {
				FluxNetworks.network.sendTo(new PacketFluxError(getPos(), FluxError.HAS_CONTROLLER), (EntityPlayerMP) player);
				changeNetwork(EmptyFluxNetwork.INSTANCE, null);
				return;
			}
			network.addFluxConnection(this);
			networkID.setObject(network.getNetworkID());
			colour.setObject(network.getNetworkColour().getRGB());
			worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(FluxConnection.CONNECTED, !network.isFakeNetwork()), 2);
			markDirty();
			markBlockForUpdate();
		}
	}

	public void disconnectFromNetwork() {
		network.removeFluxConnection(this);
		networkID.setObject(-1);
		network = EmptyFluxNetwork.INSTANCE;
		worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(FluxConnection.CONNECTED, false), 2);
	}

	public void update() {
		super.update();
		if (isServer()) {
			currentTransfer = limit.getObject();
			if (checkTicks >= 20) {
				updateConnections();
				checkTicks = 0;
			} else {
				checkTicks++;
			}
		}
	}

	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void onSyncPacketRequested(EntityPlayer player) {
		super.onSyncPacketRequested(player);
		SonarCore.sendPacketAround(this, 128, 0);
	}

	public void updateConnections() {
		if (isServer()) {
			boolean changed = false;
			hasTiles = false;
			for (EnumFacing face : EnumFacing.VALUES) {
				BlockPos pos = this.pos.offset(face);
				TileEntity tile = getWorld().getTileEntity(pos);
				if (tile != null && !(tile instanceof IFlux)) {
					if (tile instanceof IEnergyConnection) {
						if (!connections[face.getIndex()]) {
							changed = true;
							connections[face.getIndex()] = true;
							cachedTiles[face.getIndex()] = tile;
						}
						hasTiles = true;
						continue;
					}
					if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, face) != null) {
						if (!connections[face.getIndex()]) {
							changed = true;
							connections[face.getIndex()] = true;
							cachedTiles[face.getIndex()] = tile;
						}
						hasTiles = true;
					}
				} else if (connections[face.getIndex()]) {
					changed = true;
					connections[face.getIndex()] = false;
					cachedTiles[face.getIndex()] = null;
				}

			}
			if (changed)
				SonarCore.sendPacketAround(this, 128, 0);
		}
	}

	public TileEntity[] cachedTiles() {
		return cachedTiles;
	}

	public boolean canTransfer() {
		return hasTiles;
	}

	public void onFirstTick() {
		super.onFirstTick();
		addConnection();
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
		if (isServer() && networkID.getObject() != -1) {
			IFluxCommon network = FluxNetworks.getServerCache().getNetwork(networkID.getObject());
			if (!network.isFakeNetwork() && network instanceof IFluxNetwork) {
				changeNetwork((IFluxNetwork) network, null);
			}
		}
	}

	public void removeConnection() {
		if (isServer() && networkID.getObject() != -1) {
			network = FluxNetworks.getServerCache().getNetwork(networkID.getObject());
			if (!network.isFakeNetwork() && network instanceof IFluxNetwork) {
				((IFluxNetwork) network).removeFluxConnection(this);
			}
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
		return disableLimit.getObject() ? Long.MAX_VALUE : currentTransfer;
	}

	@Override
	public void onEnergyRemoved(long remove) {
		currentTransfer -= remove;
	}

	@Override
	public void onEnergyAdded(long added) {
		currentTransfer -= added;
	}

	@Override
	public int getCurrentPriority() {
		return priority.getObject();
	}

	@Override
	public World getDimension() {
		return worldObj;
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
	public int getEnergyStored(EnumFacing from) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return Integer.MAX_VALUE;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case -1:
			disableLimit.writeToBuf(buf);
			break;
		case 0:
			for (int i = 0; i < 6; i++) {
				buf.writeBoolean(connections[i]);
			}
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
		case 0:
			for (int i = 0; i < 6; i++) {
				connections[i] = buf.readBoolean();
			}
			markBlockForUpdate();
			break;
		case 1:
			priority.readFromBuf(buf);
			this.network.updateSenders();
			this.network.updateReceivers();
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
				FluxNetworks.getServerCache().addViewer(player, ViewingType.CONNECTIONS, networkID.getObject());
			}
			break;
		case 5:
			player = SonarHelper.getPlayerFromUUID(playerUUID.getUUID());
			if (player != null) {
				FluxNetworks.getServerCache().removeViewer(player);
				FluxNetworks.getServerCache().addViewer(player, ViewingType.NETWORK, networkID.getObject());
			}
			break;
		}
	}
}
