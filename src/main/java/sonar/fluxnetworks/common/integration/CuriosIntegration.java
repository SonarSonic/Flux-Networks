package sonar.fluxnetworks.common.integration;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * A holder class that prevents class-loading when Curios not available.
 *
 * @see sonar.fluxnetworks.FluxNetworks#curiosLoaded
 */
public class CuriosIntegration {

    public static LazyOptional<IItemHandlerModifiable> getEquippedCurios(ServerPlayer player) {
        return CuriosApi.getCuriosHelper().getEquippedCurios(player);
    }
}
