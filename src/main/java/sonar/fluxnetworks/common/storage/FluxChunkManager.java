package sonar.fluxnetworks.common.storage;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import sonar.fluxnetworks.FluxConfig;

import java.util.HashMap;
import java.util.List;


public class FluxChunkManager {

    public static final TicketType<ChunkPos> FLUX_TICKET_TYPE = TicketType.create("fluxnetworks:chunkloading", (l1, l2) -> 0);

    public static final int DEFAULT_DISTANCE = 31;

    public static final HashMap<ResourceLocation, List<ChunkPos>> activeChunks = new HashMap<>();

    public static void clear() {
        activeChunks.clear();
    }

    //TODO
    public static void loadWorld(ServerWorld world) {
        if (!FluxConfig.enableChunkLoading) {
            return;
        }
        RegistryKey<World> dim = world.getDimensionKey();
        /*List<ChunkPos> toLoad = FluxNetworkData.get().loadedChunks.get(dim);
        if(toLoad == null){
            return;
        }
        toLoad.forEach(pos -> registerTicket(world, pos));
        int count = activeChunks.getOrDefault(dim, new ArrayList<>()).size();
        if(count > 0) {
            FluxNetworks.LOGGER.info("Chunks Loaded in Dim: {}  Dim Id: {}  Chunks Loaded: {}", DimensionType.getKey(world.getDimension().getType()), dim, count);
        }*/
    }

    public static boolean addChunkLoader(ServerWorld world, ChunkPos pos) {
        /*int dim = world.getDimension().getType().getId();
        if(!FluxNetworkData.get().loadedChunks.computeIfAbsent(dim, d -> new ArrayList<>()).contains(pos)) {
            FluxNetworkData.get().loadedChunks.get(dim).add(pos);
            registerTicket(world, pos);
            FluxNetworks.LOGGER.info("Added Chunk Loader in Dim: {} Dim Id: {} Chunk: {}", DimensionType.getKey(world.getDimension().getType()), dim, pos);
            return true;
        }
        return false;*/
        return false;
    }

    public static void removeChunkLoader(ServerWorld world, ChunkPos pos) {
        /*int dim = world.getDimension().getType().getId();
        long count = FluxNetworkData.get().loadedChunks.getOrDefault(dim, new ArrayList<>()).stream().filter(chunkPos -> chunkPos.equals(pos)).count();
        if(count > 0){
            FluxNetworkData.get().loadedChunks.get(dim).remove(pos);
            if(count <= 1){
                releaseTicket(world, pos);
            }
            FluxNetworks.LOGGER.info("Removed Chunk Loader in Dim: {} Dim Id: {} Chunk: {}", DimensionType.getKey(world.getDimension().getType()), dim, pos);
        }*/
    }

    public static void registerTicket(ServerWorld world, ChunkPos pos) {
        /*List<ChunkPos> active = activeChunks.computeIfAbsent(world.getDimension().getType().getId(), d -> new ArrayList<>());
        if(!active.contains(pos)){
            world.forceChunk(pos.x, pos.z, true);
            *//* TODO - switch back to tickets - currently on world unloads they seem to be ignored -- see DimensionManager.getForcedChunks()
            world.getChunk(pos.x , pos.z); //loads the chunk.
            ServerChunkProvider chunkProvider = world.getChunkProvider();
            chunkProvider.registerTicket(FLUX_TICKET_TYPE, pos, DEFAULT_DISTANCE, pos);
             *//*
            active.add(pos);
        }*/
    }

    public static void releaseTicket(ServerWorld world, ChunkPos pos) {
        /*List<ChunkPos> active = activeChunks.get(world.getDimension().getType().getId());
        if(active != null && active.contains(pos)) {
            world.forceChunk(pos.x, pos.z, false);
            *//*
            ServerChunkProvider chunkProvider = world.getChunkProvider();
            chunkProvider.releaseTicket(FLUX_TICKET_TYPE, pos, DEFAULT_DISTANCE, pos);
             *//*
            active.remove(pos);

        }*/
    }

}