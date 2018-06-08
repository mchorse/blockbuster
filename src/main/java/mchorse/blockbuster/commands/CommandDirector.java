package mchorse.blockbuster.commands;

import java.util.List;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Command /director
 *
 * This command is responsible for playing or stopping director block.
 *
 * This command is unified version of previous existed commands CommandStopDirector
 * and CommandPlayDirector. These unifications were made to avoid duplicate
 * code, and less command memorizing.
 */
public class CommandDirector extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "director";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {
        return "blockbuster.commands.director";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException(this.getCommandUsage(null));
        }

        String action = args[0];
        BlockPos pos = CommandBase.parseBlockPos(sender, args, 1, false);
        TileEntityDirector tile = this.getDirector(server, pos);

        if (tile == null)
        {
            L10n.error(sender, "director.no_director", pos.getX(), pos.getY(), pos.getZ());
            return;
        }

        String play = "director.play";
        String stop = "director.stop";

        if (action.equals("play"))
        {
            if (tile.isPlaying())
            {
                L10n.error(sender, "director.playing", args[1], args[2], args[3]);
                return;
            }

            tile.director.startPlayback(0);
            L10n.success(sender, play, args[1], args[2], args[3]);
        }
        else if (action.equals("stop"))
        {
            if (!tile.isPlaying())
            {
                L10n.error(sender, "director.stopped", args[1], args[2], args[3]);
                return;
            }

            tile.director.stopPlayback();
            L10n.success(sender, stop, args[1], args[2], args[3]);
        }
        else if (action.equals("loop") && args.length >= 5)
        {
            tile.director.loops = CommandBase.parseBoolean(args[4]);
            tile.markDirty();

            L10n.info(sender, "director." + (tile.director.loops ? "looped" : "unlooped"));
        }
        else if (action.equals("disable_state") && args.length >= 5)
        {
            tile.director.disableStates = CommandBase.parseBoolean(args[4]);
            tile.markDirty();

            L10n.info(sender, "director." + (tile.director.disableStates ? "disabled_state" : "enabled_state"));
        }
        else if (action.equals("toggle"))
        {
            boolean isPlaying = tile.director.togglePlayback();
            L10n.success(sender, isPlaying ? play : stop, args[1], args[2], args[3]);
        }
    }

    /**
     * Get abstract director from block pos
     */
    protected TileEntityDirector getDirector(MinecraftServer server, BlockPos pos)
    {
        TileEntity entity = server.getEntityWorld().getTileEntity(pos);

        if (entity instanceof TileEntityDirector)
        {
            return (TileEntityDirector) entity;
        }

        return null;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "play", "stop", "toggle", "loop", "disable_state");
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}