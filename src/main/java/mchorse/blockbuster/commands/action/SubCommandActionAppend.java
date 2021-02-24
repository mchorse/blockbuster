package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.scene.Scene;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /action append
 *
 * This sub-command is responsible for starting recording given
 */
public class SubCommandActionAppend extends BBCommandBase
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
    public String getSyntax()
    {
        return "{l}{6}/{r}action {8}append{r} {7}<filename> <offset> [scene]{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
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