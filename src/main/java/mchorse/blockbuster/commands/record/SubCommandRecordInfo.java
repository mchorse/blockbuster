package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /record info
 *
 * This command is responsible for outputting information about given record.
 */
public class SubCommandRecordInfo extends SubCommandRecordBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public String getName()
    {
        return "info";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.info";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        Record record = CommandRecord.getRecord(filename);

        Blockbuster.l10n.info(sender, "record.info", args[0], record.version, record.frames.size(), record.unload);
    }
}