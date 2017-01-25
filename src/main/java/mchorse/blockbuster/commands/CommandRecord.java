package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.record.SubCommandRecordGet;
import mchorse.blockbuster.commands.record.SubCommandRecordInfo;
import mchorse.blockbuster.commands.record.SubCommandRecordSearch;
import mchorse.blockbuster.commands.record.SubCommandRecordSet;

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
}