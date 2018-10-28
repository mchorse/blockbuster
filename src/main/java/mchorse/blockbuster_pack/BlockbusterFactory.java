package mchorse.blockbuster_pack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.api.ModelHandler;
import mchorse.blockbuster.api.ModelHandler.ModelCell;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster_pack.client.gui.GuiCustomMorph;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphList;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.elements.GuiAbstractMorph;
import net.minecraft.client.Minecraft;
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
 * folder) or added by API (steve, alex and fred).
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
        GuiAbstractMorph.EDITORS.add(new GuiCustomMorph(Minecraft.getMinecraft()));
    }

    @SideOnly(Side.CLIENT)
    public void updateRenderers()
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
        name = name.substring(name.indexOf(".") + 1);
        CustomMorph morph = this.morphs.get(name);

        if (morph != null)
        {
            morph = (CustomMorph) morph.clone(Blockbuster.proxy.isClient());
            morph.fromNBT(tag);
        }
        else
        {
            morph = new CustomMorph();
            morph.fromNBT(tag);
        }

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
            if (original.model.defaultTexture != null || original.model.providesMtl)
            {
                CustomMorph actor = (CustomMorph) original.clone(world.isRemote);

                morphs.addMorphVariant(actor.name, "blockbuster", "", actor);
            }

            /* Morphs with skins */
            List<String> skins = new ArrayList<String>();

            for (String str : this.models.pack.getSkins(original.model.skins))
            {
                skins.add(original.model.skins + "/" + str);
            }

            for (String str : this.models.pack.getSkins(key))
            {
                skins.add(key + "/" + str);
            }

            for (String skin : skins)
            {
                CustomMorph actor = (CustomMorph) original.clone(world.isRemote);

                actor.skin = new ResourceLocation("blockbuster.actors", skin);
                morphs.addMorphVariant(actor.name, "blockbuster", skin, actor);
            }
        }
    }

    @Override
    public boolean hasMorph(String morph)
    {
        return morph.startsWith("blockbuster.");
    }
}