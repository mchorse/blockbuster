package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record prolong
 * 
 * This command is responsible for duplicating player recording to a new 
 * player recording file
 */
public class SubCommandRecordProlong extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public String getName()
    {
        return "prolong";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.prolong";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Record record = CommandRecord.getRecord(args[0]);

        if (args.length >= 2)
        {
            record.postDelay = CommandBase.parseInt(args[1]);
        }

        if (args.length >= 3)
        {
            record.preDelay = CommandBase.parseInt(args[2]);
        }

        record.dirty = true;
        RecordUtils.unloadRecord(record);

        L10n.success(sender, "record.prolonged", args[0], record.preDelay, record.postDelay);
    }
}