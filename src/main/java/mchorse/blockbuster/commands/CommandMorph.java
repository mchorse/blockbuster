package mchorse.blockbuster.commands;

import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Command /morph
 *
 * Morphs player into given model with given skin in third person. Works only
 * in single player.
 *
 * However, when you're recording an action, you can use this command to morph
 * the actor into whatever you want it to be morphed into, and then when you're
 * going to playback it will show how you have been morphed.
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
        if (args.length == 0)
        {
            Dispatcher.sendToServer(new PacketMorph("", ""));
            sender.addChatMessage(new TextComponentTranslation("blockbuster.morph.disable"));
        }
        else
        {
            if (args.length == 1) Dispatcher.sendToServer(new PacketMorph(args[0], ""));
            if (args.length >= 2) Dispatcher.sendToServer(new PacketMorph(args[0], args[1]));

            sender.addChatMessage(new TextComponentTranslation("blockbuster.morph", args[0]));
        }
    }
}
