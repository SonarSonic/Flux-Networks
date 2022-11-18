package sonar.fluxnetworks.register;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.integration.TOPIntegration;
import sonar.fluxnetworks.data.loot.FluxLootTableProvider;
import sonar.fluxnetworks.data.tags.FluxBlockTagsProvider;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
        if (event.includeServer()) {
            generator.addProvider(true, new FluxLootTableProvider(generator));
            generator.addProvider(true, new FluxBlockTagsProvider(generator, event.getExistingFileHelper()));
        }
    }

    @SubscribeEvent
    public static void register(@Nonnull RegisterEvent event) {
        event.register(ForgeRegistries.BLOCKS.getRegistryKey(), RegistryBlocks::register);
        event.register(ForgeRegistries.ITEMS.getRegistryKey(), RegistryItems::register);
        event.register(ForgeRegistries.BLOCK_ENTITY_TYPES.getRegistryKey(), RegistryBlockEntityTypes::register);
        event.register(ForgeRegistries.MENU_TYPES.getRegistryKey(), RegistryMenuTypes::register);
        event.register(ForgeRegistries.RECIPE_SERIALIZERS.getRegistryKey(), RegistryRecipes::register);
        event.register(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), RegistrySounds::register);
    }
}
