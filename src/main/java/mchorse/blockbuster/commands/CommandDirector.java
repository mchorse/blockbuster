package mchorse.blockbuster.commands;

import java.util.List;

import mchorse.blockbuster.common.tileentity.AbstractTileEntityDirector;
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
        AbstractTileEntityDirector director = this.getDirector(server, pos);

        if (action.equals("play"))
        {
            director.startPlayback();
            L10n.send(sender, "blockbuster.success.director.play", args[1], args[2], args[3]);
        }
        else if (action.equals("stop"))
        {
            director.stopPlayback();
            L10n.send(sender, "blockbuster.success.director.stop", args[1], args[2], args[3]);
        }
    }

    /**
     * Get abstract director from block pos
     */
    protected AbstractTileEntityDirector getDirector(MinecraftServer server, BlockPos pos) throws CommandException
    {
        TileEntity entity = server.getEntityWorld().getTileEntity(pos);

        if (entity instanceof AbstractTileEntityDirector)
        {
            return (AbstractTileEntityDirector) entity;
        }

        throw new CommandException("blockbuster.error.director.no_director", pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "play", "stop");
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}