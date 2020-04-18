package sonar.fluxnetworks.common.core;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.network.EnumConnectionType;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.api.utils.FluxConfigurationType;
import sonar.fluxnetworks.api.network.FluxCacheTypes;
import sonar.fluxnetworks.api.network.IFluxNetwork;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.client.gui.button.FluxTextWidget;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.common.item.FluxConnectorBlockItem;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.UUID;

public class FluxUtils {

    public static final String FLUX_DATA = "FluxData";
    public static final String GUI_COLOR = "GuiColor";
    public static final String CONFIGS_TAG = "Configs";

    public static UUID UUID_DEFAULT = new UUID(-1, -1);

    public static <E extends Enum<?>> E incrementEnum(E enumObj, E[] values) {
        int ordinal = enumObj.ordinal() + 1;
        if (ordinal < values.length) {
            return values[ordinal];
        } else {
            return values[0];
        }
    }

    @Nullable
    public static Direction getBlockDirection(BlockPos pos, BlockPos other) {
        for(Direction face : Direction.values()) {
            if(pos.offset(face).equals(other))
                return face;
        }
        return null;
    }

    public static String getTransferInfo(EnumConnectionType type, EnergyType energyType, long change) {
        if(type.canAddEnergy()) {
            String b = FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            if(change == 0) {
                return FluxTranslate.INPUT.t() + ": " + TextFormatting.GOLD + b;
            } else {
                return FluxTranslate.INPUT.t() + ": " + TextFormatting.GREEN + "+" + b;
            }
        }
        if(type.canRemoveEnergy() || type.isController()) {
            String b = FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            if(change == 0) {
                return FluxTranslate.OUTPUT.t() + ": " + TextFormatting.GOLD + b;
            } else {
                return FluxTranslate.OUTPUT.t() + ": " + TextFormatting.RED + b;
            }
        }
        if(type == EnumConnectionType.STORAGE) {
            if(change == 0) {
                return FluxTranslate.CHANGE.t() + ": " + TextFormatting.GOLD + change + energyType.getUsageSuffix();
            } else if(change > 0) {
                return FluxTranslate.CHANGE.t() + ": " + TextFormatting.GREEN + "+" + FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            } else {
                return FluxTranslate.CHANGE.t() + ": " + TextFormatting.RED + FluxUtils.format(change, FluxUtils.TypeNumberFormat.COMMAS, energyType, true);
            }
        }
        return "";
    }

    /*public static int getPlayerXP(EntityPlayer player) {
        return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    public static void addPlayerXP(EntityPlayer player, int amount) {
        int experience = getPlayerXP(player) + amount;
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
    }

    public static boolean removePlayerXP(EntityPlayer player, int amount) {
        if(getPlayerXP(player) >= amount) {
            addPlayerXP(player, -amount);
            return true;
        }
        return false;
    }

    public static int xpBarCap(int level) {
        if (level >= 30)
            return 112 + (level - 30) * 9;

        if (level >= 15)
            return 37 + (level - 15) * 5;

        return 7 + level * 2;
    }

    private static int sum(int n, int a0, int d) {
        return n * (2 * a0 + (n - 1) * d) / 2;
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) return 0;
        if (level <= 15) return sum(level, 7, 2);
        if (level <= 30) return 315 + sum(level - 15, 37, 5);
        return 1395 + sum(level - 30, 112, 9);
    }

    public static int getLevelForExperience(int targetXp) {
        int level = 0;
        while (true) {
            final int xpToNextLevel = xpBarCap(level);
            if (targetXp < xpToNextLevel) return level;
            level++;
            targetXp -= xpToNextLevel;
        }
    }*/

    public static <T> boolean addWithCheck(Collection<T> list, T toAdd) {
        if(toAdd != null && !list.contains(toAdd)) {
            list.add(toAdd);
            return true;
        }
        return false;
    }

    public static ItemStack getBlockItem(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return new ItemStack(Item.getItemFromBlock(state.getBlock()));
    }


    public static boolean addConnection(IFluxConnector fluxConnector) {
        if(fluxConnector.getNetworkID() != -1) {
            IFluxNetwork network = FluxNetworkCache.instance.getNetwork(fluxConnector.getNetworkID());
            if(!network.isInvalid()) {
                if(fluxConnector.getConnectionType().isController() && network.getConnections(FluxCacheTypes.controller).size() > 0) {
                    return false;
                }
                network.queueConnectionAddition(fluxConnector);
                return true;
            }
        }
        return false;
    }

    public static void removeConnection(IFluxConnector fluxConnector, boolean isChunkUnload) {
        if(fluxConnector.getNetworkID() != -1) {
            IFluxNetwork network = FluxNetworkCache.instance.getNetwork(fluxConnector.getNetworkID());
            if(!network.isInvalid()) {
                network.queueConnectionRemoval(fluxConnector, isChunkUnload);
                return;
            }
        }
    }

    public static int getIntFromColor(int red, int green, int blue) {
        red = red << 16 & 0x00FF0000;
        green = green << 8 & 0x0000FF00;
        blue = blue & 0x000000FF;

        return 0xFF000000 | red | green | blue;
    }

    public static int getBrighterColor(int color, double index) {
        int red = (color >> 16) & 0x000000FF;
        int green = (color >> 8) & 0x000000FF;
        int blue = (color) & 0x000000FF;
        return getIntFromColor((int) Math.min(red * index, 255), (int) Math.min(green * index, 255), (int) Math.min(blue * index, 255));
    }

    public enum TypeNumberFormat {
        FULL,                   // Full format
        COMPACT,                // Compact format (like 3.5M)
        COMMAS,                 // Language dependent comma separated format
        NONE                    // No output (empty string)
    }

    public static String format(long in, TypeNumberFormat style, String suffix) {
        switch (style) {
            case FULL:
                return in + suffix;
            case COMPACT: {
                int unit = 1000;
                if (in < unit) {
                    return in + " " + suffix;
                }
                int exp = (int) (Math.log(in) / Math.log(unit));
                char pre;
                if (suffix.startsWith("m")) {
                    suffix = suffix.substring(1);
                    if (exp - 2 >= 0) {
                        pre = "kMGTPE".charAt(exp - 2);
                        return String.format("%.1f%s", in / Math.pow(unit, exp), pre) + suffix;
                    } else {
                        return String.format("%.1f%s", in / Math.pow(unit, exp), suffix);
                    }
                } else {
                    pre = "kMGTPE".charAt(exp - 1);
                    return String.format("%.1f%s", in / Math.pow(unit, exp), pre) + suffix;
                }
            }
            case COMMAS:
                return NumberFormat.getInstance().format(in) + suffix;
            case NONE:
                return suffix;
        }
        return Long.toString(in);
    }

    public static String format(long in, TypeNumberFormat style, EnergyType energy, boolean usage) {
        if(energy == EnergyType.EU) {
            return format(in / 4, style, usage ? energy.getUsageSuffix() : energy.getStorageSuffix());
        }
        return format(in, style, usage ? energy.getUsageSuffix() : energy.getStorageSuffix());
    }

    public static boolean checkPassword(String str) {
        for(int i = 0; i < str.length(); i++) {
            if(!Character.isLetterOrDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    public static CompoundNBT copyConfiguration(TileFluxCore flux, CompoundNBT config) {
        for(FluxConfigurationType type : FluxConfigurationType.VALUES){
            type.copy.copyFromTile(config, type.getNBTName(), flux);
        }
        return config;
    }

    public static void pasteConfiguration(TileFluxCore flux, CompoundNBT config) {
        for(FluxConfigurationType type : FluxConfigurationType.VALUES){
            if(config.contains(type.getNBTName())) {
                type.paste.pasteToTile(config, type.getNBTName(), flux);
            }
        }
    }

    public static CompoundNBT getBatchEditingTag(FluxTextWidget a, FluxTextWidget b, FluxTextWidget c, SlidedSwitchButton d, SlidedSwitchButton e, SlidedSwitchButton f) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString(FluxConnectorBlockItem.CUSTOM_NAME, a.getText());
        tag.putInt(FluxConnectorBlockItem.PRIORITY, b.getIntegerFromText(false));
        tag.putLong(FluxConnectorBlockItem.LIMIT, c.getLongFromText(true));
        tag.putBoolean(FluxConnectorBlockItem.SURGE_MODE, d != null && d.slideControl);
        tag.putBoolean(FluxConnectorBlockItem.DISABLE_LIMIT, e != null && e.slideControl);
        tag.putBoolean("chunkLoad", f != null && f.slideControl);
        return tag;
    }

}
