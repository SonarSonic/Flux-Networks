package fluxnetworks.common.item;

import fluxnetworks.FluxConfig;
import fluxnetworks.FluxTranslate;
import fluxnetworks.api.Capabilities;
import fluxnetworks.api.network.ISuperAdmin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemAdminConfigurator extends ItemConfigurator {

    public ItemAdminConfigurator() {
        super("AdminConfigurator");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        //return EnumActionResult.PASS;
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if(playerIn.isSneaking()) {
            ItemStack itemstack = playerIn.getHeldItem(handIn);
            if(!worldIn.isRemote) {
                ISuperAdmin sa = playerIn.getCapability(Capabilities.SUPER_ADMIN, null);
                if (sa != null) {
                    sa.changePermission();
                    playerIn.sendStatusMessage(new TextComponentString(sa.getPermission() ? TextFormatting.DARK_PURPLE + FluxTranslate.SA_ON : FluxTranslate.SA_OFF), true);
                }
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

}
