package sonar.fluxnetworks.client.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import sonar.fluxnetworks.FluxNetworks;

import javax.annotation.Nonnull;

/**
 * Render energy sides.
 */
public class FluxStorageRenderType extends RenderType {

    private static final ResourceLocation ENERGY_TEXTURE = FluxNetworks.location(
            "textures/model/flux_storage_energy.png");

    private static final RenderType INSTANCE;

    static {
        /*CompositeState state = CompositeState.builder()
                .setTextureState(new TextureStateShard(ENERGY_TEXTURE, false, false))
                .setSha(SHADE_ENABLED)
                .diffuseLighting(DIFFUSE_LIGHTING_DISABLED)
                .alpha(DEFAULT_ALPHA)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_ENABLED)
                .build(false);
        INSTANCE = create(FluxNetworks.MODID + ":storage_energy", DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 256, state);*/
        INSTANCE = entityTranslucentCull(ENERGY_TEXTURE);
        /*state = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(ENERGY_TEXTURE, false, false))
                .shadeModel(SHADE_ENABLED)
                .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                .alpha(DEFAULT_ALPHA)
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_ENABLED)
                .build(false);
        DIFFUSE = makeType(FluxNetworks.MODID + ":storage_energy_diffuse", DefaultVertexFormat.NEW_ENTITY,
                GL11.GL_QUADS, 256, state);*/
    }

    private FluxStorageRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                  boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState,
                                  Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    @Nonnull
    public static RenderType getType() {
        return INSTANCE;
    }
}
