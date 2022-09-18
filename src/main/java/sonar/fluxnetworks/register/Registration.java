package sonar.fluxnetworks.register;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.integration.TOPIntegration;
import sonar.fluxnetworks.data.loot.FluxLootTableProvider;
import sonar.fluxnetworks.data.tags.FluxBlockTagsProvider;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        Channel.sChannel = FluxNetworks.isModernUILoaded() ? new MUIChannel() : new FMLChannel();
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
    public static void registerCapabilities(@Nonnull RegisterCapabilitiesEvent event) {
        event.register(FluxPlayer.class);
        event.register(IFNEnergyStorage.class);
    }

    @SubscribeEvent
    public static void gatherData(@Nonnull GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(true, new FluxLootTableProvider(generator));
            generator.addProvider(true, new FluxBlockTagsProvider(generator, event.getExistingFileHelper()));
        }
    }

    public static void register(IEventBus bus) {
        RegistryBlocks.register(bus);
        RegistryItems.register(bus);
        RegistryBlockEntityTypes.register(bus);
        RegistryRecipes.register(bus);
        RegistryMenuTypes.register(bus);
        RegistrySounds.register(bus);
    }
}
