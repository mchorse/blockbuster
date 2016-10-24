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
    public boolean load_models_on_login = false;
    public int recording_delay = 1;
    public int camera_duration_step = 10;
    public int camera_duration = 30;

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
        String category = Configuration.CATEGORY_GENERAL;
        String prefix = "blockbuster.config.general.";

        this.load_models_on_login = this.config.getBoolean("load_models_on_login", category, false, "Send models and skins when player is logging in", prefix + "load_models_on_login");
        this.recording_delay = this.config.getInt("recording_delay", category, 1, 1, 10, "Frames to skip before record or play from record", prefix + "recording_delay");
        this.camera_duration_step = this.config.getInt("camera_duration_step", category, 10, 1, 100, "Which step to use when adding or reducing duration of the camera fixture");
        this.camera_duration = this.config.getInt("camera_duration", category, 30, 1, 1000, "What is default duration of the camera fixture");

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