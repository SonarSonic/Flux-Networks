package sonar.fluxnetworks.register;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.client.gui.GuiFluxAdminHome;
import sonar.fluxnetworks.client.gui.GuiFluxDeviceHome;
import sonar.fluxnetworks.client.render.FluxStorageEntityRenderer;
import sonar.fluxnetworks.common.connection.FluxMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;
import sonar.fluxnetworks.common.integration.MUIIntegration;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        if (FluxNetworks.isModernUILoaded()) {
            event.enqueueWork(() -> MenuScreens.register(RegistryMenuTypes.FLUX_MENU.get(),
                    MUIIntegration.upgradeScreenFactory(getScreenFactory())));
        } else {
            event.enqueueWork(() -> MenuScreens.register(RegistryMenuTypes.FLUX_MENU.get(),
                    getScreenFactory()));
        }
    }

    @Nonnull
    private static MenuScreens.ScreenConstructor<FluxMenu, AbstractContainerScreen<FluxMenu>> getScreenFactory() {
        return (menu, inventory, title) -> {
            if (menu.mProvider instanceof TileFluxDevice) {
                return new GuiFluxDeviceHome(menu, inventory.player);
            }
            /*if (menu.bridge instanceof ItemFluxConfigurator.MenuBridge) {
                return new GuiFluxConfiguratorHome(menu, inventory.player);
            }*/
            return new GuiFluxAdminHome(menu, inventory.player);
        };
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegistryBlockEntityTypes.BASIC_FLUX_STORAGE.get(),
                FluxStorageEntityRenderer.PROVIDER);
        event.registerBlockEntityRenderer(RegistryBlockEntityTypes.HERCULEAN_FLUX_STORAGE.get(),
                FluxStorageEntityRenderer.PROVIDER);
        event.registerBlockEntityRenderer(RegistryBlockEntityTypes.GARGANTUAN_FLUX_STORAGE.get(),
                FluxStorageEntityRenderer.PROVIDER);
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(@Nonnull RegisterColorHandlersEvent.Item event) {
        event.register(FluxColorHandler.INSTANCE,
                RegistryBlocks.FLUX_CONTROLLER.get(),
                RegistryBlocks.FLUX_POINT.get(),
                RegistryBlocks.FLUX_PLUG.get());
        event.register(FluxColorHandler::colorMultiplierForConfigurator,
                RegistryItems.FLUX_CONFIGURATOR.get());
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(@Nonnull RegisterColorHandlersEvent.Block event) {
        event.register(FluxColorHandler.INSTANCE,
                RegistryBlocks.FLUX_CONTROLLER.get(),
                RegistryBlocks.FLUX_POINT.get(),
                RegistryBlocks.FLUX_PLUG.get(),
                RegistryBlocks.BASIC_FLUX_STORAGE.get(),
                RegistryBlocks.HERCULEAN_FLUX_STORAGE.get(),
                RegistryBlocks.GARGANTUAN_FLUX_STORAGE.get());
    }
}
