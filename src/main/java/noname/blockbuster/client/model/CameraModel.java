package noname.blockbuster.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Hardcoded camera model
 *
 * Those comments are for me, by the way
 */
@SideOnly(Side.CLIENT)
public class CameraModel extends ModelBase
{
    /**
     * Body parts of me camera: body with recording thingy, two storage thingies
     * on top, and super-puper awesome magnifying lens
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
        float yOffset = 5.0F;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-2.0F, 8.0F + yOffset, -4.0F, 4, 4, 8);

        this.backTape = new ModelRenderer(this, 24, 0);
        this.backTape.addBox(-1.0F, 5.0F + yOffset, 0.5F, 2, 3, 3);
        this.frontTape = new ModelRenderer(this, 24, 0);
        this.frontTape.addBox(-1.0F, 5.0F + yOffset, -3.5F, 2, 3, 3);

        this.lens = new ModelRenderer(this, 0, 12);
        this.lens.addBox(-1.0F, 9.0F + yOffset, -5.0F, 2, 2, 1);
        this.lens.setTextureOffset(0, 15).addBox(-2.0F, 8.0F + yOffset, -7.0F, 4, 4, 2);
    }

    /**
     * At least I can use GL commands down here, probably (yes I can!). Maybe
     * it's pretty neat, maybe...
     */
    @Override
    public void render(Entity entityIn, float p_78088_2_, float limbSwing, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.backTape.render(scale);
        this.frontTape.render(scale);
        this.lens.render(scale);
        this.body.render(scale);
    }
}
