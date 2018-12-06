package sonar.flux;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.core.SonarRegister;
import sonar.flux.api.FluxAPI;
import sonar.flux.common.block.FluxController;
import sonar.flux.common.block.FluxPlug;
import sonar.flux.common.block.FluxPoint;
import sonar.flux.common.block.FluxStorage;
import sonar.flux.common.entity.EntityFireItem;
import sonar.flux.common.item.ItemAdminConfigurator;
import sonar.flux.common.item.ItemConfigurator;
import sonar.flux.common.item.ItemFlux;
import sonar.flux.common.item.ItemNetworkConnector;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.common.tileentity.TileFluxPlug;
import sonar.flux.common.tileentity.TileFluxPoint;
import sonar.flux.common.tileentity.TileStorage;
import sonar.flux.connection.FNEnergyTransferHandler;
import sonar.flux.connection.FNEnergyTransferProxy;
import sonar.flux.network.ClientNetworkCache;
import sonar.flux.network.FluxCommon;
import sonar.flux.network.FluxNetworkCache;

import java.util.List;

@Mod(modid = FluxConstants.MODID, name = FluxConstants.NAME, acceptedMinecraftVersions = FluxConstants.MC_VERSIONS, version = FluxConstants.VERSION, dependencies = FluxConstants.DEPENDENCIES)
public class FluxNetworks {

	@SidedProxy(clientSide = "sonar.flux.network.FluxClient", serverSide = "sonar.flux.network.FluxCommon")
	public static FluxCommon proxy;

	@Instance(FluxConstants.MODID)
	public static FluxNetworks instance;

	public static List<Block> block_connection_blacklist;
	public static List<Item> item_connection_blacklist;
	public static final FNEnergyTransferHandler TRANSFER_HANDLER = new FNEnergyTransferHandler();

	public static SimpleNetworkWrapper network;
	public static Logger logger = (Logger) LogManager.getLogger(FluxConstants.MODID);

	public static Item flux, fluxCore, fluxConfigurator, adminConfigurator;
	public static Block fluxBlock, fluxPlug, fluxPoint, fluxCable, fluxStorage, largeFluxStorage, massiveFluxStorage, fluxController;

	public static CreativeTabs tab = new CreativeTabs("Flux Networks") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Item.getItemFromBlock(fluxPlug));
		}
	};
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Initialising API");
		FluxAPI.init();

		logger.info("Loading Config");
		FluxConfig.startLoading();

		logger.info("Loading Network");
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Flux-Networks");

		logger.info("Loading Blocks/Items");
		fluxBlock = SonarRegister.addBlock(FluxConstants.MODID, tab, "FluxBlock", new Block(Material.ROCK));

		flux = SonarRegister.addItem(FluxConstants.MODID, tab, "Flux", new ItemFlux());
		fluxCore = SonarRegister.addItem(FluxConstants.MODID, tab, "FluxCore", new Item());
		fluxConfigurator = SonarRegister.addItem(FluxConstants.MODID, tab, "FluxConfigurator", new ItemConfigurator());
		adminConfigurator = SonarRegister.addItem(FluxConstants.MODID, tab, "AdminConfigurator", new ItemAdminConfigurator());

		fluxPlug = SonarRegister.addBlock(FluxConstants.MODID, tab, new ItemNetworkConnector.FluxConnectorRegistry(new FluxPlug().setHardness(0.4F).setResistance(20.0F), "FluxPlug", TileFluxPlug.class));
		fluxPoint = SonarRegister.addBlock(FluxConstants.MODID, tab, new ItemNetworkConnector.FluxConnectorRegistry(new FluxPoint().setHardness(0.4F).setResistance(20.0F), "FluxPoint", TileFluxPoint.class));
		fluxController = SonarRegister.addBlock(FluxConstants.MODID, tab, new ItemNetworkConnector.FluxConnectorRegistry(new FluxController().setHardness(0.4F).setResistance(20.0F), "FluxController", TileController.class));
		fluxStorage = SonarRegister.addBlock(FluxConstants.MODID, tab, new ItemNetworkConnector.FluxConnectorRegistry(new FluxStorage().setHardness(0.4F).setResistance(20.0F), "FluxStorage", TileStorage.Basic.class));
		largeFluxStorage = SonarRegister.addBlock(FluxConstants.MODID, tab, new ItemNetworkConnector.FluxConnectorRegistry(new FluxStorage.Herculean().setHardness(0.4F).setResistance(20.0F), "HerculeanFluxStorage", TileStorage.Herculean.class));
		massiveFluxStorage = SonarRegister.addBlock(FluxConstants.MODID, tab, new ItemNetworkConnector.FluxConnectorRegistry(new FluxStorage.Gargantuan().setHardness(0.4F).setResistance(20.0F), "GargantuanFluxStorage", TileStorage.Gargantuan.class));

		logger.info("Loading Entities");
		EntityRegistry.registerModEntity(new ResourceLocation(FluxConstants.MODID, "Flux"), EntityFireItem.class, "Flux", 0, instance, 64, 10, true);

		logger.info("Loading Recipes");
		FluxCrafting.addRecipes();

		logger.info("Loading Packets");
		FluxCommon.registerPackets();

		logger.info("Loading Renderers");
		proxy.registerRenderThings();

		proxy.preInit(event);
		MinecraftForge.EVENT_BUS.register(proxy);
		logger.info("Finished Pre-Initialization");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("Loading Events");
		MinecraftForge.EVENT_BUS.register(new FluxEvents());
		logger.info("Loaded Events");

		proxy.init(event);
		logger.info("Finished Initialization");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		FluxConfig.finishLoading();
		block_connection_blacklist = FNEnergyTransferProxy.getBlackListedValues(ForgeRegistries.BLOCKS, FluxConfig.block_connection_blacklist_strings);
		item_connection_blacklist = FNEnergyTransferProxy.getBlackListedValues(ForgeRegistries.ITEMS, FluxConfig.item_connection_blacklist_strings);
		proxy.postInit(event);
	}

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {}
    
	@EventHandler
	public void onServerStopped(FMLServerStoppedEvent event) {
		proxy.shutdown(event);
		logger.info("Cleared Network Caches");
	}

	public static ClientNetworkCache getClientCache() {
		return FluxNetworks.proxy.clientCache;
	}

	public static FluxNetworkCache getServerCache() {
		return FluxNetworks.proxy.serverCache;
	}
}
