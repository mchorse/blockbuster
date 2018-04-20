package mchorse.blockbuster_pack;

import java.util.HashMap;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelHandler.ModelCell;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Blockbuster morph factory
 *
 * This factory is responsible for adding all custom modeled morphs provided by
 * a user (in his config folder), the server (in world save's blockbuster
 * folder) or added by API (steve and alex).
 */
public class BlockbusterFactory implements IMorphFactory
{
    public Map<String, CustomMorph> morphs = new HashMap<String, CustomMorph>();
    public ModelHandler models;

    @Override
    public void register(MorphManager manager)
    {
        /* Blacklist actors */
        this.registerModels();
    }

    public void registerModels()
    {
        this.morphs.clear();
        this.morphs.put("yike", this.createMorph("yike"));
        this.morphs.put("alex", this.createMorph("alex"));
        this.morphs.put("steve", this.createMorph("steve"));
        this.morphs.put("fred", this.createMorph("fred"));

        for (String model : this.models.pack.getModels())
        {
            this.morphs.put(model, this.createMorph(model));
        }
    }

    private CustomMorph createMorph(String name)
    {
        CustomMorph morph = new CustomMorph();
        ModelCell entry = this.models.models.get(name);

        morph.name = "blockbuster." + name;
        morph.model = entry == null ? null : entry.model;

        return morph;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient(MorphManager manager)
    {
        for (CustomMorph morph : this.morphs.values())
        {
            morph.renderer = ClientProxy.actorRenderer;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String displayNameForMorph(AbstractMorph morph)
    {
        String[] splits = morph.name.split("\\.");

        if (splits.length >= 2 && splits[0].equals("blockbuster") && this.morphs.containsKey(splits[1]))
        {
            String name = this.morphs.get(splits[1]).model.name;

            return name.isEmpty() ? splits[1] : name;
        }

        return null;
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound tag)
    {
        String name = tag.getString("Name");
        CustomMorph morph = (CustomMorph) this.morphs.get(name.substring(name.indexOf(".") + 1)).clone(Blockbuster.proxy.isClient());

        morph.fromNBT(tag);

        return morph;
    }

    @Override
    public void getMorphs(MorphList morphs, World world)
    {
        for (Map.Entry<String, CustomMorph> morph : this.morphs.entrySet())
        {
            String key = morph.getKey();
            CustomMorph original = morph.getValue();

            if (key.equals("yike"))
            {
                continue;
            }

            /* Morphs with default texture */
            if (original.model.defaultTexture != null)
            {
                CustomMorph actor = (CustomMorph) original.clone(world.isRemote);

                morphs.addMorphVariant(actor.name, "blockbuster", "", actor);

                for (Map.Entry<String, Model.Pose> entry : actor.model.poses.entrySet())
                {
                    String pose = entry.getKey();

                    if (Model.REQUIRED_POSES.contains(pose) || pose.equals("riding"))
                    {
                        continue;
                    }

                    CustomMorph poseActor = (CustomMorph) actor.clone(world.isRemote);

                    poseActor.currentPose = pose;
                    poseActor.setPose(entry.getValue());
                    morphs.addMorphVariant(actor.name, "blockbuster", "pose " + pose, poseActor);
                }
            }

            /* Morphs with skins */
            for (String skin : this.models.pack.getSkins(key))
            {
                CustomMorph actor = (CustomMorph) original.clone(world.isRemote);
                String path = actor.name.substring(actor.name.indexOf(".") + 1) + "/" + skin;

                actor.skin = new ResourceLocation("blockbuster.actors", path);
                morphs.addMorphVariant(actor.name, "blockbuster", skin, actor);

                for (Map.Entry<String, Model.Pose> entry : actor.model.poses.entrySet())
                {
                    String pose = entry.getKey();

                    if (Model.REQUIRED_POSES.contains(pose) || pose.equals("riding"))
                    {
                        continue;
                    }

                    CustomMorph poseActor = (CustomMorph) actor.clone(world.isRemote);

                    poseActor.currentPose = pose;
                    poseActor.setPose(entry.getValue());
                    morphs.addMorphVariant(actor.name + "." + pose, "blockbuster", skin, poseActor);
                }
            }
        }
    }

    @Override
    public boolean hasMorph(String morph)
    {
        return this.morphs.containsKey(morph.substring(morph.indexOf(".") + 1));
    }
}