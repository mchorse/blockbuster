package mchorse.blockbuster.commands;

import java.io.FileNotFoundException;

import mchorse.blockbuster.commands.record.SubCommandRecordGet;
import mchorse.blockbuster.commands.record.SubCommandRecordInfo;
import mchorse.blockbuster.commands.record.SubCommandRecordSearch;
import mchorse.blockbuster.commands.record.SubCommandRecordSet;
import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.recording.data.Record;
import net.minecraft.command.CommandException;

/**
 * Command /record
 *
 * This command provides an interface which allows the manipulation of player
 * recordings on the server.
 */
public class CommandRecord extends SubCommandBase
{
    public CommandRecord()
    {
        /* Register sub-commands */
        this.add(new SubCommandRecordGet());
        this.add(new SubCommandRecordInfo());
        this.add(new SubCommandRecordSet());
        this.add(new SubCommandRecordSearch());
    }

    @Override
    public String getCommandName()
    {
        return "record";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.record.help";
    }

    public static Record getRecord(String filename) throws CommandException
    {
        try
        {
            return CommonProxy.manager.getRecord(filename);
        }
        catch (FileNotFoundException e)
        {
            throw new CommandException("record.not_exist", filename);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new CommandException("recording.read", filename);
        }
    }
}