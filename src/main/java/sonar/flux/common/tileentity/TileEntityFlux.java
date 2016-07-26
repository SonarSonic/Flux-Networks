package sonar.flux.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import sonar.core.SonarCore;
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.ActionType;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.integration.SonarLoader;
import sonar.core.network.sync.SyncEnergyStorage;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.LONG;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;
import sonar.flux.FluxNetworks;
import sonar.flux.api.FluxError;
import sonar.flux.api.IFlux;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.IFluxController;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.client.GuiFluxController;
import sonar.flux.client.GuiFluxPlug;
import sonar.flux.client.GuiFluxPoint;
import sonar.flux.client.GuiFluxStorage;
import sonar.flux.common.ContainerFlux;
import sonar.flux.network.CommonNetworkCache;
import sonar.flux.network.CommonNetworkCache.ViewingType;
import sonar.flux.network.PacketFluxError;
import sonar.flux.network.ServerNetworkCache;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public abstract class TileEntityFlux extends TileEntitySonar implements IFlux, IEnergyHandler, IByteBufTile {

	public static class Controller extends TileEntityFlux implements IGuiTile, IFluxController {

		public SyncEnum<PriorityMode> sendMode = new SyncEnum(PriorityMode.values(), 10);
		public SyncEnum<PriorityMode> receiveMode = new SyncEnum(PriorityMode.values(), 11);
		public SyncEnum<TransmitterMode> transmitter = new SyncEnum(TransmitterMode.values(), 12);
		public SyncEnum<TransferMode> transfer = new SyncEnum(TransferMode.values(), 13);

		public Controller() {
			super(ConnectionType.CONTROLLER);
			syncParts.addAll(Arrays.asList(sendMode, receiveMode, transmitter, transfer));
			customName.setDefault("Flux Controller");
		}

		@Override
		public PriorityMode getSendMode() {
			return sendMode.getObject();
		}

		@Override
		public PriorityMode getReceiveMode() {
			return receiveMode.getObject();
		}

		@Override
		public TransmitterMode getTransmitterMode() {
			return transmitter.getObject();
		}

		@Override
		public TransferMode getTransferMode() {
			return transfer.getObject();
		}

		@Override
		public boolean canConnectEnergy(EnumFacing from) {
			return false;
		}

		@Override
		public Object getGuiContainer(EntityPlayer player) {
			return new ContainerFlux(player, this, false);
		}

		@Override
		public Object getGuiScreen(EntityPlayer player) {
			return new GuiFluxController(player, this);
		}

		@Override
		public void writePacket(ByteBuf buf, int id) {
			super.writePacket(buf, id);
			switch (id) {
			case 10:
				sendMode.writeToBuf(buf);
				break;
			case 11:
				receiveMode.writeToBuf(buf);
				break;
			case 12:
				transfer.writeToBuf(buf);
				break;
			case 13:
				transmitter.writeToBuf(buf);
				break;
			case 14:
				customName.writeToBuf(buf);
				break;
			}
		}

		@Override
		public void readPacket(ByteBuf buf, int id) {
			super.readPacket(buf, id);
			switch (id) {
			case 10:
				sendMode.readFromBuf(buf);
				break;
			case 11:
				receiveMode.readFromBuf(buf);
				break;
			case 12:
				transfer.readFromBuf(buf);
				break;
			case 13:
				transmitter.readFromBuf(buf);
				break;
			case 14:
				customName.readFromBuf(buf);
				break;
			}
		}

	}

	@Optional.InterfaceList({ @Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = "CoFHAPI"), @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "Tesla") })
	public static class Point extends TileEntityFlux implements IGuiTile, IEnergyProvider, ITeslaProducer {

		public Point() {
			super(ConnectionType.POINT);
			customName.setDefault("Flux Point");
		}

		@Override
		public Object getGuiContainer(EntityPlayer player) {
			return new ContainerFlux(player, this, false);
		}

		@Override
		public Object getGuiScreen(EntityPlayer player) {
			return new GuiFluxPoint(player, this);
		}

		@Override
		public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
			if (maxExtract == 0) {
				return 0;
			}
			return (int) (this.getNetwork().extractEnergy(Math.min(maxExtract, getTransferLimit()), simulate ? ActionType.SIMULATE : ActionType.PERFORM));
		}

		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			if (SonarLoader.teslaLoaded) {
				if (capability == TeslaCapabilities.CAPABILITY_PRODUCER)
					return true;
			}
			return super.hasCapability(capability, facing);
		}

		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (SonarLoader.teslaLoaded) {
				if (capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
					//in this situation this is just easier.
					return (T) this;
				}
			}
			return super.getCapability(capability, facing);
		}

		@Override
		public long takePower(long power, boolean simulated) {
			return this.extractEnergy(null, (int) Math.min(power, Integer.MAX_VALUE), simulated);
		}

	}

	@Optional.InterfaceList({ @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHAPI"), @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "Tesla") })
	public static class Plug extends TileEntityFlux implements IGuiTile, IEnergyReceiver, ITeslaConsumer {

		public Plug() {
			super(ConnectionType.PLUG);
			customName.setDefault("Flux Plug");
		}

		@Override
		public Object getGuiContainer(EntityPlayer player) {
			return new ContainerFlux(player, this, false);
		}

		@Override
		public Object getGuiScreen(EntityPlayer player) {
			return new GuiFluxPlug(player, this);
		}

		@Override
		public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
			if (maxReceive == 0) {
				return 0;
			}
			return (int) (this.getNetwork().receiveEnergy(Math.min(maxReceive, getTransferLimit()), simulate ? ActionType.SIMULATE : ActionType.PERFORM));
		}

		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			if (SonarLoader.teslaLoaded) {
				if (capability == TeslaCapabilities.CAPABILITY_CONSUMER)
					return true;
			}
			return super.hasCapability(capability, facing);
		}

		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (SonarLoader.teslaLoaded) {
				if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
					//in this situation this is just easier.
					return (T) this;
				}
			}
			return super.getCapability(capability, facing);
		}

		@Override
		public long givePower(long power, boolean simulated) {
			return this.receiveEnergy(null, (int) Math.min(power, Integer.MAX_VALUE), simulated);
		}
	}

	public static class Storage extends TileEntityFlux implements IGuiTile {

		public final SyncEnergyStorage storage;
		public int maxTransfer;

		public static class Basic extends Storage {
			public Basic() {
				super(128000, 6400);
				customName.setDefault("Basic Storage");
			}
		}

		public static class Advanced extends Storage {
			public Advanced() {
				super(12800000, 12800);
				customName.setDefault("Herculean Storage");
			}
		}

		public static class Massive extends Storage {
			public Massive() {
				super(128000000, 256000);
				customName.setDefault("Gargantuan Storage");
			}
		}

		public Storage(int capacity, int transfer) {
			super(ConnectionType.STORAGE);
			maxTransfer = transfer;
			storage = new SyncEnergyStorage(capacity, maxTransfer);
			syncParts.add(storage);
		}

		public void update() {
			super.update();
			if (storage.hasChanged()) {
				SonarCore.sendPacketAround(this, 128, 10);
			}
			if (colour.hasChanged()) {
				SonarCore.sendPacketAround(this, 128, 11);
			}
		}

		@Override
		public Object getGuiContainer(EntityPlayer player) {
			return new ContainerFlux(player, this, false);
		}

		@Override
		public Object getGuiScreen(EntityPlayer player) {
			return new GuiFluxStorage(player, this);
		}

		@Override
		public long getTransferLimit() {
			return storage.getMaxExtract();
		}

		@Override
		public boolean canConnectEnergy(EnumFacing from) {
			return true;
		}

		@Override
		public int getEnergyStored(EnumFacing from) {
			return storage.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored(EnumFacing from) {
			return storage.getMaxEnergyStored();
		}

		@Override
		public void onFirstTick() {
			super.onFirstTick();
		}

		public void readData(NBTTagCompound nbt, SyncType type) {
			super.readData(nbt, type);
			if (type.isType(SyncType.DROP)) {
				this.storage.setEnergyStored(nbt.getInteger("energy"));
			}
		}

		public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
			super.writeData(nbt, type);
			if (type.isType(SyncType.DROP)) {
				nbt.setInteger("energy", this.storage.getEnergyStored());
			}
			return nbt;
		}

		@Override
		public void writePacket(ByteBuf buf, int id) {
			super.writePacket(buf, id);
			switch (id) {
			case 10:
				storage.writeToBuf(buf);
				break;
			case 11:
				colour.writeToBuf(buf);
				break;
			}
		}

		@Override
		public void readPacket(ByteBuf buf, int id) {
			super.readPacket(buf, id);
			switch (id) {
			case 10:
				storage.readFromBuf(buf);
				break;
			case 11:
				colour.readFromBuf(buf);
				break;
			}
		}
	}

	// shared
	public SyncTagType.INT priority = new SyncTagType.INT(0);
	public SyncTagType.LONG limit = (LONG) new SyncTagType.LONG(1).setDefault(Long.valueOf(256000));
	public SyncTagType.STRING networkName = new SyncTagType.STRING(2);
	public SyncTagType.STRING networkOwner = new SyncTagType.STRING(3);
	public SyncTagType.INT networkID = new SyncTagType.INT(4);
	public SyncTagType.STRING playerName = new SyncTagType.STRING(5);
	public SyncTagType.STRING customName = (STRING) new SyncTagType.STRING(6).setDefault("Flux Connection");
	public SyncTagType.INT colour = new SyncTagType.INT(7);
	public boolean[] connections = new boolean[6];
	private final ConnectionType type;

	// server only
	private IFluxNetwork network = ServerNetworkCache.empty;
	private int checkTicks = 0;

	// client only
	public FluxError error = FluxError.NONE;

	public TileEntityFlux(ConnectionType type) {
		this.type = type;
		syncParts.addAll(Arrays.asList(priority, limit, networkName, networkOwner, networkID, playerName, customName, colour));
	}

	public void setPlayerName(String name) {
		this.playerName.setObject(name);
	}

	public void changeNetwork(IFluxNetwork network) {
		changeNetwork(network, null);
	}

	public void changeNetwork(IFluxNetwork network, EntityPlayer player) {
		if (this.isServer()) {
			this.network = network;
			if (player != null && !network.isFakeNetwork() && this instanceof IFluxController) {
				if (network.hasController()) {
					FluxNetworks.network.sendTo(new PacketFluxError(getPos(), FluxError.HAS_CONTROLLER), (EntityPlayerMP) player);
					changeNetwork(CommonNetworkCache.empty, null);
					return;
				}
			}
			this.network.addFluxConnection(this);
			this.networkName.setObject(network.getNetworkName());
			this.networkOwner.setObject(network.getOwnerName());
			this.networkID.setObject(network.getNetworkID());
			this.colour.setObject(network.getNetworkColour().getRGB());
			this.markBlockForUpdate();
		}
	}

	public void update() {
		super.update();
		if (isServer()) {
			if (checkTicks >= 20) {
				updateConnections();
				checkTicks=0;
			} else {
				checkTicks++;
			}
		}
	}

	/*
	 * public void update() { super.update(); if (isServer() && playerName != null) { // networks = FluxNetworkRegistry.getAvailableNetworks(playerName.getObject()); // FluxNetworks.network.sendToAllAround(new PacketFluxNetworkList(pos, networks), new TargetPoint(worldObj.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 64));
	 * 
	 * if (network != FluxNetworkRegistry.empty) { } } }
	 */
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		SonarCore.sendPacketAround(this, 128, 0);

	}

	public void updateConnections() {
		for (EnumFacing face : EnumFacing.VALUES) {
			BlockPos pos = this.pos.offset(face);
			TileEntity tile = getWorld().getTileEntity(pos);
			if (tile != null && !(tile instanceof IFlux)) {
				if (tile instanceof IEnergyConnection) {
					connections[face.getIndex()] = true;
					continue;
				}
				if (SonarAPI.getEnergyHelper().canTransferEnergy(tile, face) != null) {
					connections[face.getIndex()] = true;
				}
			} else {
				connections[face.getIndex()] = false;
			}
		}
		SonarCore.sendPacketAround(this, 128, 0);
	}

	public void validate() {
		super.validate();
		if (playerName != null && isServer()) {
			IFluxCommon network = FluxNetworks.cache.getNetwork(networkID.getObject());
			if (!network.isFakeNetwork() && network instanceof IFluxNetwork) {
				changeNetwork((IFluxNetwork) network, null);
			}
		}
	}

	public void invalidate() {
		super.invalidate();
		if (playerName != null && isServer()) {
			IFluxCommon network = FluxNetworks.cache.getNetwork(networkID.getObject());
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
		return limit.getObject();
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
		case 0:
			for (int i = 0; i < 6; i++) {
				connections[i] = buf.readBoolean();
			}
			markBlockForUpdate();
			break;
		case 1:
			priority.readFromBuf(buf);
			break;
		case 2:
			limit.readFromBuf(buf);
			break;
		case 3:
			customName.readFromBuf(buf);
			break;
		case 4:
			EntityPlayerMP player = SonarHelper.getPlayerFromName(playerName.getObject());
			if (player != null)
				FluxNetworks.cache.addViewer(player, ViewingType.CONNECTIONS, networkID.getObject());
			break;
		case 5:
			player = SonarHelper.getPlayerFromName(playerName.getObject());
			if (player != null) {
				FluxNetworks.cache.removeViewer(player);
				FluxNetworks.cache.addViewer(player, ViewingType.CLIENT, networkID.getObject());
			}
			break;
		}
	}
}
