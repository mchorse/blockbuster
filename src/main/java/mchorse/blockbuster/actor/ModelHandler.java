package mchorse.blockbuster.actor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mchorse.blockbuster.capabilities.morphing.IMorphing;
import mchorse.blockbuster.capabilities.morphing.MorphingProvider;
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
    public ActorsPack pack;

    public ModelHandler(ActorsPack pack)
    {
        this.pack = pack;
        this.pack.reload();
    }

    /**
     * Load user and default provided models into model map
     */
    public void loadModels(ActorsPack pack)
    {
        pack.reload();

        /* Load user provided models */
        for (String model : pack.getModels())
        {
            ResourceLocation resource = new ResourceLocation("blockbuster.actors", model);

            try
            {
                this.models.put(model, Model.parse(pack.getInputStream(resource)));
            }
            catch (Exception e)
            {
                System.out.println("Model by key \"" + model + "\" couldn't be parsed and loaded!");
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
            System.out.println("Default provided models couldn't be loaded!");
            e.printStackTrace();
        }
    }

    /**
     * On player tick, we have to change AABB box
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START) return;

        EntityPlayer player = event.player;
        IMorphing cap = player.getCapability(MorphingProvider.MORPHING_CAP, null);
        Model data = this.models.get(cap.getModel());

        if (data == null) return;

        float width = 0.0F;
        float height = 0.0F;

        if (player.isElytraFlying())
        {
            float[] pose = data.poses.get("flying").size;

            width = pose[0];
            height = pose[1];
        }
        else if (player.isSneaking())
        {
            float[] pose = data.poses.get("sneaking").size;

            width = pose[0];
            height = pose[1];
        }
        else
        {
            float[] pose = data.poses.get("standing").size;

            width = pose[0];
            height = pose[1];
        }

        /* This is a total rip-off of EntityPlayer#setSize method */
        if (width != player.width || height != player.height)
        {
            float f = player.width;
            player.width = width;
            player.height = height;
            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
            player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + width, axisalignedbb.minY + height, axisalignedbb.minZ + width));
            player.eyeHeight = height * 0.9F;

            if (player.width > f && !player.worldObj.isRemote)
            {
                player.moveEntity(f - player.width, 0.0D, f - player.width);
            }
        }
    }

    /**
     * When player is logs in, send him available models and skins
     */
    @SubscribeEvent
    public void onPlayerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        PacketModels message = new PacketModels();

        for (String model : this.pack.getModels())
        {
            try
            {
                Map<String, ByteBuf> skinMap = new HashMap<String, ByteBuf>();
                InputStream modelStream = this.pack.getInputStream(new ResourceLocation(model));

                for (String skin : this.pack.getSkins(model))
                {
                    skinMap.put(skin, this.fileToBuffer(this.pack.getInputStream(new ResourceLocation(model + "/" + skin))));
                }

                message.skins.put(model, skinMap);

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

        Dispatcher.sendTo(message, player);
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
}