package sonar.fluxnetworks.register;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.capabilities.DefaultSuperAdmin;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.core.FireItemEntity;
import sonar.fluxnetworks.common.data.FluxChunkManager;
import sonar.fluxnetworks.common.event.FluxConnectionEvent;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.NetworkUpdatePacket;
import sonar.fluxnetworks.common.network.SuperAdminPacket;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventHandler {

    //// SERVER EVENTS \\\\

    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event){
        FluxNetworks.proxy.onServerStarted();
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event){
        FluxNetworkCache.instance.clearNetworks();
        FluxNetworkCache.instance.clearClientCache();
        FluxNetworks.proxy.onServerStopped();
        FluxChunkManager.clear();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            for(IFluxNetwork network : FluxNetworkCache.instance.getAllNetworks()) {
                network.onEndServerTick();
            }
        }
    }

    //// WORLD EVENTS \\\\

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerWorld) {
            FluxChunkManager.loadWorld((ServerWorld)event.getWorld());
        }
    }


    //// PLAYER EVENTS \\\\

    @SubscribeEvent(receiveCanceled = true)
    public static void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if(event.getSide().isServer()) {
            if(!FluxConfig.enableFluxRecipe) {
                return;
            }
            ServerWorld world = (ServerWorld)event.getWorld();
            BlockPos pos = event.getPos();
            BlockState crusher = world.getBlockState(pos);
            BlockState base = world.getBlockState(pos.down(2));
            if (crusher.getBlock().equals(Blocks.OBSIDIAN) && (base.getBlock().equals(Blocks.BEDROCK)) || base.getBlock().equals(RegistryBlocks.FLUX_BLOCK)) {
                List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.down()));
                if(entities.isEmpty())
                    return;
                List<ItemEntity> s = Lists.newArrayList();
                AtomicInteger count = new AtomicInteger();
                entities.forEach(e -> {
                    if (e.getItem().getItem().equals(Items.REDSTONE)) {
                        s.add(e);
                        count.addAndGet(e.getItem().getCount());
                    }
                });
                if (s.isEmpty())
                    return;
                ItemStack stack = new ItemStack(RegistryItems.FLUX, count.getAndIncrement());
                s.forEach(Entity::remove);
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack));
                world.setBlockState(pos.down(), Blocks.OBSIDIAN.getDefaultState());
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityAdded(EntityJoinWorldEvent event) {
        if (!FluxConfig.enableFluxRecipe || !FluxConfig.enableOldRecipe || event.getWorld().isRemote) {
            return;
        }
        final Entity entity = event.getEntity();
        if (entity instanceof ItemEntity && !(entity instanceof FireItemEntity)) {
            ItemEntity entityItem = (ItemEntity) entity;
            ItemStack stack = entityItem.getItem();
            if (!stack.isEmpty() && stack.getItem() == Items.REDSTONE) {
                FireItemEntity newEntity = new FireItemEntity(entityItem);
                entityItem.remove();
                event.getWorld().addEntity(newEntity);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if(!player.world.isRemote) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new NetworkUpdatePacket(new ArrayList<>(FluxNetworkCache.instance.getAllNetworks()), NBTType.NETWORK_GENERAL));
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SuperAdminPacket(DefaultSuperAdmin.isPlayerSuperAdmin(player)));
        }
    }

    //// TILE EVENTS \\\\

    @SubscribeEvent
    public static void onFluxConnected(FluxConnectionEvent.Connected event) {
        if(!event.flux.getWorld0().isRemote) {
            event.flux.connect(event.network);
        }
    }

    @SubscribeEvent
    public static void onFluxDisconnect(FluxConnectionEvent.Disconnected event) {
        if(!event.flux.getWorld0().isRemote) {
            event.flux.disconnect(event.network);
        }
    }

}
