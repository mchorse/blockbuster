package mchorse.blockbuster.camera;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mchorse.blockbuster.camera.fixtures.AbstractFixture;
import mchorse.blockbuster.camera.fixtures.CircularFixture;
import mchorse.blockbuster.camera.fixtures.FollowFixture;
import mchorse.blockbuster.camera.fixtures.IdleFixture;
import mchorse.blockbuster.camera.fixtures.LookFixture;
import mchorse.blockbuster.camera.fixtures.PathFixture;
import mchorse.blockbuster.camera.json.AbstractFixtureAdapter;
import mchorse.blockbuster.capabilities.recording.IRecording;
import mchorse.blockbuster.capabilities.recording.RecordingProvider;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.camera.PacketCameraProfile;
import mchorse.blockbuster.network.common.camera.PacketCameraState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;

/**
 * Utilities for camera classes
 *
 * Includes methods for writing/reading camera profile, position, point, angle
 * to the file, and finding entity via ray tracing.
 */
public class CameraUtils
{
    /**
     * Get the entity at which given player is looking at.
     * Taken from EntityRenderer class.
     *
     * That's a big method... Why Minecraft has lots of these big methods?
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

        return file.getAbsolutePath() + "/" + filename + ".json";
    }

    /**
     * Get a camera JSON builder. This will include custom serializers for some
     * of the camera fixture classes. Also custom serializers.
     */
    public static Gson cameraJSONBuilder(boolean pretty)
    {
        GsonBuilder builder = new GsonBuilder();

        if (pretty)
        {
            builder.setPrettyPrinting();
        }

        builder.excludeFieldsWithoutExposeAnnotation();

        /* Serializers and deserializers */
        AbstractFixtureAdapter fixtureAdapter = new AbstractFixtureAdapter();

        builder.registerTypeAdapter(AbstractFixture.class, fixtureAdapter);
        builder.registerTypeAdapter(IdleFixture.class, fixtureAdapter);
        builder.registerTypeAdapter(PathFixture.class, fixtureAdapter);
        builder.registerTypeAdapter(LookFixture.class, fixtureAdapter);
        builder.registerTypeAdapter(FollowFixture.class, fixtureAdapter);
        builder.registerTypeAdapter(CircularFixture.class, fixtureAdapter);

        return builder.create();
    }

    /**
     * Read CameraProfile instance from given file
     */
    public static String readCameraProfile(String filename) throws Exception
    {
        String path = cameraFile(filename);
        DataInputStream stream = new DataInputStream(new FileInputStream(path));
        Scanner scanner = new Scanner(stream, "UTF-8");
        String content = scanner.useDelimiter("\\A").next();

        scanner.close();

        return content;
    }

    /**
     * Write CameraProfile instance to given file
     */
    public static void writeCameraProfile(String filename, String profile) throws IOException
    {
        PrintWriter printer = new PrintWriter(cameraFile(filename));

        printer.print(profile);
        printer.close();
    }

    /* Commands */

    /**
     * Send a camera profile that was read from given file to player.
     *
     * This method also checks if player has same named camera profile, and if
     * it's expired (server has newer version), send him new one.
     */
    public static void sendProfileToPlayer(String filename, EntityPlayerMP player, boolean play)
    {
        try
        {
            IRecording recording = player.getCapability(RecordingProvider.RECORDING, null);

            if (recording.currentProfile().equals(filename))
            {
                File profile = new File(cameraFile(filename));

                if (recording.currentProfileTimestamp() > profile.lastModified())
                {
                    if (play)
                    {
                        Dispatcher.sendTo(new PacketCameraState(true), player);
                    }

                    return;
                }
            }

            Dispatcher.sendTo(new PacketCameraProfile(filename, readCameraProfile(filename), play), player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            player.addChatMessage(new TextComponentTranslation("blockbuster.profile.cant_load", filename));
        }
    }

    /**
     * Save given camera profile to file. Inform user about the problem, if the
     * camera profile couldn't be saved.
     */
    public static boolean saveCameraProfile(String filename, String profile, EntityPlayerMP player)
    {
        try
        {
            writeCameraProfile(filename, profile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            player.addChatMessage(new TextComponentTranslation("blockbuster.profile.cant_save", filename));

            return false;
        }

        return true;
    }

    /**
     * Gets all camera profiles names. This is used for playback GUI's
     * tab completion thingy.
     */
    public static List<String> listProfiles()
    {
        File file = new File(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/cameras");
        List<String> files = new ArrayList<String>();

        file.mkdirs();

        for (String filename : file.list())
        {
            if (filename.endsWith(".json"))
            {
                files.add(filename.substring(0, filename.lastIndexOf(".json")));
            }
        }

        return files;
    }
}