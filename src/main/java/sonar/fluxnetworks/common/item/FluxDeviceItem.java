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
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.misc.FluxUtils;
import sonar.fluxnetworks.common.storage.FluxNetworkData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class FluxDeviceItem extends BlockItem {

    public static String CUSTOM_NAME   = "customName";
    public static String PRIORITY      = "priority";
    public static String SURGE_MODE    = "surgeMode";
    public static String LIMIT         = "limit";
    public static String DISABLE_LIMIT = "disableLimit";

    public FluxDeviceItem(Block block, Item.Properties props) {
        super(block, props);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
        if (tag != null && tag.contains(CUSTOM_NAME)) {
            return new StringTextComponent(tag.getString(CUSTOM_NAME));
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
        if (tag != null) {
            if (tag.contains(FluxNetworkData.NETWORK_ID))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.NETWORK_FULL_NAME.t() + ": " + TextFormatting.RESET + FluxColorHandler.INSTANCE.getOrRequestNetworkName(tag.getInt(FluxNetworkData.NETWORK_ID))));

            if (tag.contains(LIMIT))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.RESET + FluxUtils.format(tag.getLong(LIMIT), FluxUtils.TypeNumberFormat.COMMAS, " " + EnergyType.FE.getStorageSuffix())));

            if (tag.contains(PRIORITY))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.PRIORITY.t() + ": " + TextFormatting.RESET + tag.getInt(PRIORITY)));

            if (tag.contains("energy"))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.RESET + NumberFormat.getInstance().format(tag.getInt("energy")) + " " + EnergyType.FE.getStorageSuffix()));

            if (tag.contains("buffer"))
                tooltip.add(new StringTextComponent(TextFormatting.BLUE + FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.RESET + FluxUtils.format(tag.getLong("buffer"), FluxUtils.TypeNumberFormat.COMMAS, EnergyType.FE.getStorageSuffix())));

        } else {
            super.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

}
