package mchorse.blockbuster_pack.client.render.part;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.api.ModelPose;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MorphBodyPart implements IBodyPart
{
    public AbstractMorph morph;
    public float[] translate = new float[3];
    public float[] scale = new float[] {1, 1, 1};
    public float[] rotate = new float[3];

    @SideOnly(Side.CLIENT)
    private EntityLivingBase entity;

    @Override
    @SideOnly(Side.CLIENT)
    public void init()
    {
        this.entity = new EntityActor(Minecraft.getMinecraft().world);
        this.entity.rotationYaw = this.entity.prevRotationYaw;
        this.entity.rotationYawHead = this.entity.prevRotationYawHead;
        this.entity.onGround = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(float partialTicks)
    {
        if (this.morph == null || this.entity == null)
        {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(this.translate[0], this.translate[1], this.translate[2]);

        GL11.glRotatef(this.rotate[2], 0, 0, 1);
        GL11.glRotatef(this.rotate[1], 0, 1, 0);
        GL11.glRotatef(this.rotate[0], 1, 0, 0);

        GL11.glScalef(this.scale[0], this.scale[1], this.scale[2]);

        this.morph.render(this.entity, 0, 0, 0, 0, partialTicks);
        GL11.glPopMatrix();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void update()
    {
        if (this.entity != null)
        {
            this.entity.ticksExisted++;

            if (this.morph != null)
            {
                this.morph.update(this.entity, null);
            }
        }
    }

    public MorphBodyPart clone(boolean isRemote)
    {
        MorphBodyPart part = new MorphBodyPart();

        part.morph = this.morph.clone(isRemote);
        part.translate[0] = this.translate[0];
        part.translate[1] = this.translate[1];
        part.translate[2] = this.translate[2];
        part.scale[0] = this.scale[0];
        part.scale[1] = this.scale[1];
        part.scale[2] = this.scale[2];
        part.rotate[0] = this.rotate[0];
        part.rotate[1] = this.rotate[1];
        part.rotate[2] = this.rotate[2];

        return part;
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        if (tag.hasKey("Morph", 10))
        {
            this.morph = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Morph"));
        }

        ModelPose.readFloatList(tag.getTagList("T", 5), this.translate);
        ModelPose.readFloatList(tag.getTagList("S", 5), this.scale);
        ModelPose.readFloatList(tag.getTagList("R", 5), this.rotate);
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        if (this.morph != null)
        {
            NBTTagCompound morph = new NBTTagCompound();

            this.morph.toNBT(morph);
            tag.setTag("Morph", morph);

            tag.setTag("T", ModelPose.writeFloatList(new NBTTagList(), this.translate));
            tag.setTag("S", ModelPose.writeFloatList(new NBTTagList(), this.scale));
            tag.setTag("R", ModelPose.writeFloatList(new NBTTagList(), this.rotate));
        }
    }
}