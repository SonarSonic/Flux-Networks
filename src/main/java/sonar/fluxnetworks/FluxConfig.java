package sonar.fluxnetworks;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import sonar.fluxnetworks.common.misc.EnergyUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class FluxConfig {

    private static final Client CLIENT_CONFIG;
    private static final ForgeConfigSpec CLIENT_SPEC;

    private static final Common COMMON_CONFIG;
    private static final ForgeConfigSpec COMMON_SPEC;

    private static final Server SERVER_CONFIG;
    private static final ForgeConfigSpec SERVER_SPEC;

    static {
        ForgeConfigSpec.Builder builder;

        if (FMLEnvironment.dist.isClient()) {
            builder = new ForgeConfigSpec.Builder();
            CLIENT_CONFIG = new Client(builder);
            CLIENT_SPEC = builder.build();
        } else {
            CLIENT_CONFIG = null;
            CLIENT_SPEC = null;
        }

        builder = new ForgeConfigSpec.Builder();
        COMMON_CONFIG = new Common(builder);
        COMMON_SPEC = builder.build();

        builder = new ForgeConfigSpec.Builder();
        SERVER_CONFIG = new Server(builder);
        SERVER_SPEC = builder.build();
    }

    static void init() {
        if (FMLEnvironment.dist.isClient()) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(FluxConfig::reload);
    }

    static void reload(@Nonnull ModConfig.ModConfigEvent event) {
        final ForgeConfigSpec spec = event.getConfig().getSpec();
        if (spec == CLIENT_SPEC) {
            CLIENT_CONFIG.load();
            FluxNetworks.LOGGER.info("CLIENT CONFIG LOADED");
        } else if (spec == COMMON_SPEC) {
            COMMON_CONFIG.load();
            EnergyUtils.reloadBlacklist();
            FluxNetworks.LOGGER.info("COMMON CONFIG LOADED");
        } else if (spec == SERVER_SPEC) {
            SERVER_CONFIG.load();
            FluxNetworks.LOGGER.info("SERVER CONFIG LOADED");
        }
    }

    public static boolean enableButtonSound, enableOneProbeBasicInfo, enableOneProbeAdvancedInfo, enableOneProbeSneaking;
    public static boolean enableFluxRecipe, enableChunkLoading, enableSuperAdmin;
    public static long defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer, gargantuanCapacity, gargantuanTransfer;
    public static int maximumPerPlayer, superAdminRequiredPermission;
    public static List<String> blockBlacklistStrings, itemBlackListStrings;
    public static boolean enableGuiDebug;

    @OnlyIn(Dist.CLIENT)
    private static class Client {

        private final ForgeConfigSpec.BooleanValue mEnableButtonSound, mEnableOneProbeBasicInfo, mEnableOneProbeAdvancedInfo,
                mEnableOneProbeSneaking;
        private final ForgeConfigSpec.BooleanValue mEnableGuiDebug;

        private Client(@Nonnull ForgeConfigSpec.Builder builder) {
            builder.push("gui");
            mEnableButtonSound = builder
                    .comment("Enable navigation buttons sound when pressing it")
                    .translation(FluxNetworks.MODID + ".config." + "enableButtonSound")
                    .define("enableButtonSound", true);
            mEnableGuiDebug = builder
                    .comment("Internal use only! Keep this to false!")
                    .define("enableGuiDebug", false);

            builder.pop();

            builder.push("OneProbe");
            mEnableOneProbeBasicInfo = builder
                    .comment("Displays: Network Name, Live Transfer Rate & Internal Buffer")
                    .translation(FluxNetworks.MODID + ".config." + "enableOneProbeBasicInfo")
                    .define("enableOneProbeBasicInfo", true);
            mEnableOneProbeAdvancedInfo = builder
                    .comment("Displays: Transfer Limit & Priority etc")
                    .translation(FluxNetworks.MODID + ".config." + "enableOneProbeAdvancedInfo")
                    .define("enableOneProbeAdvancedInfo", true);
            mEnableOneProbeSneaking = builder
                    .comment("Displays Advanced Info when sneaking only")
                    .translation(FluxNetworks.MODID + ".config." + "enableOneProbeSneaking")
                    .define("enableOneProbeSneaking", true);

            builder.pop();
        }

        private void load() {
            enableButtonSound = mEnableButtonSound.get();
            enableOneProbeBasicInfo = mEnableOneProbeBasicInfo.get();
            enableOneProbeAdvancedInfo = mEnableOneProbeAdvancedInfo.get();
            enableOneProbeSneaking = mEnableOneProbeSneaking.get();
            enableGuiDebug = mEnableGuiDebug.get();
        }
    }

    private static class Common {

        // networks
        private final ForgeConfigSpec.IntValue mMaximumPerPlayer, mSuperAdminRequiredPermission;
        private final ForgeConfigSpec.BooleanValue mEnableSuperAdmin;

        // general
        private final ForgeConfigSpec.BooleanValue mEnableFluxRecipe, mEnableChunkLoading;

        // blacklist
        private final ForgeConfigSpec.ConfigValue<List<String>> mBlockBlacklistStrings, mItemBlackListStrings;

        private Common(@Nonnull ForgeConfigSpec.Builder builder) {
            builder.push("networks");
            mMaximumPerPlayer = builder
                    .comment("Maximum networks each player can have. -1 = no limit")
                    .translation(FluxNetworks.MODID + ".config." + "maximumPerPlayer")
                    .defineInRange("maximumPerPlayer", 5, -1, Integer.MAX_VALUE);
            mEnableSuperAdmin = builder
                    .comment("Allows someone to be a network super admin, otherwise, no one can access or dismantle your flux devices or delete your networks without permission")
                    .translation(FluxNetworks.MODID + ".config." + "enableSuperAdmin")
                    .define("enableSuperAdmin", true);
            mSuperAdminRequiredPermission = builder
                    .comment("See ops.json. If the player has permission level equal or greater to the value set here they will be able to Activate Super Admin. Setting this to 0 will allow anyone to active Super Admin.")
                    .translation(FluxNetworks.MODID + ".config." + "superAdminRequiredPermission")
                    .defineInRange("superAdminRequiredPermission", 1, 0, Integer.MAX_VALUE);
            builder.pop();

            builder.push("general");
            mEnableFluxRecipe = builder
                    .comment("Enables redstone being compressed with the bedrock and obsidian to get flux")
                    .translation(FluxNetworks.MODID + ".config." + "enableFluxRecipe")
                    .define("enableFluxRecipe", true);
            mEnableChunkLoading = builder
                    .comment("Allows flux tiles to work as chunk loaders")
                    .translation(FluxNetworks.MODID + ".config." + "enableChunkLoading")
                    .define("enableChunkLoading", true);

            builder.pop();
            builder.push("blacklist");
            mBlockBlacklistStrings = builder
                    .comment("A blacklist for blocks which flux devices shouldn't connect to, use format 'modid:registry_name'")
                    .translation(FluxNetworks.MODID + ".config." + "blockBlacklistStrings")
                    .define("blockBlacklistStrings", Lists.newArrayList("actuallyadditions:block_phantom_energyface"));

            mItemBlackListStrings = builder
                    .comment("A blacklist for items which wireless charging shouldn't charge to, use format 'modid:registry_name'")
                    .translation(FluxNetworks.MODID + ".config." + "itemBlackListStrings")
                    .define("itemBlackListStrings", Lists.newArrayList(""));
            builder.pop();
        }

        private void load() {
            maximumPerPlayer = mMaximumPerPlayer.get();
            superAdminRequiredPermission = mSuperAdminRequiredPermission.get();
            enableSuperAdmin = mEnableSuperAdmin.get();

            enableFluxRecipe = mEnableFluxRecipe.get();
            enableChunkLoading = mEnableChunkLoading.get();

            blockBlacklistStrings = mBlockBlacklistStrings.get();
            itemBlackListStrings = mItemBlackListStrings.get();
        }
    }

    private static class Server {

        // energy
        private final ForgeConfigSpec.LongValue mDefaultLimit, mBasicCapacity, mBasicTransfer, mHerculeanCapacity,
                mHerculeanTransfer, mGargantuanCapacity, mGargantuanTransfer;

        private Server(@Nonnull ForgeConfigSpec.Builder builder) {
            builder.push("energy");
            mDefaultLimit = builder
                    .comment("The default transfer limit of a flux connector")
                    .translation(FluxNetworks.MODID + ".config." + "defaultLimit")
                    .defineInRange("defaultLimit", 800000, 0, Long.MAX_VALUE);
            mBasicCapacity = builder
                    .translation(FluxNetworks.MODID + ".config." + "basicCapacity")
                    .defineInRange("basicCapacity", 1000000, 0, Long.MAX_VALUE);
            mBasicTransfer = builder
                    .translation(FluxNetworks.MODID + ".config." + "basicTransfer")
                    .defineInRange("basicTransfer", 20000, 0, Long.MAX_VALUE);
            mHerculeanCapacity = builder
                    .translation(FluxNetworks.MODID + ".config." + "herculeanCapacity")
                    .defineInRange("herculeanCapacity", 8000000, 0, Long.MAX_VALUE);
            mHerculeanTransfer = builder
                    .translation(FluxNetworks.MODID + ".config." + "herculeanTransfer")
                    .defineInRange("herculeanTransfer", 120000, 0, Long.MAX_VALUE);
            mGargantuanCapacity = builder
                    .translation(FluxNetworks.MODID + ".config." + "gargantuanCapacity")
                    .defineInRange("gargantuanCapacity", 128000000, 0, Long.MAX_VALUE);
            mGargantuanTransfer = builder
                    .translation(FluxNetworks.MODID + ".config." + "gargantuanTransfer")
                    .defineInRange("gargantuanTransfer", 1440000, 0, Long.MAX_VALUE);
            builder.pop();
        }

        private void load() {
            defaultLimit = mDefaultLimit.get();
            basicCapacity = mBasicCapacity.get();
            basicTransfer = mBasicTransfer.get();
            herculeanCapacity = mHerculeanCapacity.get();
            herculeanTransfer = mHerculeanTransfer.get();
            gargantuanCapacity = mGargantuanCapacity.get();
            gargantuanTransfer = mGargantuanTransfer.get();
        }
    }

    /*public static void generateFluxChunkConfig() {
        if(!ForgeChunkManager.getConfig().hasCategory(FluxNetworks.MODID)) {
            ForgeChunkManager.getConfig().get(FluxNetworks.MODID, "maximumChunksPerTicket", 1000000).setMinValue(0);
            ForgeChunkManager.getConfig().get(FluxNetworks.MODID, "maximumTicketCount", 1000000).setMinValue(0);
            ForgeChunkManager.getConfig().save();
        }
    }*/
}
