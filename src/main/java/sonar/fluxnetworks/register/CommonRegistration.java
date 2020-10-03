package sonar.fluxnetworks.register;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.IForgeRegistry;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.energy.FNEnergyStorage;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.render.FluxStorageItemRenderer;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.capability.SuperAdmin;
import sonar.fluxnetworks.common.handler.NetworkHandler;
import sonar.fluxnetworks.common.integration.TOPIntegration;
import sonar.fluxnetworks.common.item.ItemAdminConfigurator;
import sonar.fluxnetworks.common.misc.ContainerConnector;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.handler.TileEntityHandler;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.item.ItemFluxDevice;
import sonar.fluxnetworks.common.item.ItemFluxDust;
import sonar.fluxnetworks.common.loot.FluxLootTableProvider;
import sonar.fluxnetworks.common.recipe.FluxStorageRecipeSerializer;
import sonar.fluxnetworks.common.recipe.NBTWipeRecipeSerializer;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;
import sonar.fluxnetworks.common.registry.RegistrySounds;
import sonar.fluxnetworks.common.tileentity.TileFluxController;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import sonar.fluxnetworks.common.tileentity.TileFluxPoint;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonRegistration {

    public static final ItemGroup ITEM_GROUP = new ItemGroup(FluxNetworks.MODID) {
        @Nonnull
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryItems.FLUX_CORE);
        }
    };

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        PacketHandler.registerMessages();
        NetworkHandler.registerMessages();
        TileEntityHandler.registerEnergyHandler();

        SuperAdmin.register();
        FNEnergyStorage.register();

        FluxNetworks.LOGGER.info("Finished Common Setup");
    }

    @SubscribeEvent
    public static void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("carryon", "blacklistBlock", () -> FluxNetworks.MODID + ":*");
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPIntegration::new);
    }

    @SubscribeEvent
    public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        Block.Properties normalProps = Block.Properties.create(Material.IRON).sound(SoundType.METAL)
                .hardnessAndResistance(1.0F, 1000F);
        Block.Properties deviceProps = Block.Properties.create(Material.IRON).sound(SoundType.METAL)
                .hardnessAndResistance(1.0F, 1000F).notSolid();

        registry.register(new Block(normalProps).setRegistryName("fluxblock"));

        registry.register(new FluxPlugBlock(deviceProps).setRegistryName("fluxplug"));
        registry.register(new FluxPointBlock(deviceProps).setRegistryName("fluxpoint"));
        registry.register(new FluxControllerBlock(deviceProps).setRegistryName("fluxcontroller"));
        registry.register(new FluxStorageBlock.Basic(deviceProps).setRegistryName("basicfluxstorage"));
        registry.register(new FluxStorageBlock.Herculean(deviceProps).setRegistryName("herculeanfluxstorage"));
        registry.register(new FluxStorageBlock.Gargantuan(deviceProps).setRegistryName("gargantuanfluxstorage"));

        FluxNetworks.LOGGER.info("Finished Registering Blocks");
    }

    @SubscribeEvent
    public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        Item.Properties props = new Item.Properties().group(ITEM_GROUP);

        registry.register(new BlockItem(RegistryBlocks.FLUX_BLOCK, props).setRegistryName("fluxblock"));
        registry.register(new ItemFluxDevice(RegistryBlocks.FLUX_PLUG, props).setRegistryName("fluxplug"));
        registry.register(new ItemFluxDevice(RegistryBlocks.FLUX_POINT, props).setRegistryName("fluxpoint"));
        registry.register(new ItemFluxDevice(RegistryBlocks.FLUX_CONTROLLER, props).setRegistryName("fluxcontroller"));

        // the 'new' method is in another class, so there will be no server crash
        Item.Properties storageProps = new Item.Properties().group(ITEM_GROUP).setISTER(() -> FluxStorageItemRenderer::new);

        registry.register(new ItemFluxDevice(RegistryBlocks.BASIC_FLUX_STORAGE, storageProps).setRegistryName("basicfluxstorage"));
        registry.register(new ItemFluxDevice(RegistryBlocks.HERCULEAN_FLUX_STORAGE, storageProps).setRegistryName("herculeanfluxstorage"));
        registry.register(new ItemFluxDevice(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, storageProps).setRegistryName("gargantuanfluxstorage"));

        registry.register(new ItemFluxDust(props).setRegistryName("flux"));
        registry.register(new Item(props).setRegistryName("fluxcore"));
        registry.register(new ItemFluxConfigurator(props).setRegistryName("fluxconfigurator"));
        registry.register(new ItemAdminConfigurator(props).setRegistryName("adminconfigurator"));

        FluxNetworks.LOGGER.info("Finished Registering Items");
    }

    /*@SubscribeEvent
    public static void onEntityRegistry(@Nonnull final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.<FireItemEntity>create(FireItemEntity::new, EntityClassification.MISC).immuneToFire().build("fireitem").setRegistryName("fireitem"));
    }*/

    @SubscribeEvent
    public static void onTileEntityRegistry(@Nonnull final RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

        registry.register(TileEntityType.Builder.create(TileFluxPlug::new, RegistryBlocks.FLUX_PLUG).build(null)
                .setRegistryName("fluxplug"));
        registry.register(TileEntityType.Builder.create(TileFluxPoint::new, RegistryBlocks.FLUX_POINT).build(null)
                .setRegistryName("fluxpoint"));
        registry.register(TileEntityType.Builder.create(TileFluxController::new, RegistryBlocks.FLUX_CONTROLLER).build(null)
                .setRegistryName("fluxcontroller"));
        registry.register(TileEntityType.Builder.create(TileFluxStorage.Basic::new, RegistryBlocks.BASIC_FLUX_STORAGE).build(null)
                .setRegistryName("basicfluxstorage"));
        registry.register(TileEntityType.Builder.create(TileFluxStorage.Herculean::new, RegistryBlocks.HERCULEAN_FLUX_STORAGE).build(null).
                setRegistryName("herculeanfluxstorage"));
        registry.register(TileEntityType.Builder.create(TileFluxStorage.Gargantuan::new, RegistryBlocks.GARGANTUAN_FLUX_STORAGE).build(null)
                .setRegistryName("gargantuanfluxstorage"));

        FluxNetworks.LOGGER.info("Finished Registering Tile Entities");
    }

    /**
     * ContainerType has the function to create container on client side
     * Register the create container function that will be opened on client side from the packet that from the server
     */
    @SubscribeEvent
    public static void onContainerRegistry(final @Nonnull RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create((windowId, inventory, packet) -> {
            // check if it's tile entity
            if (packet.readBoolean()) {
                BlockPos pos = packet.readBlockPos();
                TileEntity tile = inventory.player.getEntityWorld().getTileEntity(pos);
                if (tile instanceof INetworkConnector) {
                    return new ContainerConnector<>(windowId, inventory, (INetworkConnector) tile);
                }
            } else {
                ItemStack stack = inventory.player.getHeldItemMainhand();
                // build a bridge to connect to a flux network
                if (stack.getItem() instanceof ItemAdminConfigurator) {
                    return new ContainerConnector<>(windowId, inventory, new ItemAdminConfigurator.ContainerProvider(stack));
                }
                if (stack.getItem() instanceof ItemFluxConfigurator) {
                    return new ContainerConnector<>(windowId, inventory, new ItemFluxConfigurator.ContainerProvider(stack));
                }
            }
            // return null, because players have broken some rules, and there's no gui will be opened, and the server container will be closed as well
            return null;
        }).setRegistryName("connector"));

        FluxNetworks.LOGGER.info("Finished Registering Containers");
    }

    @SubscribeEvent
    public static void registerRecipes(@Nonnull RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(FluxStorageRecipeSerializer.INSTANCE.setRegistryName(FluxNetworks.MODID, "fluxstoragerecipe"));
        event.getRegistry().register(NBTWipeRecipeSerializer.INSTANCE.setRegistryName(FluxNetworks.MODID, "nbtwiperecipe"));
    }


    @SubscribeEvent
    public static void registerSounds(@Nonnull RegistryEvent.Register<SoundEvent> event) {
        RegistrySounds.registerSounds(event.getRegistry());
    }

    @SubscribeEvent
    public static void gatherData(@Nonnull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new FluxLootTableProvider(generator));
        }
    }
}
