package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class SubCommandRecordErase extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "erase";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.erase";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}erase{r} {7}<filename> <count> <from>{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 3;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Record record = CommandRecord.getRecord(args[0]);
        int count = CommandBase.parseInt(args[1], 0);
        int from = CommandBase.parseInt(args[2], 0, record.getLength() - 1);

        count = MathUtils.clamp(count, 0, record.getLength() - 1 - from);

        if (record.getLength() == 0)
        {
            Blockbuster.l10n.error(sender, "record.empty", record.filename);

            return;
        }

        /* Process */
        List<Frame> frames = new ArrayList<Frame>();
        List<List<Action>> actions = new ArrayList<List<Action>>();

        frames.addAll(record.frames);
        actions.addAll(record.actions);

        for (int i = 0; i < count; i++)
        {
            frames.remove(from);
            actions.remove(from);
        }

        record.frames = frames;
        record.actions = actions;

        try
        {
            RecordUtils.saveRecord(record);

            Blockbuster.l10n.success(sender, "record.erase", count, from, args[0], record.getLength());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Blockbuster.l10n.error(sender, "record.couldnt_save", args[1]);
        }
    }
}