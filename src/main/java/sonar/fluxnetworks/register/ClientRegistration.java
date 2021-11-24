package sonar.fluxnetworks.register;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.client.mui.MUIIntegration;
import sonar.fluxnetworks.client.render.FluxStorageTileRenderer;
import sonar.fluxnetworks.common.registry.RegistryBlocks;
import sonar.fluxnetworks.common.registry.RegistryItems;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(RegistryBlocks.BASIC_FLUX_STORAGE_TILE, FluxStorageTileRenderer::new);
        BlockEntityRenderers.register(RegistryBlocks.HERCULEAN_FLUX_STORAGE_TILE, FluxStorageTileRenderer::new);
        BlockEntityRenderers.register(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_TILE, FluxStorageTileRenderer::new);

        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.FLUX_PLUG, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.FLUX_POINT, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.FLUX_CONTROLLER, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.BASIC_FLUX_STORAGE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.HERCULEAN_FLUX_STORAGE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, RenderType.cutout());

        if (FluxConfig.enableGuiDebug && FluxNetworks.modernUILoaded) {
            MUIIntegration.init();
        }

        //RenderingRegistry.registerEntityRenderingHandler(RegistryItems.FIRE_ITEM_ENTITY, manager -> new
        // ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
    }

    /*@Nonnull
    private static ScreenManager.IScreenFactory<FluxContainerMenu, GuiTabCore> getScreenFactory() {
        return (container, inventory, windowID) -> {
            if (container.bridge instanceof FluxDeviceEntity) {
                return new GuiFluxDeviceHome(container, inventory.player);
            }
            if (container.bridge instanceof ItemFluxConfigurator.MenuBridge) {
                return new GuiFluxConfiguratorHome(container, inventory.player);
            }
            return new GuiFluxAdminHome(container, inventory.player);
        };
    }*/

    @SubscribeEvent
    public static void registerItemColorHandlers(@Nonnull ColorHandlerEvent.Item event) {
        event.getItemColors().register(FluxColorHandler.INSTANCE,
                RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG);
        event.getItemColors().register(FluxColorHandler::colorMultiplierForConfigurator,
                RegistryItems.FLUX_CONFIGURATOR);
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(@Nonnull ColorHandlerEvent.Block event) {
        event.getBlockColors().register(FluxColorHandler.INSTANCE,
                RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG,
                RegistryBlocks.BASIC_FLUX_STORAGE, RegistryBlocks.HERCULEAN_FLUX_STORAGE,
                RegistryBlocks.GARGANTUAN_FLUX_STORAGE);
    }
}
