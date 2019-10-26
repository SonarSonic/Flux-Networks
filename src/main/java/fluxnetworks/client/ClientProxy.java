package fluxnetworks.client;

import fluxnetworks.api.translate.FluxTranslate;
import fluxnetworks.api.gui.EnumFeedbackInfo;
import fluxnetworks.client.render.FluxStorageModel;
import fluxnetworks.client.render.TileFluxStorageRenderer;
import fluxnetworks.common.CommonProxy;
import fluxnetworks.common.connection.FluxNetworkCache;
import fluxnetworks.common.handler.LocalizationHandler;
import fluxnetworks.common.registry.RegistryBlocks;
import fluxnetworks.common.registry.RegistryItems;
import fluxnetworks.common.tileentity.TileFluxStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class ClientProxy extends CommonProxy {

    private LocalizationHandler localizationHandler = new LocalizationHandler();
    private EnumFeedbackInfo feedbackInfo = EnumFeedbackInfo.NONE; // Text message.
    private EnumFeedbackInfo feedbackInfoSuccess = EnumFeedbackInfo.NONE; // Special operation.
    private int feedbackTimer = 0;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ClientRegistry.bindTileEntitySpecialRenderer(TileFluxStorage.class, new TileFluxStorageRenderer());
        ModelLoaderRegistry.registerLoader(FluxStorageModel.INSTANCE);
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        if (manager instanceof SimpleReloadableResourceManager) {
            SimpleReloadableResourceManager resources = (SimpleReloadableResourceManager) manager;
            resources.registerReloadListener(localizationHandler);
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        localizationHandler.add(FluxTranslate.INSTANCE);
    }

    @Override
    public void onServerStopped() {
        super.onServerStopped();
        FluxColorHandler.reset();
        FluxNetworkCache.instance.clearClientCache();
    }
    @Override
    public void registerItemModel(Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    @SubscribeEvent
    public void clientFlux(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        onServerStopped(); //FMLServerStoppedEvent is not fired on the client side when disconnecting from a server
    }

    @SubscribeEvent
    public void registerBlockColor(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(FluxColorHandler.INSTANCE, RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG, RegistryBlocks.FLUX_STORAGE_1, RegistryBlocks.FLUX_STORAGE_2, RegistryBlocks.FLUX_STORAGE_3);
    }

    @SubscribeEvent
    public void registerItemColor(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(FluxColorHandler.INSTANCE, RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG, RegistryBlocks.FLUX_STORAGE_1, RegistryBlocks.FLUX_STORAGE_2, RegistryBlocks.FLUX_STORAGE_3);
        event.getItemColors().registerItemColorHandler(FluxColorHandler::colorMultiplierForConfigurator, RegistryItems.FLUX_CONFIGURATOR);
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        FluxColorHandler.sendRequests();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            if(feedbackInfo.hasFeedback()) {
                feedbackTimer++;
                if(feedbackTimer >= 60) {
                    feedbackTimer = 0;
                    setFeedback(EnumFeedbackInfo.NONE, false);
                }
            }
        }
    }

    @Override
    public EnumFeedbackInfo getFeedback(boolean operation) {
        return operation ? feedbackInfoSuccess : feedbackInfo;
    }

    @Override
    public void setFeedback(EnumFeedbackInfo info, boolean operation) {
        if(operation) {
            this.feedbackInfoSuccess = info;
        } else {
            this.feedbackInfo = info;
        }
        feedbackTimer = 0;
    }

    @Override
    public void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache) {
        FluxColorHandler.receiveCache(cache);
    }

    @Override
    public EntityPlayer getPlayer(MessageContext ctx) {
        return ctx.side.isServer() ? super.getPlayer(ctx) : Minecraft.getMinecraft().player;
    }

}
