package mchorse.blockbuster.commands;

/**
 * Command /record
 *
 * This command is responsible for
 */
public class CommandRecord extends SubCommandBase
{
    public CommandRecord()
    {

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