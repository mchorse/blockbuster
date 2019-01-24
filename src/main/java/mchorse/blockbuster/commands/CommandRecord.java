package mchorse.blockbuster.commands;

import java.io.FileNotFoundException;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.record.SubCommandRecordAdd;
import mchorse.blockbuster.commands.record.SubCommandRecordClean;
import mchorse.blockbuster.commands.record.SubCommandRecordDupe;
import mchorse.blockbuster.commands.record.SubCommandRecordGet;
import mchorse.blockbuster.commands.record.SubCommandRecordInfo;
import mchorse.blockbuster.commands.record.SubCommandRecordOrigin;
import mchorse.blockbuster.commands.record.SubCommandRecordProlong;
import mchorse.blockbuster.commands.record.SubCommandRecordRemove;
import mchorse.blockbuster.commands.record.SubCommandRecordSearch;
import mchorse.blockbuster.commands.record.SubCommandRecordTP;
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
        this.add(new SubCommandRecordAdd());
        this.add(new SubCommandRecordClean());
        this.add(new SubCommandRecordDupe());
        this.add(new SubCommandRecordGet());
        this.add(new SubCommandRecordInfo());
        this.add(new SubCommandRecordOrigin());
        this.add(new SubCommandRecordProlong());
        this.add(new SubCommandRecordRemove());
        this.add(new SubCommandRecordSearch());
        this.add(new SubCommandRecordTP());
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

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Get record by given filename
     *
     * This is a command-friendly method for retrieving a player recording. In
     * case of error, {@link CommandException} will be thrown.
     */
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