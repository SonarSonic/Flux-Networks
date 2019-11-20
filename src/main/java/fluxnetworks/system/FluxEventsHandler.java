package fluxnetworks.system;

import com.google.common.collect.Lists;
import fluxnetworks.api.network.IFluxNetwork;
import fluxnetworks.network.FluxDataHandler;
import fluxnetworks.system.registry.RegistryBlocks;
import fluxnetworks.system.registry.RegistryItems;
import fluxnetworks.system.registry.RegistryTiles;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class FluxEventsHandler {

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if(event.getSide().isServer()) {
            //TODO Config
            World world = event.getWorld();
            BlockPos pos = event.getPos();

            if (world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) && world.getBlockState(pos.down(2)).getBlock().equals(Blocks.BEDROCK)) {
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
                if(s.isEmpty())
                    return;
                ItemStack stack = new ItemStack(RegistryItems.FLUX, count.getAndIncrement());
                s.forEach(Entity::remove);
                world.removeBlock(pos, false);
                world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack));
                world.setBlockState(pos.down(), Blocks.OBSIDIAN.getDefaultState());
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if(RegistryBlocks.BLOCKS.stream().anyMatch(b -> b.equals(event.getState().getBlock()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            FluxDataHandler.INSTANCE.getAllNetworks().forEach(IFluxNetwork::tick);
        }
    }

    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event) {
        FluxDataHandler.INSTANCE.loadData(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(FMLServerStoppedEvent event) {
        FluxDataHandler.INSTANCE.releaseData();
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(Dist.CLIENT)
    public static class ClientEvent {

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModSetup {

        @SubscribeEvent
        public static void setupCommon(FMLCommonSetupEvent event) {

        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().register(RegistryBlocks.FLUX_BLOCK);
            RegistryBlocks.BLOCKS.forEach(b -> event.getRegistry().register(b));
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            RegistryItems.ITEMS.forEach(i -> event.getRegistry().register(i));
        }

        @SubscribeEvent
        public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
            RegistryTiles.TILES.forEach(t -> event.getRegistry().register(t));
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientSetup {

        @SubscribeEvent
        public static void setupClient(FMLClientSetupEvent event) {

        }
    }
}
