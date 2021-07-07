package mchorse.blockbuster_pack.trackers;

import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import mchorse.aperture.Aperture;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.mclib.utils.MatrixUtils.Transformation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.Optional.Method;

public class ApertureCamera extends BaseTracker
{
    private static final Matrix4f BUFFER = new Matrix4f();

    public static boolean enable = false;
    public static String tracking = "";

    public static final Vector3f pos = new Vector3f();
    public static final Vector3f rot = new Vector3f();

    public static final Vector3f offsetPos = new Vector3f();
    public static final Vector3f offsetRot = new Vector3f();

    @Override
    @Method(modid = Aperture.MOD_ID)
    public void track(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (enable && tracking != null && !tracking.isEmpty() && tracking.equals(this.name))
        {
            GL11.glPushMatrix();
            GL11.glTranslated(offsetPos.x, offsetPos.y, offsetPos.z);
            GL11.glRotatef(-offsetRot.y, 0, 1, 0);
            GL11.glRotatef(offsetRot.x, 1, 0, 0);
            GL11.glRotatef(offsetRot.z, 0, 0, 1);
            MatrixUtils.readModelView(BUFFER);
            GL11.glPopMatrix();

            enable = false;

            Transformation transform = MatrixUtils.extractTransformations(null, BUFFER);
            pos.set(transform.getTranslation3f());

            try
            {
                transform.rotation.invert();
            }
            catch (SingularMatrixException e)
            {
                return;
            }

            Vector3f rotation = transform.getRotation(Transformation.RotationOrder.YXZ, 1);
            if (rotation != null)
            {
                rot.set(rotation);
                if (transform.getScale(1).y < 0)
                {
                    rot.z += 180;
                }
                else
                {
                    rot.x *= -1;
                    rot.z *= -1;
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof ApertureCamera && super.equals(obj);
    }
}
