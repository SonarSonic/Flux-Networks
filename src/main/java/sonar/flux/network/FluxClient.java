package sonar.flux.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import sonar.core.network.SonarClient;
import sonar.core.translate.ILocalisationHandler;
import sonar.core.translate.Localisation;
import sonar.flux.FluxNetworks;
import sonar.flux.FluxTranslate;
import sonar.flux.api.ClientFlux;
import sonar.flux.api.FluxError;
import sonar.flux.client.FluxColourHandler;
import sonar.flux.client.FluxStorageModel;
import sonar.flux.client.RenderFluxStorageItem;
import sonar.flux.client.RenderFluxStorageTile;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.common.tileentity.TileStorage;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FluxClient extends FluxCommon implements ILocalisationHandler {

	@Override
	public void registerRenderThings() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileStorage.class, new RenderFluxStorageTile());
		Item.getItemFromBlock(FluxNetworks.fluxStorage).setTileEntityItemStackRenderer(RenderFluxStorageItem.INSTANCE);
		Item.getItemFromBlock(FluxNetworks.largeFluxStorage).setTileEntityItemStackRenderer(RenderFluxStorageItem.INSTANCE);
		Item.getItemFromBlock(FluxNetworks.massiveFluxStorage).setTileEntityItemStackRenderer(RenderFluxStorageItem.INSTANCE);
		ModelLoaderRegistry.registerLoader(FluxStorageModel.INSTANCE);
	}

	@SubscribeEvent
	public void initBlockColours(ColorHandlerEvent.Block event){
		event.getBlockColors().registerBlockColorHandler(FluxColourHandler.INSTANCE, FluxNetworks.fluxPlug, FluxNetworks.fluxPoint, FluxNetworks.fluxController, FluxNetworks.fluxStorage, FluxNetworks.largeFluxStorage, FluxNetworks.massiveFluxStorage);

	}

	@SubscribeEvent
	public void initItemColours(ColorHandlerEvent.Item event){
		event.getItemColors().registerItemColorHandler(FluxColourHandler.INSTANCE, FluxNetworks.fluxPlug, FluxNetworks.fluxPoint, FluxNetworks.fluxController, FluxNetworks.fluxStorage, FluxNetworks.largeFluxStorage, FluxNetworks.massiveFluxStorage);
		event.getItemColors().registerItemColorHandler(FluxColourHandler::itemViewerMultiplier, FluxNetworks.fluxConfigurator);
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event) {
		FluxColourHandler.sendRequests();
	}

	@Override
	public void shutdown(FMLServerStoppedEvent event) {
		super.shutdown(event);
		FluxColourHandler.reset();
		clientCache.clearNetworks();
	}

	@Override
	public void receiveColourCache(Map<Integer, Tuple<Integer, String>> cache){
		super.receiveColourCache(cache);
		FluxColourHandler.receiveCache(cache);
	}

	@Override
	public void clearNetwork(int networkID){
		super.clearNetwork(networkID);
		FluxColourHandler.colourCache.put(networkID, FluxColourHandler.NO_NETWORK_COLOUR);
		FluxColourHandler.nameCache.put(networkID, "NONE");

	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		clientCache = new ClientNetworkCache();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		SonarClient.translator.add(this);
		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
			IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
		}
	}

	@Override
	public List<Localisation> getLocalisations(List<Localisation> current) {
		current.addAll(FluxTranslate.locals);
		return current;
	}

	private FluxError error;

	public void setFluxError(FluxError error){
		this.error = error;
	}

	@Nullable
	public FluxError getFluxError(){
		return error;
	}


	private TileFlux flux;

	public void setFluxTile(TileFlux flux){
		this.flux = flux;
	}

	@Nullable
	public TileFlux getFluxTile(){
		return flux;
	}


	private ItemStack flux_stack;

	public void setFluxStack(ItemStack flux_stack){
		this.flux_stack = flux_stack;
	}

	@Nullable
	public ItemStack getFluxStack(){
		return flux_stack;
	}

	private List<ClientFlux> tiles;

	public void setEditingFluxTile(List<ClientFlux> tiles){
		this.tiles = tiles;
	}

	public List<ClientFlux> getEditingFluxTile(){
		return tiles;
	}
}
