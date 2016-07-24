package noname.blockbuster.commands;

import noname.blockbuster.ClientProxy;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.commands.camera.SubCommandCameraClear;
import noname.blockbuster.commands.camera.SubCommandCameraGoto;
import noname.blockbuster.commands.camera.SubCommandCameraLoad;
import noname.blockbuster.commands.camera.SubCommandCameraSave;
import noname.blockbuster.commands.camera.SubCommandCameraStart;
import noname.blockbuster.commands.camera.SubCommandCameraStop;
import noname.blockbuster.commands.fixture.SubCommandFixtureAdd;
import noname.blockbuster.commands.fixture.SubCommandFixtureDuration;
import noname.blockbuster.commands.fixture.SubCommandFixtureEdit;
import noname.blockbuster.commands.fixture.SubCommandFixtureMove;
import noname.blockbuster.commands.fixture.SubCommandFixturePath;
import noname.blockbuster.commands.fixture.SubCommandFixtureRemove;

/**
 * Camera /command
 *
 * This command is an interface to work with camera, in general, but specifically
 * this commands provides sub-commands for manipulating camera fixtures and
 * camera profiles.
 *
 * This command works on the client side, so there's no way you could use it in
 * command block, yet.
 *
 * @todo Create /cb-camera, which will be able to start/stop camera using
 *       command block, only if being requested.
 */
public class CommandCamera extends SubCommandBase
{
    private static CameraProfile profile;

    public static CameraProfile getProfile()
    {
        return profile;
    }

    public static void setProfile(CameraProfile profile)
    {
        CommandCamera.profile = profile;
        ClientProxy.profileRunner.setProfile(profile);
        ClientProxy.profileRenderer.setProfile(profile);
    }

    static
    {
        setProfile(new CameraProfile());
    }

    /**
     * Camera's command constructor
     *
     * This constructor is responsible for registering its sub-commands.
     */
    public CommandCamera()
    {
        /* Start/stop */
        this.subcommands.add(new SubCommandCameraStart());
        this.subcommands.add(new SubCommandCameraStop());

        /* Load/save */
        this.subcommands.add(new SubCommandCameraLoad());
        this.subcommands.add(new SubCommandCameraSave());

        /* Profile */
        this.subcommands.add(new SubCommandCameraClear());
        this.subcommands.add(new SubCommandCameraGoto());

        /* Fixture editing */
        this.subcommands.add(new SubCommandFixtureAdd());
        this.subcommands.add(new SubCommandFixtureEdit());
        this.subcommands.add(new SubCommandFixtureMove());
        this.subcommands.add(new SubCommandFixtureRemove());
        this.subcommands.add(new SubCommandFixtureDuration());
        this.subcommands.add(new SubCommandFixturePath());
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