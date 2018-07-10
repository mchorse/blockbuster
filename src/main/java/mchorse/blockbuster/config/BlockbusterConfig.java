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

    /**
     * Write ticks in the log during director block recording
     */
    public boolean debug_playback_ticks;

    /**
     * Makes the sky fully green for chroma keying purposes (suggested 
     * by Andruxioid)
     */
    public boolean green_screen_sky;

    /* Model block */

    /**
     * Whether model block item rendering should be disabled 
     */
    public boolean model_block_disable_item_rendering;

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

    /**
     * Do actors emits a swish sound when swiping? 
     */
    public boolean actor_swish_swipe;

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
        String actor = "actor";
        String damage = "damage_control";
        String model = "model_block";

        /* General */
        this.disable_teleport_playback_button = this.getBoolean("disable_teleport_playback_button", general, false, "Is teleport feature disabled when you sneak and using the playback button?");
        this.extra_wubs = this.getBoolean("extra_wubs", general, false, "This option does literally nothing. Or maybe it does...?");
        this.auto_refresh_models = this.getBoolean("auto_refresh_models", general, true, "Refresh models and skins when entering in Metamorph or Blockbuster GUIs?");
        this.debug_playback_ticks = this.getBoolean("debug_playback_ticks", general, false, "Write ticks in the log during director block recording");
        this.green_screen_sky = this.getBoolean("green_screen_sky", general, false, "Makes the sky fully green for chroma keying purposes");

        /* Model block */
        this.model_block_disable_item_rendering = this.getBoolean("model_block_disable_item_rendering", model, false, "Whether model block item rendering should be disabled");

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
        this.actor_tracking_range = this.getInt("actor_tracking_range", actor, 256, 64, 1024, "How far actors are tracked? Requires restart of the game.");
        this.actor_rendering_range = this.getInt("actor_rendering_range", actor, 256, 64, 1024, "How far actors are seen?");
        this.actor_always_render_names = this.getBoolean("actor_always_render_names", actor, false, "Enable unconditional actor nametag rendering");
        this.actor_swish_swipe = this.getBoolean("actor_swish_swipe", actor, false, "Do actors emit swish sound when swiping?");

        /* Damage control */
        this.damage_control = this.getBoolean("damage_control", damage, true, "Whether damage control is enabled");
        this.damage_control_distance = this.getInt("damage_control_distance", damage, 64, 1, 1024, "Radius of effect for damage control");

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