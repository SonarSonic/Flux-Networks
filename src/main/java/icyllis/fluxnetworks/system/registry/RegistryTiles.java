package icyllis.fluxnetworks.system.registry;

import icyllis.fluxnetworks.common.tileentity.TileFluxPlug;
import icyllis.fluxnetworks.common.tileentity.TileFluxPoint;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.List;

public class RegistryTiles {

    public static final List<TileEntityType<?>> TILES = new ArrayList<>();

    public static final TileEntityType<?> FLUX_PLUG = TileEntityType.Builder.create(TileFluxPlug::new, RegistryBlocks.FLUX_PLUG).build(null).setRegistryName("fluxplug");
    public static final TileEntityType<?> FLUX_POINT = TileEntityType.Builder.create(TileFluxPoint::new, RegistryBlocks.FLUX_POINT).build(null).setRegistryName("fluxpoint");

    static {
        TILES.add(FLUX_PLUG);
        TILES.add(FLUX_POINT);
    }

}
