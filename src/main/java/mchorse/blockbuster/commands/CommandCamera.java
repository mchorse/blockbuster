package mchorse.blockbuster.commands;

import mchorse.blockbuster.camera.CameraControl;
import mchorse.blockbuster.camera.CameraProfile;
import mchorse.blockbuster.commands.camera.SubCommandCameraClear;
import mchorse.blockbuster.commands.camera.SubCommandCameraGoto;
import mchorse.blockbuster.commands.camera.SubCommandCameraLoad;
import mchorse.blockbuster.commands.camera.SubCommandCameraNew;
import mchorse.blockbuster.commands.camera.SubCommandCameraSave;
import mchorse.blockbuster.commands.camera.SubCommandCameraStart;
import mchorse.blockbuster.commands.camera.SubCommandCameraStop;
import mchorse.blockbuster.commands.fixture.SubCommandFixtureAdd;
import mchorse.blockbuster.commands.fixture.SubCommandFixtureDuration;
import mchorse.blockbuster.commands.fixture.SubCommandFixtureEdit;
import mchorse.blockbuster.commands.fixture.SubCommandFixtureMove;
import mchorse.blockbuster.commands.fixture.SubCommandFixturePath;
import mchorse.blockbuster.commands.fixture.SubCommandFixtureRemove;
import mchorse.blockbuster.common.ClientProxy;

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