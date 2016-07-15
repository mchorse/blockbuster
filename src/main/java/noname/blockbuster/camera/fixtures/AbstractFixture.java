package noname.blockbuster.camera.fixtures;

import noname.blockbuster.camera.Position;

/**
 * Abstract camera fixture
 *
 * Camera fixtures are the special types of class that store camera
 * transformations based on some variables.
 *
 * Every fixture have duration field.
 */
public abstract class AbstractFixture
{
    protected float duration;

    public AbstractFixture(float duration)
    {
        this.setDuration(duration);
    }

    public void setDuration(float duration)
    {
        this.duration = duration;
    }

    public float getDuration()
    {
        return this.duration;
    }

    public abstract void applyFixture(long progress, Position pos);
}
