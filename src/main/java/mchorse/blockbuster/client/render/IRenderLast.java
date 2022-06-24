package mchorse.blockbuster.client.render;

import javax.vecmath.Vector3d;

public interface IRenderLast
{
    /**
     * @return the position used to depth sort
     */
    public Vector3d getRenderLastPos();
}
