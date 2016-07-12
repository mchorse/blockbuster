package noname.blockbuster.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.tileentity.AbstractTileEntityDirector;

/**
 * Command stop director
 *
 * This command is stopping playback in a director block which is located
 * in passed coordinates. Makes a nice addition to adventure maps and command
 * blocks.
 *
 * I added 'cause I hate to wait when a long scene would finish playing. So,
 * shutdown this mutha... you get it.
 */
public class CommandStopDirector extends CommandDirector
{
    @Override
    public String getCommandName()
    {
        return "stop-director";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "blockbuster.commands.stop_director";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        AbstractTileEntityDirector director = this.getDirector(server, args[0], args[1], args[2]);

        if (director == null)
        {
            throw new CommandException("blockbuster.commands.no_director", args[0], args[1], args[2]);
        }

        director.stopPlayback();
    }
}