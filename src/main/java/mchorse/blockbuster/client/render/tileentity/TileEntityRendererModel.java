package mchorse.blockbuster.client.render.tileentity;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;

public class TileEntityRendererModel extends TileEntitySpecialRenderer<TileEntityModel>
{
    @Override
    public void renderTileEntityAt(TileEntityModel te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);

        if (te.morph != null)
        {
            if (te.entity == null)
            {
                te.entity = new EntityActor(this.getWorld());
            }

            EntityLivingBase entity = te.entity;

            entity.setPositionAndRotation(x, y, z, 0, 0);
            entity.rotationYawHead = entity.prevRotationYawHead = 0;
            entity.rotationPitch = entity.prevRotationPitch = te.rotateY;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5F + te.x, y + te.y, z + 0.5F + te.z);
            GlStateManager.rotate(te.rotateX, 0, 1, 0);
            te.morph.render(entity, 0, 0, 0, 0, partialTicks);
            GlStateManager.popMatrix();
        }
    }
}