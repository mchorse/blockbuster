package mchorse.blockbuster.recording.scene;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.CommonProxy;
import mchorse.blockbuster.recording.Utils;
import mchorse.blockbuster.recording.data.Mode;
import mchorse.mclib.utils.Patterns;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Scene manager
 *
 * This bro allows to manage scenes (those are something like remote director blocks).
 */
public class SceneManager
{
    /**
     * Currently loaded scenes
     */
    private Map<String, Scene> scenes = new HashMap<String, Scene>();

    private List<String> toRemove = new ArrayList<String>();
    private Map<String, Scene> toPut = new HashMap<String, Scene>();
    private boolean ticking;

    public Map<String, Scene> getScenes()
    {
        return new HashMap<>(this.scenes);
    }

    public static boolean isValidFilename(String filename)
    {
        return !filename.isEmpty() && Patterns.FILENAME.matcher(filename).matches();
    }

    /**
     * Reset scene manager
     */
    public void reset()
    {
        this.ticking = false;
        this.toRemove.clear();
        this.toPut.clear();

        this.scenes.clear();
    }

    /**
     * Spawn actors and execute unsafe actions
     */
    public void worldTick(World world)
    {
        for (Map.Entry<String, Scene> entry : this.scenes.entrySet())
        {
            Scene scene = entry.getValue();

            scene.worldTick(world);
        }
    }

    /**
     * Tick scenes
     */
    public void tick()
    {
        this.ticking = true;

        try
        {
            for (Map.Entry<String, Scene> entry : this.scenes.entrySet())
            {
                Scene scene = entry.getValue();

                scene.tick();

                if (!scene.playing)
                {
                    this.toRemove.add(entry.getKey());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.ticking = false;

        for (String scene : this.toRemove)
        {
            this.scenes.remove(scene);
        }

        this.scenes.putAll(this.toPut);

        this.toRemove.clear();
        this.toPut.clear();
    }

    /**
     * Play a scene
     */
    public boolean play(String filename, World world)
    {
        Scene scene = this.get(filename,world);

        if (scene == null)
        {
            return false;
        }

        scene.startPlayback(0);

        return true;
    }

    public void record(String filename, String record, EntityPlayerMP player)
    {
        this.record(filename, record, 0, player);
    }

    /**
     * Record the player
     */
    public void record(String filename, String record, int offset, EntityPlayerMP player)
    {
        final Scene scene = this.get(filename, player.world);

        if (scene != null)
        {
            scene.setWorld(player.world);

            final Replay replay = scene.getByFile(record);

            if (replay != null)
            {
                CommonProxy.manager.record(replay.id, player, Mode.ACTIONS, replay.teleportBack, true, offset, () ->
                {
                    if (!CommonProxy.manager.recorders.containsKey(player))
                    {
                        this.put(filename, scene);
                        scene.startPlayback(record, offset);
                    }
                    else
                    {
                        scene.stopPlayback(true);
                    }

                    replay.apply(player);
                });
            }
        }
    }

    /**
     * Toggle playback of a scene by given filename
     */
    public boolean toggle(String filename, World world)
    {
        Scene scene = this.scenes.get(filename);

        if (scene != null)
        {
            scene.stopPlayback(true);

            return false;
        }

        return this.play(filename, world);
    }

    /**
     * Get currently running or load a scene
     */
    public Scene get(String filename, World world)
    {
        Scene scene = this.scenes.get(filename);

        if (scene != null)
        {
            return scene;
        }

        try
        {
            scene = this.load(filename);

            if (scene != null)
            {
                scene.setWorld(world);
                scene.setSender(new SceneSender(scene));
                this.put(filename, scene);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return scene;
    }

    /**
     * Load a scene by given filename
     */
    public Scene load(String filename) throws IOException
    {
        File file = sceneFile(filename);

        if (!file.isFile())
        {
            return null;
        }

        NBTTagCompound compound = CompressedStreamTools.readCompressed(new FileInputStream(file));
        Scene scene = new Scene();

        scene.setId(filename);
        scene.fromNBT(compound);

        return scene;
    }

    public void save(String filename, Scene scene) throws IOException
    {
        this.save(filename, scene, Blockbuster.sceneSaveUpdate.get());
    }

    /**
     * Save a scene by given filename
     */
    public void save(String filename, Scene scene, boolean reload) throws IOException
    {
        Scene present = this.scenes.get(scene.getId());

        if (reload && present != null)
        {
            present.copy(scene);
            present.reload(present.getCurrentTick());
        }

        File file = sceneFile(filename);
        NBTTagCompound compound = new NBTTagCompound();

        scene.toNBT(compound);

        CompressedStreamTools.writeCompressed(compound, new FileOutputStream(file));
    }

    /**
     * Rename a scene on the disk
     */
    public boolean rename(String from, String to)
    {
        File fromFile = sceneFile(from);
        File toFile = sceneFile(to);

        if (fromFile.isFile() && !toFile.exists())
        {
            return fromFile.renameTo(toFile);
        }

        return false;
    }

    /**
     * Remove a scene from the disk
     */
    public boolean remove(String filename)
    {
        File file = sceneFile(filename);

        if (file.exists())
        {
            return file.delete();
        }

        return false;
    }

    /**
     * Returns a file instance to the scene by given filename
     */
    private File sceneFile(String filename)
    {
        return Utils.serverFile("blockbuster/scenes", filename);
    }

    private void put(String filename, Scene scene)
    {
        (this.ticking ? this.toPut : this.scenes).put(filename, scene);
    }

    /**
     * Get all the NBT files in the scenes folder
     */
    public List<String> sceneFiles()
    {
        return Utils.serverFiles("blockbuster/scenes");
    }
}