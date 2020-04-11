package sonar.fluxnetworks.common.data;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;


public class FluxChunkManager {

    /*
    public static final Map<Integer, ForgeChunkManager.Ticket> worldTickets = new HashMap<>();
    */
    public static void clear() {
        //worldTickets.clear();
    }

    public static boolean forceChunk(World world, ChunkPos chunk) {
        //TODO
        //world.getChunkProvider().registerTicket(TicketType.FORCED, chunk, 1, chunk);
        /*
        if(FluxNetworkData.get().loadedChunks.computeIfAbsent(world.provider.getDimension(), l -> new ArrayList<>()).contains(chunk)) {
            return false;
        }
        ForgeChunkManager.Ticket ticket = worldTickets.computeIfAbsent(world.provider.getDimension(), t -> ForgeChunkManager.requestTicket(FluxNetworks.instance, world, ForgeChunkManager.Type.NORMAL));
        ForgeChunkManager.forceChunk(ticket, chunk);
        FluxNetworkData.get().loadedChunks.get(world.provider.getDimension()).add(chunk);
        */
        return true;
    }

    public static void releaseChunk(World world, ChunkPos chunk) {
        /*
        ForgeChunkManager.Ticket ticket = worldTickets.get(world.provider.getDimension());
        if(ticket == null) {
            return;
        }
        long count = FluxNetworkData.get().loadedChunks.getOrDefault(world.provider.getDimension(), new ArrayList<>()).stream().filter(chunkPos -> chunkPos.equals(chunk)).count();
        if(count > 0) {
            FluxNetworkData.get().loadedChunks.get(world.provider.getDimension()).remove(chunk);
            if(count <= 1) {
                ForgeChunkManager.unforceChunk(ticket, chunk);
            }
        }
        if(ticket.getChunkList().isEmpty()) {
            worldTickets.remove(ticket.world.provider.getDimension());
            ForgeChunkManager.releaseTicket(ticket);
        }
        */
    }
    /*
    public static void callback(List<ForgeChunkManager.Ticket> tickets, World world) {
        int dim = world.provider.getDimension();
        ForgeChunkManager.Ticket ticket = tickets.get(0); // Every world has only one ticket
        worldTickets.putIfAbsent(dim, ticket); // Unnecessary
        if(FluxConfig.enableChunkLoading) {
            for (ChunkPos pos : FluxNetworkData.get().loadedChunks.getOrDefault(dim, new ArrayList<>())) {
                ForgeChunkManager.forceChunk(ticket, pos);
            }
        }
    }
    */
}
