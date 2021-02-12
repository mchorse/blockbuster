package mchorse.blockbuster.recording;

import mchorse.blockbuster.recording.data.Frame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Method;

/**
 * LittleTiles helper method
 *
 * This bad boy is responsible contacting LittleTile API (introduced in v1.5.0-pre199_31_mc1.12.2)
 * to allow opening the doors. Big thanks to CreativeMD for helping out with this issue!
 *
 * @link https://www.curseforge.com/minecraft/mc-mods/littletiles/files/2960578
 */
public class LTHelper
{
    private static Method method;
    private static boolean weTried;

    public static boolean playerRightClickServer(EntityPlayer player, Frame frame)
    {
        try
        {
            if (method == null && !weTried)
            {
                weTried = true;

                Class clazz = Class.forName("com.creativemd.littletiles.common.api.LittleTileAPI");

                method = clazz.getMethod("playerRightClickServer", EntityPlayer.class, Vec3d.class, Vec3d.class);
            }
        }
        catch (Exception e)
        {}

        if (method != null)
        {
            try
            {
                player.rotationPitch = frame.pitch;
                player.rotationYaw = frame.yaw;

                Vec3d pos = new Vec3d(frame.x, frame.y, frame.z);
                Vec3d look = player.getLookVec().scale(8);

                pos = pos.addVector(0, player.getEyeHeight(), 0);

                Object object = method.invoke(null, player, pos, pos.add(look));

                return object instanceof Boolean && ((Boolean) object).booleanValue();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }
}