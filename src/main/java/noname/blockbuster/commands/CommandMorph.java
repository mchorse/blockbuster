package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import noname.blockbuster.ClientProxy;
import noname.blockbuster.client.render.RenderPlayer;

/**
 * Morph command
 *
 * Morphs player into given model with given skin in third person
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
            render.model = "";
            render.skin = "";

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
