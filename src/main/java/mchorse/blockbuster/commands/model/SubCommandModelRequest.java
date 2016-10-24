package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketRequestModels;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /model request
 *
 * This sub-command is responsible for requesting models from the server. These
 * models include models which located in "blockbuster/models" in world's save
 * folder and host's config models (if on intergrated server).
 */
public class SubCommandModelRequest extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "request";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.model.request";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        Dispatcher.sendToServer(new PacketRequestModels());
    }
}