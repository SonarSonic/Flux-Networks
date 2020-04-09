package sonar.fluxnetworks.common.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.utils.EnergyType;
import sonar.fluxnetworks.client.FluxColorHandler;
import sonar.fluxnetworks.common.data.FluxNetworkData;
import sonar.fluxnetworks.common.core.FluxUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class FluxConnectorBlockItem extends BlockItem {

    public static String CUSTOM_NAME = "customName";
    public static String PRIORITY = "priority";
    public static String SURGE_MODE = "surgeMode";
    public static String LIMIT = "limit";
    public static String DISABLE_LIMIT = "disableLimit";

    public FluxConnectorBlockItem(Block block, Item.Properties props) {
        super(block, props);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
        if(tag != null && tag.contains(CUSTOM_NAME)) {
            return new StringTextComponent(tag.getString(CUSTOM_NAME));
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
        if(tag != null) {
            tooltip.add(new StringTextComponent(FluxTranslate.NETWORK_FULL_NAME.t() + ": " + TextFormatting.WHITE + FluxColorHandler.getOrRequestNetworkName(tag.getInt(FluxNetworkData.NETWORK_ID))));
            tooltip.add(new StringTextComponent(FluxTranslate.TRANSFER_LIMIT.t() + ": " + TextFormatting.WHITE + FluxUtils.format(tag.getLong(LIMIT), FluxUtils.TypeNumberFormat.COMMAS, " " + EnergyType.FE.getStorageSuffix())));
            tooltip.add(new StringTextComponent(FluxTranslate.PRIORITY.t() + ": " + TextFormatting.WHITE + tag.getInt(PRIORITY)));
            if(tag.contains("energy")) {
                tooltip.add(new StringTextComponent(FluxTranslate.ENERGY_STORED.t() + ": " + TextFormatting.WHITE + NumberFormat.getInstance().format(tag.getInt("energy")) + " " + EnergyType.FE.getStorageSuffix()));
            } else {
                tooltip.add(new StringTextComponent(FluxTranslate.INTERNAL_BUFFER.t() + ": " + TextFormatting.WHITE + FluxUtils.format(tag.getLong("buffer"), FluxUtils.TypeNumberFormat.COMMAS, EnergyType.FE.getStorageSuffix())));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

}
