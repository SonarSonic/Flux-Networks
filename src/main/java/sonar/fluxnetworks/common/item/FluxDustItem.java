package sonar.fluxnetworks.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.FluxTranslate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FluxDustItem extends Item {

    public FluxDustItem(@Nonnull Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip,
                                @Nonnull TooltipFlag flag) {
        if (FluxConfig.enableFluxRecipe) {
            tooltip.add(FluxTranslate.FLUX_DUST_TOOLTIP.component());
        }
    }
}
