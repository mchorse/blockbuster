package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.BBCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /action stop
 *
 * This sub-command is responsible for stopping the action recording of current
 * player.
 */
public class SubCommandActionStop extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "stop";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.stop";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}action {8}stop{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CommonProxy.manager.halt(getCommandSenderAsPlayer(sender), false, true);
    }
}