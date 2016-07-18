package noname.blockbuster.camera.fixtures;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noname.blockbuster.camera.CameraUtils;
import noname.blockbuster.camera.Point;
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

    /**
     * This is abstract's fixture factory method.
     *
     * It's responsible for creating a fixture from command line arguments and
     * player's space attributes (i.e. position and rotation).
     *
     * Commands can also be updated using {@link #edit(String[], EntityPlayer)}
     * method.
     */
    public static AbstractFixture fromCommand(String[] args, EntityPlayer player) throws CommandException
    {
        if (args.length < 2 || player == null)
        {
            throw new CommandException("Not enough data to create from command!");
        }

        String type = args[0];
        Entity target = CameraUtils.getTargetEntity(player);
        long duration = CommandBase.parseLong(args[1]);

        if (type.equals("idle"))
        {
            return new IdleFixture(duration, new Position(player));
        }
        else if (type.equals("circular"))
        {
            return new CircularFixture(duration, new Point(player), new Point(player), 360);
        }
        else if (type.equals("follow") || type.equals("look"))
        {
            if (target == null)
            {
                throw new CommandException("Player must look at entity to create this fixture!");
            }

            return type.equals("follow") ? new FollowFixture(duration, new Position(player), target) : new LookFixture(duration, new Position(player), target);
        }
        else if (type.equals("path"))
        {
            return new PathFixture(duration);
        }

        return null;
    }

    /* Instance stuff */

    public AbstractFixture(long duration)
    {
        this.setDuration(duration);
    }

    /* Duration management */

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public long getDuration()
    {
        return this.duration;
    }

    /**
     * Edit this fixture with given CLI arguments and given player. For every
     * fixture the editing process may vary.
     */
    public abstract void edit(String args[], EntityPlayer player) throws CommandException;

    /**
     * Apply fixture onto position
     *
     * @todo Add interpolation argument for different types of interpolations,
     *       if this feature would be requested.
     */
    public abstract void applyFixture(float progress, Position pos);
}
