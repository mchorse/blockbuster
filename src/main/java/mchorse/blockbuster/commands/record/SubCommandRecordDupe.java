package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record dupe
 * 
 * This command is responsible for duplicating player recording to a new 
 * player recording file
 */
public class SubCommandRecordDupe extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "dupe";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.dupe";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}dupe{r} {7}<filename> <new_filename> [overwrite]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        boolean overwrite = (args.length>2) ? CommandBase.parseBoolean(args[2]) : false;

        if (RecordUtils.isReplayExists(args[1]) && !overwrite)
        {
            throw new CommandException("record.already_exists", args[1]);
        }

        try
        {
            Record record = CommandRecord.getRecord(args[0]).clone();

            record.filename = args[1];
            record.save(RecordUtils.replayFile(record.filename));

            CommonProxy.manager.records.put(args[1], record);
            Blockbuster.l10n.success(sender, "record.duped", args[0], args[1]);
        }
        catch (Exception e)
        {
            Blockbuster.l10n.error(sender, "record.couldnt_save", args[1]);
        }
    }
}