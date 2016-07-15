package noname.blockbuster.camera.fixtures;

import noname.blockbuster.camera.Position;

/**
 * Idle camera fixture
 *
 * This fixture is the basic fixture type. This fixture is responsible for
 * outputting static values for camera.
 */
public class IdleFixture extends AbstractFixture
{
    protected Position position;

    public IdleFixture(float duration, Position position)
    {
        super(duration);

        this.position = position;
    }

    public Position getPosition()
    {
        return this.position;
    }

    @Override
    public void applyFixture(long progress, Position pos)
    {
        pos.copy(this.position);
    }
}