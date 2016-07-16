package noname.blockbuster.commands.sub;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.commands.CommandCamera;

public class SubCommandCameraStop extends CommandCameraBase
{
    public SubCommandCameraStop(CommandCamera parent)
    {
        super(parent);
    }

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
        this.parent.runner.stop();
    }
}
