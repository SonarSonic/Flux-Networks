package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.IGuiItem;
import sonar.core.utils.IGuiTile;
import sonar.core.utils.SonarCompat;
import sonar.flux.FluxNetworks;

public class FluxCommon implements IGuiHandler {

	public void registerRenderThings() {
	}

	public static void registerPackets() {
		FluxNetworks.network.registerMessage(PacketFluxButton.Handler.class, PacketFluxButton.class, 0, Side.SERVER);
		FluxNetworks.network.registerMessage(PacketFluxNetworkList.Handler.class, PacketFluxNetworkList.class, 1, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketFluxConnectionsList.Handler.class, PacketFluxConnectionsList.class, 2, Side.CLIENT);
		FluxNetworks.network.registerMessage(PacketFluxError.Handler.class, PacketFluxError.class, 3, Side.CLIENT);
        FluxNetworks.network.registerMessage(PacketConfiguratorSettings.Handler.class, PacketConfiguratorSettings.class, 4, Side.SERVER);
        FluxNetworks.network.registerMessage(PacketNetworkStatistics.Handler.class, PacketNetworkStatistics.class, 5, Side.CLIENT);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
		if (entity != null) {

			if (entity instanceof TileEntitySonar) {
				((TileEntitySonar) entity).forceNextSync();
			}
			switch (ID) {
			case IGuiTile.ID:
				return ((IGuiTile) entity).getGuiContainer(player);
			case 2:
				break;
			}
		} else {
			ItemStack equipped = player.getHeldItemMainhand();
			if (!SonarCompat.isEmpty(equipped)) {
				switch (ID) {
				case IGuiItem.ID:
					return ((IGuiItem) equipped.getItem()).getGuiContainer(player, equipped);
				}
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
		if (entity != null) {
			switch (ID) {
			case IGuiTile.ID:
				return ((IGuiTile) entity).getGuiScreen(player);
			}
		} else {
			ItemStack equipped = player.getHeldItemMainhand();
			if (!SonarCompat.isEmpty(equipped)) {
				switch (ID) {
				case IGuiItem.ID:
					return ((IGuiItem) equipped.getItem()).getGuiScreen(player, equipped);
				}
			}
		}

		return null;
	}
}
