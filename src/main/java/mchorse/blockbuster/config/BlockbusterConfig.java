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
    /* General */

    /**
     * Send models and skins to the player which is about to log in?
     */
    public boolean load_models_on_login;

    /**
     * Clean downloaded models after exiting a server?
     */
    public boolean clean_model_downloads;

    /* Camera */

    /**
     * Camera duration step (used by keyboard duration bindings)
     */
    public int camera_duration_step;

    /**
     * Default camera duration (used by keyboard fixture bindings)
     */
    public int camera_duration;

    /**
     * Interpolate target fixtures position?
     */
    public boolean camera_interpolate_target;

    /**
     * Ratio for target fixtures interpolation
     */
    public float camera_interpolate_target_value;

    /**
     * Switch to spectator mode when starting camera playback
     */
    public boolean camera_spectator;

    /* Recording */

    /**
     * Recording frame skip
     */
    public int recording_delay;

    /**
     * How long it takes (in ticks) to unload a record
     */
    public int record_unload_time;

    /**
     * Enable automatic record unloading?
     */
    public boolean record_unload;

    /**
     * How often a record going to synchronize with the server
     */
    public int record_sync_rate;

    /**
     * Does attack action get recorded with swipe action?
     */
    public boolean record_attack_on_swipe;

    /* Actors */

    /**
     * Does actor receive fall damage?
     */
    public boolean actor_fall_damage;

    /**
     * Actor tracking range. Requires restart
     */
    public int actor_tracking_range;

    /**
     * Actor rendering range
     */
    public int actor_rendering_range;

    private Configuration config;

    public BlockbusterConfig(Configuration config)
    {
        this.config = config;
        this.reload();
    }

    /**
     * Reload config values
     *
     * Pretty dense code, am I right?
     */
    public void reload()
    {
        String general = Configuration.CATEGORY_GENERAL;
        String recording = "recording";
        String camera = "camera";
        String actor = "actor";

        String genPrefix = "blockbuster.config.general.";
        String recPrefix = "blockbuster.config.recording.";
        String camPrefix = "blockbuster.config.camera.";
        String actPrefix = "blockbuster.config.actor.";

        /* General */
        this.load_models_on_login = this.config.getBoolean("load_models_on_login", general, false, "Send models and skins when player is logging in", genPrefix + "load_models_on_login");
        this.clean_model_downloads = this.config.getBoolean("clean_model_downloads", general, true, "Clean downloaded models upon exiting a server", genPrefix + "clean_model_downloads");

        /* Camera */
        this.camera_duration_step = this.config.getInt("camera_duration_step", camera, 10, 1, 100, "What is default step to use when adding or reducing duration of the camera fixture (in ticks)", camPrefix + "camera_duration_step");
        this.camera_duration = this.config.getInt("camera_duration", camera, 30, 1, 1000, "What is default duration of the camera fixture (in ticks)", camPrefix + "camera_duration");
        this.camera_interpolate_target = this.config.getBoolean("camera_interpolate_target", camera, false, "Interpolate target based camera fixtures (follow and look) outcome", camPrefix + "camera_interpolate_target");
        this.camera_interpolate_target_value = this.config.getFloat("camera_interpolate_target_value", camera, 0.5F, 0.0F, 1.0F, "Interpolation value for target based camera fixture interpolation", camPrefix + "camera_interpolate_target_value");
        this.camera_spectator = this.config.getBoolean("camera_spectator", camera, true, "Switch to spectator mode when starting camera playback", camPrefix + "camera_spectator");

        /* Recording */
        this.recording_delay = this.config.getInt("recording_delay", recording, 1, 1, 10, "Frame delay for recording", recPrefix + "recording_delay");
        this.record_unload_time = this.config.getInt("record_unload_time", recording, 2400, 600, 72000, "How long is it takes to unload a record (in ticks)", recPrefix + "record_unload_time");
        this.record_unload = this.config.getBoolean("record_unload", recording, true, "Enable automatic record unloading?", recPrefix + "record_unload");
        this.record_sync_rate = this.config.getInt("record_sync_rate", recording, 6, 1, 30, "How often a record going to synchronize with the server", recPrefix + "record_sync_rate");
        this.record_attack_on_swipe = this.config.getBoolean("record_attack_on_swipe", recording, false, "Does attack action get recorded with swipe action?", recPrefix + "record_attack_on_swipe");

        /* Actor */
        this.actor_fall_damage = this.config.getBoolean("actor_fall_damage", actor, true, "Does actor receive fall damage?", actPrefix + "actor_fall_damage");
        this.actor_tracking_range = this.config.getInt("actor_tracking_range", actor, 96, 64, 1024, "How far actors are tracked? Requires restart of the game.", actPrefix + "actor_tracking_range");
        this.actor_rendering_range = this.config.getInt("actor_rendering_range", actor, 64, 64, 1024, "How far actors are seen?", actPrefix + "actor_rendering_range");

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