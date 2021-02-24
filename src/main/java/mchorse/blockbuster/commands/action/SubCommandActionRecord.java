package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.blockbuster.recording.scene.Scene;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /action record
 *
 * This sub-command is responsible for starting recording given filename'd
 * action with optionally provided scene.
 */
public class SubCommandActionRecord extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "record";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.record";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}action {8}record{r} {7}<filename> [scene]{7}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);

        if (args.length >= 2)
        {
            Scene scene = CommonProxy.scenes.get(args[1], sender.getEntityWorld());

            if (scene != null)
            {
                CommonProxy.scenes.record(args[1], args[0], player);
            }
        }
        else
        {
            CommonProxy.manager.record(args[0], player, Mode.ACTIONS, true, true, null);
        }
    }
}