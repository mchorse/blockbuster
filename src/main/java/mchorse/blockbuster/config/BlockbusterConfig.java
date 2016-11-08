package mchorse.blockbuster.config;

import mchorse.blockbuster.Blockbuster;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Blockbuster config class
 *
 * This class stores config properties powered by Forge built-in configuration
 * library.
 *
 * I looked up how this configuration works in Minema mod, so don't wonder if
 * it looks pretty similar. I also looked up Chroonster TestMod3.
 */
public class BlockbusterConfig
{
    /**
     * Send models and skins to the player which is about to log in?
     */
    public boolean load_models_on_login;

    /**
     * Recording frame skip
     */
    public int recording_delay;

    /**
     * Camera duration step (used by keyboard duration bindings)
     */
    public int camera_duration_step;

    /**
     * Default camera duration (used by keyboard fixture bindings)
     */
    public int camera_duration;

    /**
     * How long it takes (in ticks) to unload a record
     */
    public int record_unload_time;

    /**
     * Enable automatic record unloading?
     */
    public boolean record_unload;

    private Configuration config;

    public BlockbusterConfig(Configuration config)
    {
        this.config = config;
        this.reload();
    }

    /**
     * Reload config values
     */
    public void reload()
    {
        String general = Configuration.CATEGORY_GENERAL;
        String recording = "recording";
        String camera = "camera";

        String genPrefix = "blockbuster.config.general.";
        String recPrefix = "blockbuster.config.recording.";
        String camPrefix = "blockbuster.config.camera.";

        this.load_models_on_login = this.config.getBoolean("load_models_on_login", general, false, "Send models and skins when player is logging in", genPrefix + "load_models_on_login");

        this.camera_duration_step = this.config.getInt("camera_duration_step", camera, 10, 1, 100, "What is default step to use when adding or reducing duration of the camera fixture (in ticks)", camPrefix + "camera_duration_step");
        this.camera_duration = this.config.getInt("camera_duration", camera, 30, 1, 1000, "What is default duration of the camera fixture (in ticks)", camPrefix + "camera_duration");

        this.recording_delay = this.config.getInt("recording_delay", recording, 1, 1, 10, "Frame delay for recording", recPrefix + "recording_delay");
        this.record_unload_time = this.config.getInt("record_unload_time", recording, 2400, 600, 72000, "How long is it takes to unload a record (in ticks)", recPrefix + "record_unload_time");
        this.record_unload = this.config.getBoolean("record_unload", recording, true, "Enable automatic record unloading?", recPrefix + "record_unload");

        if (this.config.hasChanged())
        {
            this.config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Blockbuster.MODID) && this.config.hasChanged())
        {
            this.reload();
        }
    }
}