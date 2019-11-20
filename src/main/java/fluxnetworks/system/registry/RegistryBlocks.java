package fluxnetworks.system.registry;

import fluxnetworks.common.block.BlockFluxController;
import fluxnetworks.common.block.BlockFluxPlug;
import fluxnetworks.common.block.BlockFluxPoint;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class RegistryBlocks {

    public static List<Block> BLOCKS = new ArrayList<>();

    public static final Block FLUX_BLOCK = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5f, 17.5f)).setRegistryName("fluxblock");
    public static final Block FLUX_PLUG = new BlockFluxPlug().setRegistryName("fluxplug");
    public static final Block FLUX_POINT = new BlockFluxPoint().setRegistryName("fluxpoint");
    public static final Block FLUX_CONTROLLER = new BlockFluxController().setRegistryName("fluxcontroller");

    static {
        BLOCKS.add(FLUX_PLUG);
        BLOCKS.add(FLUX_POINT);
        BLOCKS.add(FLUX_CONTROLLER);
    }
}
