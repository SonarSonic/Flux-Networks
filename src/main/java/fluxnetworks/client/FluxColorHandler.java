package fluxnetworks.client;

import com.google.common.collect.Lists;
import fluxnetworks.client.gui.GuiFluxCore;
import fluxnetworks.common.block.BlockFluxCore;
import fluxnetworks.common.connection.NetworkSettings;
import fluxnetworks.common.handler.PacketHandler;
import fluxnetworks.common.network.PacketColorRequest;
import fluxnetworks.common.tileentity.TileFluxCore;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Render network color on blocks and items.
 */
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
        if(id != -1) colorCache.put(id, color);
    }

    public static void loadNameCache(int id, String name) {
        if(id != -1) nameCache.put(id, name);
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
                PacketHandler.network.sendToServer(new PacketColorRequest.ColorRequestMessage(Lists.newArrayList(requests)));
                sent_requests.addAll(requests);
                requests = new ArrayList<>();
            }
        }
    }

    public static void receiveCache(Map<Integer, Tuple<Integer, String>> cache) {
        cache.forEach((ID,DETAILS) -> {
            loadColorCache(ID, DETAILS.getFirst());
            loadNameCache(ID, DETAILS.getSecond());
            sent_requests.remove(ID);
            requests.remove(ID);
        });
    }

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex == 1 && pos != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (!state.getValue(BlockFluxCore.CONNECTED)) {
                return NO_NETWORK_COLOR;
            }
            if (tile instanceof TileFluxCore) {
                TileFluxCore t = (TileFluxCore) tile;
                return FluxUtils.getBrighterColor(t.color, 1.2);
            }
            return DEFAULT_COLOR;
        }
        return -1;
    }

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("GuiColor")) {
                Gui screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof GuiFluxCore) {
                    GuiFluxCore guiFluxCore = (GuiFluxCore) screen;
                    return guiFluxCore.network.isInvalid() ? NO_NETWORK_COLOR : guiFluxCore.network.getSetting(NetworkSettings.NETWORK_COLOR) | 0xff000000;
                }
            }
            NBTTagCompound tag = stack.getSubCompound("fluxData");
            if (tag != null) {
                return getOrRequestNetworkColor(tag.getInteger("NetworkID"));
            }
            return NO_NETWORK_COLOR;
        }
        return -1;
    }
}
