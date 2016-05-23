package noname.blockbuster.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Hardcoded camera model
 * 
 * Those comments are for me, by the way
 */
public class CameraModel extends ModelBase 
{
	/**
	 * Body parts of me camera: body with recording thingy, two storage thingies on top, 
	 * and super-puper awesome magnifying lens
	 */
	ModelRenderer body;
	ModelRenderer backTape;
	ModelRenderer frontTape;
	ModelRenderer lens;
	
	/**
	 * I hate hard-coded models ):[ 
	 */
	public CameraModel()
	{
		body = new ModelRenderer(this, 0, 0);
		body.addBox(-2.0F, 8.0F, -4.0F, 4, 4, 8);
		
		backTape = new ModelRenderer(this, 24, 0);
		backTape.addBox(-1.0F, 5.0F, 0.5F, 2, 3, 3);
		frontTape = new ModelRenderer(this, 24, 0);
		frontTape.addBox(-1.0F, 5.0F, -3.5F, 2, 3, 3);
		
		lens = new ModelRenderer(this, 0, 12);
		lens.addBox(-1.0F, 9.0F, -5.0F, 2, 2, 1);
		lens.setTextureOffset(0, 15).addBox(-2.0F, 8.0F, -7.0F, 4, 4, 2);
	}
	
	/**
	 * At least I can use GL commands down here, probably.
	 * Maybe it's pretty neat, maybe...
	 */
	@Override
	public void render(Entity entityIn, float p_78088_2_, float limbSwing, float ageInTicks, float netHeadYaw, float headPitch, float scale) 
	{
		backTape.render(scale);
		frontTape.render(scale);
		lens.render(scale);
		body.render(scale);
	}
}
