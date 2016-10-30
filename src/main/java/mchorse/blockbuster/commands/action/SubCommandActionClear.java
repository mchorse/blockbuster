package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.capabilities.recording.Recording;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.recording.PacketUnloadRecordings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /action clear
 *
 * This sub-command is responsible for clearing out tracking information about
 * the player records on the server and also sending a packet for unloading the
 * records on the client.
 *
 * Used in some buggy situations when server doesn't send unload packet or
 * doesn't want to send clients a new record.
 */
public class SubCommandActionClear extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "clear";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.clear";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        Recording.get(player).removeRecordings();

        Dispatcher.sendTo(new PacketUnloadRecordings(), player);
    }
}