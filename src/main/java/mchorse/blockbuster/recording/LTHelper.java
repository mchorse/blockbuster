package mchorse.blockbuster.recording;

import mchorse.blockbuster.recording.data.Frame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Method;

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
				Vec3d look = player.getLookVec().scale(5);

				Object object = method.invoke(null, player, pos, pos.add(look));

				return object instanceof Boolean && ((Boolean) object).booleanValue();
			}
			catch (Exception e)
			{}
		}

		return false;
	}
}