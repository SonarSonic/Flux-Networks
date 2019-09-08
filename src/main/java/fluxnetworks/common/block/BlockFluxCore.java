package fluxnetworks.common.block;

import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.common.connection.NetworkMember;
import fluxnetworks.common.core.FluxUtils;
import fluxnetworks.common.core.NBTType;
import fluxnetworks.common.item.ItemConfigurator;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public abstract class BlockFluxCore extends BlockCore {

    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    public AxisAlignedBB bounding = FULL_BLOCK_AABB;

    public BlockFluxCore(String name) {
        super(name, FluxUtils.MACHINE, true);
        setHardness(0.2f);
        setResistance(1000000);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if(worldIn.isRemote) {
            return true;
        }

        if(playerIn.getHeldItem(hand).getItem() instanceof ItemConfigurator) {
            return false;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileFluxCore) {
            TileFluxCore fluxCore = (TileFluxCore) tileEntity;
            if(fluxCore.playerUsing.size() > 0) {
                playerIn.sendStatusMessage(new TextComponentString(TextFormatting.DARK_RED + FluxTranslate.EMPTY + TextFormatting.BOLD + FluxTranslate.ACCESS_OCCUPY), true);
                return true;
            } else if(fluxCore.canAccess(playerIn)) {
                playerIn.openGui(FluxNetworks.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
            }
        }

        playerIn.sendStatusMessage(new TextComponentString(TextFormatting.DARK_RED + FluxTranslate.EMPTY + TextFormatting.BOLD + FluxTranslate.ACCESS_DENIED), true);

        return true;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(!worldIn.isRemote) {
            readDataFromStack(stack, pos, worldIn);
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileFluxCore) {
                TileFluxCore fluxCore = (TileFluxCore) tileEntity;
                if(placer instanceof EntityPlayer) {
                    fluxCore.playerUUID = EntityPlayer.getUUID(((EntityPlayer) placer).getGameProfile());
                }
            }
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if(world.isRemote) {
            return false;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFluxCore) {
            TileFluxCore tileFluxCore = (TileFluxCore) tile;
            if(tileFluxCore.canAccess(player)) {
                ItemStack stack = new ItemStack(this, 1, damageDropped(state));
                writeDataToStack(stack, pos, world);
                EntityItem entityItem = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
                world.setBlockToAir(pos);
                world.spawnEntity(entityItem);
                return true;
            }
        }
        player.sendStatusMessage(new TextComponentString(TextFormatting.DARK_RED + FluxTranslate.EMPTY + TextFormatting.BOLD + FluxTranslate.REMOVAL_DENIED), true);
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileFluxCore tile = (TileFluxCore) worldIn.getTileEntity(pos);
        state = state.withProperty(CONNECTED, tile.connected);
        return state;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.removeTileEntity(pos);
    }

    protected void writeDataToStack(ItemStack stack, BlockPos pos, World world) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileFluxCore) {
            TileFluxCore t = (TileFluxCore) tile;
            NBTTagCompound tag = stack.getOrCreateSubCompound(FluxUtils.FLUX_DATA);
            t.writeCustomNBT(tag, NBTType.DROP);
        }
    }

    protected void readDataFromStack(ItemStack stack, BlockPos pos, World world) {
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof TileFluxCore && stack.hasTagCompound()) {
            TileFluxCore t = (TileFluxCore) tile;
            NBTTagCompound tag = stack.getSubCompound(FluxUtils.FLUX_DATA);
            if(tag != null)
                t.readCustomNBT(tag, NBTType.DROP);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return bounding;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECTED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CONNECTED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CONNECTED) ? 1 : 0;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
