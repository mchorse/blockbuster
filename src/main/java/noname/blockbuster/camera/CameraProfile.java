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

    /**
     * Get duration of current camera profile
     */
    public long getDuration()
    {
        long duration = 0;

        for (AbstractFixture fixture : this.fixtures)
        {
            duration += fixture.getDuration();
        }

        return duration;
    }

    /**
     * Get the amount of fixtures in current profile
     */
    public int getCount()
    {
        return this.fixtures.size();
    }

    /**
     * Get fixture at specified index
     */
    public AbstractFixture get(int index)
    {
        return this.fixtures.get(index);
    }

    /**
     * Get all of the fixtures
     */
    public List<AbstractFixture> getAll()
    {
        return this.fixtures;
    }

    /**
     * Add a fixture in the camera profile
     */
    public void add(AbstractFixture fixture)
    {
        this.fixtures.add(fixture);
    }

    /**
     * Move fixture on index {@code from} to index {@code to}
     */
    public void move(int from, int to)
    {
        this.fixtures.add(to, this.fixtures.remove(from));
    }

    /**
     * Remove fixture at specified index
     */
    public void remove(int index)
    {
        this.fixtures.remove(index);
    }

    /**
     * Reset camera profile (remove all fixtures in profile)
     */
    public void reset()
    {
        this.fixtures.clear();
    }

    /**
     * Apply camera profile transformation at given time on passed position
     */
    public void applyProfile(long progress, Position position)
    {
        int index = 0;

        for (AbstractFixture fixture : this.fixtures)
        {
            long duration = fixture.getDuration();

            if (progress <= duration) break;

            progress -= duration;
            index += 1;
        }

        AbstractFixture fixture = this.fixtures.get(index);

        fixture.applyFixture(Math.abs((float) progress / (float) fixture.getDuration()), position);
    }
}
