package sonar.fluxnetworks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.api.text.StyleUtils;
import sonar.fluxnetworks.common.item.ItemFluxConfigurator;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Defines the block base class for any flux device
 */
public abstract class FluxDeviceBlock extends Block {

    public FluxDeviceBlock(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos,
                                             @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult result) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }

        if (player.getHeldItem(hand).getItem() instanceof ItemFluxConfigurator) {
            return ActionResultType.FAIL;
        }

        TileEntity tile = worldIn.getTileEntity(pos);

        if (tile instanceof TileFluxDevice) {
            TileFluxDevice flux = (TileFluxDevice) tile;
            if (!flux.playerUsing.isEmpty()) {
                player.sendStatusMessage(StyleUtils.error(FluxTranslate.ACCESS_OCCUPY), true);
                return ActionResultType.SUCCESS;
            } else if (flux.canPlayerAccess(player)) {
                NetworkHooks.openGui((ServerPlayerEntity) player, flux, buf -> {
                    buf.writeBoolean(true);
                    buf.writeBlockPos(pos);
                });
                return ActionResultType.SUCCESS;
            } else {
                player.sendStatusMessage(StyleUtils.error(FluxTranslate.ACCESS_DENIED), true);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileFluxDevice) {
            TileFluxDevice flux = (TileFluxDevice) tile;
            if (stack.hasTag()) {
                CompoundNBT tag = stack.getChildTag(FluxConstants.TAG_FLUX_DATA);
                if (tag != null) {
                    // doing this client side to prevent network flickering when placing, we send a block update next tick anyway.
                    flux.readCustomNBT(tag, FluxConstants.TYPE_TILE_DROP);
                }
            }
            if (placer instanceof ServerPlayerEntity) {
                flux.setConnectionOwner(PlayerEntity.getUUID(((PlayerEntity) placer).getGameProfile()));
            }
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, @Nonnull World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileFluxDevice)
                if (!((TileFluxDevice) tile).canPlayerAccess(player)) {
                    player.sendStatusMessage(StyleUtils.error(FluxTranslate.REMOVAL_DENIED), true);
                    return false;
                }
        }
        onBlockHarvested(world, pos, state, player);
        return world.setBlockState(pos, fluid.getBlockState(), world.isRemote ?
                Constants.BlockFlags.DEFAULT_AND_RERENDER : Constants.BlockFlags.DEFAULT);
    }

    /*@Override
    public void harvestBlock(World worldIn, PlayerEntity player, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable TileEntity te, @Nonnull ItemStack heldStack) {
        player.addStat(Stats.BLOCK_MINED.get(this));
        player.addExhaustion(0.005F);
        if (worldIn instanceof ServerWorld) {
            TileEntity tile = worldIn.getTileEntity(pos);

            if (tile instanceof TileFluxCore) {
                TileFluxCore tileFluxCore = (TileFluxCore) tile;
                //if(tileFluxCore.canAccess(player)) {
                ItemStack stack = new ItemStack(this, 1);
                writeDataToStack(stack, pos, worldIn);
                spawnAsEntity(worldIn, pos, stack);

                    *//*float motion = 0.7F;
                    double motionX = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
                    double motionY = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;
                    double motionZ = (world.rand.nextFloat() * motion) + (1.0F - motion) * 0.5D;

                    ItemEntity entityItem = new ItemEntity(world, pos.getX() + motionX, pos.getY() + motionY, pos.getZ() + motionZ, stack);*//*

                //world.removeBlock(pos, false);
                //world.addEntity(entityItem);
                //   return true;
                //}else{
                //    player.sendStatusMessage(StyleUtils.getErrorStyle(FluxTranslate.REMOVAL_DENIED_KEY), true);
                //}
            }
        }
    }*/

    // see FluxBlockLootTables
    /*public static void writeDataToStack(ItemStack stack, BlockPos pos, World world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFluxDevice) {
            TileFluxDevice t = (TileFluxDevice) tile;
            CompoundNBT tag = stack.getOrCreateChildTag(FluxUtils.FLUX_DATA);
            t.writeCustomNBT(tag, NBTType.TILE_DROP);
        }
    }

    public void readDataFromStack(ItemStack stack, BlockPos pos, @Nonnull World world) {

    }*/

    @Override
    public final boolean hasTileEntity(BlockState state) {
        return true;
    }
}
