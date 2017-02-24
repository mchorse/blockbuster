package mchorse.blockbuster.commands.record;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.commands.McCommandBase;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SubCommandRecordInfo extends McCommandBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public String getCommandName()
    {
        return "info";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.record.info";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        String filename = args[0];
        Record record = CommandRecord.getRecord(filename);

        L10n.info(sender, "record.info", args[0], record.version, record.frames.size(), record.delay, record.unload);
    }
}