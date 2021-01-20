package sonar.fluxnetworks.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import sonar.fluxnetworks.common.block.*;

import java.util.ArrayList;
import java.util.List;

public class RegistryBlocks {

    public static final List<Block> BLOCKS = new ArrayList<Block>();

    public static final Block FLUX_BLOCK = new BlockCore("FluxBlock", Material.ROCK);
    public static final Block FLUX_PLUG = new BlockFluxPlug();
    public static final Block FLUX_POINT = new BlockFluxPoint();
    public static final Block FLUX_CONTROLLER = new BlockFluxController();
    public static final Block FLUX_STORAGE_1 = new BlockFluxStorage();
    public static final Block FLUX_STORAGE_2 = new BlockFluxStorage.Herculean();
    public static final Block FLUX_STORAGE_3 = new BlockFluxStorage.Gargantuan();
}
