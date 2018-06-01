package sonar.flux;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.core.SonarRegister;
import sonar.flux.api.FluxAPI;
import sonar.flux.common.block.FluxController;
import sonar.flux.common.block.FluxPlug;
import sonar.flux.common.block.FluxPoint;
import sonar.flux.common.block.FluxStorage;
import sonar.flux.common.entity.EntityFireItem;
import sonar.flux.common.item.AdminConfigurator;
import sonar.flux.common.item.FluxConfigurator;
import sonar.flux.common.item.FluxItem;
import sonar.flux.common.tileentity.TileController;
import sonar.flux.common.tileentity.TileFluxPlug;
import sonar.flux.common.tileentity.TileFluxPoint;
import sonar.flux.common.tileentity.TileStorage;
import sonar.flux.connection.FNEnergyTransferHandler;
import sonar.flux.connection.FNEnergyTransferProxy;
import sonar.flux.network.ClientNetworkCache;
import sonar.flux.network.FluxCommon;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.NetworkData;

import java.util.List;

@Mod(modid = FluxConstants.MODID, name = FluxConstants.NAME, acceptedMinecraftVersions = FluxConstants.MC_VERSIONS, version = FluxConstants.VERSION, dependencies = FluxConstants.DEPENDENCIES)
public class FluxNetworks {

	@SidedProxy(clientSide = "sonar.flux.network.FluxClient", serverSide = "sonar.flux.network.FluxCommon")
	public static FluxCommon proxy;

	@Instance(FluxConstants.MODID)
	public static FluxNetworks instance;

	public FluxNetworkCache serverCache = new FluxNetworkCache();
	public ClientNetworkCache clientCache = new ClientNetworkCache();
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

		flux = SonarRegister.addItem(FluxConstants.MODID, tab, "Flux", new FluxItem());
		fluxCore = SonarRegister.addItem(FluxConstants.MODID, tab, "FluxCore", new Item());
		fluxConfigurator = SonarRegister.addItem(FluxConstants.MODID, tab, "FluxConfigurator", new FluxConfigurator());
		adminConfigurator = SonarRegister.addItem(FluxConstants.MODID, tab, "AdminConfigurator", new AdminConfigurator());

		fluxPlug = SonarRegister.addBlock(FluxConstants.MODID, tab, "FluxPlug", new FluxPlug().setHardness(0.4F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileFluxPlug.class, "FluxPlug");

		fluxPoint = SonarRegister.addBlock(FluxConstants.MODID, tab, "FluxPoint", new FluxPoint().setHardness(0.2F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileFluxPoint.class, "FluxPoint");

		fluxController = SonarRegister.addBlock(FluxConstants.MODID, tab, "FluxController", new FluxController().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileController.class, "FluxController");

		fluxStorage = SonarRegister.addBlock(FluxConstants.MODID, tab, "FluxStorage", new FluxStorage().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileStorage.Basic.class, "FluxStorage");

		largeFluxStorage = SonarRegister.addBlock(FluxConstants.MODID, tab, "HerculeanFluxStorage", new FluxStorage.Herculean().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileStorage.Herculean.class, "HerculeanFluxStorage");

		massiveFluxStorage = SonarRegister.addBlock(FluxConstants.MODID, tab, "GargantuanFluxStorage", new FluxStorage.Gargantuan().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileStorage.Gargantuan.class, "GargantuanFluxStorage");

		logger.info("Loading Entities");
		EntityRegistry.registerModEntity(new ResourceLocation(FluxConstants.MODID, "Flux"), EntityFireItem.class, "Flux", 0, instance, 64, 10, true);

		logger.info("Loading Recipes");
		FluxCrafting.addRecipes();

		logger.info("Loading Packets");
		FluxCommon.registerPackets();

		logger.info("Loading Renderers");
		proxy.registerRenderThings();

		proxy.preInit(event);
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
    public void onServerStarting(FMLServerStartingEvent event) {
        MapStorage storage = DimensionManager.getWorld(0).getMapStorage();
        if(storage.getOrLoadData(NetworkData.class, NetworkData.IDENTIFIER) == null) {
            storage.setData(NetworkData.IDENTIFIER, new NetworkData());
        }
    }
    
	@EventHandler
	public void onServerStopped(FMLServerStoppedEvent event) {
		serverCache.clearNetworks();
		clientCache.clearNetworks();
		logger.info("Cleared Network Caches");
	}

	public static ClientNetworkCache getClientCache() {
		return FluxNetworks.instance.clientCache;
	}

	public static FluxNetworkCache getServerCache() {
		return FluxNetworks.instance.serverCache;
	}
}
