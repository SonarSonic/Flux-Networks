package sonar.fluxnetworks;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.api.FluxDataComponents;
import sonar.fluxnetworks.common.integration.cctweaked.CCTPeripheral;
import sonar.fluxnetworks.register.DataAttachments;
import sonar.fluxnetworks.register.RegistryBlockEntityTypes;

import javax.annotation.Nonnull;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID = "fluxnetworks";
    public static final String NAME = "Flux Networks";
    public static final String NAME_CPT = "FluxNetworks";

    public static final Logger LOGGER = LogManager.getLogger(NAME_CPT);

    private static boolean sCuriosLoaded;
    private static boolean sModernUILoaded;
    private static boolean sComputercraftLoaded;

    public FluxNetworks(IEventBus bus) {
        sCuriosLoaded = ModList.get().isLoaded("curios");
        sModernUILoaded = ModList.get().isLoaded("modernui");
        sComputercraftLoaded = ModList.get().isLoaded("computercraft");

        FluxConfig.init();


        DataAttachments.REGISTER.register(bus);
        FluxDataComponents.REGISTRY.register(bus);

        if (sComputercraftLoaded)
            bus.addListener((RegisterCapabilitiesEvent event) -> {
                event.registerBlockEntity(PeripheralCapability.get(), RegistryBlockEntityTypes.FLUX_CONTROLLER.get(), (b,d) -> new CCTPeripheral(b));
                event.registerBlockEntity(PeripheralCapability.get(), RegistryBlockEntityTypes.FLUX_PLUG.get(), (b,d) -> new CCTPeripheral(b));
                event.registerBlockEntity(PeripheralCapability.get(), RegistryBlockEntityTypes.FLUX_POINT.get(), (b,d) -> new CCTPeripheral(b));
                event.registerBlockEntity(PeripheralCapability.get(), RegistryBlockEntityTypes.BASIC_FLUX_STORAGE.get(), (b,d) -> new CCTPeripheral(b));
                event.registerBlockEntity(PeripheralCapability.get(), RegistryBlockEntityTypes.HERCULEAN_FLUX_STORAGE.get(), (b,d) -> new CCTPeripheral(b));
                event.registerBlockEntity(PeripheralCapability.get(), RegistryBlockEntityTypes.GARGANTUAN_FLUX_STORAGE.get(), (b,d) -> new CCTPeripheral(b));
            });
    }

    public static boolean isCuriosLoaded() {
        return sCuriosLoaded;
    }

    public static boolean isModernUILoaded() {
        return sModernUILoaded;
    }
    public static boolean issComputercraftLoaded() {
        return sComputercraftLoaded;
    }

    @Nonnull
    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
