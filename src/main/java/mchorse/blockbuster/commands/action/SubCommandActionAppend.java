package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.scene.Scene;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /action append
 *
 * This sub-command is responsible for starting recording given
 */
public class SubCommandActionAppend extends CommandBase
{
    @Override
    public String getName()
    {
        return "append";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.append";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getUsage(sender));
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        int offset = CommandBase.parseInt(args[1], 0);

        if (args.length >= 3)
        {
            Scene scene = CommonProxy.scenes.get(args[2], sender.getEntityWorld());

            if (scene != null)
            {
                CommonProxy.scenes.record(args[2], args[0], offset, player);
            }
        }
        else
        {
            CommonProxy.manager.record(args[0], player, Mode.ACTIONS, true, true, offset, null);
        }
    }
}