package mchorse.blockbuster.commands;

import java.util.Collections;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketMorph;
import mchorse.blockbuster.utils.L10n;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

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
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            Dispatcher.sendToServer(new PacketMorph("", null));
            L10n.success(sender, "morph.disable");
        }
        else
        {
            if (!Blockbuster.proxy.models.models.containsKey(args[0]))
            {
                L10n.error(sender, "morph.wrong", args[0]);

                return;
            }

            if (args.length == 1) Dispatcher.sendToServer(new PacketMorph(args[0], null));
            if (args.length >= 2) Dispatcher.sendToServer(new PacketMorph(args[0], RLUtils.fromString(args[1], args[0])));

            L10n.success(sender, "morph.enable", args[0]);
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            List<String> models = ClientProxy.actorPack.pack.getModels();

            models.add(0, "alex");
            models.add(0, "steve");

            return getListOfStringsFromIterableMatchingLastWord(args, models);
        }
        else if (args.length == 2)
        {
            return getListOfStringsFromIterableMatchingLastWord(args, ClientProxy.actorPack.pack.getSkins(args[0]));
        }

        return Collections.<String> emptyList();
    }
}