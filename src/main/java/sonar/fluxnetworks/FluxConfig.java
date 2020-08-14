package sonar.fluxnetworks;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import java.util.List;

public class FluxConfig {

    private static final ClientConfig    CLIENT_CONFIG;
    private static final ForgeConfigSpec CLIENT_SPEC;

    private static final CommonConfig    COMMON_CONFIG;
    private static final ForgeConfigSpec COMMON_SPEC;

    static {
        ForgeConfigSpec.Builder builder;

        builder = new ForgeConfigSpec.Builder();
        COMMON_CONFIG = new CommonConfig(builder);
        COMMON_SPEC = builder.build();

        builder = new ForgeConfigSpec.Builder();
        CLIENT_CONFIG = new ClientConfig(builder);
        CLIENT_SPEC = builder.build();
    }

    static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FluxConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FluxConfig.CLIENT_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(FluxConfig::reloadConfig);
    }

    static void reloadConfig(@Nonnull ModConfig.ModConfigEvent event) {
        final ForgeConfigSpec spec = event.getConfig().getSpec();
        if (spec == FluxConfig.COMMON_SPEC) {
            bakeCommonConfig();
            verifyAndReadBlacklist();
            generateFluxChunkConfig();
            FluxNetworks.LOGGER.info("COMMON CONFIG LOADED");
        } else if (spec == FluxConfig.CLIENT_SPEC) {
            bakeClientConfig();
            FluxNetworks.LOGGER.info("CLIENT CONFIG LOADED");
        }
    }

    public static boolean enableButtonSound, enableOneProbeBasicInfo, enableOneProbeAdvancedInfo, enableOneProbeSneaking;
    public static boolean enableFluxRecipe, /*enableOldRecipe,*/
            enableChunkLoading, enableSuperAdmin;
    public static int defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer, gargantuanCapacity, gargantuanTransfer;
    public static int maximumPerPlayer, superAdminRequiredPermission;
    public static List<String> blockBlacklistStrings, itemBlackListStrings;
    public static boolean enableGuiDebug;

    public static void bakeCommonConfig() {
        CommonConfig config = COMMON_CONFIG;

        defaultLimit = config.defaultLimit.get();
        basicCapacity = config.basicCapacity.get();
        basicTransfer = config.basicTransfer.get();
        herculeanCapacity = config.herculeanCapacity.get();
        herculeanTransfer = config.herculeanTransfer.get();
        gargantuanCapacity = config.gargantuanCapacity.get();
        gargantuanTransfer = config.gargantuanTransfer.get();

        maximumPerPlayer = config.maximumPerPlayer.get();
        superAdminRequiredPermission = config.superAdminRequiredPermission.get();
        enableSuperAdmin = config.enableSuperAdmin.get();

        enableFluxRecipe = config.enableFluxRecipe.get();
        //enableOldRecipe = config.enableOldRecipe.get();
        enableChunkLoading = config.enableChunkLoading.get();

        blockBlacklistStrings = config.blockBlacklistStrings.get();
        itemBlackListStrings = config.itemBlackListStrings.get();
    }

    public static void bakeClientConfig() {
        ClientConfig config = CLIENT_CONFIG;

        enableButtonSound = config.enableButtonSound.get();
        enableOneProbeBasicInfo = config.enableOneProbeBasicInfo.get();
        enableOneProbeAdvancedInfo = config.enableOneProbeAdvancedInfo.get();
        enableOneProbeSneaking = config.enableOneProbeSneaking.get();
        enableGuiDebug = config.enableGuiDebug.get();
    }

    public static class CommonConfig {

        ///energy
        public ForgeConfigSpec.IntValue defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer, gargantuanCapacity, gargantuanTransfer;

        //networks
        public ForgeConfigSpec.IntValue maximumPerPlayer, superAdminRequiredPermission;
        public ForgeConfigSpec.BooleanValue enableSuperAdmin;

        ////general
        public ForgeConfigSpec.BooleanValue enableFluxRecipe, /*enableOldRecipe,*/
                enableChunkLoading;

        //blacklist
        public ForgeConfigSpec.ConfigValue<List<String>> blockBlacklistStrings, itemBlackListStrings;


        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("energy");
            defaultLimit = builder
                    .comment("The default transfer limit of a flux connector")
                    .translation(FluxNetworks.MODID + ".config." + "defaultLimit")
                    .defineInRange("defaultLimit", 800000, 0, Integer.MAX_VALUE);
            basicCapacity = builder
                    .translation(FluxNetworks.MODID + ".config." + "basicCapacity")
                    .defineInRange("basicCapacity", 1000000, 0, Integer.MAX_VALUE);
            basicTransfer = builder
                    .translation(FluxNetworks.MODID + ".config." + "basicTransfer")
                    .defineInRange("basicTransfer", 20000, 0, Integer.MAX_VALUE);
            herculeanCapacity = builder
                    .translation(FluxNetworks.MODID + ".config." + "herculeanCapacity")
                    .defineInRange("herculeanCapacity", 8000000, 0, Integer.MAX_VALUE);
            herculeanTransfer = builder
                    .translation(FluxNetworks.MODID + ".config." + "herculeanTransfer")
                    .defineInRange("herculeanTransfer", 120000, 0, Integer.MAX_VALUE);
            gargantuanCapacity = builder
                    .translation(FluxNetworks.MODID + ".config." + "gargantuanCapacity")
                    .defineInRange("gargantuanCapacity", 128000000, 0, Integer.MAX_VALUE);
            gargantuanTransfer = builder
                    .translation(FluxNetworks.MODID + ".config." + "gargantuanTransfer")
                    .defineInRange("gargantuanTransfer", 1440000, 0, Integer.MAX_VALUE);

            builder.pop();

            builder.push("networks");
            maximumPerPlayer = builder
                    .comment("Maximum networks each player can have. -1 = no limit")
                    .translation(FluxNetworks.MODID + ".config." + "maximumPerPlayer")
                    .defineInRange("maximumPerPlayer", 5, -1, Integer.MAX_VALUE);
            enableSuperAdmin = builder
                    .comment("Allows someone to be a network super admin, otherwise, no one can access or dismantle your flux devices or delete your networks without permission")
                    .translation(FluxNetworks.MODID + ".config." + "enableSuperAdmin")
                    .define("enableSuperAdmin", true);
            superAdminRequiredPermission = builder
                    .comment("See ops.json. If the player has permission level equal or greater to the value set here they will be able to Activate Super Admin. Setting this to 0 will allow anyone to active Super Admin.")
                    .translation(FluxNetworks.MODID + ".config." + "superAdminRequiredPermission")
                    .defineInRange("superAdminRequiredPermission", 1, 0, Integer.MAX_VALUE);
            builder.pop();

            builder.push("general");
            enableFluxRecipe = builder
                    .comment("Enables redstones being compressed with the bedrock and obsidian to get flux")
                    .translation(FluxNetworks.MODID + ".config." + "enableFluxRecipe")
                    .define("enableFluxRecipe", true);
            /*enableOldRecipe = builder
                    .comment("Enables redstone being turned into Flux when dropped in fire. (Need \"Enable Flux Recipe\" = true, so the default recipe can't be disabled if turns this on)")
                    .translation(FluxNetworks.MODID + ".config." + "enableOldRecipe")
                    .define("enableOldRecipe", false);*/
            enableChunkLoading = builder
                    .comment("Allows flux tiles to work as chunk loaders")
                    .translation(FluxNetworks.MODID + ".config." + "enableChunkLoading")
                    .define("enableChunkLoading", true);

            builder.pop();
            builder.push("blacklist");
            blockBlacklistStrings = builder
                    .comment("a blacklist for blocks which flux connections shouldn't connect to, use format 'modid:name@blockstate'")
                    .translation(FluxNetworks.MODID + ".config." + "blockBlacklistStrings")
                    .define("blockBlacklistStrings", Lists.newArrayList("actuallyadditions:block_phantom_energyface"));

            itemBlackListStrings = builder
                    .comment("a blacklist for items which the Flux Controller shouldn't transfer to, use format 'modid:name@blockstate'")
                    .translation(FluxNetworks.MODID + ".config." + "itemBlackListStrings")
                    .define("itemBlackListStrings", Lists.newArrayList(""));
            builder.pop();
        }

    }


    public static class ClientConfig {

        public ForgeConfigSpec.BooleanValue enableButtonSound, enableOneProbeBasicInfo, enableOneProbeAdvancedInfo, enableOneProbeSneaking;
        public ForgeConfigSpec.BooleanValue enableGuiDebug;

        public ClientConfig(ForgeConfigSpec.Builder builder) {

            builder.push("gui");
            enableButtonSound = builder
                    .comment("Enable navigation buttons sound when pressing it")
                    .translation(FluxNetworks.MODID + ".config." + "enableButtonSound")
                    .define("enableButtonSound", true);
            enableGuiDebug = builder
                    .comment("Internal use only! Keep this to false!")
                    .define("enableGuiDebug", false);

            builder.pop();

            builder.push("OneProbe");
            enableOneProbeBasicInfo = builder
                    .comment("Displays: Network Name, Live Transfer Rate & Internal Buffer")
                    .translation(FluxNetworks.MODID + ".config." + "enableOneProbeBasicInfo")
                    .define("enableOneProbeBasicInfo", true);
            enableOneProbeAdvancedInfo = builder
                    .comment("Displays: Transfer Limit & Priority etc")
                    .translation(FluxNetworks.MODID + ".config." + "enableOneProbeAdvancedInfo")
                    .define("enableOneProbeAdvancedInfo", true);
            enableOneProbeSneaking = builder
                    .comment("Displays Advanced Info when sneaking only")
                    .translation(FluxNetworks.MODID + ".config." + "enableOneProbeSneaking")
                    .define("enableOneProbeSneaking", true);

            builder.pop();

        }
    }

    public static void verifyAndReadBlacklist() {
        //TODO FIX BLACK LIST VERIFICATION
        /*
        TileEntityHandler.blockBlacklist.clear();
        for(String str : blockBlacklistStrings) {
            if(!str.contains(":")) {
                FluxNetworks.LOGGER.error("BLACKLIST ERROR: " + str + " has incorrect formatting, please use 'modid:name@meta'");
            }
            String root = str;
            int meta = -1;
            if(str.contains("@")) {
                String[] split = str.split("@");
                root = split[0];
                try {
                    meta = Integer.parseInt(split[1]);
                    TileEntityHandler.blockBlacklist.put(root, meta);
                } catch (Exception e) {
                    FluxNetworks.LOGGER.error("BLACKLIST ERROR: " + str + " has incorrect formatting, meta must be positive integer'");
                }
            } else {
                TileEntityHandler.blockBlacklist.put(root, meta);
            }
        }
        ItemEnergyHandler.itemBlackList.clear();
        for(String str : itemBlackListStrings) {
            if(!str.contains(":")) {
                FluxNetworks.LOGGER.error("BLACKLIST ERROR: " + str + " has incorrect formatting, please use 'modid:name@meta'");
            }
            String root = str;
            int meta = -1;
            if(str.contains("@")) {
                String[] split = str.split("@");
                root = split[0];
                try {
                    meta = Integer.parseInt(split[1]);
                    ItemEnergyHandler.itemBlackList.put(root, meta);
                } catch (Exception e) {
                    FluxNetworks.LOGGER.error("BLACKLIST ERROR: " + str + " has incorrect formatting, meta must be positive integer'");
                }
            } else {
                ItemEnergyHandler.itemBlackList.put(root, meta);
            }
        }

         */
    }

    public static void generateFluxChunkConfig() {
        /* TODO CHUNK LOADING!
        if(!ForgeChunkManager.getConfig().hasCategory(FluxNetworks.MODID)) {
            ForgeChunkManager.getConfig().get(FluxNetworks.MODID, "maximumChunksPerTicket", 1000000).setMinValue(0);
            ForgeChunkManager.getConfig().get(FluxNetworks.MODID, "maximumTicketCount", 1000000).setMinValue(0);
            ForgeChunkManager.getConfig().save();
        }
        */
    }
}
