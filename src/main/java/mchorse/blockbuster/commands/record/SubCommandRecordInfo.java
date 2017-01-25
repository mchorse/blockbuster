package mchorse.blockbuster.commands.record;

import java.io.FileNotFoundException;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class SubCommandRecordInfo extends CommandBase
{
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String filename = args[0];

        Record record;

        try
        {
            record = CommonProxy.manager.getRecord(filename);
        }
        catch (FileNotFoundException e)
        {
            L10n.error(sender, "record.not_exist", filename);
            return;
        }
        catch (Exception e)
        {
            L10n.error(sender, "recording.read", filename);
            return;
        }

        L10n.info(sender, "record.info", args[0], record.version, record.frames.size(), record.delay, record.unload);
    }
}