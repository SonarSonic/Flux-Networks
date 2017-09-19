package sonar.flux;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.common.entity.EntityFireItem;
import sonar.flux.network.FluxNetworkCache;
import sonar.flux.network.NetworkData;

import java.util.ArrayList;

public class FluxEvents {

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.side == Side.CLIENT) {
			return;
		}
		if (event.phase == Phase.START) {
			FluxNetworkCache cache = FluxNetworks.getServerCache();
			ArrayList<IFluxNetwork> networks = cache.getAllNetworks();
			for (IFluxNetwork network : networks) {
				network.updateNetwork();
			}
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (event.getWorld().isRemote) {
			return;
		}
		if (event.getWorld().provider.getDimension() == FluxNetworks.saveDimension) {
			NetworkData data = (NetworkData) event.getWorld().getPerWorldStorage().getOrLoadData(NetworkData.class, NetworkData.tag);
		}
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
		if (event.getWorld().isRemote) {
			return;
		}
		if (event.getWorld().provider.getDimension() == FluxNetworks.saveDimension) {
			MapStorage storage = event.getWorld().getPerWorldStorage();
			NetworkData data = (NetworkData) storage.getOrLoadData(NetworkData.class, NetworkData.tag);
			if (data == null && !FluxNetworks.getServerCache().getAllNetworks().isEmpty()) {
				storage.setData(NetworkData.tag, new NetworkData(NetworkData.tag));
			}
		}
	}

	@SubscribeEvent
	public void onEntityAdded(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote) {
			return;
		}
		final Entity entity = event.getEntity();
		if (entity instanceof EntityItem && !(entity instanceof EntityFireItem)) {
			EntityItem entityItem = (EntityItem) entity;
            ItemStack stack = entityItem.getItem();
            Item item;
			if (!stack.isEmpty() && (item = stack.getItem()) != null && (item == Items.REDSTONE || item == Items.ENDER_EYE || item == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))) {
				EntityFireItem newEntity = new EntityFireItem(event.getWorld(), entityItem.posX, entityItem.posY, entityItem.posZ, stack);
				newEntity.motionX = entityItem.motionX;
				newEntity.motionY = entityItem.motionY;
				newEntity.motionZ = entityItem.motionZ;
				newEntity.setDefaultPickupDelay();
				if (newEntity != null) {
					event.getEntity().setDead();
					event.setCanceled(true);
					event.getWorld().spawnEntity(newEntity);
				}
			}
		}
    }

    public static void logNewNetwork(IFluxNetwork network) {
        FluxNetworks.logger.info("[NEW NETWORK] '" + network.getNetworkName() + "' with ID '" + network.getNetworkID() + "' was created by " + network.getCachedPlayerName());
    }

    public static void logRemoveNetwork(IFluxNetwork network) {
        FluxNetworks.logger.info("[DELETE NETWORK] '" + network.getNetworkName() + "' with ID '" + network.getNetworkID() + "' was removed by " + network.getCachedPlayerName());
    }

    public static void logLoadedNetwork(IFluxNetwork network) {
        FluxNetworks.logger.info("[LOADED NETWORK] '" + network.getNetworkName() + "' with ID '" + network.getNetworkID() + "' with owner " + network.getCachedPlayerName());
	}
}