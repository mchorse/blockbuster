package noname.blockbuster.camera.fixtures;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

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
    protected long duration;

    public AbstractFixture(long duration)
    {
        this.setDuration(duration);
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public long getDuration()
    {
        return this.duration;
    }

    public abstract void applyFixture(long progress, Position pos);

    @Override
    public String toString()
    {
        return this.getToStringHelper().toString();
    }

    protected ToStringHelper getToStringHelper()
    {
        return Objects.toStringHelper(this).add("duration", this.duration);
    }
}
