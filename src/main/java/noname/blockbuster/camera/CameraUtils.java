package noname.blockbuster.camera;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.DimensionManager;

/**
 * Utils for camera classes.
 */
public class CameraUtils
{
    /**
     * Get the entity at which given player is looking at.
     * Taken from EntityRenderer class.
     */
    public static Entity getTargetEntity(Entity input)
    {
        double maxReach = 64;
        double blockDistance = maxReach;
        RayTraceResult result = input.rayTrace(maxReach, 1.0F);

        Vec3d eyes = input.getPositionEyes(1.0F);

        if (result != null)
        {
            blockDistance = result.hitVec.distanceTo(eyes);
        }

        Vec3d look = input.getLook(1.0F);
        Vec3d max = eyes.addVector(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach);
        Entity target = null;

        float area = 1.0F;

        List<Entity> list = Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(input, input.getEntityBoundingBox().addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach).expand(area, area, area), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            @Override
            public boolean apply(@Nullable Entity entity)
            {
                return entity != null && entity.canBeCollidedWith();
            }
        }));

        double entityDistance = blockDistance;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = list.get(i);
            AxisAlignedBB aabb = entity.getEntityBoundingBox().expandXyz(entity.getCollisionBorderSize());
            RayTraceResult intercept = aabb.calculateIntercept(eyes, max);

            if (aabb.isVecInside(eyes))
            {
                if (entityDistance >= 0.0D)
                {
                    target = entity;
                    entityDistance = 0.0D;
                }
            }
            else if (intercept != null)
            {
                double eyesDistance = eyes.distanceTo(intercept.hitVec);

                if (eyesDistance < entityDistance || entityDistance == 0.0D)
                {
                    if (entity.getLowestRidingEntity() == input.getLowestRidingEntity() && !input.canRiderInteract())
                    {
                        if (entityDistance == 0.0D)
                        {
                            target = entity;
                        }
                    }
                    else
                    {
                        target = entity;
                        entityDistance = eyesDistance;
                    }
                }
            }
        }

        return target;
    }

    /**
     * Get path to camera profile file (located in current world save's folder)
     */
    public static String cameraFile(String filename)
    {
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/cameras");

        if (!file.exists())
        {
            file.mkdirs();
        }

        return file.getAbsolutePath() + "/" + filename;
    }

    /* Load utilities */

    public static CameraProfile readCameraProfile(String filename) throws Exception
    {
        filename = cameraFile(filename);

        DataInputStream file = new DataInputStream(new FileInputStream(filename));
        CameraProfile profile = new CameraProfile();

        profile.read(file);

        return profile;
    }

    public static Position readPosition(DataInput in) throws IOException
    {
        return new Position(readPoint(in), readAngle(in));
    }

    public static Point readPoint(DataInput in) throws IOException
    {
        return new Point(in.readFloat(), in.readFloat(), in.readFloat());
    }

    public static Angle readAngle(DataInput in) throws IOException
    {
        return new Angle(in.readFloat(), in.readFloat());
    }

    /* Save utilities */

    public static void writeCameraProfile(String filename, CameraProfile profile) throws IOException
    {
        filename = cameraFile(filename);

        RandomAccessFile file = new RandomAccessFile(filename, "rw");

        profile.write(file);
        file.close();
    }

    public static void writePosition(DataOutput out, Position position) throws IOException
    {
        writePoint(out, position.point);
        writeAngle(out, position.angle);
    }

    public static void writePoint(DataOutput out, Point point) throws IOException
    {
        out.writeFloat(point.x);
        out.writeFloat(point.y);
        out.writeFloat(point.z);
    }

    public static void writeAngle(DataOutput out, Angle angle) throws IOException
    {
        out.writeFloat(angle.yaw);
        out.writeFloat(angle.pitch);
    }
}