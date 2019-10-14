package fluxnetworks.common.core.registry;

import fluxnetworks.common.block.BlockCore;
import fluxnetworks.common.block.BlockFluxPlug;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class RegistryBlocks {

    public static final List<Block> BLOCKS = new ArrayList<>();

    public static final Block FLUX_BLOCK = new BlockCore("FluxBlock", Block.Properties.create(Material.ROCK));
    public static final Block FLUX_PLUG = new BlockFluxPlug();
}
