package sonar.flux.client;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import sonar.core.common.block.SonarBlock;
import sonar.core.helpers.FontHelper;
import sonar.flux.FluxNetworks;
import sonar.flux.api.IFluxItemGui;
import sonar.flux.api.network.IFluxNetwork;
import sonar.flux.client.gui.GuiTabAbstract;
import sonar.flux.common.block.FluxConnection;
import sonar.flux.common.item.ItemNetworkConnector;
import sonar.flux.common.tileentity.TileFlux;
import sonar.flux.connection.NetworkSettings;
import sonar.flux.network.PacketColourRequest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluxColourHandler implements IBlockColor, IItemColor {

    public static final FluxColourHandler INSTANCE = new FluxColourHandler();
    public static final int DEFAULT_COLOUR = FontHelper.getIntFromColor(41, 94, 138);
    public static final int NO_NETWORK_COLOUR = FontHelper.getIntFromColor(178, 178, 178);
    public static final Map<Integer, Integer> colourCache = new HashMap<>();
    public static final Map<Integer, String> nameCache = new HashMap<>();
    private static List<Integer> requests = new ArrayList<>();
    private static List<Integer> sent_requests = new ArrayList<>();

    public static void reset(){
        colourCache.clear();
        nameCache.clear();
        requests.clear();
    }

    public static void loadColourCache(int id, int colour){
        if(id != -1) colourCache.put(id, colour);
    }

    public static void loadNameCache(int id, String name){
        if(id != -1) nameCache.put(id, name);
    }

    public static void placeRequest(int id){
        if(id != -1 && !requests.contains(id) && !sent_requests.contains(id)){
            requests.add(id);
        }
    }

    public static int getOrRequestNetworkColour(int id){
        if(id == -1){
            return NO_NETWORK_COLOUR;
        }
        Integer cached = colourCache.get(id);
        if(cached != null){
            return cached;
        }
        placeRequest(id);
        return NO_NETWORK_COLOUR;
    }

    public static String getOrRequestNetworkName(int id){
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

    public static void sendRequests(){
        if(!requests.isEmpty()){
            tickCount++;
            if(tickCount > 40){
                tickCount = 0;
                FluxNetworks.network.sendToServer(new PacketColourRequest(Lists.newArrayList(requests)));
                sent_requests.addAll(requests);
                requests = new ArrayList<>();
            }
        }
    }

    public static void receiveCache(Map<Integer, Tuple<Integer, String>> cache){
        cache.forEach((ID,DETAILS)->{
            loadColourCache(ID, DETAILS.getFirst());
            loadNameCache(ID, DETAILS.getSecond());
            sent_requests.remove(ID);
            requests.remove(ID);
        });
    }

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex == 1 && pos != null) {
            TileEntity tile = world.getTileEntity(pos);
            if (!state.getValue(FluxConnection.CONNECTED)) {
                return NO_NETWORK_COLOUR;
            }
            if (tile instanceof TileFlux) {
                TileFlux plug = (TileFlux) tile;
                int colour = FluxColourHandler.getOrRequestNetworkColour(plug.getNetworkID());
                int red = (colour >> 16) & 0x000000FF;
                int green = (colour >> 8) & 0x000000FF;
                int blue = (colour) & 0x000000FF;
                return FontHelper.getIntFromColor((int) Math.min(red * 1.5, 255), (int) Math.min(green * 1.5, 255), (int) Math.min(blue * 1.5, 255));
            }
            return DEFAULT_COLOUR;
        }
        return -1;
    }

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("gui_colour")) {
                Object screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof GuiTabAbstract) {
                    IFluxNetwork common = ((GuiTabAbstract) screen).common;
                    return common.isFakeNetwork() ? NO_NETWORK_COLOUR : common.getSetting(NetworkSettings.NETWORK_COLOUR).getRGB();
                }
            }
            NBTTagCompound tag = stack.getSubCompound(SonarBlock.DROP_TAG_NAME);
            if (tag != null) {
                return FluxColourHandler.getOrRequestNetworkColour(tag.getInteger(ItemNetworkConnector.NETWORK_ID_TAG));
            }
            return NO_NETWORK_COLOUR;
        }
        return -1;
    }

    public static int itemViewerMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex == 1 && stack.getItem() instanceof IFluxItemGui) {
            int networkID = ((IFluxItemGui)stack.getItem()).getViewingNetworkID(stack);
            return FluxColourHandler.getOrRequestNetworkColour(networkID);
        }
        return -1;
    }
}
