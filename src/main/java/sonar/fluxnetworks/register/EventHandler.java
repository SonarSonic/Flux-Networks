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
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.capability.SuperAdminInstance;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.core.FireItemEntity;
import sonar.fluxnetworks.common.data.FluxChunkManager;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.event.FluxConnectionEvent;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.network.NetworkUpdatePacket;
import sonar.fluxnetworks.common.network.SuperAdminPacket;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EventHandler {

    //// SERVER EVENTS \\\\

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        // Mainly used to switch data while changing single-player saves
        // Useless on dedicated server
        FluxNetworkData.release();
        FluxChunkManager.clear();
    }

    @SubscribeEvent
    public static void onServerTick(@Nonnull TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (IFluxNetwork network : FluxNetworkCache.INSTANCE.getAllNetworks()) {
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
            if (entities.isEmpty())
                return;
            List<ItemEntity> validEntities = Lists.newArrayList();
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
            if (validEntities.isEmpty())
                return;
            if (event.getSide().isServer()) {
                ItemStack stack = new ItemStack(RegistryItems.FLUX, count);
                validEntities.forEach(Entity::remove);
                world.removeBlock(pos, false);
                world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack));
                if (world.getRandom().nextDouble() > Math.pow(0.9, count >> 4)) {
                    world.setBlockState(pos.down(), Blocks.COBBLESTONE.getDefaultState());
                    world.playSound(null, pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                } else {
                    world.setBlockState(pos.down(), Blocks.OBSIDIAN.getDefaultState());
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
            } else {
                int max = MathHelper.clamp(count >> 2, 1, 64);
                //TODO send to all nearby player
                for (int i = 0; i < max; i++) {
                    // speed won't work with lava particle, because its constructor doesn't use these params
                    world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0);
                }
            }

            event.setCanceled(true);
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
    public static void onPlayerJoined(@Nonnull PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new NetworkUpdatePacket(new ArrayList<>(FluxNetworkCache.INSTANCE.getAllNetworks()), NBTType.NETWORK_GENERAL));
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SuperAdminPacket(SuperAdminInstance.isPlayerSuperAdmin(player)));
    }

    //// TILE EVENTS \\\\

    @SubscribeEvent
    public static void onFluxConnected(@Nonnull FluxConnectionEvent.Connected event) {
        if (!event.flux.getWorld().isRemote) {
            event.flux.connect(event.network);
        }
    }

    @SubscribeEvent
    public static void onFluxDisconnect(@Nonnull FluxConnectionEvent.Disconnected event) {
        if (!event.flux.getWorld().isRemote) {
            event.flux.disconnect(event.network);
        }
    }

}
