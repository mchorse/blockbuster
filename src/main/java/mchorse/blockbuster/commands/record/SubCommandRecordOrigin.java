package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Command /record origin
 *
 * This command is responsible for changing the origin of player recordings.
 */
public class SubCommandRecordOrigin extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "origin";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.origin";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}origin{r} {7}<filename> [rotation] [x] [y] [z]{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];

        Vec3d position = sender.getPositionVector();
        Record record = CommandRecord.getRecord(filename);

        double x = position.x;
        double y = position.y;
        double z = position.z;

        double rotation = args.length >= 2 ? CommandBase.parseDouble(args[1]) : 0;

        double firstX = 0;
        double firstY = 0;
        double firstZ = 0;
        int i = 0;

        for (Frame frame : record.frames)
        {
            if (i == 0)
            {
                firstX = frame.x;
                firstY = frame.y;
                firstZ = frame.z;

                if (args.length >= 5)
                {
                    x = CommandBase.parseDouble(firstX, args[2], false);
                    y = CommandBase.parseDouble(firstY, args[3], false);
                    z = CommandBase.parseDouble(firstZ, args[4], false);
                }
            }

            double frameX = frame.x - firstX;
            double frameY = frame.y - firstY;
            double frameZ = frame.z - firstZ;

            if (rotation != 0)
            {
                float cos = (float) Math.cos(rotation / 180 * Math.PI);
                float sin = (float) Math.sin(rotation / 180 * Math.PI);

                double xx = frameX * cos - frameZ * sin;
                double zz = frameX * sin + frameZ * cos;

                frameX = xx;
                frameZ = zz;

                frame.yaw += rotation;
                frame.yawHead += rotation;

                if (frame.hasBodyYaw)
                {
                    frame.bodyYaw += rotation;
                }
            }

            frame.x = x + frameX;
            frame.y = y + frameY;
            frame.z = z + frameZ;

            i++;
        }

        for (List<Action> actions : record.actions)
        {
            if (actions == null || actions.isEmpty())
            {
                continue;
            }

            for (Action action : actions)
            {
                action.changeOrigin(rotation, x, y, z, firstX, firstY, firstZ);
            }
        }

        try
        {
            RecordUtils.saveRecord(record);

            Blockbuster.l10n.success(sender, "record.changed_origin", args[0], firstX, firstY, firstZ, x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Blockbuster.l10n.error(sender, "record.couldnt_save", args[1]);
        }
    }
}