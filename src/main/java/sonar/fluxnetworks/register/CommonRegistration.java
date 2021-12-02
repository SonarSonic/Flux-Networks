package sonar.fluxnetworks.register;

import icyllis.modernui.forge.NetworkHandler;
import icyllis.modernui.mcgui.OpenMenuEvent;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.client.mui.FluxDeviceUI;
import sonar.fluxnetworks.common.block.FluxControllerBlock;
import sonar.fluxnetworks.common.block.FluxPlugBlock;
import sonar.fluxnetworks.common.block.FluxPointBlock;
import sonar.fluxnetworks.common.block.FluxStorageBlock;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.device.*;
import sonar.fluxnetworks.common.integration.TOPIntegration;
import sonar.fluxnetworks.common.item.*;
import sonar.fluxnetworks.common.crafting.FluxStorageRecipeSerializer;
import sonar.fluxnetworks.common.crafting.NBTWipeRecipeSerializer;
import sonar.fluxnetworks.data.loot.FluxLootTableProvider;
import sonar.fluxnetworks.data.tags.FluxBlockTagsProvider;

import javax.annotation.Nonnull;
import java.util.Set;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonRegistration {

    static NetworkHandler sNetwork;

    public static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab(FluxNetworks.MODID) {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegistryItems.FLUX_CORE);
        }
    };

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        sNetwork = new NetworkHandler(FluxNetworks.MODID, () -> Messages::msg, Messages::msg, "701", false);
    }

    @SubscribeEvent
    public static void enqueueIMC(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("carryon")) {
            InterModComms.sendTo("carryon", "blacklistBlock", () -> FluxNetworks.MODID + ":*");
        }
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPIntegration::new);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        Block.Properties normalProps = Block.Properties.of(Material.METAL).sound(SoundType.METAL)
                .strength(1.0F, 1000F);
        Block.Properties deviceProps = Block.Properties.of(Material.METAL).sound(SoundType.METAL)
                .strength(1.0F, 1000F).noOcclusion();

        registry.register(new Block(normalProps)
                .setRegistryName("flux_block"));

        registry.register(new FluxPlugBlock(deviceProps)
                .setRegistryName("flux_plug"));
        registry.register(new FluxPointBlock(deviceProps)
                .setRegistryName("flux_point"));
        registry.register(new FluxControllerBlock(deviceProps)
                .setRegistryName("flux_controller"));
        registry.register(new FluxStorageBlock.Basic(deviceProps)
                .setRegistryName("basic_flux_storage"));
        registry.register(new FluxStorageBlock.Herculean(deviceProps)
                .setRegistryName("herculean_flux_storage"));
        registry.register(new FluxStorageBlock.Gargantuan(deviceProps)
                .setRegistryName("gargantuan_flux_storage"));
    }

    @SubscribeEvent
    public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        Item.Properties props = new Item.Properties().tab(CREATIVE_MODE_TAB);

        registry.register(new BlockItem(RegistryBlocks.FLUX_BLOCK, props)
                .setRegistryName("flux_block"));
        registry.register(new FluxDeviceItem(RegistryBlocks.FLUX_PLUG, props)
                .setRegistryName("flux_plug"));
        registry.register(new FluxDeviceItem(RegistryBlocks.FLUX_POINT, props)
                .setRegistryName("flux_point"));
        registry.register(new FluxDeviceItem(RegistryBlocks.FLUX_CONTROLLER, props)
                .setRegistryName("flux_controller"));

        registry.register(new FluxStorageItem(RegistryBlocks.BASIC_FLUX_STORAGE, props)
                .setRegistryName("basic_flux_storage"));
        registry.register(new FluxStorageItem(RegistryBlocks.HERCULEAN_FLUX_STORAGE, props)
                .setRegistryName("herculean_flux_storage"));
        registry.register(new FluxStorageItem(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, props)
                .setRegistryName("gargantuan_flux_storage"));

        registry.register(new FluxDustItem(props)
                .setRegistryName("flux_dust"));
        registry.register(new Item(props)
                .setRegistryName("flux_core"));
        registry.register(new ItemFluxConfigurator(props)
                .setRegistryName("flux_configurator"));
        registry.register(new ItemAdminConfigurator(props)
                .setRegistryName("admin_configurator"));
    }

    /*@SubscribeEvent
    public static void onEntityRegistry(@Nonnull final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.<FireItemEntity>create(FireItemEntity::new,
        EntityClassification.MISC).immuneToFire().build("fireitem").setRegistryName("fireitem"));
    }*/

    @SubscribeEvent
    public static void registerBlockEntities(@Nonnull RegistryEvent.Register<BlockEntityType<?>> event) {
        IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();

        registry.register(new BlockEntityType<>(TileFluxPlug::new,
                Set.of(RegistryBlocks.FLUX_PLUG), null)
                .setRegistryName("flux_plug"));
        registry.register(new BlockEntityType<>(TileFluxPoint::new,
                Set.of(RegistryBlocks.FLUX_POINT), null)
                .setRegistryName("flux_point"));
        registry.register(new BlockEntityType<>(TileFluxController::new,
                Set.of(RegistryBlocks.FLUX_CONTROLLER), null)
                .setRegistryName("flux_controller"));
        registry.register(new BlockEntityType<>(TileFluxStorage.Basic::new,
                Set.of(RegistryBlocks.BASIC_FLUX_STORAGE), null)
                .setRegistryName("basic_flux_storage"));
        registry.register(new BlockEntityType<>(TileFluxStorage.Herculean::new,
                Set.of(RegistryBlocks.HERCULEAN_FLUX_STORAGE), null)
                .setRegistryName("herculean_flux_storage"));
        registry.register(new BlockEntityType<>(TileFluxStorage.Gargantuan::new,
                Set.of(RegistryBlocks.GARGANTUAN_FLUX_STORAGE), null)
                .setRegistryName("gargantuan_flux_storage"));
    }

    /**
     * ContainerType has the function to create container on client side
     * Register the create container function that will be opened on client side from the packet that from the server
     */
    @SubscribeEvent
    public static void registerMenus(@Nonnull RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create(FluxDeviceMenu::new).setRegistryName("flux_menu"));
    }

    @SubscribeEvent
    public static void registerRecipes(@Nonnull RegistryEvent.Register<RecipeSerializer<?>> event) {
        event.getRegistry().register(FluxStorageRecipeSerializer.INSTANCE
                .setRegistryName("flux_storage_recipe"));
        event.getRegistry().register(NBTWipeRecipeSerializer.INSTANCE
                .setRegistryName("nbt_wipe_recipe"));
    }

    @SubscribeEvent
    public static void registerSounds(@Nonnull RegistryEvent.Register<SoundEvent> event) {
        ResourceLocation soundID = new ResourceLocation(FluxNetworks.MODID, "button");
        event.getRegistry().register(new SoundEvent(soundID).setRegistryName(soundID));
    }

    @SubscribeEvent
    public static void registerCapabilities(@Nonnull RegisterCapabilitiesEvent event) {
        event.register(FluxPlayer.class);
        event.register(IFNEnergyStorage.class);
    }

    @SubscribeEvent
    public static void gatherData(@Nonnull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new FluxLootTableProvider(generator));
            generator.addProvider(new FluxBlockTagsProvider(generator, event.getExistingFileHelper()));
        }
    }

    @SubscribeEvent
    public static void openMenu(@Nonnull OpenMenuEvent event) {
        if (event.getMenu() instanceof FluxDeviceMenu menu && menu.mDevice != null) {
            event.setCallback(new FluxDeviceUI(menu.mDevice));
        }
    }
}
