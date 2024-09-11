package sonar.fluxnetworks.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.integration.TOPIntegration;
import sonar.fluxnetworks.common.util.EnergyUtils;
import sonar.fluxnetworks.data.loot.FluxLootTableProvider;
import sonar.fluxnetworks.data.tags.FluxBlockTagsProvider;

import javax.annotation.Nonnull;

@EventBusSubscriber(modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        Channel.sChannel = new FMLChannel();
        event.enqueueWork(() -> ForgeChunkManager.setForcedChunkLoadingCallback(FluxNetworks.MODID, (level, helper) -> {
            if (!FluxConfig.enableChunkLoading) {
                helper.getBlockTickets().keySet().forEach(helper::removeAllTickets);
                FluxNetworks.LOGGER.info("Removed all chunk loaders because chunk loading is disabled");
            } else {
                int chunks = 0;
                for (var entry : helper.getBlockTickets().entrySet()) {
                    // this also loads the chunk
                    if (level.getBlockEntity(entry.getKey()) instanceof TileFluxDevice e) {
                        e.setForcedLoading(true);
                        var pair = entry.getValue();
                        int count = 0;
                        count += pair.getFirst().size();
                        count += pair.getSecond().size();
                        if (count != 1) {
                            FluxNetworks.LOGGER.warn("{} in {} didn't load just one chunk {}",
                                    entry.getValue(), level.dimension().location(), pair);
                        }
                        chunks += count;
                    } else {
                        helper.removeAllTickets(entry.getKey());
                    }
                }
                FluxNetworks.LOGGER.info("Load {} chunks by {} flux devices in {}",
                        chunks, helper.getBlockTickets().size(), level.dimension().location());
            }
        }));
        EnergyUtils.register();
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
    public static void gatherData(@Nonnull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        if (event.includeServer()) {
            generator.addProvider(true, new FluxLootTableProvider(packOutput));
            generator.addProvider(true, new FluxBlockTagsProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper()));
        }
    }

    @SubscribeEvent
    public static void register(@Nonnull IEventBus modBus) {
        RegistryBlocks.BLOCKS.register(modBus);
        RegistryItems.ITEMS.register(modBus);
        RegistryBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modBus);
        RegistryMenuTypes.MENU_TYPES.register(modBus);
        RegistryRecipes.RECIPES.register(modBus);
        RegistrySounds.SOUNDS.register(modBus);
        RegistryCreativeModeTabs.CREATIVE_MODE_TABS.register(modBus);

//        event.register(BuiltInRegistries.BLOCK.key(), RegistryBlocks::register);
//        event.register(BuiltInRegistries.ITEM.key(), RegistryItems::register);
//        event.register(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), RegistryBlockEntityTypes::register);
//        event.register(BuiltInRegistries.MENU.key(), RegistryMenuTypes::register);
//        event.register(BuiltInRegistries.RECIPE_SERIALIZER.key(), RegistryRecipes::register);
//        event.register(BuiltInRegistries.SOUND_EVENT.key(), RegistrySounds::register);
//        event.register(BuiltInRegistries.CREATIVE_MODE_TAB.key(), RegistryCreativeModeTabs::register);
    }
}
