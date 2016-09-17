package mchorse.blockbuster.actor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.MorphingProvider;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
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
        pack.reload();

        /* Load user provided models */
        for (String model : pack.getModels())
        {
            ResourceLocation resource = new ResourceLocation("blockbuster.actors", model);

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
            this.models.put("alex", Model.parse(this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/alex.json")));
            this.models.put("steve", Model.parse(this.getClass().getClassLoader().getResourceAsStream("assets/blockbuster/models/entity/steve.json")));
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

    /**
     * When player is logs in, send him all available models and skins
     */
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerLoggedInEvent event)
    {
        PacketModels message = new PacketModels();

        this.pack.reload();

        for (String model : this.pack.getAllSkins().keySet())
        {
            List<String> skins = this.pack.getSkins(model);

            try
            {
                Map<String, ByteBuf> output = new HashMap<String, ByteBuf>();

                for (String skin : skins)
                {
                    InputStream skinStream = new FileInputStream(this.pack.skins.get(model).get(skin));

                    output.put(skin, this.fileToBuffer(skinStream));
                }

                message.skins.put(model, output);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        for (String model : this.pack.getModels())
        {
            try
            {
                InputStream modelStream = new FileInputStream(this.pack.models.get(model));

                if (modelStream != null)
                {
                    message.models.put(model, this.fileToString(modelStream));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Dispatcher.sendTo(message, (EntityPlayerMP) event.player);
    }

    /**
     * Convert file into string
     */
    private String fileToString(InputStream input) throws IOException
    {
        StringBuilder builder = new StringBuilder();
        int letter;

        while ((letter = input.read()) != -1)
        {
            builder.append((char) letter);
        }

        input.close();

        return builder.toString();
    }

    /**
     * Convert file to netty's byte buffer
     */
    private ByteBuf fileToBuffer(InputStream input) throws IOException
    {
        return Unpooled.wrappedBuffer(IOUtils.toByteArray(input));
    }

    /**
     * On player tick, we have to change AABB box (total rip-off of
     * EntityActor#updateSize method)
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START) return;

        EntityPlayer player = event.player;
        IMorphing cap = player.getCapability(MorphingProvider.MORPHING_CAP, null);
        Model data = this.models.get(cap.getModel());

        if (data == null)
        {
            /* Restore default eye height */
            player.eyeHeight = player.getDefaultEyeHeight();
            return;
        }

        String key = player.isElytraFlying() ? "flying" : (player.isSneaking() ? "sneaking" : "standing");

        float[] pose = data.poses.get(key).size;
        float width = pose[0];
        float height = pose[1];

        /* This is a total rip-off of EntityPlayer#setSize method */
        if (width != player.width || height != player.height)
        {
            float f = player.width;
            player.width = width;
            player.height = height;
            player.eyeHeight = height * 0.9F;

            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
            player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + width, axisalignedbb.minY + height, axisalignedbb.minZ + width));

            if (player.width > f && !player.worldObj.isRemote)
            {
                player.moveEntity(f - player.width, 0.0D, f - player.width);
            }
        }
    }
}