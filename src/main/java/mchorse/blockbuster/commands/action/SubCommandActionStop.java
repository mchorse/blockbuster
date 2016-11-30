package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.common.CommonProxy;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * Sub-command /action stop
 *
 * This sub-command is responsible for stopping the action recording of current
 * player.
 */
public class SubCommandActionStop extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "stop";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.stop";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        CommonProxy.manager.stopRecording(getCommandSenderAsPlayer(sender), true);
    }
}