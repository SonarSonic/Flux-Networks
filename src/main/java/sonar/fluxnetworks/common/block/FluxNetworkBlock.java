package sonar.fluxnetworks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.translate.StyleUtils;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import sonar.fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class FluxNetworkBlock extends Block {

    public FluxNetworkBlock(Properties props) {
        super(props);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if(worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }

        if(player.getHeldItem(hand).getItem() instanceof FluxConfiguratorItem) {
            return ActionResultType.FAIL;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileFluxCore) {
            TileFluxCore fluxCore = (TileFluxCore) tileEntity;
            if(fluxCore.playerUsing.size() > 0) {
                player.sendStatusMessage(StyleUtils.getErrorStyle(FluxTranslate.ACCESS_OCCUPY_KEY), true);
                return ActionResultType.SUCCESS;
            } else if(fluxCore.canAccess(player)) {
                NetworkHooks.openGui((ServerPlayerEntity) player, fluxCore, buf -> { buf.writeBoolean(true); buf.writeBlockPos(pos);});
                return ActionResultType.SUCCESS;
            }else{
                player.sendStatusMessage(StyleUtils.getErrorStyle(FluxTranslate.ACCESS_DENIED_KEY), true);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public ResourceLocation getLootTable() {
        return null; //TODO look table with NBT drops.
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        readDataFromStack(stack, pos, worldIn); //doing this client side to prevent network flickering when placing, we send a block update next tick anyway.
        if(!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileFluxCore) {
                TileFluxCore fluxCore = (TileFluxCore) tileEntity;
                if(placer instanceof PlayerEntity) {
                    fluxCore.playerUUID = PlayerEntity.getUUID(((PlayerEntity) placer).getGameProfile());
                }
            }
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        if(world.isRemote) {
            return false;
        }

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileFluxCore) {
            TileFluxCore tileFluxCore = (TileFluxCore) tile;
            if(tileFluxCore.canAccess(player)) {
                ItemStack stack = new ItemStack(this, 1);
                writeDataToStack(stack, pos, world);

                float motion = 0.7F;
                double motionX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
                double motionY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
                double motionZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

                ItemEntity entityItem = new ItemEntity(world, pos.getX() + motionX, pos.getY() + motionY, pos.getZ() + motionZ, stack);

                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                world.addEntity(entityItem);
                return true;
            }else{
                player.sendStatusMessage(StyleUtils.getErrorStyle(FluxTranslate.REMOVAL_DENIED_KEY), true);
            }
        }
        return false;
    }

    public static void writeDataToStack(ItemStack stack, BlockPos pos, World world) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileFluxCore) {
            TileFluxCore t = (TileFluxCore) tile;
            CompoundNBT tag = stack.getOrCreateChildTag(FluxUtils.FLUX_DATA);
            t.writeCustomNBT(tag, NBTType.TILE_DROP);
        }
    }

    protected void readDataFromStack(ItemStack stack, BlockPos pos, World world) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileFluxCore && stack.hasTag()) {
            TileFluxCore t = (TileFluxCore) tile;
            CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
            if(tag != null) {
                t.readCustomNBT(tag, NBTType.TILE_DROP);
            }
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

}
