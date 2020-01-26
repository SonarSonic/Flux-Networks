package icyllis.fluxnetworks.system;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FluxConfig {
    private static final Builder b = new Builder();
    private static final Builder clientbuilder = new Builder();
    private static final ForgeConfigSpec server;
    private static final ForgeConfigSpec client;

    public final static BooleanValue enableButtonSound, enableOneProbeBasicInfo, enableOneProbeAdvancedInfo, enableOneProbeSneaking;
    public final static BooleanValue enableFluxRecipe, enableOldRecipe, enableChunkLoading, enableSuperAdmin;
    public final static IntValue defaultLimit, basicCapacity, basicTransfer, herculeanCapacity, herculeanTransfer, gargantuanCapacity, gargantuanTransfer;
    public final static IntValue maximumPerPlayer, superAdminRequiredPermission;
    public final static ConfigValue<String> blockBlacklistStrings, itemBlackListStrings;

    static {
        b.push("blacklists");
        blockBlacklistStrings = b
                .comment("a blacklist for blocks which flux connections shouldn't connect to, use format 'modid:name', split with comma")
                .define("blockBlacklistStrings", "" );
        itemBlackListStrings = b
                .comment("a blacklist for items which the Flux Controller shouldn't transfer to, use format 'modid:name' split with comma")
                .define("itemBlackListStrings", "");
        b.pop();

        b.push("energy");
        basicCapacity = b.defineInRange("basicCapacity", 256000, 0, Integer.MAX_VALUE);
        basicTransfer = b.defineInRange("basicTransfer", 6400, 0, Integer.MAX_VALUE);
        defaultLimit = b
                .comment("the default transfer limit of a flux connection")
                .defineInRange("defaultLimit", 256000, 0, Integer.MAX_VALUE);
        herculeanCapacity = b.defineInRange("herculeanCapacity", 12800000, 0, Integer.MAX_VALUE);
        herculeanTransfer = b.defineInRange("herculeanTransfer", 12800, 0, Integer.MAX_VALUE);
        gargantuanCapacity = b.defineInRange("herculeanCapacity", 128000000, 0, Integer.MAX_VALUE);
        gargantuanTransfer = b.defineInRange("herculeanTransfer", 256000, 0, Integer.MAX_VALUE);
        b.pop();

        b.push("networks");
        maximumPerPlayer = b
                .comment("-1 = no limit")
                .defineInRange("maximumPerPlayer", -1, -1, Integer.MAX_VALUE);
        enableSuperAdmin = b
                .comment("Allows someone to be a network super admin, otherwise, no one can access or dismantle your flux devices or delete your networks without permission")
                .define("enableSuperAdmin", true);
        superAdminRequiredPermission = b
                .comment("See ops.json. If the player has permission level equal or greater to the value set here they will be able to Activate Super Admin. Setting this to 0 will allow anyone to active Super Admin.")
                .defineInRange("superAdminRequiredPermission", 1, 0, 4);

        b.pop();

        b.push("general");
        enableFluxRecipe = b
                .comment("Enables redstones being compressed with the bedrock and obsidian to get flux")
                .define("enableFluxRecipe", true);
        enableOldRecipe = b
                .comment("Enables redstone being turned into Flux when dropped in fire. (Need \\\"Enable Flux Recipe\\\" = true, so the default recipe can't be disabled if turns this on)\"")
                .define("enableOldRecipe", false);
        enableChunkLoading = b
                .comment("Allows flux tiles to work as chunk loaders")
                .define("enableChunkLoading", true);
        b.pop();

        server = b.build();

        clientbuilder.push("client");
        enableButtonSound = clientbuilder
                .comment("Enable navigation buttons sound when pressing it")
                .define("enableButtonSound", true);
        enableOneProbeBasicInfo = clientbuilder
                .comment("Displays: Network Name, Live Transfer Rate & Internal Buffer")
                .define("enableOneProbeBasicInfo", true);
        enableOneProbeAdvancedInfo = clientbuilder
                .comment("Displays: Transfer Limit & Priority etc")
                .define("enableOneProbeAdvancedInfo", true);
        enableOneProbeSneaking = clientbuilder
                .comment("Displays Advanced Info when sneaking only")
                .define("enableOneProbeSneaking", true);
        clientbuilder.pop();
        client = clientbuilder.build();
    }

    public static void setup()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, client);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, server);
    }
}