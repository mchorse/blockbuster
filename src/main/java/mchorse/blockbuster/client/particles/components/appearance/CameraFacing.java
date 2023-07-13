package mchorse.blockbuster.client.particles.components.appearance;

/**
 * Camera facing mode
 */
public enum CameraFacing
{
    ROTATE_XYZ("rotate_xyz"), ROTATE_Y("rotate_y"),
    LOOKAT_XYZ("lookat_xyz", true, false), LOOKAT_Y("lookat_y", true, false), LOOKAT_DIRECTION("lookat_direction", true, true),
    DIRECTION_X("direction_x", false, true), DIRECTION_Y("direction_y", false, true), DIRECTION_Z("direction_z", false, true),
    EMITTER_XY("emitter_transform_xy"), EMITTER_XZ("emitter_transform_xz"), EMITTER_YZ("emitter_transform_yz");

    public final String id;
    public final boolean isLookAt;
    public final boolean isDirection;

    public static CameraFacing fromString(String string)
    {
        for (CameraFacing facing : values())
        {
            if (facing.id.equals(string))
            {
                return facing;
            }
        }

        return null;
    }

    private CameraFacing(String id, boolean isLookAt, boolean isDirection)
    {
        this.id = id;
        this.isLookAt = isLookAt;
        this.isDirection = isDirection;
    }

    private CameraFacing(String id)
    {
        this(id, false, false);
    }

}
