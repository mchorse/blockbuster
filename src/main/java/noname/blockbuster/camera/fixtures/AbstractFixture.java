package noname.blockbuster.camera.fixtures;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayer;
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
     * This is abstract's fixture factory method. It's responsible for creating
     * a fixture from command line arguments and player's space attributes (i.e.
     * position and rotation).
     */
    public static AbstractFixture fromCommand(String[] args, EntityPlayer player) throws Exception
    {
        if (args.length < 2 || player == null)
        {
            throw new Exception("Not enough data to create from command!");
        }

        String type = args[0];
        long duration = CommandBase.parseLong(args[1]);

        if (type.equals("idle"))
        {
            return new IdleFixture(duration, new Position(player));
        }
        else if (type.equals("circular"))
        {
            return new CircularFixture(duration, new Point(player), new Point(player), 360);
        }
        else if (type.equals("follow"))
        {
            // I'll implement it later (when I'll be able to find a way how to use ray tracer :D)
        }
        else if (type.equals("look"))
        {
            // The same as "follow"
        }
        else if (type.equals("path"))
        {
            return new PathFixture(duration);
        }

        return null;
    }

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

    public void edit(String args[], EntityPlayer player) throws CommandException
    {
        if (args.length > 0)
        {
            this.duration = CommandBase.parseInt(args[0]);
        }
    }

    public abstract void applyFixture(float progress, Position pos);

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
