package sonar.flux;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sonar.core.common.block.SonarBlockTip;
import sonar.flux.api.FluxAPI;
import sonar.flux.common.block.FluxCable;
import sonar.flux.common.block.FluxController;
import sonar.flux.common.block.FluxPlug;
import sonar.flux.common.block.FluxPoint;
import sonar.flux.common.block.FluxStorage;
import sonar.flux.common.item.FluxItem;
import sonar.flux.common.tileentity.TileEntityCable;
import sonar.flux.common.tileentity.TileEntityFlux;
import sonar.flux.network.FluxCommon;
import sonar.flux.network.ServerNetworkCache;

@Mod(modid = FluxNetworks.modid, name = "FluxNetworks", version = FluxNetworks.version)
public class FluxNetworks {

	public static final String modid = "FluxNetworks";
	public static final String version = "1.0.0";

	public static final int saveDimension = 0;

	@SidedProxy(clientSide = "sonar.flux.network.FluxClient", serverSide = "sonar.flux.network.FluxCommon")
	public static FluxCommon proxy;

	@Instance(modid)
	public static FluxNetworks instance;
	public static SimpleNetworkWrapper network;
	public static ServerNetworkCache cache = new ServerNetworkCache();
	public static Logger logger = (Logger) LogManager.getLogger(modid);
	
	public static Item flux, fluxCore;
	public static Block fluxBlock, fluxPlug, fluxPoint, fluxCable, fluxStorage, largeFluxStorage, massiveFluxStorage, fluxController;
	
	public static ArrayList<Item> registeredItems = new ArrayList();
	public static ArrayList<Block> registeredBlocks = new ArrayList();
	public static CreativeTabs tab = new CreativeTabs("Flux Networks") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(fluxPlug);
		}
	};

	public static Block registerBlock(String name, Block block) {
		block.setCreativeTab(tab);
		GameRegistry.registerBlock(block.setUnlocalizedName(name), SonarBlockTip.class, name);
		registeredBlocks.add(block);
		return block;
	}

	public static Item registerItem(String name, Item item) {
		item.setCreativeTab(tab);
		GameRegistry.registerItem(item.setUnlocalizedName(name), name);
		registeredItems.add(item);
		return item;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Initilising API");
		FluxAPI.init();
		logger.info("Initilised API");

		logger.info("Loading Network");
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Flux-Networks");

		logger.info("Loading Blocks/Items");
		fluxBlock = registerBlock("FluxBlock", new Block(Material.ROCK));
		
		flux = registerItem("Flux", new FluxItem());		
		fluxCore = registerItem("FluxCore", new Item());	
		
		//fluxCable = registerBlock("FluxCable", new FluxCable().setHardness(0.4F).setResistance(20.0F));
		//GameRegistry.registerTileEntity(TileEntityCable.class, "FluxCable");	
		
		fluxPlug = registerBlock("FluxPlug", new FluxPlug().setHardness(0.4F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileEntityFlux.Plug.class, "FluxPlug");

		fluxPoint = registerBlock("FluxPoint", new FluxPoint().setHardness(0.2F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileEntityFlux.Point.class, "FluxPoint");
		
		fluxController = registerBlock("FluxController", new FluxController().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileEntityFlux.Controller.class, "FluxController");

		fluxStorage = registerBlock("FluxStorage", new FluxStorage().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileEntityFlux.Storage.Basic.class, "FluxStorage");

		largeFluxStorage = registerBlock("HerculeanFluxStorage", new FluxStorage.Herculean().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileEntityFlux.Storage.Advanced.class, "HerculeanFluxStorage");

		massiveFluxStorage = registerBlock("GargantuanFluxStorage", new FluxStorage.Gargantuan().setHardness(0.6F).setResistance(20.0F));
		GameRegistry.registerTileEntity(TileEntityFlux.Storage.Massive.class, "GargantuanFluxStorage");		

		logger.info("Loaded Blocks/Items");
		
		logger.info("Loading Recipes");
		FluxCrafting.addRecipes();
		logger.info("Loaded Recipes");
		
		logger.info("Loading Packets");
		proxy.registerPackets();
		logger.info("Loaded Packets");
		
		logger.info("Loading Renderers");
		proxy.registerRenderThings();
		logger.info("Loaded Renderers");

		logger.info("Finished Pre-Initialization");
		
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("Loading Handlers");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new FluxCommon());
		logger.info("Loaded Handlers");

		logger.info("Loading Handlers");
		MinecraftForge.EVENT_BUS.register(new FluxEvents());
		logger.info("Loaded Events");
		
		logger.info("Finished Initialization");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
		logger.info("Removing Viewers");
		cache.removeAllViewers();
		logger.info("Removed Viewers");

		logger.info("Removing Networks");
		cache.clearNetworks();
		logger.info("Removed Networks");
		
	}
}
