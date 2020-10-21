package sonar.fluxnetworks.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import sonar.fluxnetworks.api.misc.EnergyType;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.misc.NumberFormatType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemFluxDevice extends BlockItem {

    public ItemFluxDevice(Block block, Item.Properties props) {
        super(block, props);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        CompoundNBT tag = stack.getChildTag(FluxConstants.TAG_FLUX_DATA);
        if (tag != null && tag.contains(FluxConstants.CUSTOM_NAME)) {
            return new StringTextComponent(tag.getString(FluxConstants.CUSTOM_NAME));
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip,
                               @Nonnull ITooltipFlag flagIn) {
        CompoundNBT tag = stack.getChildTag(FluxConstants.TAG_FLUX_DATA);
        if (tag != null) {
            if (tag.contains(FluxConstants.NETWORK_ID))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.NETWORK_FULL_NAME.t() + ": " +
                        TextFormatting.RESET + FluxClientCache.getDisplayName(tag.getInt(FluxConstants.NETWORK_ID))));

            if (tag.contains(FluxConstants.LIMIT))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.TRANSFER_LIMIT.t() + ": " +
                        TextFormatting.RESET + FluxUtils.format(tag.getLong(FluxConstants.LIMIT),
                        NumberFormatType.COMMAS, EnergyType.FE, false)));

            if (tag.contains(FluxConstants.PRIORITY))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.PRIORITY.t() + ": " +
                        TextFormatting.RESET + tag.getInt(FluxConstants.PRIORITY)));

            if (tag.contains(FluxConstants.BUFFER))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.INTERNAL_BUFFER.t() + ": " +
                        TextFormatting.RESET + FluxUtils.format(tag.getLong(FluxConstants.BUFFER),
                        NumberFormatType.COMMAS, EnergyType.FE, false)));
            else if (tag.contains(FluxConstants.ENERGY))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.ENERGY_STORED.t() + ": " +
                        TextFormatting.RESET + FluxUtils.format(tag.getLong(FluxConstants.ENERGY),
                        NumberFormatType.COMMAS, EnergyType.FE, false)));

        } else {
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
}
