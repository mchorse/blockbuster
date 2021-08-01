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

import java.util.ArrayList;
import java.util.List;

public class SubCommandRecordFill extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "fill";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.fill";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}fill{r} {7}<filename> <count> [tick]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        int count = CommandBase.parseInt(args[1], 1);
        Record record = CommandRecord.getRecord(filename);

        int tick = record.frames.size() - 1;
        boolean add = true;

        if (args.length > 2)
        {
            tick = CommandBase.parseInt(args[2], 0, tick);
            add = false;
        }

        Frame original = record.getFrame(tick).copy();

        List<Frame> frames = record.frames;
        List<List<Action>> actions = record.actions;

        if (!add)
        {
            frames = new ArrayList<Frame>();
            actions = new ArrayList<List<Action>>();

            for (int i = 0; i <= tick; i ++)
            {
                frames.add(record.getFrame(i));
                actions.add(record.getActions(i));
            }
        }

        for (int i = 0; i < count; i ++)
        {
            frames.add(original.copy());
            actions.add(null);
        }

        if (!add)
        {
            for (int i = tick + 1; i < record.frames.size(); i ++)
            {
                frames.add(record.getFrame(i));
                actions.add(record.getActions(i));
            }

            record.frames = frames;
            record.actions = actions;
        }

        try
        {
            RecordUtils.saveRecord(record);

            Blockbuster.l10n.success(sender, "record.fill", count, tick, filename, record.frames.size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Blockbuster.l10n.error(sender, "record.couldnt_save", args[1]);
        }
    }
}
