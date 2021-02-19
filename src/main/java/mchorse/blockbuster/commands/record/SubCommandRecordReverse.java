package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.RecordUtils;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record reverse
 *
 * This command is responsible for reversing all frames and actions in
 * the player recording
 */
public class SubCommandRecordReverse extends SubCommandRecordBase
{
    @Override
    public String getName()
    {
        return "reverse";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.reverse";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}record {8}reverse{r} {7}<filename>{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        Record record = CommandRecord.getRecord(filename);

        record.reverse();

        try
        {
            RecordUtils.saveRecord(record);

            Blockbuster.l10n.success(sender, "record.reversed", filename);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Blockbuster.l10n.error(sender, "record.couldnt_save", filename);
        }
    }
}