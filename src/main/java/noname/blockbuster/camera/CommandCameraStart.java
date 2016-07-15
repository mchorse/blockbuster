package noname.blockbuster.camera;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import noname.blockbuster.camera.fixtures.CircularFixture;
import noname.blockbuster.camera.fixtures.IdleFixture;

public class CommandCameraStart extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "cam-start";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera_start";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        CameraProfile profile = new CameraProfile();

        profile.addFixture(new IdleFixture(1000, new Position(-132, 9, -95, 0, 45)));
        profile.addFixture(new IdleFixture(1000, new Position(-126, 9, -95, 90, 0)));
        profile.addFixture(new CircularFixture(16000, new Point(-132, 9, -95), new Point(-132, 9, -100), 720));

        new ProfileRunner(profile).start();
    }
}
