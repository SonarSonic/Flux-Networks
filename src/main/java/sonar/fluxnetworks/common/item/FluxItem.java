package sonar.fluxnetworks.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import sonar.fluxnetworks.FluxConfig;
import sonar.fluxnetworks.api.translate.FluxTranslate;

import javax.annotation.Nullable;
import java.util.List;

public class FluxItem extends Item {

    public FluxItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (FluxConfig.enableFluxRecipe) {
            tooltip.add(new TranslationTextComponent(FluxTranslate.FLUX_TOOLTIP.k()));
        }
    }
}
