package sonar.flux;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class FluxConfig extends FluxNetworks {

	public static long defaultLimit;
	public static boolean banHyper, banGod;
	public static int basicCapacity, herculeanCapacity, gargantuanCapacity;
	public static int basicTransfer, herculeanTransfer, gargantuanTransfer;
	public static int hyper = 4, god = 10;

	public static void initConfiguration(FMLPreInitializationEvent event) {
		loadMainConfig();
	}

	public static void loadMainConfig() {
		Configuration config = new Configuration(new File("config/flux_networks.cfg"));
		config.load();
		defaultLimit = (long)config.getFloat("Default Transfer Limit", "energy", 256000, 0, Long.MAX_VALUE, "the default transfer limit of a flux connection");
		
		basicCapacity = config.getInt("Basic Storage Capacity", "energy", 256000, 0, Integer.MAX_VALUE, "the storage capacity of Basic Flux Storage");
		herculeanCapacity = config.getInt("Herculean Storage Capacity", "energy", 12800000, 0, Integer.MAX_VALUE, "the storage capacity of Herculean Flux Storage");
		gargantuanCapacity = config.getInt("Gargantuan Storage Capacity", "energy", 128000000, 0, Integer.MAX_VALUE, "the storage capacity of Gargantuan Flux Storage");
		
		basicTransfer = config.getInt("Basic Storage Transfer", "energy", 6400, 0, Integer.MAX_VALUE, "the transfer rate of Basic Flux Storage");
		herculeanTransfer = config.getInt("Herculean Storage Transfer", "energy", 12800, 0, Integer.MAX_VALUE, "the transfer rate of Herculean Flux Storage");
		gargantuanTransfer = config.getInt("Gargantuan Storage Transfer", "energy", 256000, 0, Integer.MAX_VALUE, "the transfer rate of Gargantuan Flux Storage");
		
		hyper = config.getInt("Hyper Mode Multiplier", "energy", 4, 1, 16, "the multiplier for hyper mode - for how much energy is transfer compared to normal rate");
		god = config.getInt("God Mode Multiplier", "energy", 10, 1, 16, "the multiplier god mod - for how much energy is transfer compared to normal rate");

		banHyper = config.getBoolean("Ban Hyper Mode", "settings", false, "prevents the use of Hyper Mode");
		banGod = config.getBoolean("Ban God Mode", "settings", false, "prevents the use of God Mode");
		
		config.save();

	}

}
