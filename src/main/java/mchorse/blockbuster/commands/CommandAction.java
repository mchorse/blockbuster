package mchorse.blockbuster.commands;

import java.util.List;

import mchorse.blockbuster.common.tileentity.TileEntityDirector;
import mchorse.blockbuster.recording.Mocap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Command /action
 *
 * This command is responsible for recording player actions or playbacking
 * already recorded player actions.
 *
 * This command is merged version of CommandPlay and CommandRecord (which both
 * were removed). These commands were merged together, because they had similar
 * signature and work with player recordings.
 */
public class CommandAction extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "action";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }

        String action = args[0];

        if (action.equals("record") && args.length >= 2)
        {
            EntityPlayer player = getCommandSenderAsPlayer(sender);
            boolean recording = Mocap.startRecording(args[1], player);

            if (recording && args.length >= 5)
            {
                BlockPos pos = CommandBase.parseBlockPos(sender, args, 2, false);
                TileEntity tile = sender.getEntityWorld().getTileEntity(pos);

                if (tile instanceof TileEntityDirector)
                {
                    TileEntityDirector director = (TileEntityDirector) tile;

                    director.applyReplay(director.byFile(args[1]), player);
                    director.startPlayback(args[1]);
                }
            }
        }
        else if (action.equals("play") && args.length >= 2)
        {
            Mocap.startPlayback(SubCommandBase.dropFirstArgument(args), sender.getEntityWorld(), true);
        }
        else if (action.equals("stop"))
        {
            Mocap.stopRecording(getCommandSenderAsPlayer(sender));
        }
        else
        {
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "record", "play", "stop");
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}
