package fluxnetworks.common.item;

import fluxnetworks.client.FluxColorHandler;
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
import java.util.List;

public class ItemFluxConnector extends ItemBlock {

    public ItemFluxConnector(Block block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound("fluxData");
        if(tag != null && tag.hasKey("CustomName")) {
            return tag.getString("CustomName");
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound tag = stack.getSubCompound("fluxData");
        if(tag != null) {
            tooltip.add("Network Name: " + TextFormatting.WHITE + FluxColorHandler.getOrRequestNetworkName(tag.getInteger("NetworkID")));
            tooltip.add("Transfer Limit: " + TextFormatting.WHITE + tag.getLong("Limit"));
            tooltip.add("Priority: " + TextFormatting.WHITE + tag.getInteger("Priority"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
