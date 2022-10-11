package mchorse.blockbuster.commands.action;

import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.commands.BBCommandBase;
import mchorse.blockbuster.recording.RecordUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command /action request
 *
 * This command is responsible for requesting record frames from the server
 */
public class SubCommandActionRequest extends BBCommandBase
{
    @Override
    public String getName()
    {
        return "request";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "blockbuster.commands.action.request";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}action {8}request{r} {7}<filename>{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CommonProxy.scenes.play(args[0], sender.getEntityWorld());
        RecordUtils.sendRecordTo(args[0], getCommandSenderAsPlayer(sender));
    }
}