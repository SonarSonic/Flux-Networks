package fluxnetworks.common.block;

import fluxnetworks.FluxConfig;
import fluxnetworks.FluxNetworks;
import fluxnetworks.FluxTranslate;
import fluxnetworks.client.render.ItemFluxStorageRenderer;
import fluxnetworks.common.registry.RegistryBlocks;
import fluxnetworks.common.tileentity.TileFluxStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluxStorage extends BlockFluxCore {

    public BlockFluxStorage(String name) {
        super(name);
    }

    public BlockFluxStorage() {
        super("FluxStorage");
    }

    public int getMaxStorage() {
        return FluxConfig.basicCapacity;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFluxStorage();
    }

    public static class Herculean extends BlockFluxStorage {

        public Herculean() {
            super("HerculeanFluxStorage");
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(World world, IBlockState state) {
            return new TileFluxStorage.Herculean();
        }

        @Override
        public int getMaxStorage() {
            return FluxConfig.herculeanCapacity;
        }
    }

    public static class Gargantuan extends BlockFluxStorage {

        public Gargantuan() {
            super("GargantuanFluxStorage");
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(World world, IBlockState state) {
            return new TileFluxStorage.Gargantuan();
        }

        @Override
        public int getMaxStorage() {
            return FluxConfig.gargantuanCapacity;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(FluxTranslate.FLUX_STORAGE_TOOLTIP.t());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels() {
        Item.getItemFromBlock(this).setTileEntityItemStackRenderer(ItemFluxStorageRenderer.INSTANCE);
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(FluxNetworks.MODID + ':' + "fluxstoragebuiltin", "inventory"));
    }
}
