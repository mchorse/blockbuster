package noname.blockbuster.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.Point;
import noname.blockbuster.camera.Position;
import noname.blockbuster.camera.ProfileRunner;
import noname.blockbuster.camera.fixtures.CircularFixture;
import noname.blockbuster.camera.fixtures.IdleFixture;
import noname.blockbuster.commands.sub.SubCommandBase;
import noname.blockbuster.commands.sub.SubCommandCameraProfile;
import noname.blockbuster.commands.sub.SubCommandCameraStart;
import noname.blockbuster.commands.sub.SubCommandCameraStop;

/**
 * Camera command
 *
 * This `camera` command is an interface to work with camera, in general, but
 * specifically this commands provides sub commands for manipulating camera
 * fixtures and camera profiles.
 *
 * This command provides following sub commands:
 * - Managing profiles
 *   - Create new profiles
 *   - Select current profile
 *   - Remove profiles
 *   - Moving fixtures around in current profile
 *   - Add a new fixture to current profile
 *   - Display all fixtures in current profile
 *   - Remove fixtures
 * - Managing fixtures
 *   - Edit fixture properties
 *   - Special sub command to manage path fixture
 *     - Add point
 *     - Remove point
 *   - Edit fixture based on player's properties
 */
public class CommandCamera extends SubCommandBase
{
    public ProfileRunner runner;

    public CommandCamera()
    {
        CameraProfile profile = new CameraProfile();

        profile.add(new IdleFixture(1000, new Position(-132, 9, -95, 0, 45)));
        profile.add(new IdleFixture(1000, new Position(-126, 9, -95, 90, 0)));
        profile.add(new CircularFixture(16000, new Point(-132, 9, -95), new Point(-132, 9, -100), 720));

        this.runner = new ProfileRunner(profile);
        this.subcommands = new CommandBase[] {new SubCommandCameraStart(this), new SubCommandCameraStop(this), new SubCommandCameraProfile(this)};
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
}