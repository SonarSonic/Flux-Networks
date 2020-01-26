package icyllis.fluxnetworks.common.block;

import icyllis.fluxnetworks.api.util.NBTType;
import icyllis.fluxnetworks.system.FluxNetworks;
import icyllis.fluxnetworks.system.util.FluxLibs;
import icyllis.fluxnetworks.common.tileentity.TileFluxCore;
import icyllis.fluxnetworks.system.util.FluxUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class BlockFluxCore extends Block {

    private static Material MACHINE = (new Material.Builder(MaterialColor.BLACK)).notSolid().build();

    protected static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    VoxelShape bounding = VoxelShapes.fullCube();

    BlockFluxCore() {
        super(Block.Properties.create(MACHINE).hardnessAndResistance(0.3f, 1000000.0f));
        setDefaultState(getStateContainer().getBaseState().with(CONNECTED, false));
        RenderTypeLookup.setRenderLayer(this, RenderType.cutout());
    }

    @Override
    @Deprecated
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        }
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileFluxCore) {
            TileFluxCore fluxCore = (TileFluxCore) tileEntity;
            if(fluxCore.playerUsing.size() > 0) {

            } else {

            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return bounding;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!worldIn.isRemote) {
            if(stack.hasTag()) {
                FluxUtils.getFluxTE(worldIn, pos).ifPresent(t -> {
                    CompoundNBT tag = stack.getChildTag(FluxLibs.TAG_DROP);
                    if(tag != null) {
                        t.readNetworkNBT(tag, NBTType.TILE_DROP);
                    }
                });
            }
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
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
