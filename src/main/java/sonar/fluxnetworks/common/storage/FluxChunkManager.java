package sonar.fluxnetworks.common.storage;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class FluxChunkManager {

    private static final TicketType<TileFluxDevice> FLUX_TICKET_TYPE
            = TicketType.create("fluxnetworks:chunk_loading", Comparator.comparing(TileFluxDevice::getPos));

    /*public static final Map<ResourceLocation, List<ChunkPos>> activeChunks = new HashMap<>();

    public static void clear() {
        activeChunks.clear();
    }*/

    public static void loadWorld(ServerWorld world) {
        if (!FluxConfig.enableChunkLoading) {
            return;
        }
        RegistryKey<World> dim = world.getDimensionKey();
        LongSet tickets = FluxNetworkData.getForcedChunks(dim);
        if (tickets.isEmpty()) {
            return;
        }
        /*List<ChunkPos> chunks = tickets.stream().map(l -> new ChunkPos(BlockPos.fromLong(l)))
                .distinct().collect(Collectors.toList());
        chunks.forEach(pos -> registerTicket(world, pos));*/
        ServerChunkProvider chunkProvider = world.getChunkProvider();
        for (long p : tickets) {
            BlockPos blockPos = BlockPos.fromLong(p);
            TileEntity tile = world.getTileEntity(blockPos);
            if (tile instanceof TileFluxDevice) {
                ChunkPos chunkPos = new ChunkPos(blockPos);
                chunkProvider.registerTicket(FLUX_TICKET_TYPE, chunkPos, 2, (TileFluxDevice) tile);
            }
        }
        FluxNetworks.LOGGER.info("Chunks Loaded in Dim: {}, Tickets Count: {}",
                dim.getLocation(), tickets.size());
    }

    public static void addChunkLoader(@Nonnull ServerWorld world, @Nonnull TileFluxDevice tile) {
        RegistryKey<World> dim = world.getDimensionKey();
        BlockPos blockPos = tile.getPos();
        if (FluxNetworkData.getForcedChunks(dim).add(blockPos.toLong())) {
            ChunkPos chunkPos = new ChunkPos(blockPos);
            world.getChunk(blockPos); // loads the chunk
            world.getChunkProvider().registerTicket(FLUX_TICKET_TYPE, chunkPos, 2, tile);
            FluxNetworks.LOGGER.info("Added Chunk Loader in Dim: {}, Chunk: {} at {}",
                    dim.getLocation(), chunkPos, blockPos);
        }
    }

    public static void removeChunkLoader(@Nonnull ServerWorld world, @Nonnull TileFluxDevice tile) {
        RegistryKey<World> dim = world.getDimensionKey();
        BlockPos blockPos = tile.getPos();
        if (FluxNetworkData.getForcedChunks(dim).remove(blockPos.toLong())) {
            ChunkPos chunkPos = new ChunkPos(blockPos);
            world.getChunkProvider().releaseTicket(FLUX_TICKET_TYPE, chunkPos, 2, tile);
            FluxNetworks.LOGGER.info("Removed Chunk Loader in Dim: {}, Chunk: {} at {}",
                    dim.getLocation(), chunkPos, blockPos);
        }
    }

    /*private static void registerTicket(@Nonnull ServerWorld world, @Nonnull ChunkPos pos) {
        world.getChunk(pos.x , pos.z); // loads the chunk
        ServerChunkProvider chunkProvider = world.getChunkProvider();
        chunkProvider.registerTicket(FLUX_TICKET_TYPE, pos, 2, pos);
    }

    private static void releaseTicket(@Nonnull ServerWorld world, ChunkPos pos) {
        ServerChunkProvider chunkProvider = world.getChunkProvider();
        chunkProvider.releaseTicket(FLUX_TICKET_TYPE, pos, 2, pos);
    }*/
}