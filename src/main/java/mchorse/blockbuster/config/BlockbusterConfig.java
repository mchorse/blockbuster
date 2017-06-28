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
     * Send all server models and skins to the player who's logging in?
     */
    public boolean load_models_on_login;

    /**
     * Remove all downloaded models after exiting a server?
     */
    public boolean clean_model_downloads;

    /**
     * Is teleport feature disabled when you sneak and using the playback button?
     */
    public boolean disable_teleport_playback_button;

    /**
     * This option does literally nothing. Or maybe it does...?
     */
    public boolean extra_wubs;

    /**
     * Refresh models and skins when entering in Metamorph or Blockbuster GUIs?
     */
    public boolean auto_refresh_models;

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
     * Switch to spectator mode when starting camera playback?
     */
    public boolean camera_spectator;

    /**
     * Factor for step keys
     */
    public float camera_step_factor;

    /**
     * Factor for rotate keys
     */
    public float camera_rotate_factor;

    /**
     * Enable Minema recording on camera playback, and finish Minema recording
     * when camera finish to playback
     */
    public boolean camera_minema;

    /**
     * Clamp smooth camera's pitch between -90 and 90 degrees range?
     */
    public boolean camera_smooth_clamp;

    /**
     * Default camera path interpolation method
     */
    public String camera_path_default_interp;

    /* Recording */

    /**
     * Recording <s>final</s> countdown
     */
    public int recording_countdown;

    /**
     * Recording frame skip
     */
    public int recording_delay;

    /**
     * How long does it take to unload a record
     */
    public int record_unload_time;

    /**
     * Enable automatic record unloading?
     */
    public boolean record_unload;

    /**
     * How often a recording is going to synchronize with the server
     */
    public int record_sync_rate;

    /**
     * Does attack action get recorded with swipe action?
     */
    public boolean record_attack_on_swipe;

    /**
     * Does command action get recorded during recording?
     */
    public boolean record_commands;

    /* Actors */

    /**
     * Do actors receive fall damage?
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

    /**
     * Enable unconditional actor nametag rendering
     */
    public boolean actor_always_render_names;

    /* Damage control */

    /**
     * Whether damage control is active
     */
    public boolean damage_control;

    /**
     * Radius of effect for damage control
     */
    public int damage_control_distance;

    /* Non conifg option stuff */

    /**
     * Forge configuration
     */
    public Configuration config;

    public BlockbusterConfig(Configuration config)
    {
        this.config = config;
        this.reload();
    }

    /**
     * Reload config values
     *
     * Pretty dense code, am I right? Welcome to the Jungle, I guess.
     */
    public void reload()
    {
        String general = Configuration.CATEGORY_GENERAL;
        String recording = "recording";
        String camera = "camera";
        String actor = "actor";
        String damage = "damage_control";

        /* General */
        this.load_models_on_login = this.getBoolean("load_models_on_login", general, false, "Send all server models and skins to the player who's logging in?");
        this.clean_model_downloads = this.getBoolean("clean_model_downloads", general, true, "Remove all downloaded models after exiting a server?");
        this.disable_teleport_playback_button = this.getBoolean("disable_teleport_playback_button", general, false, "Is teleport feature disabled when you sneak and using the playback button?");
        this.extra_wubs = this.getBoolean("extra_wubs", general, false, "This option does literally nothing. Or maybe it does...?");
        this.auto_refresh_models = this.getBoolean("auto_refresh_models", general, true, "Refresh models and skins when entering in Metamorph or Blockbuster GUIs?");

        /* Camera */
        this.camera_duration_step = this.getInt("camera_duration_step", camera, 10, 1, 100, "What is default step to use when adding or reducing duration of the camera fixture (in ticks)");
        this.camera_duration = this.getInt("camera_duration", camera, 30, 1, 1000, "What is default duration of the camera fixture (in ticks)");
        this.camera_interpolate_target = this.getBoolean("camera_interpolate_target", camera, false, "Interpolate target based camera fixtures' (follow and look) outcome");
        this.camera_interpolate_target_value = this.getFloat("camera_interpolate_target_value", camera, 0.5F, 0.0F, 1.0F, "Interpolation value for target based camera fixture interpolation");
        this.camera_spectator = this.getBoolean("camera_spectator", camera, true, "Switch to spectator mode when starting camera playback?");
        this.camera_step_factor = this.getFloat("camera_step_factor", camera, 0.01F, 0, 10, "Camera step factor for step keys");
        this.camera_rotate_factor = this.getFloat("camera_rotate_factor", camera, 0.1F, 0, 10, "Camera rotate factor for rotate keys");
        this.camera_minema = this.getBoolean("camera_minema", camera, false, "Enable Minema recording on camera playback, and finish Minema recording when camera finish to playback");
        this.camera_path_default_interp = this.getString("camera_path_default_interp", camera, "linear", "Default interpolation method for path fixture (linear, cubic or hermite)");

        /* Smooth camera */
        this.camera_smooth_clamp = this.getBoolean("camera_smooth_clamp", "camera.smooth", true, "Clip smooth camera's pitch between -90 and 90 degrees range?");

        /* Recording */
        this.recording_countdown = this.getInt("recording_countdown", recording, 3, 0, 10, "Recording countdown (in seconds)");
        this.recording_delay = this.getInt("recording_delay", recording, 1, 1, 10, "Frame delay for recording");
        this.record_unload_time = this.getInt("record_unload_time", recording, 2400, 600, 72000, "How long does it take to unload a record (in ticks)");
        this.record_unload = this.getBoolean("record_unload", recording, true, "Enable automatic record unloading?");
        this.record_sync_rate = this.getInt("record_sync_rate", recording, 6, 1, 30, "How often a recording is going to synchronize with the server");
        this.record_attack_on_swipe = this.getBoolean("record_attack_on_swipe", recording, false, "Does attack action get recorded with swipe action?");
        this.record_commands = this.getBoolean("record_commands", recording, true, "Does command action get recorded during recording?");

        /* Actor */
        this.actor_fall_damage = this.getBoolean("actor_fall_damage", actor, true, "Do actors receive fall damage?");
        this.actor_tracking_range = this.getInt("actor_tracking_range", actor, 96, 64, 1024, "How far actors are tracked? Requires restart of the game.");
        this.actor_rendering_range = this.getInt("actor_rendering_range", actor, 64, 64, 1024, "How far actors are seen?");
        this.actor_always_render_names = this.getBoolean("actor_always_render_names", actor, false, "Enable unconditional actor nametag rendering");

        /* Damage control */
        this.damage_control = this.getBoolean("damage_control", damage, false, "Whether damage control is enabled");
        this.damage_control_distance = this.getInt("damage_control_distance", damage, 32, 1, 1024, "Radius of effect for damage control");

        Blockbuster.proxy.onConfigChange(this.config);

        if (this.config.hasChanged())
        {
            this.config.save();
        }
    }

    protected boolean getBoolean(String name, String category, boolean defaultValue, String comment)
    {
        String langKey = "blockbuster.config." + category + "." + name;
        String commentKey = "blockbuster.config.comments." + category + "." + name;

        return this.config.getBoolean(name, category, defaultValue, Blockbuster.proxy.getLanguageString(commentKey, comment), langKey);
    }

    protected int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment)
    {
        String langKey = "blockbuster.config." + category + "." + name;
        String commentKey = "blockbuster.config.comments." + category + "." + name;

        return this.config.getInt(name, category, defaultValue, minValue, maxValue, Blockbuster.proxy.getLanguageString(commentKey, comment), langKey);
    }

    protected float getFloat(String name, String category, float defaultValue, float minValue, float maxValue, String comment)
    {
        String langKey = "blockbuster.config." + category + "." + name;
        String commentKey = "blockbuster.config.comments." + category + "." + name;

        return this.config.getFloat(name, category, defaultValue, minValue, maxValue, Blockbuster.proxy.getLanguageString(commentKey, comment), langKey);
    }

    protected String getString(String name, String category, String defaultValue, String comment)
    {
        String langKey = "blockbuster.config." + category + "." + name;
        String commentKey = "blockbuster.config.comments." + category + "." + name;

        return this.config.getString(name, category, defaultValue, Blockbuster.proxy.getLanguageString(commentKey, comment), langKey);
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