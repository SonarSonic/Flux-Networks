package sonar.fluxnetworks.register;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.capability.SuperAdminProvider;
import sonar.fluxnetworks.common.network.NetworkHandler;
import sonar.fluxnetworks.common.network.SLavaParticleMessage;
import sonar.fluxnetworks.common.network.SNetworkUpdateMessage;
import sonar.fluxnetworks.common.network.SSuperAdminMessage;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;
import sonar.fluxnetworks.common.storage.FluxChunkManager;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

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
        FluxNetworkData.release();
    }

    @SubscribeEvent
    public static void onServerTick(@Nonnull TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (IFluxNetwork network : FluxNetworkData.getAllNetworks()) {
                network.onEndServerTick();
            }
        }
    }

    //// WORLD EVENTS \\\\

    @SubscribeEvent
    public static void onWorldLoad(@Nonnull WorldEvent.Load event) {
        if (!event.getWorld().isRemote()) {
            FluxChunkManager.loadWorld((ServerWorld) event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldTick(@Nonnull TickEvent.WorldTickEvent event) {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END) {
            FluxChunkManager.tickWorld((ServerWorld) event.world);
        }
    }

    //// PLAYER EVENTS \\\\

    @SubscribeEvent(receiveCanceled = true)
    public static void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (!FluxConfig.enableFluxRecipe) {
            return;
        }
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState crusher = world.getBlockState(pos);
        BlockState base = world.getBlockState(pos.down(2));
        if (crusher.getBlock() == Blocks.OBSIDIAN && (base.getBlock() == Blocks.BEDROCK || base.getBlock() == RegistryBlocks.FLUX_BLOCK)) {
            List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.down()));
            if (entities.isEmpty()) {
                return;
            }
            final List<ItemEntity> validEntities = new ArrayList<>();
            int count = 0;
            for (ItemEntity entity : entities) {
                if (entity.getItem().getItem() == Items.REDSTONE) {
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
            final int max = MathHelper.clamp(count >> 2, 4, 64);
            if (!event.getWorld().isRemote) {
                ItemStack stack = new ItemStack(RegistryItems.FLUX_DUST, count);
                validEntities.forEach(Entity::remove);
                world.removeBlock(pos, false);
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                entity.setNoPickupDelay();
                world.addEntity(entity);
                if (world.getRandom().nextDouble() > Math.pow(0.9, count >> 4)) {
                    world.setBlockState(pos.down(), Blocks.COBBLESTONE.getDefaultState());
                    world.playSound(null, pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                } else {
                    world.setBlockState(pos.down(), Blocks.OBSIDIAN.getDefaultState());
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                NetworkHandler.INSTANCE.sendToEntityTracking(new SLavaParticleMessage(pos, max), event.getPlayer());
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
        NetworkHandler.INSTANCE.sendToPlayer(new SNetworkUpdateMessage(FluxNetworkData.getAllNetworks(),
                FluxConstants.TYPE_NET_BASIC), event.getPlayer());
        NetworkHandler.INSTANCE.sendToPlayer(new SSuperAdminMessage(SuperAdmin.isPlayerSuperAdmin(event.getPlayer())),
                event.getPlayer());
    }

    @SubscribeEvent
    public static void onAttachCapability(@Nonnull AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
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
