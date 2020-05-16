package sonar.fluxnetworks.client;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.ILightReader;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.utils.FluxConfigurationType;
import sonar.fluxnetworks.client.gui.basic.GuiFluxCore;
import sonar.fluxnetworks.client.mui.NavigationHome;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.common.handler.PacketHandler;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import sonar.fluxnetworks.common.network.NetworkColourRequestPacket;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Render network color on blocks and items.*/
public class FluxColorHandler implements IBlockColor, IItemColor {

    public static final FluxColorHandler INSTANCE = new FluxColorHandler();

    public static final int DEFAULT_COLOR = FluxUtils.getIntFromColor(41, 94, 138);
    public static final int NO_NETWORK_COLOR = FluxUtils.getIntFromColor(178, 178, 178);
    public static final Map<Integer, Integer> colorCache = new HashMap<>();
    public static final Map<Integer, String> nameCache = new HashMap<>();
    private static List<Integer> requests = new ArrayList<>();
    private static List<Integer> sent_requests = new ArrayList<>();

    public static void reset() {
        colorCache.clear();
        nameCache.clear();
        requests.clear();
    }

    public static void loadColorCache(int id, int color) {
        if(id != -1) {
            colorCache.put(id, color);
        }
    }

    public static void loadNameCache(int id, String name) {
        if(id != -1) {
            nameCache.put(id, name);
        }
    }

    public static void placeRequest(int id) {
        if(id != -1 && !requests.contains(id) && !sent_requests.contains(id)){
            requests.add(id);
        }
    }

    public static int getOrRequestNetworkColor(int id) {
        if(id == -1){
            return NO_NETWORK_COLOR;
        }
        Integer cached = colorCache.get(id);
        if(cached != null){
            return cached;
        }
        placeRequest(id);
        return NO_NETWORK_COLOR;
    }

    public static String getOrRequestNetworkName(int id) {
        if(id == -1){
            return "NONE";
        }
        String cached = nameCache.get(id);
        if(cached != null){
            return cached;
        }
        placeRequest(id);
        return "WAITING FOR SERVER";
    }

    public static int tickCount;

    public static void sendRequests() {
        if(!requests.isEmpty()){
            tickCount++;
            if(tickCount > 40){
                tickCount = 0;
                PacketHandler.INSTANCE.sendToServer(new NetworkColourRequestPacket(Lists.newArrayList(requests)));
                sent_requests.addAll(requests);
                requests.clear();
            }
        }
    }

    public static void receiveCache(Map<Integer, Tuple<Integer, String>> cache) {
        cache.forEach((ID,DETAILS) -> {
            loadColorCache(ID, DETAILS.getA());
            loadNameCache(ID, DETAILS.getB());
            sent_requests.remove(ID);
            requests.remove(ID);
        });
    }

    @Override
    public int getColor(BlockState state, @Nullable ILightReader world, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex == 1 && pos != null && world != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileFluxCore) {
                TileFluxCore t = (TileFluxCore) tile;
                if(t.getNetworkID() == -1){
                    return NO_NETWORK_COLOR;
                }
                return FluxUtils.getBrighterColor(t.color, 1.2);
            }
            return DEFAULT_COLOR;
        }
        return -1;
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            if (stack.hasTag() && stack.getTag().getBoolean(FluxUtils.GUI_COLOR)) {
                if (FluxConfig.enableGuiDebug && FluxNetworks.modernUILoaded) {
                    return NavigationHome.network.isInvalid() ? NO_NETWORK_COLOR : NavigationHome.network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
                }
                Screen screen = Minecraft.getInstance().currentScreen;
                if (screen instanceof GuiFluxCore) {
                    GuiFluxCore guiFluxCore = (GuiFluxCore) screen;
                    return guiFluxCore.network.isInvalid() ? NO_NETWORK_COLOR : guiFluxCore.network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
                }
            }
            CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
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
                GuiFluxCore guiFluxCore = (GuiFluxCore)screen;
                if(guiFluxCore.connector instanceof FluxConfiguratorItem.ContainerProvider){
                    return guiFluxCore.network.getSetting(NetworkSettings.NETWORK_COLOR);
                }
            }
            CompoundNBT tag = stack.getChildTag(FluxUtils.CONFIGS_TAG);
            if (tag != null) {
                return getOrRequestNetworkColor(tag.getInt(FluxConfigurationType.NETWORK.getNBTName()));
            }
            return NO_NETWORK_COLOR;
        }
        return -1;
    }
}
