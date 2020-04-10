package sonar.fluxnetworks.register;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.render.ItemRendererCallable;
import sonar.fluxnetworks.common.block.*;
import sonar.fluxnetworks.common.capabilities.DefaultSuperAdmin;
import sonar.fluxnetworks.common.core.ContainerCore;
import sonar.fluxnetworks.common.handler.CapabilityHandler;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.handler.TileEntityHandler;
import sonar.fluxnetworks.common.item.AdminConfiguratorItem;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import sonar.fluxnetworks.common.item.FluxConnectorBlockItem;
import sonar.fluxnetworks.common.item.FluxItem;
import sonar.fluxnetworks.common.recipes.FluxStorageRecipeSerializer;
import sonar.fluxnetworks.common.recipes.NBTWipeRecipeSerializer;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistrySounds;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import sonar.fluxnetworks.common.tileentity.TileFluxController;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import sonar.fluxnetworks.common.tileentity.TileFluxPoint;
import sonar.fluxnetworks.common.tileentity.TileFluxStorage;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonRegistration {

    public static final ItemGroup ITEM_GROUP = new ItemGroup("fluxnetworks") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(RegistryBlocks.FLUX_PLUG);
        }
    };

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        FluxNetworks.LOGGER.info("Started Common Setup");


        PacketHandler.registerMessages();
        TileEntityHandler.registerEnergyHandler();

        DefaultSuperAdmin.register();
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());


        FluxNetworks.LOGGER.info("Finished Common Setup");
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        FluxNetworks.LOGGER.info("Started Registering Blocks");

        Block.Properties normalProps = Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(1.0F, 1000F);
        Block.Properties connectorProps = Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(1.0F, 1000F).notSolid();

        event.getRegistry().register(new Block(normalProps).setRegistryName("fluxblock"));
        event.getRegistry().register(new FluxPlugBlock(connectorProps).setRegistryName("fluxplug"));
        event.getRegistry().register(new FluxPointBlock(connectorProps).setRegistryName("fluxpoint"));
        event.getRegistry().register(new FluxControllerBlock(connectorProps).setRegistryName("fluxcontroller"));
        event.getRegistry().register(new FluxStorageBlock.Basic(connectorProps).setRegistryName("basicfluxstorage"));
        event.getRegistry().register(new FluxStorageBlock.Herculean(connectorProps).setRegistryName("herculeanfluxstorage"));
        event.getRegistry().register(new FluxStorageBlock.Gargantuan(connectorProps).setRegistryName("gargantuanfluxstorage"));

        FluxNetworks.LOGGER.info("Finished Registering Blocks");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        FluxNetworks.LOGGER.info("Started Registering Items");

        Item.Properties props =  new Item.Properties().group(ITEM_GROUP);

        //BLOCKS
        event.getRegistry().register(new BlockItem(RegistryBlocks.FLUX_BLOCK, props).setRegistryName("fluxblock"));
        event.getRegistry().register(new FluxConnectorBlockItem(RegistryBlocks.FLUX_PLUG, props).setRegistryName("fluxplug"));
        event.getRegistry().register(new FluxConnectorBlockItem(RegistryBlocks.FLUX_POINT, props).setRegistryName("fluxpoint"));
        event.getRegistry().register(new FluxConnectorBlockItem(RegistryBlocks.FLUX_CONTROLLER, props).setRegistryName("fluxcontroller"));


        Item.Properties storageProps =  new Item.Properties().group(ITEM_GROUP).setISTER(ItemRendererCallable::getStorageRenderer);

        event.getRegistry().register(new FluxConnectorBlockItem(RegistryBlocks.BASIC_FLUX_STORAGE, storageProps).setRegistryName("basicfluxstorage"));
        event.getRegistry().register(new FluxConnectorBlockItem(RegistryBlocks.HERCULEAN_FLUX_STORAGE, storageProps).setRegistryName("herculeanfluxstorage"));
        event.getRegistry().register(new FluxConnectorBlockItem(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, storageProps).setRegistryName("gargantuanfluxstorage"));

        event.getRegistry().register(new FluxItem(props).setRegistryName("flux"));
        event.getRegistry().register(new Item(props).setRegistryName("fluxcore"));
        event.getRegistry().register(new FluxConfiguratorItem(props).setRegistryName("fluxconfigurator"));
        event.getRegistry().register(new AdminConfiguratorItem(props).setRegistryName("adminconfigurator"));

        FluxNetworks.LOGGER.info("Finished Registering Items");
    }


    @SubscribeEvent
    public static void onEntityRegistry(final RegistryEvent.Register<EntityType<?>> event) {
        //TODO FIXME event.getRegistry().register(EntityType.Builder.<FireItemEntity>create(FireItemEntity::new, EntityClassification.MISC).immuneToFire().build(null).setRegistryName("fireitementity"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        FluxNetworks.LOGGER.info("Started Registering Tile Entities");

        event.getRegistry().register(TileEntityType.Builder.create(TileFluxPlug::new, RegistryBlocks.FLUX_PLUG).build(null).setRegistryName("fluxplug"));
        event.getRegistry().register(TileEntityType.Builder.create(TileFluxPoint::new, RegistryBlocks.FLUX_POINT).build(null).setRegistryName("fluxpoint"));
        event.getRegistry().register(TileEntityType.Builder.create(TileFluxController::new, RegistryBlocks.FLUX_CONTROLLER).build(null).setRegistryName("fluxcontroller"));
        event.getRegistry().register(TileEntityType.Builder.create(TileFluxStorage.Basic::new, RegistryBlocks.BASIC_FLUX_STORAGE).build(null).setRegistryName("basicfluxstorage"));
        event.getRegistry().register(TileEntityType.Builder.create(TileFluxStorage.Herculean::new, RegistryBlocks.HERCULEAN_FLUX_STORAGE).build(null).setRegistryName("herculeanfluxstorage"));
        event.getRegistry().register(TileEntityType.Builder.create(TileFluxStorage.Gargantuan::new, RegistryBlocks.GARGANTUAN_FLUX_STORAGE).build(null).setRegistryName("gargantuanfluxstorage"));

        FluxNetworks.LOGGER.info("Finished Registering Tile Entities");
    }

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
        FluxNetworks.LOGGER.info("Started Registering Containers");

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            if(data.readBoolean()){ //check for tileentity
                BlockPos pos = data.readBlockPos();
                TileEntity tile = inv.player.getEntityWorld().getTileEntity(pos);
                if(tile instanceof INetworkConnector){
                    return new ContainerCore(windowId, inv, (INetworkConnector) tile);
                }
            }else{
                ItemStack stack = inv.player.getHeldItemMainhand();
                if(stack.getItem() instanceof AdminConfiguratorItem){
                    return new ContainerCore(windowId, inv, new AdminConfiguratorItem.ContainerProvider(stack));
                }
                if(stack.getItem() instanceof FluxConfiguratorItem){
                    return new ContainerCore(windowId, inv, new FluxConfiguratorItem.ContainerProvider(stack));
                }
            }
            return null;
        }).setRegistryName("containercore"));

        FluxNetworks.LOGGER.info("Finished Registering Containers");
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new FluxStorageRecipeSerializer().setRegistryName(FluxNetworks.MODID, "fluxstoragerecipe"));
        event.getRegistry().register(new NBTWipeRecipeSerializer().setRegistryName(FluxNetworks.MODID, "nbtwiperecipe"));
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        RegistrySounds.registerSounds(event.getRegistry());
    }
}
