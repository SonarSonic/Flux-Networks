package sonar.fluxnetworks.register;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.client.gui.GuiFluxAdminHome;
import sonar.fluxnetworks.client.gui.GuiFluxConfiguratorHome;
import sonar.fluxnetworks.client.gui.GuiFluxConnectorHome;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.mui.MUIIntegration;
import sonar.fluxnetworks.client.render.FluxStorageTileRenderer;
import sonar.fluxnetworks.common.core.ContainerConnector;
import sonar.fluxnetworks.common.item.AdminConfiguratorItem;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;

@Mod.EventBusSubscriber(modid = FluxNetworks.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        FluxNetworks.LOGGER.info("Started Client Setup Event");

        FluxNetworks.LOGGER.info("Registering TileEntity Renderers");
        ClientRegistry.bindTileEntityRenderer(RegistryBlocks.BASIC_FLUX_STORAGE_TILE, FluxStorageTileRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RegistryBlocks.HERCULEAN_FLUX_STORAGE_TILE, FluxStorageTileRenderer::new);
        ClientRegistry.bindTileEntityRenderer(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_TILE, FluxStorageTileRenderer::new);


        FluxNetworks.LOGGER.info("Registering Render Types");
        RenderTypeLookup.setRenderLayer(RegistryBlocks.FLUX_PLUG, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryBlocks.FLUX_POINT, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryBlocks.FLUX_CONTROLLER, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryBlocks.BASIC_FLUX_STORAGE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryBlocks.HERCULEAN_FLUX_STORAGE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, RenderType.getCutout());
        FluxNetworks.LOGGER.info("Registering Screens");

        if (FluxConfig.enableGuiDebug && FluxNetworks.modernUILoaded){
            MUIIntegration.init();
        } else {
            ScreenManager.registerFactory(RegistryBlocks.CONTAINER_CONNECTOR, (ScreenManager.IScreenFactory<ContainerConnector<?>, GuiTabCore>) (container, inventory, windowID) -> {
                if (container == null) {
                    return null;
                }
                INetworkConnector connector = container.connector;
                if (connector instanceof TileFluxCore) {
                    return new GuiFluxConnectorHome(inventory.player, (TileFluxCore) connector);
                }
                if (connector instanceof FluxConfiguratorItem.ContainerProvider) {
                    return new GuiFluxConfiguratorHome(inventory.player, (FluxConfiguratorItem.ContainerProvider) connector);
                }
                if (connector instanceof AdminConfiguratorItem.ContainerProvider) {
                    return new GuiFluxAdminHome(inventory.player, connector);
                }
                return null;
            });
        }


        FluxNetworks.LOGGER.info("Finished Client Setup Event");

    }

    @SubscribeEvent
    public static void registerItemColorHandlers(ColorHandlerEvent.Item event) {
        FluxNetworks.LOGGER.info("Starting Registering Item Color Handlers");

        event.getItemColors().register(FluxColorHandler.INSTANCE, RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG, RegistryBlocks.BASIC_FLUX_STORAGE, RegistryBlocks.HERCULEAN_FLUX_STORAGE, RegistryBlocks.GARGANTUAN_FLUX_STORAGE);
        event.getItemColors().register(FluxColorHandler::colorMultiplierForConfigurator, RegistryItems.FLUX_CONFIGURATOR);

        FluxNetworks.LOGGER.info("Finished Registering Item Color Handlers");
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(ColorHandlerEvent.Block event) {
        FluxNetworks.LOGGER.info("Starting Registering Block Color Handlers");

        event.getBlockColors().register(FluxColorHandler.INSTANCE, RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG, RegistryBlocks.BASIC_FLUX_STORAGE, RegistryBlocks.HERCULEAN_FLUX_STORAGE, RegistryBlocks.GARGANTUAN_FLUX_STORAGE);

        FluxNetworks.LOGGER.info("Finished Registering Block Color Handlers");
    }

}
