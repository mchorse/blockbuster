package mchorse.blockbuster.commands;

import java.util.Collections;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Command /morph
 *
 * Morphs player into given model with given skin in third person. Works in
 * multiplayer.
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
            Dispatcher.sendToServer(new PacketMorph("", null));
            sender.addChatMessage(new TextComponentTranslation("blockbuster.success.morph.disable"));
        }
        else
        {
            if (!Blockbuster.proxy.models.models.containsKey(args[0]))
            {
                sender.addChatMessage(new TextComponentString(I18n.format("blockbuster.error.morph.wrong", args[0])));

                return;
            }

            if (args.length == 1) Dispatcher.sendToServer(new PacketMorph(args[0], null));
            if (args.length >= 2) Dispatcher.sendToServer(new PacketMorph(args[0], RLUtils.fromString(args[1], args[0])));

            sender.addChatMessage(new TextComponentString(I18n.format("blockbuster.success.morph.enable", args[0])));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, ClientProxy.actorPack.pack.getModels());
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, ClientProxy.actorPack.pack.getSkins(args[0]));
        }

        return Collections.<String> emptyList();
    }
}