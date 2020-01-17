package icyllis.fluxnetworks.system.util;

import icyllis.fluxnetworks.api.util.EnergyType;
import icyllis.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Optional;

public class FluxUtils {

    public static Direction getBlockDirection(BlockPos pos, BlockPos other) {
        float dx = other.getX() - pos.getX();
        float dy = other.getY() - pos.getY();
        float dz = other.getZ() - pos.getZ();
        return Direction.getFacingFromVector(dx, dy, dz);
    }

    public static Optional<TileFluxCore> getFluxTE(IWorldReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return Optional.ofNullable((te instanceof TileFluxCore) ? (TileFluxCore) te : null);
    }

    public static <T> boolean addWithCheck(Collection<T> list, T toAdd) {
        if(toAdd != null && !list.contains(toAdd)) {
            list.add(toAdd);
            return true;
        }
        return false;
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

}
