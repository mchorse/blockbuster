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
public class ModelCamera extends ModelBase
{
    /**
     * Body parts of me camera: body with recording thingy, two storage thingies
     * on top, and super-puper awesome magnifying lens
     */
    private ModelRenderer body;
    private ModelRenderer lens;
    private ModelRenderer cap;
    private ModelRenderer button;
    private ModelRenderer viewFinder;

    /**
     * So there's the deal with the coordinate system:
     *
     * - y=16 is actually the bottom of the entity
     * - x=0,y=0 is the center point (x and z) above the head (max y)
     * - z<0 shifts in direction of head rotation (where the entity looks)
     * - Box's origin is actually bottom, left, front corner of the box (relative to the entity's yaw rotation)
     *
     * Still hate hard-coded models
     */
    public ModelCamera()
    {
        int yOffset = 16;

        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-3, yOffset - 3, -2, 6, 6, 10);

        this.lens = new ModelRenderer(this, 14, 16);
        this.lens.addBox(-2.5F, yOffset - 2.5F, -6.0F, 5, 5, 4);
        this.lens.setTextureOffset(22, 0).addBox(-2F, yOffset - 2F, -7.0F, 4, 4, 1);

        this.cap = new ModelRenderer(this, 0, 16);
        this.cap.addBox(3, yOffset - 2, 1, 1, 4, 6);

        this.button = new ModelRenderer(this, 0, 0);
        this.button.addBox(-3.5F, yOffset - 2F, 6F, 1, 1, 1);

        this.viewFinder = new ModelRenderer(this, 32, 5);
        this.viewFinder.addBox(-1.5F, yOffset - 4.0F, 6, 3, 3, 4);
        this.viewFinder.setTextureOffset(32, 0).addBox(-1.0F, yOffset - 3.5F, 10.0F, 2, 2, 3);
    }

    /**
     * At least I can use GL commands down here, probably (yes I can!). Maybe
     * it's pretty neat, maybe...
     */
    @Override
    public void render(Entity entityIn, float p_78088_2_, float limbSwing, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.body.render(scale);
        this.lens.render(scale);
        this.cap.render(scale);
        this.button.render(scale);
        this.viewFinder.render(scale);
    }
}
