package fluxnetworks.common.item;

import fluxnetworks.api.EnergyType;
import fluxnetworks.client.FluxColorHandler;
import fluxnetworks.common.data.FluxNetworkData;
import fluxnetworks.common.core.FluxUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class ItemFluxConnector extends ItemBlock {

    public static String CUSTOM_NAME = "customName";
    public static String PRIORITY = "priority";
    public static String SURGE_MODE = "surgeMode";
    public static String LIMIT = "limit";
    public static String DISABLE_LIMIT = "disableLimit";

    public ItemFluxConnector(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound(FluxUtils.FLUX_DATA);
        if(tag != null && tag.hasKey(CUSTOM_NAME)) {
            return tag.getString(CUSTOM_NAME);
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound tag = stack.getSubCompound(FluxUtils.FLUX_DATA);
        if(tag != null) {
            tooltip.add("Network Name: " + TextFormatting.WHITE + FluxColorHandler.getOrRequestNetworkName(tag.getInteger(FluxNetworkData.NETWORK_ID)));
            tooltip.add("Transfer Limit: " + TextFormatting.WHITE + FluxUtils.format(tag.getLong(LIMIT), FluxUtils.TypeNumberFormat.COMMAS, EnergyType.RF.getStorageSuffix()));
            tooltip.add("Priority: " + TextFormatting.WHITE + tag.getInteger(PRIORITY));
            if(tag.hasKey("energy")) {
                tooltip.add("Energy Stored: " + TextFormatting.WHITE + NumberFormat.getInstance().format(tag.getInteger("energy")) + "RF");
            } else {
                tooltip.add("Internal Buffer: " + TextFormatting.WHITE + FluxUtils.format(tag.getLong("buffer"), FluxUtils.TypeNumberFormat.COMMAS, "RF"));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
