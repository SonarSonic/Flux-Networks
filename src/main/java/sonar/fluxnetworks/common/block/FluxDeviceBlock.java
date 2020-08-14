package sonar.fluxnetworks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import sonar.fluxnetworks.api.translate.FluxTranslate;
import sonar.fluxnetworks.api.translate.StyleUtils;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.core.FluxUtils;
import sonar.fluxnetworks.common.item.FluxConfiguratorItem;
import sonar.fluxnetworks.common.tileentity.TileFluxDevice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Defines the block base class for any flux device
 */
//TODO look table with NBT drops.
public abstract class FluxDeviceBlock extends Block {

    public FluxDeviceBlock(Properties props) {
        super(props);
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }

        if (player.getHeldItem(hand).getItem() instanceof FluxConfiguratorItem) {
            return ActionResultType.FAIL;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileFluxDevice) {
            TileFluxDevice fluxCore = (TileFluxDevice) tileEntity;
            if (fluxCore.playerUsing.size() > 0) {
                player.sendStatusMessage(StyleUtils.getErrorStyle(FluxTranslate.ACCESS_OCCUPY_KEY), true);
                return ActionResultType.SUCCESS;
            } else if (fluxCore.canAccess(player)) {
                NetworkHooks.openGui((ServerPlayerEntity) player, fluxCore, buf -> {
                    buf.writeBoolean(true);
                    buf.writeBlockPos(pos);
                });
                return ActionResultType.SUCCESS;
            } else {
                player.sendStatusMessage(StyleUtils.getErrorStyle(FluxTranslate.ACCESS_DENIED_KEY), true);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        readDataFromStack(stack, pos, worldIn); //doing this client side to prevent network flickering when placing, we send a block update next tick anyway.
        if (!worldIn.isRemote) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileFluxDevice) {
                TileFluxDevice fluxCore = (TileFluxDevice) tileEntity;
                if (placer instanceof PlayerEntity) {
                    fluxCore.playerUUID = PlayerEntity.getUUID(((PlayerEntity) placer).getGameProfile());
                }
            }
        }
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

    //TODO see FluxBlockLootTables.class
    @Deprecated
    public static void writeDataToStack(ItemStack stack, BlockPos pos, World world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFluxDevice) {
            TileFluxDevice t = (TileFluxDevice) tile;
            CompoundNBT tag = stack.getOrCreateChildTag(FluxUtils.FLUX_DATA);
            t.writeCustomNBT(tag, NBTType.TILE_DROP);
        }
    }

    protected void readDataFromStack(ItemStack stack, BlockPos pos, World world) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFluxDevice && stack.hasTag()) {
            TileFluxDevice t = (TileFluxDevice) tile;
            CompoundNBT tag = stack.getChildTag(FluxUtils.FLUX_DATA);
            if (tag != null) {
                t.readCustomNBT(tag, NBTType.TILE_DROP);
            }
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
