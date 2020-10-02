package sonar.fluxnetworks.client;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.misc.FluxConfigurationType;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.network.NetworkColourRequestPacket;
import sonar.fluxnetworks.common.storage.FluxNetworkData;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Render network color on blocks and items.
 */
//TODO use flux networks cache
public class FluxColorHandler implements IBlockColor, IItemColor {

    public static final FluxColorHandler INSTANCE = new FluxColorHandler();

    public static final int DEFAULT_COLOR    = FluxUtils.getIntFromColor(41, 94, 138);
    public static final int NO_NETWORK_COLOR = FluxUtils.getIntFromColor(178, 178, 178);

    private final Int2IntMap colorMap = new Int2IntArrayMap();

    private final Int2ObjectMap<String> nameMap = new Int2ObjectArrayMap<>();

    private final List<Integer> requests     = new ArrayList<>();
    private final List<Integer> sentRequests = new ArrayList<>();

    {
        colorMap.defaultReturnValue(-1);
    }

    public void reset() {
        colorMap.clear();
        nameMap.clear();
        requests.clear();
    }

    public void loadColorCache(int id, int color) {
        if (id != -1) {
            colorMap.put(id, color);
        }
    }

    public void loadNameCache(int id, String name) {
        if (id != -1) {
            nameMap.put(id, name);
        }
    }

    public void placeRequest(int id) {
        if (id != -1 && !requests.contains(id) && !sentRequests.contains(id)) {
            requests.add(id);
        }
    }

    public int getOrRequestNetworkColor(int id) {
        if (id == -1) {
            return NO_NETWORK_COLOR;
        }
        int cached = colorMap.get(id);
        if (cached != -1) {
            return cached;
        }
        placeRequest(id);
        return NO_NETWORK_COLOR;
    }

    public String getOrRequestNetworkName(int id) {
        if (id == -1) {
            return "NONE";
        }
        String cached = nameMap.get(id);
        if (cached != null) {
            return cached;
        }
        placeRequest(id);
        return "WAITING FOR SERVER";
    }

    public int tickCount;

    public void tick() {
        if (!requests.isEmpty()) {
            tickCount++;
            if (tickCount > 10) {
                tickCount = 0;
                PacketHandler.CHANNEL.sendToServer(new NetworkColourRequestPacket(Lists.newArrayList(requests)));
                sentRequests.addAll(requests);
                requests.clear();
            }
        }
    }

    public void receiveCache(@Nonnull Map<Integer, Tuple<Integer, String>> cache) {
        cache.forEach((id, colorToName) -> {
            loadColorCache(id, colorToName.getA());
            loadNameCache(id, colorToName.getB());
            sentRequests.remove(id);
            requests.remove(id);
        });
    }

    @Override
    public int getColor(@Nonnull BlockState state, @Nullable IBlockDisplayReader world, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex == 1 && pos != null && world != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileFluxDevice) {
                TileFluxDevice t = (TileFluxDevice) tile;
                if (t.getNetworkID() == -1) {
                    return NO_NETWORK_COLOR;
                }
                return FluxUtils.getBrighterColor(t.color, 1.2f);
            }
            return DEFAULT_COLOR;
        }
        return ~0;
    }

    @Override
    public int getColor(@Nonnull ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            CompoundNBT tag = stack.getTag();
            if (tag != null && tag.getBoolean(FluxUtils.GUI_COLOR)) {
                /*if (FluxConfig.enableGuiDebug && FluxNetworks.modernUILoaded) {
                    return NavigationHome.network.isInvalid() ? NO_NETWORK_COLOR : NavigationHome.network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
                }*/
                Screen screen = Minecraft.getInstance().currentScreen;
                if (screen instanceof GuiFluxCore) {
                    GuiFluxCore guiFluxCore = (GuiFluxCore) screen;
                    return !guiFluxCore.network.isValid() ? NO_NETWORK_COLOR : guiFluxCore.network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
                }
            }
            tag = stack.getChildTag(FluxUtils.FLUX_DATA);
            if (tag != null) {
                return getOrRequestNetworkColor(tag.getInt(FluxNetworkData.NETWORK_ID));
            }
            return NO_NETWORK_COLOR;
        }
        return -1;
    }

    public static int colorMultiplierForConfigurator(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            Screen screen = Minecraft.getInstance().currentScreen;
            if (screen instanceof GuiFluxCore) {
                GuiFluxCore guiFluxCore = (GuiFluxCore) screen;
                if (guiFluxCore.connector instanceof ItemFluxConfigurator.ContainerProvider) {
                    return guiFluxCore.network.getSetting(NetworkSettings.NETWORK_COLOR);
                }
            }
            CompoundNBT tag = stack.getChildTag(FluxUtils.CONFIGS_TAG);
            if (tag != null) {
                return INSTANCE.getOrRequestNetworkColor(tag.getInt(FluxConfigurationType.NETWORK.getNBTName()));
            }
            return NO_NETWORK_COLOR;
        }
        return -1;
    }
}
