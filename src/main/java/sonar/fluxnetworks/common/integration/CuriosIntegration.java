package sonar.fluxnetworks.common.integration;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosIntegration {

    public static LazyOptional<IItemHandlerModifiable> getEquippedCurios(ServerPlayerEntity player) {
        return CuriosApi.getCuriosHelper().getEquippedCurios(player);
    }
}
