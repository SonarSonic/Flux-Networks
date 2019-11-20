package fluxnetworks.common.block;

import fluxnetworks.api.utils.NBTType;
import fluxnetworks.system.util.FluxLibs;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BlockFluxCore extends Block {

    private static Material MACHINE = (new Material.Builder(MaterialColor.BLACK)).notSolid().build();

    protected static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    VoxelShape bounding = VoxelShapes.fullCube();

    BlockFluxCore() {
        super(Block.Properties.create(MACHINE).hardnessAndResistance(0.3f, 1000000.0f));
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote) {
            return true;
        }
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileFluxCore) {
            TileFluxCore fluxCore = (TileFluxCore) tileEntity;
            if(fluxCore.playerUsing.size() > 0) {

            } else {

            }
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return bounding;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof TileFluxCore && stack.hasTag()) {
                TileFluxCore t = (TileFluxCore) tile;
                CompoundNBT tag = stack.getChildTag(FluxLibs.TAG_DROP);
                if(tag != null) {
                    t.readCustomNBT(tag, NBTType.TILE_DROP);
                }
            }
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED);
    }
}
