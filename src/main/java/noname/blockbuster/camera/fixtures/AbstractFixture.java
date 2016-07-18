package noname.blockbuster.camera.fixtures;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
    public static AbstractFixture fromCommand(String[] args, EntityPlayer player) throws CommandException
    {
        if (args.length < 2 || player == null)
        {
            throw new CommandException("Not enough data to create from command!");
        }

        long duration = CommandBase.parseLong(args[1]);
        String type = args[0];
        Entity target = getTargetEntity(player);

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
            if (target == null)
            {
                throw new CommandException("Player must look at entity to create this fixture!");
            }

            return new FollowFixture(duration, new Position(player), target);
        }
        else if (type.equals("look"))
        {
            if (target == null)
            {
                throw new CommandException("Player must look at entity to create this fixture!");
            }

            return new LookFixture(duration, new Position(player), target);
        }
        else if (type.equals("path"))
        {
            return new PathFixture(duration);
        }

        return null;
    }

    /**
     * Get the entity at which given player is looking at.
     * Taken from EntityRenderer class.
     */
    protected static Entity getTargetEntity(EntityPlayer player)
    {
        double maxReach = 64;
        double blockDistance = maxReach;
        RayTraceResult result = player.rayTrace(maxReach, 1.0F);

        Vec3d eyes = player.getPositionEyes(1.0F);

        if (result != null)
        {
            blockDistance = result.hitVec.distanceTo(eyes);
        }

        Vec3d look = player.getLook(1.0F);
        Vec3d max = eyes.addVector(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach);
        Entity pointedEntity = null;

        float area = 1.0F;

        List<Entity> list = Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach).expand(area, area, area), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            @Override
            public boolean apply(@Nullable Entity entity)
            {
                return entity != null && entity.canBeCollidedWith();
            }
        }));

        double entityDistance = blockDistance;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity = list.get(i);
            AxisAlignedBB aabb = entity.getEntityBoundingBox().expandXyz(entity.getCollisionBorderSize());
            RayTraceResult intercept = aabb.calculateIntercept(eyes, max);

            if (aabb.isVecInside(eyes))
            {
                if (entityDistance >= 0.0D)
                {
                    pointedEntity = entity;
                    entityDistance = 0.0D;
                }
            }
            else if (intercept != null)
            {
                double eyesDistance = eyes.distanceTo(intercept.hitVec);

                if (eyesDistance < entityDistance || entityDistance == 0.0D)
                {
                    if (entity.getLowestRidingEntity() == player.getLowestRidingEntity() && !player.canRiderInteract())
                    {
                        if (entityDistance == 0.0D)
                        {
                            pointedEntity = entity;
                        }
                    }
                    else
                    {
                        pointedEntity = entity;
                        entityDistance = eyesDistance;
                    }
                }
            }
        }

        return pointedEntity;
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

    public abstract void edit(String args[], EntityPlayer player) throws CommandException;

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
