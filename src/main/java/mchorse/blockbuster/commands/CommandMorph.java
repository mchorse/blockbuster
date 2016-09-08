package mchorse.blockbuster.commands;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.render.RenderPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Morph command
 *
 * Morphs player into given model with given skin in third person. Works only
 * in single player.
 */
public class CommandMorph extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "morph";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.morph";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        RenderPlayer render = ClientProxy.playerRender;

        if (args.length == 0)
        {
            render.reset();

            sender.addChatMessage(new TextComponentString("You've been demorphed!"));
        }
        else
        {
            if (args.length > 0) render.model = args[0];
            if (args.length > 1) render.skin = args[1];

            sender.addChatMessage(new TextComponentString("You've morphed into " + args[0] + " model!"));
        }
    }
}
