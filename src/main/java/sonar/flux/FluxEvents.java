package sonar.flux;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.SonarCore;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.common.entity.EntityFireItem;
import sonar.flux.network.FluxNetworkCache;

public class FluxEvents {

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.side == Side.CLIENT) {
			return;
		}
		FluxNetworkCache cache = FluxNetworks.getServerCache();
		List<IFluxNetwork> networks = cache.getAllNetworks();
		if (event.phase == Phase.START) {
			for (IFluxNetwork network : networks) {
				network.onStartServerTick();
			}
		}
		if (event.phase == Phase.END) {

			for (IFluxNetwork network : networks) {
				network.onEndServerTick();
			}
		}

	}

	@SubscribeEvent
	public void dropFluxEvent(HarvestDropsEvent drops) {
		if (!FluxConfig.enableFluxRedstoneDrop || !(drops.getState().getBlock() == Blocks.REDSTONE_ORE || (drops.getState().getBlock() == Blocks.LIT_REDSTONE_ORE)) || drops.getHarvester() instanceof FakePlayer || drops.isSilkTouching()) {
			return;
		}
		if (SonarCore.randInt(0, FluxConfig.redstone_ore_chance) == 1) {
			drops.getDrops().add(new ItemStack(FluxNetworks.flux, Math.max(1, SonarCore.randInt(FluxConfig.redstone_ore_min_drop, FluxConfig.redstone_ore_max_drop))));
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityAdded(EntityJoinWorldEvent event) {
		if (!FluxConfig.enableFluxRecipe || event.getWorld().isRemote) {
			return;
		}
		final Entity entity = event.getEntity();
		if (entity instanceof EntityItem && !(entity instanceof EntityFireItem)) {
			EntityItem entityItem = (EntityItem) entity;
			ItemStack stack = entityItem.getItem();
			if (!stack.isEmpty() && stack.getItem() == Items.REDSTONE) {				
				EntityFireItem newEntity = new EntityFireItem(event.getWorld(), entityItem.posX, entityItem.posY, entityItem.posZ, stack);
				newEntity.setThrower(entityItem.getThrower());
				newEntity.setDefaultPickupDelay();
				newEntity.motionX = entityItem.motionX;
				newEntity.motionY = entityItem.motionY;
				newEntity.motionZ = entityItem.motionZ;						
				event.setCanceled(true);				
		        int i = MathHelper.floor(newEntity.posX / 16.0D);
		        int j = MathHelper.floor(newEntity.posZ / 16.0D);
				event.getWorld().getChunkFromChunkCoords(i, j).addEntity(newEntity);
				event.getWorld().loadedEntityList.add(newEntity);
				event.getWorld().onEntityAdded(newEntity);
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
