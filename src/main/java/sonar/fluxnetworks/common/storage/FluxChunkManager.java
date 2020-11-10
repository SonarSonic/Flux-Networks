package sonar.fluxnetworks.common.storage;

import it.unimi.dsi.fastutil.longs.LongIterator;
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
            = TicketType.create("fluxnetworks:chunk_loading", Comparator.comparing(TileEntity::getPos));

    // level = 33 - distance = 31, TileEntity, Entity's ticking and all game logic will run
    private static final int LOAD_DISTANCE = 2;

    /*public static final Map<ResourceLocation, List<ChunkPos>> activeChunks = new HashMap<>();

    public static void clear() {
        activeChunks.clear();
    }*/

    public static void loadWorld(ServerWorld world) {
        if (!FluxConfig.enableChunkLoading) {
            return;
        }
        RegistryKey<World> dim = world.getDimensionKey();
        LongSet tickets = FluxNetworkData.getTickets(dim);
        /*List<ChunkPos> chunks = tickets.stream().map(l -> new ChunkPos(BlockPos.fromLong(l)))
                .distinct().collect(Collectors.toList());
        chunks.forEach(pos -> registerTicket(world, pos));*/
        if (!tickets.isEmpty()) {
            ServerChunkProvider chunkProvider = world.getChunkProvider();
            LongIterator iterator = tickets.iterator();
            while (iterator.hasNext()) {
                BlockPos blockPos = BlockPos.fromLong(iterator.nextLong());
                TileEntity tile = world.getTileEntity(blockPos); // loads the chunk
                if (tile instanceof TileFluxDevice) {
                    ChunkPos chunkPos = new ChunkPos(blockPos);
                    chunkProvider.registerTicket(FLUX_TICKET_TYPE, chunkPos, LOAD_DISTANCE, (TileFluxDevice) tile);
                    ((TileFluxDevice) tile).setForcedLoading(true);
                } else {
                    // remove invalid tickets
                    iterator.remove();
                }
            }
        }
        FluxNetworks.LOGGER.info("Chunks Loaded in {}, Tickets Count: {}",
                dim.getLocation(), tickets.size());
    }

    public static void tickWorld(@Nonnull ServerWorld world) {
        if (!FluxNetworkData.getTickets(world.getDimensionKey()).isEmpty()) {
            // keep world ticking
            world.resetUpdateEntityTick();
        }
    }

    public static void addChunkLoader(@Nonnull TileFluxDevice tile) {
        ServerWorld world = (ServerWorld) tile.getFluxWorld();
        RegistryKey<World> dim = world.getDimensionKey();
        BlockPos blockPos = tile.getPos();
        if (FluxNetworkData.getTickets(dim).add(blockPos.toLong())) {
            ChunkPos chunkPos = new ChunkPos(blockPos);
            world.getChunk(blockPos); // loads the chunk
            world.getChunkProvider().registerTicket(FLUX_TICKET_TYPE, chunkPos, LOAD_DISTANCE, tile);
            FluxNetworks.LOGGER.debug("Added Chunk Loader in {}, Chunk: {} at {}",
                    dim.getLocation(), chunkPos, blockPos);
        } else {
            FluxNetworks.LOGGER.warn("There's already a Chunk Loader added in {} at {}", dim.getLocation(), blockPos);
        }
    }

    public static void removeChunkLoader(@Nonnull TileFluxDevice tile) {
        ServerWorld world = (ServerWorld) tile.getFluxWorld();
        RegistryKey<World> dim = world.getDimensionKey();
        BlockPos blockPos = tile.getPos();
        if (FluxNetworkData.getTickets(dim).remove(blockPos.toLong())) {
            ChunkPos chunkPos = new ChunkPos(blockPos);
            world.getChunkProvider().releaseTicket(FLUX_TICKET_TYPE, chunkPos, LOAD_DISTANCE, tile);
            FluxNetworks.LOGGER.debug("Removed Chunk Loader in {}, Chunk: {} at {}",
                    dim.getLocation(), chunkPos, blockPos);
        } else {
            FluxNetworks.LOGGER.warn("There's no such a Chunk Loader to remove in {} at {}", dim.getLocation(), blockPos);
        }
    }

    public static boolean isChunkLoader(@Nonnull TileFluxDevice tile) {
        RegistryKey<World> dim = tile.getFluxWorld().getDimensionKey();
        BlockPos blockPos = tile.getPos();
        return FluxNetworkData.getTickets(dim).contains(blockPos.toLong());
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