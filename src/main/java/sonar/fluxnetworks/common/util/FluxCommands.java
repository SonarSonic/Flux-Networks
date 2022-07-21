package sonar.fluxnetworks.common.util;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.common.capability.FluxPlayer;
import sonar.fluxnetworks.register.Messages;

import javax.annotation.Nonnull;
import java.util.Collection;

public class FluxCommands {

    public static void register(@Nonnull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(FluxNetworks.MODID)
                .then(Commands.literal("superadmin")
                        .requires(s -> s.hasPermission(1))
                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                .then(Commands.argument("enable", BoolArgumentType.bool())
                                        .executes(s -> superAdmin(s.getSource(),
                                                GameProfileArgument.getGameProfiles(s, "targets"),
                                                BoolArgumentType.getBool(s, "enable"))
                                        )
                                )
                        )
                )
        );
    }

    private static int superAdmin(@Nonnull CommandSourceStack source,
                                  @Nonnull Collection<GameProfile> profiles, boolean enable) {
        PlayerList playerList = source.getServer().getPlayerList();
        int success = 0;

        for (GameProfile profile : profiles) {
            ServerPlayer player = playerList.getPlayer(profile.getId());
            if (player != null) {
                final FluxPlayer fp = FluxUtils.get(player, FluxPlayer.FLUX_PLAYER);
                if (fp != null &&
                        (((fp.isSuperAdmin() || FluxConfig.enableSuperAdmin) && source.hasPermission(3)) ||
                                (player == source.getEntity() && (fp.isSuperAdmin() || FluxPlayer.canActivateSuperAdmin(player)))) &&
                        fp.setSuperAdmin(enable)) {
                    Messages.syncCapability(player);
                    player.sendMessage(new TranslatableComponent(enable ?
                            "gui.fluxnetworks.superadmin.on" : "gui.fluxnetworks.superadmin.off"), Util.NIL_UUID);
                    success++;
                }
            }
        }

        return success;
    }
}
