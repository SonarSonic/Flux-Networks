package fluxnetworks.client;

import fluxnetworks.api.FeedbackInfo;
import fluxnetworks.common.CommonProxy;
import fluxnetworks.common.registry.RegistryBlocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;

public class ClientProxy extends CommonProxy {

    private FeedbackInfo feedbackInfo = FeedbackInfo.NONE;
    private int feedbackTimer = 0;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void onServerStopped() {
        super.onServerStopped();
        FluxColorHandler.reset();
    }

    @Override
    public void registerItemModel(Item item, int meta, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    @SubscribeEvent
    public void registerBlockColor(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(FluxColorHandler.INSTANCE, RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG);
    }

    @SubscribeEvent
    public void registerItemColor(ColorHandlerEvent.Item event) {
        event.getItemColors().registerItemColorHandler(FluxColorHandler.INSTANCE, RegistryBlocks.FLUX_CONTROLLER, RegistryBlocks.FLUX_POINT, RegistryBlocks.FLUX_PLUG);
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
                    setFeedback(FeedbackInfo.NONE);
                }
            }
        }
    }

    @Override
    public FeedbackInfo getFeedback() {
        return feedbackInfo;
    }

    @Override
    public void setFeedback(FeedbackInfo info) {
        this.feedbackInfo = info;
        if(!info.hasFeedback()) {
            feedbackTimer = 0;
        }
    }

    @Override
    public void receiveColorCache(Map<Integer, Tuple<Integer, String>> cache) {
        FluxColorHandler.receiveCache(cache);
    }
}
