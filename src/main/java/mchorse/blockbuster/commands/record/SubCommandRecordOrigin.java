package mchorse.blockbuster.commands.record;

import java.util.List;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record origin
 *
 * This command is responsible for changing the origin of the player 
 * recording.
 */
public class SubCommandRecordOrigin extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public String getCommandName()
    {
        return "origin";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.origin";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];

        EntityPlayer player = getCommandSenderAsPlayer(sender);
        Record record = CommandRecord.getRecord(filename);

        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        if (args.length >= 4)
        {
            x = CommandBase.parseDouble(args[1]);
            y = CommandBase.parseDouble(args[2]);
            z = CommandBase.parseDouble(args[3]);
        }

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
            }

            frame.x = x + (frame.x - firstX);
            frame.y = y + (frame.y - firstY);
            frame.z = z + (frame.z - firstZ);

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
                action.changeOrigin(x, y, z, firstX, firstY, firstZ);
            }
        }

        record.dirty = true;

        Utils.unloadRecord(record);
        L10n.success(sender, "record.changed_origin", args[0], firstX, firstY, firstZ, x, y, z);
    }
}