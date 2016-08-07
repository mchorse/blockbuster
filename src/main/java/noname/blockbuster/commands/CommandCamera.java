package noname.blockbuster.commands;

import noname.blockbuster.ClientProxy;
import noname.blockbuster.camera.CameraControl;
import noname.blockbuster.camera.CameraProfile;
import noname.blockbuster.commands.camera.SubCommandCameraClear;
import noname.blockbuster.commands.camera.SubCommandCameraGoto;
import noname.blockbuster.commands.camera.SubCommandCameraLoad;
import noname.blockbuster.commands.camera.SubCommandCameraNew;
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
    private static CameraControl control = new CameraControl();

    public static CameraProfile getProfile()
    {
        return profile;
    }

    public static CameraControl getControl()
    {
        return control;
    }

    public static void setProfile(CameraProfile profile)
    {
        CommandCamera.profile = profile;
        CommandCamera.control.profile = profile;
        ClientProxy.profileRunner.setProfile(profile);
        ClientProxy.profileRenderer.setProfile(profile);
    }

    public static void reset()
    {
        control.reset();
        setProfile(new CameraProfile(""));
    }

    /**
     * Camera's command constructor
     *
     * This constructor is responsible for registering its sub-commands.
     */
    public CommandCamera()
    {
        /* Start/stop */
        this.add(new SubCommandCameraStart());
        this.add(new SubCommandCameraStop());

        /* Load/save */
        this.add(new SubCommandCameraNew());
        this.add(new SubCommandCameraLoad());
        this.add(new SubCommandCameraSave());

        /* Profile */
        this.add(new SubCommandCameraClear());
        this.add(new SubCommandCameraGoto());

        /* Fixture editing */
        this.add(new SubCommandFixtureAdd());
        this.add(new SubCommandFixtureEdit());
        this.add(new SubCommandFixtureMove());
        this.add(new SubCommandFixtureRemove());
        this.add(new SubCommandFixtureDuration());
        this.add(new SubCommandFixturePath());
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