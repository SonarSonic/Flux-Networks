package icyllis.fluxnetworks.system.registry;

import icyllis.fluxnetworks.common.block.BlockFluxController;
import icyllis.fluxnetworks.common.block.BlockFluxPlug;
import icyllis.fluxnetworks.common.block.BlockFluxPoint;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class RegistryBlocks {

    public static List<Block> MACHINE_BLOCKS = new ArrayList<>();

    public static final Block FLUX_BLOCK = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5f, 17.5f)).setRegistryName("fluxblock");
    public static final Block FLUX_PLUG = new BlockFluxPlug().setRegistryName("fluxplug");
    public static final Block FLUX_POINT = new BlockFluxPoint().setRegistryName("fluxpoint");
    public static final Block FLUX_CONTROLLER = new BlockFluxController().setRegistryName("fluxcontroller");

    static {
        MACHINE_BLOCKS.add(FLUX_PLUG);
        MACHINE_BLOCKS.add(FLUX_POINT);
        MACHINE_BLOCKS.add(FLUX_CONTROLLER);
    }
}
