package fluxnetworks;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class FluxConfig {

    public static Configuration config;

    public static String GENERAL = "general";
    public static String ENERGY = "energy";
    public static String NETWORKS = "networks";

    public static boolean enableFluxRecipe;
    public static int defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer, gargantuanCapacity, gargantuanTransfer;
    public static int maximumPerPlayer;

    public static void init(File file) {
        config = new Configuration(new File(file.getPath(), "flux_networks.cfg"));
        config.load();
        read();
        config.save();
    }

    public static void read() {
        defaultLimit = config.getInt("Default Transfer Limit", ENERGY, 800000, 0, Integer.MAX_VALUE, "the default transfer limit of a flux connector");

        basicCapacity = config.getInt("Basic Storage Capacity", ENERGY, 800000, 0, Integer.MAX_VALUE, "");
        basicTransfer = config.getInt("Basic Storage Transfer", ENERGY, 8000, 0, Integer.MAX_VALUE, "");
        herculeanCapacity = config.getInt("Herculean Storage Capacity", ENERGY, 6400000, 0, Integer.MAX_VALUE, "");
        herculeanTransfer = config.getInt("Herculean Storage Transfer", ENERGY, 48000, 0, Integer.MAX_VALUE, "");
        gargantuanCapacity = config.getInt("Gargantuan Storage Capacity", ENERGY, 51200000, 0, Integer.MAX_VALUE, "");
        gargantuanTransfer = config.getInt("Gargantuan Storage Transfer", ENERGY, 288000, 0, Integer.MAX_VALUE, "");

        maximumPerPlayer = config.getInt("Maximum Networks Per Player", NETWORKS, 3, -1, Integer.MAX_VALUE, "-1 = no limit");

        enableFluxRecipe = config.getBoolean("Enable Flux Recipe", GENERAL, true, "enables redstone being compressed with the bedrock and obsidian");
    }
}
