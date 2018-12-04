package sonar.flux.common.tileentity;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.utils.BlockCoords;
import sonar.core.common.tile.TileEntitySyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.listener.ListenableList;
import sonar.core.listener.PlayerListener;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.sync.ISonarValue;
import sonar.core.sync.ISyncValue;
import sonar.core.sync.SyncRegistry;
import sonar.flux.FluxConfig;
import sonar.flux.api.*;
import sonar.flux.api.configurator.FluxConfigurationType;
import sonar.flux.api.configurator.IFluxConfigurable;
import sonar.flux.api.network.FluxPlayer;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.api.network.PlayerAccess;
import sonar.flux.api.tiles.IFluxListenable;
import sonar.flux.client.FluxColourHandler;
import sonar.flux.client.gui.GuiTab;
import sonar.flux.client.gui.tabs.GuiTabConnectionIndex;
import sonar.flux.common.block.FluxConnection;
import sonar.flux.common.containers.ContainerFlux;
import sonar.flux.common.events.FluxConnectionEvent;
import sonar.flux.common.item.ItemNetworkConnector;
import sonar.flux.connection.FluxHelper;
import sonar.flux.connection.FluxListener;
import sonar.flux.connection.FluxNetworkInvalid;
import sonar.flux.connection.NetworkSettings;
import sonar.flux.connection.transfer.handlers.FluxTransferHandler;
import sonar.flux.network.ListenerHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public abstract class TileFlux extends TileEntitySyncable implements IFluxListenable, IByteBufTile, IFluxConfigurable, IFlexibleGui {

	private final ConnectionType type;

	//// USER CONFIGURED \\\\
	public ISyncValue<Integer> priority = SyncRegistry.createValue(Integer.class, value_watcher, "0", 0);
	public ISyncValue<Long> limit = SyncRegistry.createValue(Long.class, value_watcher, "1", FluxConfig.defaultLimit);
	public ISyncValue<Boolean> disableLimit = SyncRegistry.createValue(Boolean.class, value_watcher, "2", false);
	public ISyncValue<Integer> networkID = SyncRegistry.createValue(Integer.class, value_watcher, "4", -1);
	public ISyncValue<UUID> playerUUID = SyncRegistry.createValue(UUID.class, value_watcher, "5", new UUID(0,0));
	public ISyncValue<String> customName = SyncRegistry.createValue(String.class, value_watcher, "6", "Flux Connection");
	public ISyncValue<Integer> colour = SyncRegistry.createValue(Integer.class, value_watcher, "7", FluxNetworkInvalid.INVALID.getSetting(NetworkSettings.NETWORK_COLOUR).getRGB());

	public ISyncValue<int[]> connections = SyncRegistry.createValue(int[].class, value_watcher, "connect", new int[]{0,0,0,0,0,0});
	public ISyncValue<EnumActivationType> activation_type = SyncRegistry.createValue(EnumActivationType.class, value_watcher, "activation_type", EnumActivationType.ACTIVATED);
	public ISyncValue<EnumPriorityType> priority_type = SyncRegistry.createValue(EnumPriorityType.class, value_watcher, "priority_type", EnumPriorityType.NORMAL);
	public ISyncValue<Boolean> redstone_power = SyncRegistry.createValue(Boolean.class, value_watcher, "rpower", false);
	public ISyncValue<Integer> folder_id = SyncRegistry.createValue(Integer.class, value_watcher, "folder", -1);

	private Boolean ACTIVATED = null;

	//// PLAYERS VIEWING THE FLUX GUI \\\\
	public ListenableList<PlayerListener> listeners = new ListenableList<>(this, FluxListener.values().length);

	//// SERVER ONLY \\\\
	public IFluxNetwork network = FluxNetworkInvalid.INVALID;

	//// CLIENT ONLY \\\\
	public FluxError error = null;
	private ClientFlux client_flux;

	public TileFlux(ConnectionType type) {
		super();
		this.type = type;
	}

	public void updateTransfers(EnumFacing ...faces) {
		getTransferHandler().updateTransfers(faces);
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemstack) {
		if (player instanceof EntityPlayer) {
			setPlayerUUID(FluxPlayer.getOnlineUUID((EntityPlayer)player));
			updateTransfers(EnumFacing.VALUES);
		}
	}

	@Override
	public boolean isActive() {
		if(ACTIVATED == null){
			if(isValid()) {
				updateRedstonePower();
			}else{
				return false;
			}
		}
		return ACTIVATED;
	}

	public void updateRedstonePower(){
		if(!world.isRemote){
			redstone_power.setValue(world.isBlockPowered(getPos()));
		}
		switch(activation_type.getValue()){
			case ACTIVATED: ACTIVATED = true; break;
			case DISACTIVATED: ACTIVATED = false; break;
			case POSITIVE_SIGNAL: ACTIVATED = redstone_power.getValue(); break;
			case NEGATIVE_SIGNAL: ACTIVATED = !redstone_power.getValue(); break;
			default: ACTIVATED = true; break;
		}
	}

	//// NETWORK CONNECTION \\\\

	@Override
	public void connect(IFluxNetwork network) {
		IFluxNetwork oldNetwork = this.network;
		this.network = network;
		this.networkID.setValue(network.getNetworkID());
		colour.setValue(network.getSetting(NetworkSettings.NETWORK_COLOUR).getRGB());
		setAndSendConnectionState(true);
		ListenerHelper.onNetworkChanged(this, oldNetwork, network);
	}

	@Override
	public void disconnect(IFluxNetwork network) {
		if (network.getNetworkID() == this.networkID.getValue()) {
			IFluxNetwork oldNetwork = this.network;
			this.network = FluxNetworkInvalid.INVALID;
			this.networkID.setValue(-1);
			colour.setValue(network.getSetting(NetworkSettings.NETWORK_COLOUR).getRGB());
			setAndSendConnectionState(false);
			ListenerHelper.onNetworkChanged(this, oldNetwork, network);
		}
	}

	/** changes the block state of the Flux Connection to reflect if it is connected or not */
	public void setAndSendConnectionState(boolean bool) {
		if (world.isBlockLoaded(this.getPos())) {
			World world = getWorld();
			IBlockState state = getWorld().getBlockState(getPos());
			if (state.getBlock() instanceof FluxConnection) { // sanity check
				world.setBlockState(getPos(), state.withProperty(FluxConnection.CONNECTED, bool), 2);
			}
		}
	}

	/** makes sure tile entity is kept with connection state is changed */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	//// TILE ENTITY CONNECTIONS \\\\

	public EnumFacing[] getValidFaces() {
		return EnumFacing.values();
	}

	//// PLAYER ACCESS \\\\

	/** set the player who placed this Flux Connection */
	public void setPlayerUUID(UUID name) {
		this.playerUUID.setValue(name);
	}

	@Override
	public PlayerAccess canAccess(EntityPlayer player) {
		if (FluxHelper.isPlayerAdmin(player)) {
			return PlayerAccess.CREATIVE;
		}
		if (playerUUID.getValue() != null && playerUUID.getValue().equals(FluxPlayer.getOnlineUUID(player))) {
			return PlayerAccess.OWNER;
		}
		return getNetwork().isFakeNetwork() ? PlayerAccess.BLOCKED : getNetwork().getPlayerAccess(player);
	}

	public UUID getConnectionOwner() {
		return playerUUID.getValue();
	}

	//// TILE ENTITY EVENTS \\\\

	public boolean LOADED = false;

	@Override
	public void update(){
		super.update();
		if (!world.isRemote && !LOADED) {
			FluxHelper.addConnection(this, AdditionType.ADD);
			updateTransfers(EnumFacing.VALUES);
			LOADED = true;
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (!world.isRemote && LOADED) {
			FluxHelper.removeConnection(this, RemovalType.CHUNK_UNLOAD);
			LOADED = false;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!world.isRemote && LOADED) {
			FluxHelper.removeConnection(this, RemovalType.REMOVE);
			LOADED = false;
		}
	}

	BlockCoords coords = null;

	@Override
	public BlockCoords getCoords() {
		if(coords == null){
			coords = new BlockCoords(getPos(), getWorld());
		}
		return coords;
	}

	//// IFLUX IMPLEMENTATION \\\\

	@Override
	public int getNetworkID() {
		return networkID.getValue();
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
		return disableLimit.getValue() ? Long.MAX_VALUE : limit.getValue();
	}

	@Override
	public int getCurrentPriority() {
		return priority_type.getValue() == EnumPriorityType.NORMAL ? priority.getValue() : Integer.MAX_VALUE;
	}

	@Override
	public int getFolderID() {
		return folder_id.getValue();
	}

	@Override
	public World getDimension() {
		return world;
	}

	@Override
	public String getCustomName() {
		return customName.getValue();
	}

	//// CLIENT FLUX \\\\
	public ClientFlux getClientFlux(){
		if(client_flux==null){
			client_flux = new ClientFlux(this);
		}
		return client_flux;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.SPECIAL) || (this.network.isFakeNetwork() && type.isType(SyncType.DEFAULT_SYNC))) {
			if (nbt.hasKey("client_flux")) {
				client_flux = new ClientFlux(nbt.getCompoundTag("client_flux"));
			}
		}
		if(type.isType(SyncType.SAVE)){
			((FluxTransferHandler)getTransferHandler()).buffer = nbt.getLong("buf");
		}
		if(type.isType(SyncType.DROP)){
			if(nbt.hasKey(ItemNetworkConnector.CUSTOM_NAME_TAG)) customName.setValue(nbt.getString(ItemNetworkConnector.CUSTOM_NAME_TAG));
			if(nbt.hasKey(ItemNetworkConnector.PRIORITY_TAG)) priority.setValue(nbt.getInteger(ItemNetworkConnector.PRIORITY_TAG));
			if(nbt.hasKey(ItemNetworkConnector.TRANSFER_LIMIT_TAG)) limit.setValue(nbt.getLong(ItemNetworkConnector.TRANSFER_LIMIT_TAG));
			if(nbt.hasKey(ItemNetworkConnector.DISABLE_LIMIT_TAG)) disableLimit.setValue(nbt.getBoolean(ItemNetworkConnector.DISABLE_LIMIT_TAG));
			if(nbt.hasKey(ItemNetworkConnector.NETWORK_ID_TAG)) networkID.setValue(nbt.getInteger(ItemNetworkConnector.NETWORK_ID_TAG));
		}
		super.readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.SPECIAL) || (this.network.isFakeNetwork() && type.isType(SyncType.DEFAULT_SYNC))) {
			if (listeners.hasListeners(FluxListener.SYNC_INDEX.ordinal())) {
				client_flux = new ClientFlux(this);
				nbt.setTag("client_flux", client_flux.writeData(new NBTTagCompound(), type));
			}
		}
		if(type.isType(SyncType.SAVE)){
			nbt.setLong("buf", ((FluxTransferHandler)getTransferHandler()).buffer);
		}
		if(type.isType(SyncType.DROP)){
			nbt.setString(ItemNetworkConnector.CUSTOM_NAME_TAG, customName.getValue());
			nbt.setInteger(ItemNetworkConnector.PRIORITY_TAG, priority.getValue());
			nbt.setLong(ItemNetworkConnector.TRANSFER_LIMIT_TAG, limit.getValue());
			nbt.setBoolean(ItemNetworkConnector.DISABLE_LIMIT_TAG, disableLimit.getValue());
			nbt.setInteger(ItemNetworkConnector.NETWORK_ID_TAG, networkID.getValue());
		}
		return super.writeData(nbt, type);
	}

	@Override
	public void onValuesChanged() {
		super.onValuesChanged();
		if (this.world != null && colour.isDirty()) {
			if(!world.isRemote) {
				SonarCore.sendPacketAround(this, 128, 11);
			}else {
				FluxColourHandler.loadColourCache(getNetworkID(), colour.getValue());
				this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
			}
		}
	}

	@Override
	public void onInternalValueChanged(ISonarValue value){
		super.onInternalValueChanged(value);
		if(world != null && world.isRemote && value == colour) {
			FluxColourHandler.loadColourCache(getNetworkID(), colour.getValue());
			this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	public void markSettingChanged(ConnectionSettings setting){
		MinecraftForge.EVENT_BUS.post(new FluxConnectionEvent.SettingChanged(this, setting));
	}

	//// PACKETS \\\\\

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case -1:
			disableLimit.save(buf);
			break;
		case 1:
			priority.save(buf);
			break;
		case 2:
			limit.save(buf);
			break;
		case 3:
			customName.save(buf);
			break;
		case 11:
			colour.save(buf);
			break;
		case 12:
			activation_type.save(buf);
			break;
		case 16:
			priority_type.save(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case -1:
			disableLimit.load(buf);
			markSettingChanged(ConnectionSettings.TRANSFER_LIMIT);
			break;
		case 1:
			priority.load(buf);
			markSettingChanged(ConnectionSettings.PRIORITY);
			break;
		case 2:
			limit.load(buf);
			markSettingChanged(ConnectionSettings.TRANSFER_LIMIT);
			break;
		case 3:
			customName.load(buf);
			markSettingChanged(ConnectionSettings.CUSTOM_NAME);
			break;
		case 11:
			colour.load(buf);
			break;
		case 12:
			activation_type.load(buf);
			updateRedstonePower();
			break;
		case 16:
			priority_type.load(buf);
			markSettingChanged(ConnectionSettings.PRIORITY);
			break;

		}
	}

	@Override
	public ListenableList<PlayerListener> getListenerList() {
		return listeners;
	}

	@Override
	public boolean isValid() {
		return !isInvalid();
	}

	@Override
	public boolean isChunkLoaded() {
		return isValid();
	}

	//// Flux Configurator \\\\

	@Override
	public NBTTagCompound addConfigs(NBTTagCompound config, EntityPlayer player) {
		if (!this.getNetwork().isFakeNetwork() && network.getNetworkID() != -1) {
			config.setInteger(FluxConfigurationType.NETWORK.getNBTName(), getNetwork().getNetworkID());
		}
		config.setInteger(FluxConfigurationType.PRIORITY.getNBTName(), priority.getValue());
		config.setLong(FluxConfigurationType.TRANSFER.getNBTName(), limit.getValue());
		config.setBoolean(FluxConfigurationType.DISABLE_LIMIT.getNBTName(), disableLimit.getValue());
		return config;
	}

	@Override
	public void readConfigs(NBTTagCompound config, EntityPlayer player) {
		if (config.hasKey(FluxConfigurationType.NETWORK.getNBTName())) {
			int storedID = config.getInteger(FluxConfigurationType.NETWORK.getNBTName());
			if (storedID != -1) {
				FluxHelper.removeConnection(this, null);
				this.networkID.setValue(storedID);
				FluxHelper.addConnection(this, null);
			}
		}
		if (config.hasKey(FluxConfigurationType.PRIORITY.getNBTName())) {
			this.priority.setValue(config.getInteger(FluxConfigurationType.PRIORITY.getNBTName()));
			markSettingChanged(ConnectionSettings.PRIORITY);
		}
		if (config.hasKey(FluxConfigurationType.TRANSFER.getNBTName())) {
			this.limit.setValue(config.getLong(FluxConfigurationType.TRANSFER.getNBTName()));
			markSettingChanged(ConnectionSettings.TRANSFER_LIMIT);
		}
		if (config.hasKey(FluxConfigurationType.DISABLE_LIMIT.getNBTName())) {
			this.disableLimit.setValue(config.getBoolean(FluxConfigurationType.DISABLE_LIMIT.getNBTName()));
			markSettingChanged(ConnectionSettings.TRANSFER_LIMIT);
		}
	}

	@Override
	public void onGuiOpened(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		ListenerHelper.onPlayerOpenTileGui(this, player);
		ListenerHelper.onPlayerOpenTab(this, player, GuiTab.INDEX);

		//List<IFluxNetwork> toSend = FluxNetworkCache.instance().getAllowedNetworks(player, false);
		//ListHelper.addWithCheck(toSend, network);
		//FluxNetworks.network.sendTo(new PacketFluxNetworkUpdate(toSend, SyncType.SAVE, true), (EntityPlayerMP) player);
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new ContainerFlux(player, this);
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return GuiTab.INDEX.getGuiScreen(this, getTabs());
	}

	public List<GuiTab> getTabs(){
		return Lists.newArrayList(GuiTab.INDEX, GuiTab.NETWORK_SELECTION, GuiTab.CONNECTIONS, GuiTab.NETWORK_STATISTICS, GuiTab.PLAYERS, GuiTab.DEBUG, GuiTab.NETWORK_EDIT, GuiTab.NETWORK_CREATE);
	}

	@Nonnull
	public Object getIndexScreen(List<GuiTab> tabs){
		return new GuiTabConnectionIndex<>(this, tabs);
	}
}
