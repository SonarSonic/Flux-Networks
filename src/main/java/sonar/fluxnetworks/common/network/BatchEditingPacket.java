package sonar.fluxnetworks.common.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sonar.fluxnetworks.api.misc.Coord4D;
import sonar.fluxnetworks.api.gui.EnumFeedbackInfo;
import sonar.fluxnetworks.api.network.FluxLogicType;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemFluxDevice;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BatchEditingPacket extends AbstractPacket {

    public int networkID;
    public List<Coord4D> coord4DS = new ArrayList<>();
    public CompoundNBT tag;
    public boolean[] editions = new boolean[7];

    public BatchEditingPacket(PacketBuffer buf) {
        networkID = buf.readInt();
        for(int i = 0; i < 7; i++) {
            editions[i] = buf.readBoolean();
        }
        tag = buf.readCompoundTag();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            coord4DS.add(new Coord4D(buf));
        }
    }

    public BatchEditingPacket(int networkID, List<Coord4D> coord4DS, CompoundNBT tag, boolean[] editions) {
        this.networkID = networkID;
        this.coord4DS = coord4DS;
        this.tag = tag;
        this.editions = editions;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeInt(networkID);
        for(Boolean b : editions) {
            buf.writeBoolean(b);
        }
        buf.writeCompoundTag(tag);
        buf.writeInt(coord4DS.size());
        coord4DS.forEach(c -> c.write(buf));
    }


    @Override
    public Object handle(NetworkEvent.Context ctx) {
        PlayerEntity player = PacketHandler.getPlayer(ctx);
        if (player != null) {
            IFluxNetwork network = FluxNetworkData.getNetwork(networkID);
            if (network.isValid()) {
                if (network.getPlayerAccess(player).canEdit()) {
                    boolean editName = editions[0];
                    boolean editPriority = editions[1];
                    boolean editLimit = editions[2];
                    boolean editSurge = editions[3];
                    boolean editUnlimited = editions[4];
                    boolean editChunkLoad = editions[5];
                    boolean disconnect = editions[6];
                    String name = tag.getString(ItemFluxDevice.CUSTOM_NAME);
                    int priority = tag.getInt(ItemFluxDevice.PRIORITY);
                    long limit = tag.getLong(ItemFluxDevice.LIMIT);
                    boolean surge = tag.getBoolean(ItemFluxDevice.SURGE_MODE);
                    boolean unlimited = tag.getBoolean(ItemFluxDevice.DISABLE_LIMIT);
                    boolean load = tag.getBoolean("chunkLoad");
                    //TODO
                    List<TileFluxDevice> onlineDevices = new ArrayList<>();
                    network.getConnections(FluxLogicType.ANY).forEach(e -> onlineDevices.add((TileFluxDevice) e));
                    AtomicBoolean reject = new AtomicBoolean(false);

                    /*for (Coord4D c : coord4DS) {
                        onlineDevices.stream().filter(f -> f.getCoords().equals(c)).findFirst().ifPresent(f -> {
                            if (disconnect) {
                                f.getNetwork().enqueueConnectionRemoval(f, false);
                            } else {
                                if (editName) {
                                    f.customName = name;
                                }
                                if (editPriority) {
                                    f.priority = priority;
                                }
                                if (editLimit) {
                                    f.limit = Math.min(limit, f.getMaxTransferLimit());
                                }
                                if (editSurge) {
                                    f.surgeMode = surge;
                                }
                                if (editUnlimited) {
                                    f.disableLimit = unlimited;
                                }
                                if (editChunkLoad) {
                                    if (FluxConfig.enableChunkLoading) {
                                        if (load) {
                                            if (f.getDeviceType().isStorage()) {
                                                reject.set(true);
                                                return;
                                            }
                                            if (!f.chunkLoading) {
                                                f.chunkLoading = FluxChunkManager.addChunkLoader((ServerWorld) f.getFluxWorld(), new ChunkPos(f.getPos()));
                                                if (!f.chunkLoading) {
                                                    reject.set(true);
                                                }
                                            }
                                        } else {
                                            FluxChunkManager.removeChunkLoader((ServerWorld) f.getFluxWorld(), new ChunkPos(f.getPos()));
                                            f.chunkLoading = false;
                                        }
                                    } else {
                                        f.chunkLoading = false;
                                    }
                                }
                                f.sendFullUpdatePacket();
                            }
                        });
                    }*/
                    if (reject.get()) {
                        reply(ctx, new SFeedbackMessage(EnumFeedbackInfo.REJECT_SOME));
                    }
                    return disconnect ? new SFeedbackMessage(EnumFeedbackInfo.SUCCESS_2) : new SFeedbackMessage(EnumFeedbackInfo.SUCCESS);
                } else {
                    return new SFeedbackMessage(EnumFeedbackInfo.NO_ADMIN);
                }
            }
        }
        return null;
    }

}
