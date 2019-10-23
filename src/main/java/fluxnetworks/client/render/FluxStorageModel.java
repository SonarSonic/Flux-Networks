package fluxnetworks.client.render;

import fluxnetworks.FluxNetworks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class FluxStorageModel extends BuiltInModel implements IModel, IBakedModel, ICustomModelLoader {

    public static final FluxStorageModel INSTANCE = new FluxStorageModel(ItemCameraTransforms.DEFAULT, ItemOverrideList.NONE);
    public static ItemCameraTransforms.TransformType CURRENT_TRANSFORM = ItemCameraTransforms.TransformType.NONE;

    public FluxStorageModel(ItemCameraTransforms cameraTransforms, ItemOverrideList overrideList) {
        super(cameraTransforms, overrideList);
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return INSTANCE;
    }

    public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        CURRENT_TRANSFORM = cameraTransformType;
        return super.handlePerspective(cameraTransformType);
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if(modelLocation.getNamespace().equals(FluxNetworks.MODID)){
            return modelLocation.getPath().equals("models/item/fluxstoragebuiltin");
        }
        return false;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return INSTANCE;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {}
}
