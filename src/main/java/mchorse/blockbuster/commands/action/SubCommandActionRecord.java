package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.common.CommonProxy;
import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.recording.data.Mode;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Sub-command /action record
 *
 * This sub-command is responsible for starting recording given filename'd
 * action with optionally provided coordinates of director block.
 */
public class SubCommandActionRecord extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "record";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.record";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        EntityPlayer player = getCommandSenderAsPlayer(sender);
        boolean recording = CommonProxy.manager.startRecording(args[0], player, Mode.ACTIONS, true);

        if (recording && args.length >= 4)
        {
            BlockPos pos = CommandBase.parseBlockPos(sender, args, 1, false);
            TileEntity tile = sender.getEntityWorld().getTileEntity(pos);

            if (tile instanceof TileEntityDirector)
            {
                TileEntityDirector director = (TileEntityDirector) tile;

                director.applyReplay(director.byFile(args[0]), player);
                director.startPlayback(args[0]);
            }
        }
    }
}