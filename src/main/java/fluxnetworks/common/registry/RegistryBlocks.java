package fluxnetworks.common.registry;

import fluxnetworks.common.block.BlockController;
import fluxnetworks.common.block.BlockCore;
import fluxnetworks.common.block.BlockFluxPlug;
import fluxnetworks.common.block.BlockFluxPoint;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class RegistryBlocks {

    public static final List<Block> BLOCKS = new ArrayList<Block>();

    public static final Block FLUX_BLOCK = new BlockCore("FluxBlock", Material.ROCK);
    public static final Block FLUX_CONTROLLER = new BlockController();
    public static final Block FLUX_POINT = new BlockFluxPoint();
    public static final Block FLUX_PLUG = new BlockFluxPlug();
}
