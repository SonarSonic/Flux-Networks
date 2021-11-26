package sonar.fluxnetworks.register;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.capability.SuperAdminProvider;
import sonar.fluxnetworks.common.connection.FluxNetworkManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class CommonEventHandler {

    //// SERVER EVENTS \\\\

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        // mainly used to reload data while changing single-player saves, useless on dedicated server
        // because once server shut down, all memory deallocated
        FluxNetworkManager.release();
    }

    @SubscribeEvent
    public static void onServerTick(@Nonnull TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (IFluxNetwork network : FluxNetworkManager.getAllNetworks()) {
                network.onEndServerTick();
            }
        }
    }

    //// WORLD EVENTS \\\\

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onWorldLoad(@Nonnull WorldEvent.Load event) {
        /*if (!event.getWorld().isClientSide()) {
            ServerLevel world = (ServerLevel) event.getWorld();
            world.getServer().enqueue(new TickDelayedTask(world.getServer().getTickCounter(), () ->
                    FluxChunkManager.loadWorld(world)));
        }*/
    }

    @SubscribeEvent
    public static void onWorldTick(@Nonnull TickEvent.WorldTickEvent event) {
        /*if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            FluxChunkManager.tickWorld((ServerWorld) event.world);
        }*/
    }

    //// PLAYER EVENTS \\\\

    @SubscribeEvent(receiveCanceled = true)
    public static void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (!FluxConfig.enableFluxRecipe) {
            return;
        }
        Level world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState crusher = world.getBlockState(pos);
        BlockState base = world.getBlockState(pos.below(2));
        if (crusher.getBlock() == Blocks.OBSIDIAN && (base.getBlock() == Blocks.BEDROCK || base.getBlock() == RegistryBlocks.FLUX_BLOCK)) {
            List<ItemEntity> entities = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos.below()));
            if (entities.isEmpty()) {
                return;
            }
            final List<ItemEntity> validEntities = new ArrayList<>();
            int count = 0;
            for (ItemEntity entity : entities) {
                if (entity.getItem().is(Items.REDSTONE)) {
                    validEntities.add(entity);
                    count += entity.getItem().getCount();
                    if (count >= 512) {
                        break;
                    }
                }
            }
            if (validEntities.isEmpty()) {
                return;
            }
            final int max = Mth.clamp(count >> 2, 4, 64);
            if (!event.getWorld().isClientSide) {
                ItemStack stack = new ItemStack(RegistryItems.FLUX_DUST, count);
                validEntities.forEach(Entity::discard);
                world.removeBlock(pos, false);
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                entity.setNoPickUpDelay();
                world.addFreshEntity(entity);
                if (world.getRandom().nextDouble() > Math.pow(0.9, count >> 4)) {
                    world.setBlock(pos.below(), Blocks.COBBLESTONE.defaultBlockState(), Constants.BlockFlags.DEFAULT);
                    world.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS, 1.0f, 1.0f);
                } else {
                    world.setBlock(pos.below(), Blocks.OBSIDIAN.defaultBlockState(), Constants.BlockFlags.DEFAULT);
                    world.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                //FIXME
                //S2CNetMsg.lavaEffect(pos, max).sendToTrackingEntity(event.getPlayer());
            } else {
                for (int i = 0; i < max; i++) {
                    // speed won't work with lava particle, because its constructor doesn't use these params
                    world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0);
                }
            }

            event.setCanceled(true);
        }
    }

    /*@SubscribeEvent(priority = EventPriority.LOWEST)
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
    }*/

    @SubscribeEvent
    public static void onPlayerJoined(@Nonnull PlayerEvent.PlayerLoggedInEvent event) {
        // this event only fired on server
        //FIXME
        /*S2CNetMsg.updateNetwork(FluxNetworkData.getAllNetworks(), FluxConstants.TYPE_NET_BASIC)
                .sendToPlayer(event.getPlayer());
        S2CNetMsg.updateSuperAdmin(SuperAdmin.isPlayerSuperAdmin(event.getPlayer()))
                .sendToPlayer(event.getPlayer());*/
    }

    @SubscribeEvent
    public static void onAttachCapability(@Nonnull AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(SuperAdmin.CAP_KEY, new SuperAdminProvider());
        }
    }

    //// TILE EVENTS \\\\

    /*@SubscribeEvent
    public static void onFluxConnected(@Nonnull FluxConnectionEvent.Connected event) {
        if (!event.flux.getFluxWorld().isRemote) {
            event.flux.connect(event.network);
        }
    }

    @SubscribeEvent
    public static void onFluxDisconnect(@Nonnull FluxConnectionEvent.Disconnected event) {
        if (!event.flux.getFluxWorld().isRemote) {
            event.flux.disconnect(event.network);
        }
    }*/
}
