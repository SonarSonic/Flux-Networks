package fluxnetworks.common.tileentity;

import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.api.utils.NBTType;
import fluxnetworks.system.FluxConfig;
import fluxnetworks.system.util.FluxLibs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.UUID;

public class TileFluxCore extends TileEntity {

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

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {

    }

    public void readCustomNBT(CompoundNBT tag, NBTType type) {

    }
}
