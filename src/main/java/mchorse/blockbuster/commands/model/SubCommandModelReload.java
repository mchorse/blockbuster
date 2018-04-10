package mchorse.blockbuster.commands.model;

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
public class SubCommandModelReload extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "reload";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.reload";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Dispatcher.sendToServer(new PacketReloadModels());
    }
}