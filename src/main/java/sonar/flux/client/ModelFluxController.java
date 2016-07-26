package sonar.flux.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelFluxController extends ModelBase
{
  //fields
    ModelRenderer Base1;
    ModelRenderer Top1;
    ModelRenderer Rod1;
    ModelRenderer Rod2;
    ModelRenderer Rod3;
    ModelRenderer Rod4;
    ModelRenderer Rod5;
    ModelRenderer Rod6;
    ModelRenderer Rod7;
    ModelRenderer Rod8;
    ModelRenderer Centre1;
    ModelRenderer Centre2;
    ModelRenderer Centre3;
  
  public ModelFluxController()
  {
    textureWidth = 128;
    textureHeight = 64;
    
      Base1 = new ModelRenderer(this, 0, 0);
      Base1.addBox(0F, 0F, 0F, 16, 1, 16);
      Base1.setRotationPoint(-8F, 23F, 8F);
      Base1.setTextureSize(128, 64);
      Base1.mirror = true;
      setRotation(Base1, 3.141593F, 0F, 0F);
      Top1 = new ModelRenderer(this, 0, 0);
      Top1.addBox(0F, 0F, 0F, 16, 1, 16);
      Top1.setRotationPoint(-8F, 9F, -8F);
      Top1.setTextureSize(128, 64);
      Top1.mirror = true;
      setRotation(Top1, 0F, 0F, 0F);
      Rod1 = new ModelRenderer(this, 0, 17);
      Rod1.addBox(0F, 0F, 0F, 2, 16, 2);
      Rod1.setRotationPoint(5F, 8F, -7F);
      Rod1.setTextureSize(128, 64);
      Rod1.mirror = true;
      setRotation(Rod1, 0F, 0F, 0F);
      Rod2 = new ModelRenderer(this, 0, 17);
      Rod2.addBox(0F, 0F, 0F, 2, 16, 2);
      Rod2.setRotationPoint(-7F, 8F, -7F);
      Rod2.setTextureSize(128, 64);
      Rod2.mirror = true;
      setRotation(Rod2, 0F, 0F, 0F);
      Rod3 = new ModelRenderer(this, 0, 17);
      Rod3.addBox(-1F, 0F, -1F, 2, 16, 2);
      Rod3.setRotationPoint(6F, 8F, 6F);
      Rod3.setTextureSize(128, 64);
      Rod3.mirror = true;
      setRotation(Rod3, 0F, 0F, 0F);
      Rod4 = new ModelRenderer(this, 0, 17);
      Rod4.addBox(0F, 0F, 0F, 2, 16, 2);
      Rod4.setRotationPoint(-7F, 8F, 5F);
      Rod4.setTextureSize(128, 64);
      Rod4.mirror = true;
      setRotation(Rod4, 0F, 0F, 0F);
      Rod5 = new ModelRenderer(this, 8, 17);
      Rod5.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
      Rod5.setRotationPoint(5F, 10F, 5F);
      Rod5.setTextureSize(128, 64);
      Rod5.mirror = true;
      setRotation(Rod5, 0F, 0.7853982F, 0F);
      Rod6 = new ModelRenderer(this, 8, 17);
      Rod6.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
      Rod6.setRotationPoint(-5F, 10F, 5F);
      Rod6.setTextureSize(128, 64);
      Rod6.mirror = true;
      setRotation(Rod6, 0F, 0.7853982F, 0F);
      Rod7 = new ModelRenderer(this, 8, 17);
      Rod7.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
      Rod7.setRotationPoint(-5F, 10F, -5F);
      Rod7.setTextureSize(128, 64);
      Rod7.mirror = true;
      setRotation(Rod7, 0F, 0.7853982F, 0F);
      Rod8 = new ModelRenderer(this, 8, 17);
      Rod8.addBox(-0.5F, 0F, -0.5F, 1, 12, 1);
      Rod8.setRotationPoint(5F, 10F, -5F);
      Rod8.setTextureSize(128, 64);
      Rod8.mirror = true;
      setRotation(Rod8, 0F, 0.7853982F, 0F);
      Centre1 = new ModelRenderer(this, 12, 17);
      Centre1.addBox(-3F, 0F, -3F, 6, 3, 6);
      Centre1.setRotationPoint(0F, 21F, 0F);
      Centre1.setTextureSize(128, 64);
      Centre1.mirror = true;
      setRotation(Centre1, 0F, 0.7853982F, 0F);
      Centre2 = new ModelRenderer(this, 12, 17);
      Centre2.addBox(-3F, 0F, -3F, 6, 3, 6);
      Centre2.setRotationPoint(0F, 8F, 0F);
      Centre2.setTextureSize(128, 64);
      Centre2.mirror = true;
      setRotation(Centre2, 0F, 0.7853982F, 0F);
      Centre3 = new ModelRenderer(this, 36, 17);
      Centre3.addBox(-0.5F, 0F, -0.5F, 1, 10, 1);
      Centre3.setRotationPoint(0F, 11F, 0F);
      Centre3.setTextureSize(128, 64);
      Centre3.mirror = true;
      setRotation(Centre3, 0F, 0.7853982F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    Base1.render(f5);
    Top1.render(f5);
    Rod1.render(f5);
    Rod2.render(f5);
    Rod3.render(f5);
    Rod4.render(f5);
    Rod5.render(f5);
    Rod6.render(f5);
    Rod7.render(f5);
    Rod8.render(f5);
    Centre1.render(f5);
    Centre2.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(Entity entity,float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
