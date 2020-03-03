package mchorse.blockbuster.client.particles.components.appearance;

/**
 * Camera facing mode
 */
public enum CameraFacing
{
	ROTATE_XYZ("rotate_xyz"), ROTATE_Y("rotate_y"),
	LOOKAT_XYZ("lookat_xyz"), LOOKAT_Y("lookat_y"),
	DIRECTION_X("direction_x"), DIRECTION_Y("direction_y"), DIRECTION_Z("direction_z");

	public final String name;

	public static CameraFacing fromString(String string)
	{
		for (CameraFacing facing : values())
		{
			if (facing.name.equals(string)) return facing;
		}

		return null;
	}

	private CameraFacing(String name)
	{
		this.name = name;
	}
}
