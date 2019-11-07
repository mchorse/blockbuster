package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class SubCommandRecordCut extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 3;
    }

    @Override
    public String getName()
    {
        return "cut";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.cut";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Record record = CommandRecord.getRecord(args[0]);
        int from = CommandBase.parseInt(args[1], 0, record.getLength());
        int to = CommandBase.parseInt(args[2], 0, record.getLength());
        int min = Math.min(from, to);

        to = Math.max(from, to);
        from = min;

        if (record.getLength() == 0)
        {
            L10n.error(sender, "record.empty", record.filename);

            return;
        }

        /* Process */
        List<Frame> frames = new ArrayList<Frame>();
        List<List<Action>> actions = new ArrayList<List<Action>>();

        frames.addAll(record.frames.subList(from, to));
        actions.addAll(record.actions.subList(from, to));

        record.frames = frames;
        record.actions = actions;

        record.dirty = true;
        Utils.unloadRecord(record);

        L10n.success(sender, "record.cut", args[0], args[1], args[2]);
    }
}