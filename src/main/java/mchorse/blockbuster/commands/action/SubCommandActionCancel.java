package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.BBCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class SubCommandActionCancel extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "cancel";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.cancel";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}action {8}cancel{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        if (CommonProxy.manager.cancel(player))
        {
            Blockbuster.l10n.info(sender, "action.cancel");
        }
    }
}