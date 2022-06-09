package sonar.fluxnetworks.register;

import icyllis.modernui.forge.OpenMenuEvent;
import icyllis.modernui.util.DataSet;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.client.gui.GuiFluxDeviceHome;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.mui.FluxDeviceUI;
import sonar.fluxnetworks.client.render.FluxStorageEntityRenderer;
import sonar.fluxnetworks.common.connection.FluxDeviceMenu;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = FluxNetworks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        BlockEntityRenderers.register(RegistryBlocks.BASIC_FLUX_STORAGE_ENTITY,
                FluxStorageEntityRenderer.PROVIDER);
        BlockEntityRenderers.register(RegistryBlocks.HERCULEAN_FLUX_STORAGE_ENTITY,
                FluxStorageEntityRenderer.PROVIDER);
        BlockEntityRenderers.register(RegistryBlocks.GARGANTUAN_FLUX_STORAGE_ENTITY,
                FluxStorageEntityRenderer.PROVIDER);

        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.FLUX_PLUG, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.FLUX_POINT, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.FLUX_CONTROLLER, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.BASIC_FLUX_STORAGE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.HERCULEAN_FLUX_STORAGE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RegistryBlocks.GARGANTUAN_FLUX_STORAGE, RenderType.cutout());

        //RenderingRegistry.registerEntityRenderingHandler(RegistryItems.FIRE_ITEM_ENTITY, manager -> new
        // ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        MenuScreens.register(RegistryBlocks.FLUX_MENU, getScreenFactory());
    }

    @Nonnull
    private static MenuScreens.ScreenConstructor<FluxDeviceMenu, GuiTabCore> getScreenFactory() {
        return (menu, inventory, title) -> {
            /*if (menu.bridge instanceof FluxDeviceEntity) {
                return new GuiFluxDeviceHome(menu, inventory.player);
            }
            if (menu.bridge instanceof ItemFluxConfigurator.MenuBridge) {
                return new GuiFluxConfiguratorHome(menu, inventory.player);
            }
            return new GuiFluxAdminHome(menu, inventory.player);*/
            return new GuiFluxDeviceHome(menu, inventory.player);
        };
    }

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

    @SubscribeEvent
    public static void openMenu(@Nonnull OpenMenuEvent event) {
        if (event.getMenu() instanceof FluxDeviceMenu menu && menu.mProvider != null) {
            FluxDeviceUI fragment = new FluxDeviceUI((TileFluxDevice) menu.mProvider);
            menu.mOnResultListener = fragment;
            DataSet args = new DataSet();
            args.putInt("token", menu.containerId);
            fragment.setArguments(args);
            event.set(fragment);
        }
    }
}
