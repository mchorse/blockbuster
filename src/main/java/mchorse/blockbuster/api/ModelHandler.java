package mchorse.blockbuster.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.Morphing;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.server.ServerHandlerRequestModels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

/**
 * This class responsible for storing domain custom models and sending models to
 * players who are logged in.
 */
public class ModelHandler
{
    /**
     * Cached models, they're loaded from stuffs
     */
    public Map<String, Model> models = new HashMap<String, Model>();

    /**
     * Actors pack from which ModelHandler loads its models
     */
    public ModelPack pack;

    /**
     * Load user and default provided models into model map
     */
    public void loadModels(ModelPack pack)
    {
        this.models.clear();
        pack.reload();

        /* Load user provided models */
        for (String model : pack.getModels())
        {
            try
            {
                InputStream modelStream = new FileInputStream(pack.models.get(model));

                this.models.put(model, Model.parse(modelStream));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /* Load default provided models */
        try
        {
            String path = "assets/blockbuster/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            this.models.put("alex", Model.parse(loader.getResourceAsStream(path + "alex.json")));
            this.models.put("steve", Model.parse(loader.getResourceAsStream(path + "steve.json")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Clear models when disconnecting from server
     */
    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event)
    {
        this.models.clear();

        if (Blockbuster.proxy.config.clean_model_downloads)
        {
            try
            {
                File models = new File(ClientProxy.config + "/downloads");
                FileUtils.cleanDirectory(models);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads local models when connecting to the server
     */
    @SubscribeEvent
    public void onClientConnect(ClientConnectedToServerEvent event)
    {
        Blockbuster.proxy.loadModels(Blockbuster.proxy.getPack());
    }

    /**
     * When player is logs in, send him all available models and skins. I think
     * this should go to a separate server handler
     */
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        if (Blockbuster.proxy.config.load_models_on_login)
        {
            ServerHandlerRequestModels.sendModels(this, player);
        }
    }

    /**
     * On player tick, we have to change AABB box based on the size current's
     * morph pose
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START) return;

        EntityPlayer player = event.player;
        IMorphing cap = Morphing.get(player);
        Model data = this.models.get(cap.getModel());

        if (data == null)
        {
            /* Restore default eye height */
            player.eyeHeight = player.getDefaultEyeHeight();

            return;
        }

        String key = player.isElytraFlying() ? "flying" : (player.isSneaking() ? "sneaking" : "standing");
        float[] pose = data.poses.get(key).size;

        this.updateSize(player, pose[0], pose[1]);
    }

    /**
     * Update size of the player with given widht and height
     *
     * Taken from {@link EntityPlayer}
     */
    private void updateSize(EntityPlayer player, float width, float height)
    {
        if (width != player.width || height != player.height)
        {
            float f = player.width;
            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();

            player.width = width;
            player.height = height;
            player.eyeHeight = height * 0.9F;
            player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + width, axisalignedbb.minY + height, axisalignedbb.minZ + width));

            if (player.width > f && !player.worldObj.isRemote)
            {
                player.moveEntity(f - player.width, 0.0D, f - player.width);
            }
        }
    }
}