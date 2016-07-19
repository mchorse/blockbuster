package noname.blockbuster.commands;

import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.camera.ProfileRunner;
import noname.blockbuster.commands.camera.SubCommandCameraLoad;
import noname.blockbuster.commands.camera.SubCommandCameraProfile;
import noname.blockbuster.commands.camera.SubCommandCameraSave;
import noname.blockbuster.commands.camera.SubCommandCameraStart;
import noname.blockbuster.commands.camera.SubCommandCameraStop;
import noname.blockbuster.commands.fixture.SubCommandFixtureAdd;
import noname.blockbuster.commands.fixture.SubCommandFixtureDuration;
import noname.blockbuster.commands.fixture.SubCommandFixtureEdit;
import noname.blockbuster.commands.fixture.SubCommandFixtureList;
import noname.blockbuster.commands.fixture.SubCommandFixturePath;

/**
 * Camera /command
 *
 * This command is an interface to work with camera, in general, but specifically
 * this commands provides sub commands for manipulating camera fixtures and
 * camera profiles.
 */
public class CommandCamera extends SubCommandBase
{
    public static final ProfileRunner runner = new ProfileRunner(new CameraProfile());

    public CommandCamera()
    {
        this.subcommands.add(new SubCommandCameraStart());
        this.subcommands.add(new SubCommandCameraStop());
        this.subcommands.add(new SubCommandCameraProfile());
        this.subcommands.add(new SubCommandCameraSave());
        this.subcommands.add(new SubCommandCameraLoad());

        this.subcommands.add(new SubCommandFixtureAdd());
        this.subcommands.add(new SubCommandFixtureEdit());
        this.subcommands.add(new SubCommandFixtureDuration());
        this.subcommands.add(new SubCommandFixturePath());
        this.subcommands.add(new SubCommandFixtureList());
    }

    @Override
    public String getCommandName()
    {
        return "camera";
    }

    @Override
    protected String getHelp()
    {
        return "blockbuster.commands.camera";
    }
}