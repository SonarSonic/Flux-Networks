package sonar.fluxnetworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.common.capabilities.DefaultSuperAdmin;
import sonar.fluxnetworks.common.handler.CapabilityHandler;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.handler.TileEntityHandler;
import sonar.fluxnetworks.register.EventHandler;
import sonar.fluxnetworks.register.ProxyClient;
import sonar.fluxnetworks.register.ProxyServer;
import sonar.fluxnetworks.register.IProxy;

@Mod(FluxNetworks.MODID)
public class FluxNetworks {

    public static final String MODID = "fluxnetworks";
    public static final String NAME = "Flux Networks";
    public static final String VERSION = "5.0.0";

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ProxyClient(), () -> () -> new ProxyServer());
    public static final Logger LOGGER = LogManager.getLogger();

    public FluxNetworks() {
        LOGGER.info("FLUX NETWORKS INIT");
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FluxConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FluxConfig.CLIENT_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().register(FluxConfig.class);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);


    }
    private void enqueueIMC(final InterModEnqueueEvent event){
        InterModComms.sendTo("carryon", "blacklistBlock", () -> FluxNetworks.MODID + ":*");
        //InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPIntegration.class.getName());
    }

    private void processIMC(final InterModProcessEvent event){}

}
