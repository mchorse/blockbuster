package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketRequestModels;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

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
    public void processCommand(ICommandSender sender, String[] args)
    {
        Dispatcher.sendToServer(new PacketRequestModels());
    }
}