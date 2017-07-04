package mchorse.blockbuster.commands;

import mchorse.blockbuster.commands.model.SubCommandModelExport;
import mchorse.blockbuster.commands.model.SubCommandModelRequest;
import net.minecraft.command.ICommandSender;

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
        this.add(new SubCommandModelRequest());
        this.add(new SubCommandModelExport());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_)
    {
        return true;
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