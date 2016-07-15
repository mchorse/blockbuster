package noname.blockbuster.camera;

import java.util.ArrayList;
import java.util.List;

import noname.blockbuster.camera.fixtures.AbstractFixture;

/**
 * Camera profile class
 *
 * This class represents the camera profile. Camera profile is a list of
 * camera fixtures that can be used to playback the camera movement and can be
 * loaded/saved to the disk.
 */
public class CameraProfile
{
    protected List<AbstractFixture> fixtures = new ArrayList<AbstractFixture>();

    public long getDuration()
    {
        long duration = 0;

        for (AbstractFixture fixture : this.fixtures)
        {
            duration += fixture.getDuration();
        }

        return duration;
    }

    public AbstractFixture getFixture(int index)
    {
        return this.fixtures.get(index);
    }

    public void applyProfile(long progress, Position position)
    {
        int index = -1;

        for (AbstractFixture fixture : this.fixtures)
        {
            if (progress <= 0) break;

            progress -= fixture.getDuration();
            index += 1;
        }

        this.fixtures.get(index).applyFixture(+progress, position);
    }

    public void addFixture(AbstractFixture fixture)
    {
        this.fixtures.add(fixture);
    }
}
