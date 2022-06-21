package sonar.fluxnetworks.register;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.capability.FluxPlayerProvider;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.FluxNetworkData;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID)
public class EventHandler {

    //// SERVER EVENTS \\\\

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        // mainly used to reload data while changing single-player saves, unnecessary on dedicated server
        FluxNetworkData.release();
    }

    @SubscribeEvent
    public static void onServerTick(@Nonnull TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            FluxNetworkData.getAllNetworks().forEach(FluxNetwork::onEndServerTick);
        }
    }

    //// WORLD EVENTS \\\\

    /*@SubscribeEvent(priority = EventPriority.LOW)
    public static void onWorldLoad(@Nonnull WorldEvent.Load event) {
        if (!event.getWorld().isClientSide()) {
            ServerLevel world = (ServerLevel) event.getWorld();
            world.getServer().enqueue(new TickDelayedTask(world.getServer().getTickCounter(), () ->
                    FluxChunkManager.loadWorld(world)));
        }
    }

    @SubscribeEvent
    public static void onWorldTick(@Nonnull TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            FluxChunkManager.tickWorld((ServerWorld) event.world);
        }
    }*/

    //// PLAYER EVENTS \\\\

    @SubscribeEvent(receiveCanceled = true)
    public static void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (!FluxConfig.enableFluxRecipe || event.getWorld().isClientSide) {
            return;
        }
        ServerLevel level = (ServerLevel) event.getWorld();
        BlockPos pos = event.getPos();
        BlockState crusher = level.getBlockState(pos);
        BlockState base;
        if (crusher.getBlock() == Blocks.OBSIDIAN &&
                ((base = level.getBlockState(pos.below(2))).getBlock() == Blocks.BEDROCK ||
                        base.getBlock() == RegistryBlocks.FLUX_BLOCK)) {
            List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos.below()));
            if (entities.isEmpty()) {
                return;
            }
            int itemCount = 0;
            for (ItemEntity entity : entities) {
                if (entity.getItem().is(Items.REDSTONE)) {
                    itemCount += entity.getItem().getCount();
                    entity.discard();
                    if (itemCount >= 512) {
                        break;
                    }
                }
            }
            if (itemCount == 0) {
                return;
            }
            ItemStack stack = new ItemStack(RegistryItems.FLUX_DUST, itemCount);
            level.removeBlock(pos, false);
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
            entity.setNoPickUpDelay();
            entity.setDeltaMovement(0, 0.2, 0);
            level.addFreshEntity(entity);
            // give it a chance to turn into cobbles
            if (level.getRandom().nextDouble() > Math.pow(0.9, itemCount >> 3)) {
                level.setBlock(pos.below(), Blocks.COBBLESTONE.defaultBlockState(), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.BLOCKS, 1.0f, 1.0f);
            } else {
                level.setBlock(pos.below(), crusher, Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            int particleCount = Mth.clamp(itemCount >> 2, 4, 64);
            level.sendParticles(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY(),
                    pos.getZ() + 0.5, particleCount, 0, 0, 0, 0);

            // we succeed
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
        Channel.get().sendToPlayer(Messages.updateNetwork(
                FluxNetworkData.getAllNetworks(), FluxConstants.NBT_NET_BASIC), event.getPlayer());
        Messages.capability(event.getPlayer());
    }

    @SubscribeEvent
    public static void onAttachCapability(@Nonnull AttachCapabilitiesEvent<Entity> event) {
        // make server only
        if (event.getObject() instanceof ServerPlayer) {
            var provider = new FluxPlayerProvider();
            event.addCapability(FluxPlayerProvider.CAP_KEY, provider);
            // XXX: no invalidation should not be a problem
            //event.addListener(provider);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(@Nonnull PlayerEvent.Clone event) {
        // server only event
        event.getOriginal().reviveCaps();
        FluxPlayer oFluxPlayer = FluxUtils.get(event.getOriginal(), FluxPlayer.FLUX_PLAYER);
        if (oFluxPlayer != null) {
            FluxPlayer nFluxPlayer = FluxUtils.get(event.getPlayer(), FluxPlayer.FLUX_PLAYER);
            if (nFluxPlayer != null) {
                nFluxPlayer.set(oFluxPlayer);
            }
        }
        event.getOriginal().invalidateCaps();
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
