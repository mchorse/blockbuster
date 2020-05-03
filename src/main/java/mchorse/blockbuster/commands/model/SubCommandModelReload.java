package mchorse.blockbuster.commands.model;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketReloadModels;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

/**
 * /model reload
 * 
 * Model subcommand which is responsible for forcing the server to reload 
 * the models.
 */
public class SubCommandModelReload extends CommandBase
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        boolean force = args.length >= 1 && CommandBase.parseBoolean(args[0]);

        /* Reload models and skin */
        Blockbuster.proxy.loadModels(force);

        Dispatcher.sendToServer(new PacketReloadModels(force));
    }
}