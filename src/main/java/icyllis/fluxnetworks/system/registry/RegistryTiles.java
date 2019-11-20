package icyllis.fluxnetworks.system.registry;

import icyllis.fluxnetworks.common.tileentity.TileFluxPlug;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.List;

public class RegistryTiles {

    public static final List<TileEntityType<?>> TILES = new ArrayList<>();

    public static final TileEntityType<?> FLUX_PLUG = TileEntityType.Builder.create(TileFluxPlug::new, RegistryBlocks.FLUX_PLUG).build(null).setRegistryName("fluxplug");

    static {
        TILES.add(FLUX_PLUG);
    }

}
