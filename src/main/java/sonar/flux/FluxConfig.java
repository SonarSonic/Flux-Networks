package sonar.flux;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.utils.Pair;

public class FluxConfig extends FluxNetworks {

	public static long defaultLimit;
	public static boolean banHyper, banGod;
	public static boolean enableFluxRecipe;
	public static boolean enableFluxRedstoneDrop;
	public static int redstone_ore_chance, redstone_ore_max_drop, redstone_ore_min_drop;
	public static int basicCapacity, herculeanCapacity, gargantuanCapacity;
	public static int basicTransfer, herculeanTransfer, gargantuanTransfer;
	public static int maximum_per_player;
	public static int hyper = 4, god = 10;

	public static Map<EnergyType, Pair<Boolean, Boolean>> transfer_types = new HashMap<>();
	public static Map<EnergyType, List<EnergyType>> conversion = new HashMap<>();
	public static Map<EnergyType, List<EnergyType>> conversion_override = new HashMap<>();
	public static Map<EnergyType, List<EnergyType>> default_conversion_overrides = new HashMap<>();
	public static Configuration config;
	
	static{
		default_conversion_overrides.put(EnergyType.FE, Lists.newArrayList(EnergyType.RF, EnergyType.TESLA));
		default_conversion_overrides.put(EnergyType.RF, Lists.newArrayList(EnergyType.FE, EnergyType.TESLA));
		default_conversion_overrides.put(EnergyType.TESLA, Lists.newArrayList(EnergyType.FE, EnergyType.RF));
	}

	public static void startLoading() {
		config = new Configuration(new File("config/flux_networks.cfg"));
		config.load();
		defaultLimit = (long) config.getFloat("Default Transfer Limit", "energy", 256000, 0, Long.MAX_VALUE, "the default transfer limit of a flux connection");

		maximum_per_player = config.getInt("Maximum Networks Per Player", "networks", -1, -1, Integer.MAX_VALUE, "-1 = no limit");
		basicCapacity = config.getInt("Basic Storage Capacity", "energy", 256000, 0, Integer.MAX_VALUE, "");
		basicTransfer = config.getInt("Basic Storage Transfer", "energy", 6400, 0, Integer.MAX_VALUE, "");
		herculeanCapacity = config.getInt("Herculean Storage Capacity", "energy", 12800000, 0, Integer.MAX_VALUE, "");
		herculeanTransfer = config.getInt("Herculean Storage Transfer", "energy", 12800, 0, Integer.MAX_VALUE, "");
		gargantuanCapacity = config.getInt("Gargantuan Storage Capacity", "energy", 128000000, 0, Integer.MAX_VALUE, "");
		gargantuanTransfer = config.getInt("Gargantuan Storage Transfer", "energy", 256000, 0, Integer.MAX_VALUE, "");

		hyper = config.getInt("Hyper Mode Multiplier", "energy", 4, 1, 16, "the multiplier for hyper mode - for how much energy is transfer compared to normal rate");
		god = config.getInt("God Mode Multiplier", "energy", 10, 1, 16, "the multiplier god mod - for how much energy is transfer compared to normal rate");

		banHyper = config.getBoolean("Ban Hyper Mode", "settings", false, "prevents the use of Hyper Mode");
		banGod = config.getBoolean("Ban God Mode", "settings", false, "prevents the use of God Mode");
		enableFluxRecipe = config.getBoolean("Disables Flux Recipe (from fire)", "flux_recipe", true, "enables Redstone being turned into Flux when dropped in fire");

		enableFluxRedstoneDrop = config.getBoolean("Enable Flux Drop (from Redstone Ore)", "flux_recipe", true, "enables Redstone Ore to drop Flux with normal redstone drops");
		redstone_ore_chance = config.getInt("Chance of Flux Drop (from Redstone Ore)", "flux_recipe", 50, 1, 5000, "the chance of a drop occurring (random, but roughly every 50 blocks)");
		redstone_ore_min_drop = config.getInt("Minimum Flux Drop (from Redstone Ore)", "flux_recipe", 4, 1, 64, "the minimum Flux dropped from Redstone ore if a drop occurs");
		redstone_ore_max_drop = config.getInt("Maximum Flux Drop (from Redstone Ore)", "flux_recipe", 16, 1, 64, "the maximum Flux dropped from Redstone Ore if a drop occurs");
		config.save();
	}

	public static void finishLoading() {
		config.addCustomCategoryComment("energy types", "'Convert' - if conversion is allowed when 'allow conversion' is toggled for the network" + Configuration.NEW_LINE +//
				"'Override' - for overriding the 'allow conversion' toggle (this should be used for energy types which should be treated as equal, RF/FE/TESLA)" + Configuration.NEW_LINE);
		
		// toggle block/item transfer
		for (EnergyType type : SonarCore.energyTypes.getObjects()) {
			boolean item, block;
			block = getSimplifiedBoolean(type.getName() + " Transfer: Blocks", "energy types", true);
			item = getSimplifiedBoolean(type.getName() + " Transfer: Items", "energy types", true);
			transfer_types.put(type, new Pair(item, block));
		}
		for (EnergyType type : SonarCore.energyTypes.getObjects()) {
			List<EnergyType> converts = new ArrayList<>();
			List<EnergyType> overrides = new ArrayList<>();
			String category = "energy types: " + type.getName() + " [" + type.getStorageSuffix() + "]";
			for (EnergyType convert : SonarCore.energyTypes.getObjects()) {
				if (type != convert) {
					boolean enableConvert = getSimplifiedBoolean("Convert: " + type.getStorageSuffix() + " to " + convert.getStorageSuffix(), category, true);
					boolean overrideConvert = getSimplifiedBoolean("Override: " + type.getStorageSuffix() + " to " + convert.getStorageSuffix(), category, default_conversion_overrides.getOrDefault(type, new ArrayList()).contains(convert));
					if (enableConvert) {
						converts.add(convert);
					}
					if(overrideConvert){
						overrides.add(convert);
					}
				}
			}
			conversion.put(type, converts);
			conversion_override.put(type, overrides);
		}
		config.save();
	}

	public static boolean getSimplifiedBoolean(String name, String category, boolean defaultValue) {
		Property prop = config.get(category, name, defaultValue);
		prop.setLanguageKey(name);
		return prop.getBoolean(defaultValue);
	}
}
