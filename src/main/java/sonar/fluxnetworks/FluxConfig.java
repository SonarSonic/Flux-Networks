package sonar.fluxnetworks;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import sonar.fluxnetworks.common.handler.ItemEnergyHandler;
import sonar.fluxnetworks.common.handler.TileEntityHandler;
import java.util.List;

public class FluxConfig {


    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final CommonConfig COMMON_CONFIG;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {

        final Pair<CommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON_CONFIG = commonPair.getLeft();

        final Pair<ClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT_CONFIG = clientPair.getLeft();

    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        reloadConfig(configEvent);
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
        reloadConfig(configEvent);
    }

    public static void reloadConfig(ModConfig.ModConfigEvent configEvent){

        FluxNetworks.LOGGER.info("LOADING CONFIG");
        if (configEvent.getConfig().getSpec() == FluxConfig.COMMON_SPEC) {
            bakeCommonConfig();
            verifyAndReadBlacklist();
            generateFluxChunkConfig();
        }

        if (configEvent.getConfig().getSpec() == FluxConfig.CLIENT_SPEC) {
            bakeClientConfig();
        }
        FluxNetworks.LOGGER.info("LOADED CONFIG");
    }

    public static boolean enableButtonSound, enableOneProbeBasicInfo, enableOneProbeAdvancedInfo, enableOneProbeSneaking;
    public static boolean enableFluxRecipe, enableOldRecipe, enableChunkLoading, enableSuperAdmin;
    public static int defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer, gargantuanCapacity, gargantuanTransfer;
    public static int maximumPerPlayer, superAdminRequiredPermission;
    public static List<String> blockBlacklistStrings, itemBlackListStrings;

    public static void bakeCommonConfig(){
        defaultLimit = COMMON_CONFIG.defaultLimit.get();
        basicCapacity = COMMON_CONFIG.basicCapacity.get();
        basicTransfer = COMMON_CONFIG.basicTransfer.get();
        herculeanCapacity = COMMON_CONFIG.herculeanCapacity.get();
        herculeanTransfer = COMMON_CONFIG.herculeanTransfer.get();
        gargantuanCapacity = COMMON_CONFIG.gargantuanCapacity.get();
        gargantuanTransfer = COMMON_CONFIG.gargantuanTransfer.get();

        maximumPerPlayer = COMMON_CONFIG.maximumPerPlayer.get();
        superAdminRequiredPermission = COMMON_CONFIG.superAdminRequiredPermission.get();
        enableSuperAdmin = COMMON_CONFIG.enableSuperAdmin.get();

        enableFluxRecipe = COMMON_CONFIG.enableFluxRecipe.get();
        enableOldRecipe = COMMON_CONFIG.enableOldRecipe.get();
        enableChunkLoading = COMMON_CONFIG.enableChunkLoading.get();

        blockBlacklistStrings = COMMON_CONFIG.blockBlacklistStrings.get();
        itemBlackListStrings = COMMON_CONFIG.itemBlackListStrings.get();
    }

    public static void bakeClientConfig(){
        enableButtonSound = CLIENT_CONFIG.enableButtonSound.get();
        enableOneProbeBasicInfo = CLIENT_CONFIG.enableOneProbeBasicInfo.get();
        enableOneProbeAdvancedInfo = CLIENT_CONFIG.enableOneProbeAdvancedInfo.get();
        enableOneProbeSneaking = CLIENT_CONFIG.enableOneProbeSneaking.get();
    }

    public static class CommonConfig{

        ///energy
        public ForgeConfigSpec.IntValue defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer, gargantuanCapacity, gargantuanTransfer;

        //networks
        public ForgeConfigSpec.IntValue maximumPerPlayer, superAdminRequiredPermission;
        public ForgeConfigSpec.BooleanValue enableSuperAdmin;

        ////general
        public ForgeConfigSpec.BooleanValue enableFluxRecipe, enableOldRecipe, enableChunkLoading;

        //blacklist
        public ForgeConfigSpec.ConfigValue<List<String>> blockBlacklistStrings, itemBlackListStrings;


        public CommonConfig(ForgeConfigSpec.Builder builder){
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
            enableOldRecipe = builder
                    .comment("Enables redstone being turned into Flux when dropped in fire. (Need \"Enable Flux Recipe\" = true, so the default recipe can't be disabled if turns this on)")
                    .translation(FluxNetworks.MODID + ".config." + "enableOldRecipe")
                    .define("enableOldRecipe", false);
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



    public static class ClientConfig{

        public ForgeConfigSpec.BooleanValue enableButtonSound, enableOneProbeBasicInfo, enableOneProbeAdvancedInfo, enableOneProbeSneaking;

        public ClientConfig(ForgeConfigSpec.Builder builder) {

            builder.push("gui");
            enableButtonSound = builder
                    .comment("Enable navigation buttons sound when pressing it")
                    .translation(FluxNetworks.MODID + ".config." + "enableButtonSound")
                    .define("enableButtonSound", true);

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
