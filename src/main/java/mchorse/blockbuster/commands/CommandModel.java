package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.model.SubCommandModelClearCache;
import mchorse.blockbuster.commands.model.SubCommandModelExport;
import mchorse.blockbuster.commands.model.SubCommandModelRequest;

/**
 * Command /model
 *
 * Another client-side command which is responsible for
 */
public class CommandModel extends SubCommandBase
{
    /**
     * Model command's constructor
     *
     * This method is responsible for attaching sub commands for this model
     */
    public CommandModel()
    {
        this.add(new SubCommandModelExport());
        this.add(new SubCommandModelRequest());
        this.add(new SubCommandModelClearCache());
    }

    @Override
    public String getCommandName()
    {
        return "model";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.model.help";
    }
}