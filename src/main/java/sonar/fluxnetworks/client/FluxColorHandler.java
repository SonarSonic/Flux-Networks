package sonar.fluxnetworks.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.FluxConstants;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Render network color on blocks and items.
 */
//FIXME
@OnlyIn(Dist.CLIENT)
public class FluxColorHandler implements BlockColor, ItemColor {

    public static final FluxColorHandler INSTANCE = new FluxColorHandler();

    /*private final Int2IntMap colorMap = new Int2IntArrayMap();

    private final Int2ObjectMap<String> nameMap = new Int2ObjectArrayMap<>();

    private final List<Integer> requests = new ArrayList<>();
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
    }*/

    @Override
    public int getColor(@Nonnull BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos,
                        int tintIndex) {
        // called when renderer updated
        if (tintIndex == 1 && pos != null && world != null) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileFluxDevice) {
                /*TileFluxDevice t = (TileFluxDevice) tile;
                if (t.getNetworkID() == -1) {
                    return NO_NETWORK_COLOR;
                }*/
                return ((TileFluxDevice) tile).mClientColor;
            }
            return EnumNetworkColor.BLUE.getRGB();
        }
        return ~0;
    }

    @Override
    public int getColor(@Nonnull ItemStack stack, int tintIndex) {
        // called every frame
        if (tintIndex == 1) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.getBoolean(FluxConstants.FLUX_COLOR)) {
                /*if (FluxConfig.enableGuiDebug && FluxNetworks.modernUILoaded) {
                    return NavigationHome.network.isInvalid() ? NO_NETWORK_COLOR : NavigationHome.network.getSetting
                    (NetworkSettings.NETWORK_COLOR) | 0xff000000;
                }*/
                /*Screen screen = Minecraft.getInstance().currentScreen;
                if (screen instanceof GuiFluxCore) {
                    GuiFluxCore gui = (GuiFluxCore) screen;
                    return gui.network.getNetworkColor();
                }*/
            }
            tag = stack.getTagElement(FluxConstants.TAG_FLUX_DATA);
            if (tag != null) {
                return FluxClientCache.getNetwork(tag.getInt(FluxConstants.NETWORK_ID)).getNetworkColor();
            }
            return FluxConstants.INVALID_NETWORK_COLOR;
        }
        return ~0;
    }

    public static int colorMultiplierForConfigurator(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            /*Screen screen = Minecraft.getInstance().currentScreen;
            if (screen instanceof GuiFluxCore) {
                GuiFluxCore gui = (GuiFluxCore) screen;
                if (gui.getContainer().bridge instanceof ItemFluxConfigurator.MenuBridge) {
                    return gui.network.getNetworkColor();
                }
            }*/
            CompoundTag tag = stack.getTagElement(FluxConstants.TAG_FLUX_CONFIG);
            if (tag != null) {
                return FluxClientCache.getNetwork(tag.getInt(FluxConstants.NETWORK_ID)).getNetworkColor();
            }
            return FluxConstants.INVALID_NETWORK_COLOR;
        }
        return ~0;
    }
}
