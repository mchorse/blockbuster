package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.record.SubCommandRecordGet;

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