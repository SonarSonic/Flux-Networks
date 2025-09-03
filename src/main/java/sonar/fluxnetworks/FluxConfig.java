package sonar.fluxnetworks;

import com.google.common.collect.Lists;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import sonar.fluxnetworks.common.util.EnergyUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class FluxConfig {

    private static final Client CLIENT_CONFIG;
    private static final ModConfigSpec CLIENT_SPEC;

    private static final Common COMMON_CONFIG;
    private static final ModConfigSpec COMMON_SPEC;

    private static final Server SERVER_CONFIG;
    private static final ModConfigSpec SERVER_SPEC;

    static {
        ModConfigSpec.Builder builder;

        if (FMLEnvironment.dist.isClient()) {
            builder = new ModConfigSpec.Builder();
            CLIENT_CONFIG = new Client(builder);
            CLIENT_SPEC = builder.build();
        } else {
            CLIENT_CONFIG = null;
            CLIENT_SPEC = null;
        }

        builder = new ModConfigSpec.Builder();
        COMMON_CONFIG = new Common(builder);
        COMMON_SPEC = builder.build();

        builder = new ModConfigSpec.Builder();
        SERVER_CONFIG = new Server(builder);
        SERVER_SPEC = builder.build();
    }

    static void init() {
        if (FMLEnvironment.dist.isClient()) {
            ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
        }
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        ModLoadingContext.get().getActiveContainer().getEventBus().addListener(FluxConfig::reload);
    }

    static void reload(@Nonnull ModConfigEvent event) {
        // Ignore unloading events
        if (event instanceof ModConfigEvent.Unloading) { return; }

        final IConfigSpec spec = event.getConfig().getSpec();
        if (spec == CLIENT_SPEC) {
            CLIENT_CONFIG.load();
            FluxNetworks.LOGGER.debug("Client config loaded");
        } else if (spec == COMMON_SPEC) {
            COMMON_CONFIG.load();
            FluxNetworks.LOGGER.debug("Common config loaded");
        } else if (spec == SERVER_SPEC) {
            SERVER_CONFIG.load();
            FluxNetworks.LOGGER.debug("Server config loaded");
        }
    }

    public static boolean enableButtonSound, enableGuiDebug, enableModernDesign;
    public static boolean enableOneProbeBasicInfo, enableOneProbeAdvancedInfo, enableOneProbeSneaking;
    public static boolean enableFluxRecipe, enableChunkLoading, enableSuperAdmin, enableWirelessMainInventory;
    public static long defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer,
            gargantuanCapacity, gargantuanTransfer;
    public static int maximumPerPlayer, superAdminRequiredPermission;
    public static boolean enableGTCEU;

    @OnlyIn(Dist.CLIENT)
    private static class Client {

        private final ModConfigSpec.BooleanValue mEnableButtonSound;
        private final ModConfigSpec.BooleanValue mEnableGuiDebug;
        private final ModConfigSpec.BooleanValue mEnableModernDesign;

        private Client(@Nonnull ModConfigSpec.Builder builder) {
            builder.push("gui");
            mEnableButtonSound = builder
                    .comment("Enable navigation buttons sound when pressing it")
                    .translation(FluxNetworks.MODID + ".config." + "enableButtonSound")
                    .define("enableButtonSound", true);
            mEnableGuiDebug = builder
                    .comment("Enable ModernUI")
                    .define("enableGuiDebug", false);
            mEnableModernDesign = builder
                    .comment("Enable Modern Design for classic GUI when ModernUI is installed")
                    .define("enableModernDesign", true);

            builder.pop();
        }

        private void load() {
            enableButtonSound = mEnableButtonSound.get();
            enableGuiDebug = mEnableGuiDebug.get();
            enableModernDesign = mEnableModernDesign.get();
        }
    }

    private static class Common {

        private final ModConfigSpec.BooleanValue
                mEnableOneProbeBasicInfo,
                mEnableOneProbeAdvancedInfo,
                mEnableOneProbeSneaking;

        private final ModConfigSpec.BooleanValue mEnableGTCEU;

        private Common(@Nonnull ModConfigSpec.Builder builder) {
            builder.comment("Most configs are moved to /serverconfig/fluxnetworks-server.toml",
                            "Copy to /defaultconfig/fluxnetworks-server.toml for modpacks")
                    .define("placeholder", true);

            builder.comment("The One Probe")
                    .push("OneProbe");
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

            builder.comment("Integration")
                    .push("integration");

            mEnableGTCEU = builder
                    .comment("Whether to enable GTCEU integration if GregTech CE Unofficial Modern is installed.",
                            "4 FE = 1 GTEU, not configurable.",
                            "Note that EU-to-FE conversion is provided by GTCEU, instead of Flux Networks.",
                            "Ensure you have enabled GTCEU's nativeEUToPlatformNative and euToPlatformRatio = 4.",
                            "A game/server restart is required to reload this setting.")
                    .define("enableGTCEU", true);

            builder.pop();
        }

        private void load() {
            enableOneProbeBasicInfo = mEnableOneProbeBasicInfo.get();
            enableOneProbeAdvancedInfo = mEnableOneProbeAdvancedInfo.get();
            enableOneProbeSneaking = mEnableOneProbeSneaking.get();

            enableGTCEU = mEnableGTCEU.get();
        }
    }

    private static class Server {

        // networks
        private final ModConfigSpec.IntValue mMaximumPerPlayer;
        private final ModConfigSpec.IntValue mSuperAdminRequiredPermission;
        private final ModConfigSpec.BooleanValue mEnableSuperAdmin;

        // general
        private final ModConfigSpec.BooleanValue mEnableFluxRecipe;
        private final ModConfigSpec.BooleanValue mEnableChunkLoading;
        private final ModConfigSpec.BooleanValue mEnableWirelessMainInventory;
        //private final ModConfigSpec.BooleanValue mChunkLoadingRequiresSuperAdmin;

        // blacklist
        private final ModConfigSpec.ConfigValue<List<String>> mBlockBlacklistStrings, mItemBlackListStrings;

        // energy
        private final ModConfigSpec.LongValue mDefaultLimit, mBasicCapacity, mBasicTransfer, mHerculeanCapacity,
                mHerculeanTransfer, mGargantuanCapacity, mGargantuanTransfer;

        private Server(@Nonnull ModConfigSpec.Builder builder) {
            builder.push("networks");
            mMaximumPerPlayer = builder
                    .comment("Maximum networks each player can have. Super admin can bypass this limit. -1 = no limit",
                            "Setting this to 0 will only allow super admins to create networks.")
                    .translation(FluxNetworks.MODID + ".config." + "maximumPerPlayer")
                    .defineInRange("maximumPerPlayer", 5, -1, Integer.MAX_VALUE);
            mEnableSuperAdmin = builder
                    .comment("Allows someone to be a network super admin. Otherwise, no one can access a flux device " +
                            "or delete a network without permission.")
                    .translation(FluxNetworks.MODID + ".config." + "enableSuperAdmin")
                    .define("enableSuperAdmin", true);
            mSuperAdminRequiredPermission = builder
                    .comment("See ops.json. If the player has permission level equal or greater to the value set here" +
                                    " they will be able to activate Super Admin.",
                            "Setting this to 0 will allow anyone to active Super Admin. Single player can bypass this" +
                                    " limit.",
                            "Players have permission level 3 or 4 can use commands to set others as Super Admin " +
                                    "whether others have this permission level or not.")
                    .translation(FluxNetworks.MODID + ".config." + "superAdminRequiredPermission")
                    .defineInRange("superAdminRequiredPermission", 1, 0, 3);
            builder.pop();

            builder.push("general");
            mEnableFluxRecipe = builder
                    .comment("Enables redstone being compressed with the bedrock and obsidian to get flux dusts.")
                    .translation(FluxNetworks.MODID + ".config." + "enableFluxRecipe")
                    .define("enableFluxRecipe", true);
            mEnableChunkLoading = builder
                    .comment("Allows flux devices to enable chunk loading.")
                    .translation(FluxNetworks.MODID + ".config." + "enableChunkLoading")
                    .define("enableChunkLoading", true);
            mEnableWirelessMainInventory = builder
                    .comment("Allows to enable wireless charging for the main inventory.")
                    .translation(FluxNetworks.MODID + ".config." + "enableWirelessMainInventory")
                    .define("enableWirelessMainInventory", false);
            builder.pop();

            builder.push("blacklist");
            mBlockBlacklistStrings = builder
                    .comment("A blacklist for blocks which flux devices shouldn't connect to, use format " +
                            "'modid:registry_name'")
                    .translation(FluxNetworks.MODID + ".config." + "blockBlacklistStrings")
                    .define("blockBlacklistStrings", Lists.newArrayList("actuallyadditions:block_phantom_energyface"));
            mItemBlackListStrings = builder
                    .comment("A blacklist for items which wireless charging shouldn't charge to, use format " +
                            "'modid:registry_name'")
                    .translation(FluxNetworks.MODID + ".config." + "itemBlackListStrings")
                    .define("itemBlackListStrings", Lists.newArrayList(""));
            builder.pop();

            builder.push("energy");
            mDefaultLimit = builder
                    .comment("The default transfer limit of a Flux Plug, Point and Controller")
                    .translation(FluxNetworks.MODID + ".config." + "defaultLimit")
                    .defineInRange("defaultLimit", 800000, 0, Long.MAX_VALUE);
            mBasicCapacity = builder
                    .comment("The maximum energy storage of a Basic Flux Storage")
                    .translation(FluxNetworks.MODID + ".config." + "basicCapacity")
                    .defineInRange("basicCapacity", 2000000, 0, Long.MAX_VALUE);
            mBasicTransfer = builder
                    .comment("The default transfer limit of a Basic Flux Storage")
                    .translation(FluxNetworks.MODID + ".config." + "basicTransfer")
                    .defineInRange("basicTransfer", 20000, 0, Long.MAX_VALUE);
            mHerculeanCapacity = builder
                    .comment("The maximum energy storage of a Herculean Flux Storage")
                    .translation(FluxNetworks.MODID + ".config." + "herculeanCapacity")
                    .defineInRange("herculeanCapacity", 16000000, 0, Long.MAX_VALUE);
            mHerculeanTransfer = builder
                    .comment("The default transfer limit of a Herculean Flux Storage")
                    .translation(FluxNetworks.MODID + ".config." + "herculeanTransfer")
                    .defineInRange("herculeanTransfer", 120000, 0, Long.MAX_VALUE);
            mGargantuanCapacity = builder
                    .comment("The maximum energy storage of a Gargantuan Flux Storage")
                    .translation(FluxNetworks.MODID + ".config." + "gargantuanCapacity")
                    .defineInRange("gargantuanCapacity", 128000000, 0, Long.MAX_VALUE);
            mGargantuanTransfer = builder
                    .comment("The default transfer limit of a Gargantuan Flux Storage")
                    .translation(FluxNetworks.MODID + ".config." + "gargantuanTransfer")
                    .defineInRange("gargantuanTransfer", 720000, 0, Long.MAX_VALUE);
            builder.pop();
        }

        private void load() {
            maximumPerPlayer = mMaximumPerPlayer.get();
            superAdminRequiredPermission = mSuperAdminRequiredPermission.get();

            enableFluxRecipe = mEnableFluxRecipe.get();
            enableChunkLoading = mEnableChunkLoading.get();
            enableSuperAdmin = mEnableSuperAdmin.get();
            enableWirelessMainInventory = mEnableWirelessMainInventory.get();

            EnergyUtils.reloadBlacklist(mBlockBlacklistStrings.get(), mItemBlackListStrings.get());

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
