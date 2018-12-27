package mchorse.blockbuster.commands.record;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 * Command /record clean
 *
 * This command is responsible for cleaning given property from player 
 * recording.
 */
public class SubCommandRecordClean extends SubCommandRecordBase
{
    public static final Set<String> PROPERTIES = ImmutableSet.of("x", "y", "z", "yaw", "yaw_head", "pitch", "fall_distance", "sprinting", "sneaking", "active_hands", "mounted");

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public String getName()
    {
        return "clean";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.clean";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        String property = args[1];
        Record record = CommandRecord.getRecord(filename);

        if (!PROPERTIES.contains(property))
        {
            throw new CommandException("record.wrong_clean_property", property);
        }

        int start = 0;
        int end = record.getLength();

        if (args.length >= 4)
        {
            start = CommandBase.parseInt(args[3], start, end);
        }

        if (args.length >= 5)
        {
            end = CommandBase.parseInt(args[4], start, end);
        }

        float original = this.get(property, record.frames.get(start));

        if (args.length >= 3)
        {
            original = (float) CommandBase.parseDouble(original, args[2], false);
        }

        for (int i = start; i < end; i++)
        {
            this.set(property, record.frames.get(i), original);
        }

        record.dirty = true;

        Utils.unloadRecord(record);
        L10n.success(sender, "record.clean", filename, property, start, end);
    }

    public float get(String property, Frame frame)
    {
        if (property.equals("x"))
        {
            return (float) frame.x;
        }
        else if (property.equals("y"))
        {
            return (float) frame.y;
        }
        else if (property.equals("z"))
        {
            return (float) frame.z;
        }
        else if (property.equals("yaw"))
        {
            return frame.yaw;
        }
        else if (property.equals("yaw_head"))
        {
            return frame.yawHead;
        }
        else if (property.equals("pitch"))
        {
            return frame.pitch;
        }
        else if (property.equals("fall_distance"))
        {
            return frame.fallDistance;
        }
        else if (property.equals("sprinting"))
        {
            return frame.isSprinting ? 1 : 0;
        }
        else if (property.equals("sneaking"))
        {
            return frame.isSneaking ? 1 : 0;
        }
        else if (property.equals("active_hands"))
        {
            return frame.activeHands;
        }
        else if (property.equals("mounted"))
        {
            return frame.isMounted ? 1 : 0;
        }

        return 0;
    }

    public void set(String property, Frame frame, float value)
    {
        if (property.equals("x"))
        {
            frame.x = value;
        }
        else if (property.equals("y"))
        {
            frame.y = value;
        }
        else if (property.equals("z"))
        {
            frame.z = value;
        }
        else if (property.equals("yaw"))
        {
            frame.yaw = value;
        }
        else if (property.equals("yaw_head"))
        {
            frame.yawHead = value;
        }
        else if (property.equals("pitch"))
        {
            frame.pitch = value;
        }
        else if (property.equals("fall_distance"))
        {
            frame.fallDistance = value;
        }
        else if (property.equals("sprinting"))
        {
            frame.isSprinting = value == 1;
        }
        else if (property.equals("sneaking"))
        {
            frame.isSneaking = value == 1;
        }
        else if (property.equals("active_hands"))
        {
            frame.activeHands = (int) value;
        }
        else if (property.equals("mounted"))
        {
            frame.isMounted = value == 1;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, PROPERTIES);
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}