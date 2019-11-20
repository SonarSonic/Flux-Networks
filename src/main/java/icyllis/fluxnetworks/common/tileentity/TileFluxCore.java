package icyllis.fluxnetworks.common.tileentity;

import icyllis.fluxnetworks.api.network.IFluxNetwork;
import icyllis.fluxnetworks.api.tile.IFluxTile;
import icyllis.fluxnetworks.api.util.NBTType;
import icyllis.fluxnetworks.system.FluxConfig;
import icyllis.fluxnetworks.system.util.FluxLibs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;

public abstract class TileFluxCore extends TileEntity implements IFluxTile, ITickableTileEntity {

    public HashSet<PlayerEntity> playerUsing = new HashSet<>();

    public String customName = "";
    public int networkID = -1;
    public UUID playerUUID = FluxLibs.UUID_EMPTY;
    public int color = -1;
    public int folderID = -1;

    public int priority = 0;
    public long limit = FluxConfig.defaultLimit;

    public boolean surgeMode = false;
    public boolean disableLimit = false;

    public boolean connected = false;
    public byte[] connections = new byte[]{0,0,0,0,0,0};

    public boolean chunkLoading = false;

    protected IFluxNetwork network = FluxLibs.INVALID_NETWORK;

    protected boolean load = false;

    public TileFluxCore(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public void onChunkUnloaded() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void connect(IFluxNetwork network) {

    }

    @Override
    public void disconnect(IFluxNetwork network) {

    }

    @Override
    public IFluxNetwork getNetwork() {
        return network;
    }

    @Override
    public int getNetworkID() {
        return networkID;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, -1, writeNetworkNBT(new CompoundNBT(), NBTType.TILE_UPDATE));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readNetworkNBT(pkt.getNbtCompound(), NBTType.TILE_UPDATE);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        read(tag);
    }

    public void sendPackets() {
        if (world != null) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return writeNetworkNBT(compound, NBTType.ALL_SAVE);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        readNetworkNBT(compound, NBTType.ALL_SAVE);
    }

    @Override
    public CompoundNBT writeNetworkNBT(CompoundNBT nbt, NBTType type) {
        return nbt;
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt, NBTType type) {

    }

    @Override
    public boolean canAccess(PlayerEntity player) {
        return true;
    }

    @Override
    public UUID getConnectionOwner() {
        return playerUUID;
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public int getLogicalPriority() {
        return surgeMode ? Integer.MAX_VALUE : priority;
    }

    @Override
    public int getActualPriority() {
        return priority;
    }

    @Override
    public long getLogicalLimit() {
        return disableLimit ? Long.MAX_VALUE : limit;
    }

    @Override
    public long getActualLimit() {
        return limit;
    }

    @Override
    public boolean isDisableLimit() {
        return disableLimit;
    }

    @Override
    public boolean isSurgeMode() {
        return surgeMode;
    }

    @Override
    public boolean isChunkLoaded() {
        return !isRemoved();
    }

    @Override
    public boolean isForcedLoading() {
        return chunkLoading;
    }

    @Override
    public void open(PlayerEntity player) {

    }

    @Override
    public void close(PlayerEntity player) {

    }
}
