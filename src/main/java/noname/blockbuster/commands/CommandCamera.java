package noname.blockbuster.commands;

import net.minecraft.command.ICommandSender;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.Position;
import noname.blockbuster.camera.ProfileRunner;
import noname.blockbuster.camera.fixtures.IdleFixture;
import noname.blockbuster.camera.fixtures.PathFixture;
import noname.blockbuster.commands.sub.SubCommandBase;
import noname.blockbuster.commands.sub.SubCommandCameraFixture;
import noname.blockbuster.commands.sub.SubCommandCameraProfile;
import noname.blockbuster.commands.sub.SubCommandCameraStart;
import noname.blockbuster.commands.sub.SubCommandCameraStop;

/**
 * Camera command
 *
 * This `camera` command is an interface to work with camera, in general, but
 * specifically this commands provides sub commands for manipulating camera
 * fixtures and camera profiles.
 */
public class CommandCamera extends SubCommandBase
{
    public static final ProfileRunner runner;

    static
    {
        CameraProfile profile = new CameraProfile();
        PathFixture path = new PathFixture(8000);

        path.addPoint(new Position(-132, 9, -95, 0, 45));
        path.addPoint(new Position(-132, 9, -120, 0, 0));

        profile.add(new IdleFixture(1000, new Position(-132, 9, -95, 0, 45)));
        profile.add(new IdleFixture(1000, new Position(-126, 9, -95, 90, 0)));
        profile.add(path);

        runner = new ProfileRunner(profile);
    }

    {
        /**
         * Register camera's subcommands
         */
        this.subcommands.add(new SubCommandCameraStart());
        this.subcommands.add(new SubCommandCameraStop());
        this.subcommands.add(new SubCommandCameraProfile());
        this.subcommands.add(new SubCommandCameraFixture());
    }

    @Override
    public String getCommandName()
    {
        return "camera";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "blockbuster.commands.camera";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.camera";
    }
}