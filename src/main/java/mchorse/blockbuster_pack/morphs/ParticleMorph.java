package mchorse.blockbuster_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

public class ParticleMorph extends AbstractMorph
{
    private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");
    private static final int[] EMPTY_ARGS = new int[]{};

    /* Common arguments */
    public ParticleMode mode = ParticleMode.VANILLA;
    public int frequency = 2;

    /* Vanilla parameters */
    public EnumParticleTypes vanillaType = EnumParticleTypes.EXPLOSION_NORMAL;
    public double vanillaX;
    public double vanillaY;
    public double vanillaZ;
    public double vanillaDX = 0.1;
    public double vanillaDY = 0.1;
    public double vanillaDZ = 0.1;
    public double speed = 0.1;
    public int count = 10;
    public int[] arguments = EMPTY_ARGS;

    /* Morph parameters */
    public AbstractMorph morph;
    public int maximum;

    /* Runtime fields */
    private int tick;
    private List<MorphParticle> morphParticles = new ArrayList<>();

    public ParticleMorph()
    {
        super();

        this.name = "particle";
    }

    @Override
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE_TEXTURES);

        double factor = System.currentTimeMillis() % 1000 / 500.0 - 1;
        int size = (int) (scale * 1.5F);
        int offset = (int) (Math.floor(Math.abs(factor * factor) * 8) * 8);

        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawScaledCustomSizeModalRect(x - size / 2, y - size / 2 - size / 4, offset, 0, 8, 8, size, size, 128, 128);
    }

    @Override
    public void render(EntityLivingBase entityLivingBase, double x, double y, double z, float yaw, float partialTicks)
    {
        if (this.mode == ParticleMode.MORPH && !this.morphParticles.isEmpty())
        {
            /* TODO: Render morphs */
        }
    }

    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        super.update(target, cap);

        if (this.tick % this.frequency == 0)
        {
            if (!target.worldObj.isRemote && this.mode == ParticleMode.VANILLA && this.vanillaType != null)
            {
                double x = target.posX + this.vanillaX;
                double y = target.posY + this.vanillaY;
                double z = target.posZ + this.vanillaZ;

                ((WorldServer) target.worldObj).spawnParticle(this.vanillaType, true, x, y, z, this.count, this.vanillaDX, this.vanillaDY, this.vanillaDZ, this.speed, this.arguments);
            }
            else if (target.worldObj.isRemote && this.mode == ParticleMode.MORPH && !this.morphParticles.isEmpty())
            {
                /* TODO: Update morphs */
            }
        }

        this.tick ++;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = super.equals(obj);

        if (obj instanceof ParticleMorph)
        {
            ParticleMorph particle = (ParticleMorph) obj;

            result = result && this.mode == particle.mode;
            result = result && this.frequency == particle.frequency;

            result = result && this.vanillaType == particle.vanillaType;
            result = result && this.vanillaX == particle.vanillaX;
            result = result && this.vanillaY == particle.vanillaY;
            result = result && this.vanillaZ == particle.vanillaZ;
            result = result && this.vanillaDX == particle.vanillaDX;
            result = result && this.vanillaDY == particle.vanillaDY;
            result = result && this.vanillaDZ == particle.vanillaDZ;
            result = result && this.speed == particle.speed;
            result = result && this.count == particle.count;

            boolean sameArgs = false;

            if (this.arguments.length == particle.arguments.length)
            {
                int same = 0;

                for (int i = 0; i < this.arguments.length; i++)
                {
                    if (this.arguments[i] == particle.arguments[i])
                    {
                        same ++;
                    }
                }

                sameArgs = same == this.arguments.length;
            }

            result = result && sameArgs;
        }

        return result;
    }

    @Override
    public AbstractMorph clone(boolean b)
    {
        ParticleMorph morph = new ParticleMorph();

        morph.mode = this.mode;
        morph.frequency = this.frequency;

        morph.vanillaType = this.vanillaType;
        morph.vanillaX = this.vanillaX;
        morph.vanillaY = this.vanillaY;
        morph.vanillaZ = this.vanillaZ;
        morph.vanillaDX = this.vanillaDX;
        morph.vanillaDY = this.vanillaDY;
        morph.vanillaDZ = this.vanillaDZ;
        morph.speed = this.speed;
        morph.count = this.count;
        morph.arguments = this.arguments;

        return morph;
    }

    @Override
    public float getWidth(EntityLivingBase entityLivingBase)
    {
        return 0.6F;
    }

    @Override
    public float getHeight(EntityLivingBase entityLivingBase)
    {
        return 1.8F;
    }

    @Override
    public void reset()
    {
        super.reset();
    }

    @Override
    public void fromNBT(NBTTagCompound tag)
    {
        super.fromNBT(tag);

        if (tag.hasKey("Mode")) this.mode = tag.getString("Mode").equals(ParticleMode.MORPH.type) ? ParticleMode.MORPH : ParticleMode.VANILLA;
        if (tag.hasKey("Frequency")) this.frequency = tag.getInteger("Frequency");

        if (tag.hasKey("Type")) this.vanillaType = EnumParticleTypes.getByName(tag.getString("Type"));
        if (tag.hasKey("X")) this.vanillaX = tag.getDouble("X");
        if (tag.hasKey("Y")) this.vanillaY = tag.getDouble("Y");
        if (tag.hasKey("Z")) this.vanillaZ = tag.getDouble("Z");
        if (tag.hasKey("DX")) this.vanillaDX = tag.getDouble("DX");
        if (tag.hasKey("DY")) this.vanillaDY = tag.getDouble("DY");
        if (tag.hasKey("DZ")) this.vanillaDZ = tag.getDouble("DZ");
        if (tag.hasKey("Speed")) this.speed = tag.getDouble("Speed");
        if (tag.hasKey("Count")) this.count = tag.getInteger("Count");
        if (tag.hasKey("Args")) this.arguments = tag.getIntArray("Args");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        tag.setString("Mode", this.mode.type);
        tag.setInteger("Frequency", this.frequency);

        tag.setString("Type", this.vanillaType.getParticleName());
        tag.setDouble("X", this.vanillaX);
        tag.setDouble("Y", this.vanillaY);
        tag.setDouble("Z", this.vanillaZ);
        tag.setDouble("DX", this.vanillaDX);
        tag.setDouble("DY", this.vanillaDY);
        tag.setDouble("DZ", this.vanillaDZ);
        tag.setDouble("Speed", this.speed);
        tag.setInteger("Count", this.count);
        tag.setIntArray("Args", this.arguments);
    }

    public static class MorphParticle
    {}

    public static enum ParticleMode
    {
        VANILLA("vanilla"), MORPH("morph");

        public final String type;

        ParticleMode(String type)
        {
            this.type = type;
        }
    }
}