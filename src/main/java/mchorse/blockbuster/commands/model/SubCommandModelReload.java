package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketReloadModels;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * /model reload
 * 
 * Model subcommand which is responsible for forcing the server to reload 
 * the models.
 */
public class SubCommandModelReload extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "reload";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.reload";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}model {8}reload{r} {7}[force]{r}";
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        boolean force = args.length >= 1 && CommandBase.parseBoolean(args[0]);

        /* Reload models and skin */
        Blockbuster.proxy.loadModels(force);

        Dispatcher.sendToServer(new PacketReloadModels(force));
    }
}