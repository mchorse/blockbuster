package noname.blockbuster.commands.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import noname.blockbuster.ClientProxy;

/**
 * Camera's sub-command /camera stop
 *
 * This sub-command is responsible for stopping current running camera profile.
 */
public class SubCommandCameraStop extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "stop";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera.stop";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ClientProxy.profileRunner.stop();
        sender.addChatMessage(new TextComponentTranslation("blockbuster.profile.stop"));
    }
}