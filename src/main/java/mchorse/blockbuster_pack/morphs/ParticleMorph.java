package mchorse.blockbuster_pack.morphs;

import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.utils.Interpolations;
import mchorse.mclib.utils.MatrixUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ParticleMorph extends AbstractMorph
{
    private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");
    private static final int[] EMPTY_ARGS = {};

    /* Common arguments */
    public ParticleMode mode = ParticleMode.VANILLA;
    public int frequency = 2;
    public int duration = -1;
    public int delay = 0;
    public int cap = 2500;

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
    public boolean localRotation = true;
    public int[] arguments = EMPTY_ARGS;

    /* Morph parameters */
    public AbstractMorph morph;
    public MorphParticle.MovementType movementType = MorphParticle.MovementType.OUT;
    public boolean yaw = true;
    public boolean pitch = true;
    public boolean sequencer;
    public boolean random;
    public int fade = 10;
    public int lifeSpan = 50;
    public int maximum = 25;

    /* Runtime fields */
    private Vector3d lastGlobal = new Vector3d();
    private Matrix3f lastRotation = new Matrix3f();
    private int tick;
    private List<MorphParticle> morphParticles = new ArrayList<>();
    private int morphIndex;
    public Random rand = new Random();

    public ParticleMorph()
    {
        super();

        this.name = "particle";
    }

    public AbstractMorph getMorph()
    {
        AbstractMorph morph = this.morph;

        if (this.sequencer && morph instanceof SequencerMorph)
        {
            SequencerMorph seq = ((SequencerMorph) morph);

            morph = this.random ? seq.getRandom() : seq.get(this.morphIndex ++ % seq.morphs.size());
        }

        return MorphUtils.copy(morph.copy());
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getSubclassDisplayName()
    {
        return I18n.format("blockbuster.morph.particle");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE_TEXTURES);

        double factor = System.currentTimeMillis() % 1000 / 500.0 - 1;
        int size = (int) (scale * 1.5F);
        int offset = (int) (Math.floor(Math.abs(factor * factor) * 8) * 8);

        GlStateManager.color(1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawScaledCustomSizeModalRect(x - size / 2, y - size + size / 8, offset, 0, 8, 8, size, size, 128, 128);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void render(EntityLivingBase entityLivingBase, double x, double y, double z, float yaw, float partialTicks)
    {
        if (GuiModelRenderer.isRendering() || MorphUtils.isRenderingOnScreen)
        {
            return;
        }

        if (MatrixUtils.matrix != null)
        {
            Matrix4f parent = new Matrix4f(MatrixUtils.matrix);
            Matrix4f matrix4f = MatrixUtils.readModelView(SnowstormMorph.getMatrix());

            parent.invert();
            parent.mul(matrix4f);

            Vector4f zero = SnowstormMorph.calculateGlobal(parent, entityLivingBase, 0, 0, 0, partialTicks);

            Vector3f ax = new Vector3f(parent.m00, parent.m01, parent.m02);
            Vector3f ay = new Vector3f(parent.m10, parent.m11, parent.m12);
            Vector3f az = new Vector3f(parent.m20, parent.m21, parent.m22);

            ax.normalize();
            ay.normalize();
            az.normalize();

            this.lastRotation.setRow(0, ax);
            this.lastRotation.setRow(1, ay);
            this.lastRotation.setRow(2, az);

            this.lastGlobal.x = zero.x;
            this.lastGlobal.y = zero.y;
            this.lastGlobal.z = zero.z;
        }
        else
        {
            this.lastRotation.setIdentity();

            this.lastGlobal.x = Interpolations.lerp(entityLivingBase.prevPosX, entityLivingBase.posX, partialTicks);
            this.lastGlobal.y = Interpolations.lerp(entityLivingBase.prevPosY, entityLivingBase.posY, partialTicks);
            this.lastGlobal.z = Interpolations.lerp(entityLivingBase.prevPosZ, entityLivingBase.posZ, partialTicks);
        }

        if (!this.morphParticles.isEmpty())
        {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);

            for (MorphParticle particle : this.morphParticles)
            {
                particle.render(entityLivingBase, partialTicks);
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    public void update(EntityLivingBase target)
    {
        super.update(target);

        boolean alive = this.duration < 0 || this.tick < this.duration;

        if (this.frequency != 0 && this.tick >= this.delay && this.tick % this.frequency == 0 && alive)
        {
            final int max = this.cap;
            int particlesPerSecond = (int) (20 / (float) this.frequency * this.count);

            boolean vanillaCap = this.mode == ParticleMode.VANILLA && particlesPerSecond <= max;
            boolean morphCap = this.mode == ParticleMode.MORPH && this.maximum <= max;

            if ((vanillaCap || morphCap) && target.world.isRemote)
            {
                if (this.mode == ParticleMode.VANILLA && this.vanillaType != null)
                {
                    double x = this.lastGlobal.x + this.vanillaX;
                    double y = this.lastGlobal.y + this.vanillaY;
                    double z = this.lastGlobal.z + this.vanillaZ;

                    Vector3f vector = new Vector3f(0, 0, 0);

                    for (int i = 0; i < this.count; i ++)
                    {
                        double dx = this.rand.nextGaussian() * this.vanillaDX;
                        double dy = this.rand.nextGaussian() * this.vanillaDY;
                        double dz = this.rand.nextGaussian() * this.vanillaDZ;
                        double sx = this.rand.nextGaussian() * this.speed;
                        double sy = this.rand.nextGaussian() * this.speed;
                        double sz = this.rand.nextGaussian() * this.speed;

                        if (this.localRotation)
                        {
                            vector.set((float) dx, (float) dy, (float) dz);
                            this.lastRotation.transform(vector);

                            dx = vector.x;
                            dy = vector.y;
                            dz = vector.z;
                        }

                        try
                        {
                            target.world.spawnParticle(this.vanillaType, true, x + dx, y + dy, z + dz, sx, sy, sz, this.arguments);
                        }
                        catch (Throwable e)
                        {}
                    }
                }
                else if (this.mode == ParticleMode.MORPH && this.morph != null && this.morphParticles.size() < this.maximum)
                {
                    for (int i = 0; i < this.count && this.morphParticles.size() < this.maximum; i ++)
                    {
                        this.morphParticles.add(new MorphParticle(this));
                    }
                }
            }
        }

        /* Update morph based particles */
        if (target.world.isRemote)
        {
            Iterator<MorphParticle> it = this.morphParticles.iterator();

            while (it.hasNext())
            {
                MorphParticle particle = it.next();

                particle.update(target);

                if (particle.isDead())
                {
                    it.remove();
                }
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

            /* Common properties */
            result = result && this.mode == particle.mode;
            result = result && this.frequency == particle.frequency;
            result = result && this.duration == particle.duration;
            result = result && this.delay == particle.delay;
            result = result && this.cap == particle.cap;

            /* Vanilla properties */
            result = result && this.vanillaType == particle.vanillaType;
            result = result && this.vanillaX == particle.vanillaX;
            result = result && this.vanillaY == particle.vanillaY;
            result = result && this.vanillaZ == particle.vanillaZ;
            result = result && this.vanillaDX == particle.vanillaDX;
            result = result && this.vanillaDY == particle.vanillaDY;
            result = result && this.vanillaDZ == particle.vanillaDZ;
            result = result && this.speed == particle.speed;
            result = result && this.count == particle.count;
            result = result && this.localRotation == particle.localRotation;

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

            result = result && Objects.equals(this.morph, particle.morph);
            result = result && this.movementType == particle.movementType;
            result = result && this.yaw == particle.yaw;
            result = result && this.pitch == particle.pitch;
            result = result && this.sequencer == particle.sequencer;
            result = result && this.random == particle.random;
            result = result && this.fade == particle.fade;
            result = result && this.lifeSpan == particle.lifeSpan;
            result = result && this.maximum == particle.maximum;
        }

        return result;
    }

    @Override
    public boolean canMerge(AbstractMorph morph)
    {
        if (morph instanceof ParticleMorph)
        {
            this.copy(morph);
            this.tick = this.morphIndex = 0;

            return true;
        }

        return super.canMerge(morph);
    }

    @Override
    public boolean useTargetDefault()
    {
        return true;
    }

    @Override
    public AbstractMorph create()
    {
        return new ParticleMorph();
    }

    @Override
    public void copy(AbstractMorph from)
    {
        super.copy(from);

        if (from instanceof ParticleMorph)
        {
            ParticleMorph morph = (ParticleMorph) from;

            this.mode = morph.mode;
            this.frequency = morph.frequency;
            this.duration = morph.duration;
            this.delay = morph.delay;
            this.cap = morph.cap;

            this.vanillaType = morph.vanillaType;
            this.vanillaX = morph.vanillaX;
            this.vanillaY = morph.vanillaY;
            this.vanillaZ = morph.vanillaZ;
            this.vanillaDX = morph.vanillaDX;
            this.vanillaDY = morph.vanillaDY;
            this.vanillaDZ = morph.vanillaDZ;
            this.speed = morph.speed;
            this.count = morph.count;
            this.localRotation = morph.localRotation;
            this.arguments = morph.arguments;

            this.morph = MorphUtils.copy(morph.morph);
            this.movementType = morph.movementType;
            this.yaw = morph.yaw;
            this.pitch = morph.pitch;
            this.sequencer = morph.sequencer;
            this.random = morph.random;
            this.fade = morph.fade;
            this.lifeSpan = morph.lifeSpan;
            this.maximum = morph.maximum;
        }
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
        if (tag.hasKey("Duration")) this.duration = tag.getInteger("Duration");
        if (tag.hasKey("Delay")) this.delay = tag.getInteger("Delay");
        if (tag.hasKey("Cap")) this.cap = tag.getInteger("Cap");

        if (tag.hasKey("Type")) this.vanillaType = EnumParticleTypes.getByName(tag.getString("Type"));
        if (tag.hasKey("X")) this.vanillaX = tag.getDouble("X");
        if (tag.hasKey("Y")) this.vanillaY = tag.getDouble("Y");
        if (tag.hasKey("Z")) this.vanillaZ = tag.getDouble("Z");
        if (tag.hasKey("DX")) this.vanillaDX = tag.getDouble("DX");
        if (tag.hasKey("DY")) this.vanillaDY = tag.getDouble("DY");
        if (tag.hasKey("DZ")) this.vanillaDZ = tag.getDouble("DZ");
        if (tag.hasKey("Speed")) this.speed = tag.getDouble("Speed");
        if (tag.hasKey("Count")) this.count = tag.getInteger("Count");
        if (tag.hasKey("LocalRotation")) this.localRotation = tag.getBoolean("LocalRotation");
        if (tag.hasKey("Args")) this.arguments = tag.getIntArray("Args");

        if (tag.hasKey("Morph")) this.morph = MorphManager.INSTANCE.morphFromNBT(tag.getCompoundTag("Morph"));
        if (tag.hasKey("Movement")) this.movementType = MorphParticle.MovementType.getType(tag.getString("Movement"));
        if (tag.hasKey("Yaw")) this.yaw = tag.getBoolean("Yaw");
        if (tag.hasKey("Pitch")) this.pitch = tag.getBoolean("Pitch");
        if (tag.hasKey("Sequencer")) this.sequencer = tag.getBoolean("Sequencer");
        if (tag.hasKey("Random")) this.random = tag.getBoolean("Random");
        if (tag.hasKey("Fade")) this.fade = tag.getInteger("Fade");
        if (tag.hasKey("Life")) this.lifeSpan = tag.getInteger("Life");
        if (tag.hasKey("Max")) this.maximum = tag.getInteger("Max");
    }

    @Override
    public void toNBT(NBTTagCompound tag)
    {
        super.toNBT(tag);

        if (this.mode != ParticleMode.VANILLA) tag.setString("Mode", this.mode.type);
        if (this.frequency != 2) tag.setInteger("Frequency", this.frequency);
        if (this.duration != -1) tag.setInteger("Duration", this.duration);
        if (this.delay != 0) tag.setInteger("Delay", this.delay);
        if (this.cap != 2500) tag.setInteger("Cap", this.cap);

        if (this.vanillaType != EnumParticleTypes.EXPLOSION_NORMAL) tag.setString("Type", this.vanillaType.getParticleName());
        if (this.vanillaX != 0) tag.setDouble("X", this.vanillaX);
        if (this.vanillaY != 0) tag.setDouble("Y", this.vanillaY);
        if (this.vanillaZ != 0) tag.setDouble("Z", this.vanillaZ);
        if (this.vanillaDX != 0.1) tag.setDouble("DX", this.vanillaDX);
        if (this.vanillaDY != 0.1) tag.setDouble("DY", this.vanillaDY);
        if (this.vanillaDZ != 0.1) tag.setDouble("DZ", this.vanillaDZ);
        if (this.speed != 0.1) tag.setDouble("Speed", this.speed);
        if (this.count != 10) tag.setInteger("Count", this.count);
        if (!this.localRotation) tag.setBoolean("LocalRotation", this.localRotation);
        if (this.arguments.length != 0) tag.setIntArray("Args", this.arguments);

        if (this.morph != null)
        {
            NBTTagCompound morph = new NBTTagCompound();

            this.morph.toNBT(morph);
            tag.setTag("Morph", morph);
        }

        if (this.movementType != MorphParticle.MovementType.OUT) tag.setString("Movement", this.movementType.id);
        if (!this.yaw) tag.setBoolean("Yaw", this.yaw);
        if (!this.pitch) tag.setBoolean("Pitch", this.pitch);
        if (this.sequencer) tag.setBoolean("Sequencer", this.sequencer);
        if (this.random) tag.setBoolean("Random", this.random);
        if (this.fade != 10) tag.setInteger("Fade", this.fade);
        if (this.lifeSpan != 50) tag.setInteger("Life", this.lifeSpan);
        if (this.maximum != 25) tag.setInteger("Max", this.maximum);
    }

    public static class MorphParticle
    {
        public ParticleMorph parent;
        public AbstractMorph morph;
        public MovementType movementType;

        public float targetX;
        public float targetY;
        public float targetZ;

        public float x;
        public float y;
        public float z;
        public float prevX;
        public float prevY;
        public float prevZ;

        public float yaw;
        public float pitch;
        public float prevYaw;
        public float prevPitch;

        public int timer;

        public MorphParticle(ParticleMorph morph)
        {
            this.parent = morph;
            this.morph = morph.getMorph();
            this.movementType = morph.movementType;
            this.movementType.calculateInitial(this);

            /* Stupid workaround to fix initial rotation */
            this.timer = 1;
            this.movementType.calculate(this);
            this.calculateRotation();
            this.movementType.calculateInitial(this);

            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }

        public void update(EntityLivingBase entity)
        {
            this.timer ++;
            this.prevX = this.x;
            this.prevY = this.y;
            this.prevZ = this.z;
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;

            this.movementType.calculate(this);
            this.calculateRotation();

            this.morph.update(entity);
        }

        private void calculateRotation()
        {
            double dX = this.x - this.prevX;
            double dY = this.y - this.prevY;
            double dZ = this.z - this.prevZ;

            double horizontalDistance = (double) MathHelper.sqrt(dX * dX + dZ * dZ);
            this.yaw = (float) (180 - MathHelper.atan2(dZ, dX) * 180 / Math.PI) + 90;
            this.pitch = (float) ((MathHelper.atan2(dY, horizontalDistance) * 180 / Math.PI));
        }

        public void render(EntityLivingBase entity, float partialTicks)
        {
            GL11.glPushMatrix();

            double x = Interpolations.lerp(this.prevX, this.x, partialTicks) + this.parent.vanillaX;
            double y = Interpolations.lerp(this.prevY, this.y, partialTicks) + this.parent.vanillaY;
            double z = Interpolations.lerp(this.prevZ, this.z, partialTicks) + this.parent.vanillaZ;
            double scale = Interpolations.envelope(this.timer + partialTicks, this.parent.lifeSpan, this.parent.fade);

            GL11.glTranslated(x, y, z);
            GL11.glScaled(scale, scale, scale);

            if (this.parent.yaw) GlStateManager.rotate(Interpolations.lerp(this.prevYaw, this.yaw, partialTicks), 0.0F, 1.0F, 0.0F);
            if (this.parent.pitch) GlStateManager.rotate(Interpolations.lerp(this.prevPitch, this.pitch, partialTicks), 1.0F, 0.0F, 0.0F);

            if (this.parent.yaw || this.parent.pitch)
            {
                float yaw = entity.rotationYaw;
                float pitch = entity.rotationPitch;
                float yawHead = entity.rotationYawHead;
                float yawBody = entity.renderYawOffset;
                float prevYaw = entity.prevRotationYaw;
                float prevPitch = entity.prevRotationPitch;
                float prevYawHead = entity.prevRotationYawHead;
                float prevYawBody = entity.prevRenderYawOffset;

                entity.rotationYaw = entity.prevRotationYaw = 0;
                entity.rotationYawHead = entity.prevRotationYawHead = 0;
                entity.rotationPitch = entity.prevRotationPitch = 0;
                entity.renderYawOffset = entity.prevRenderYawOffset = 0;

                this.morph.render(entity, 0, 0, 0, 0, partialTicks);

                entity.rotationYaw = yaw;
                entity.rotationPitch = pitch;
                entity.rotationYawHead = yawHead;
                entity.renderYawOffset = yawBody;
                entity.prevRotationYaw = prevYaw;
                entity.prevRotationPitch = prevPitch;
                entity.prevRotationYawHead = prevYawHead;
                entity.prevRenderYawOffset = prevYawBody;
            }
            else
            {
                this.morph.render(entity, 0, 0, 0, 0, partialTicks);
            }

            GL11.glPopMatrix();
        }

        public boolean isDead()
        {
            return this.timer >= this.parent.lifeSpan;
        }

        public float getFactor()
        {
            return this.parent.lifeSpan == 0 ? 1 : this.timer / (float) this.parent.lifeSpan;
        }

        public static enum MovementType
        {
            OUT("out")
            {
                @Override
                public void calculateInitial(MorphParticle particle)
                {
                    particle.targetX = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDX;
                    particle.targetY = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDY;
                    particle.targetZ = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDZ;

                    particle.x = particle.prevX = 0;
                    particle.y = particle.prevY = 0;
                    particle.z = particle.prevZ = 0;
                }

                @Override
                public void calculate(MorphParticle particle)
                {
                    float factor = particle.getFactor();

                    particle.x = Interpolations.lerp(0, particle.targetX, factor);
                    particle.y = Interpolations.lerp(0, particle.targetY, factor);
                    particle.z = Interpolations.lerp(0, particle.targetZ, factor);
                }
            },
            IN("in")
            {
                @Override
                public void calculateInitial(MorphParticle particle)
                {
                    particle.targetX = particle.x = particle.prevX = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDX;
                    particle.targetY = particle.y = particle.prevY = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDY;
                    particle.targetZ = particle.z = particle.prevZ = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDZ;
                }

                @Override
                public void calculate(MorphParticle particle)
                {
                    float factor = particle.getFactor();

                    particle.x = Interpolations.lerp(particle.targetX, 0, factor);
                    particle.y = Interpolations.lerp(particle.targetY, 0, factor);
                    particle.z = Interpolations.lerp(particle.targetZ, 0, factor);
                }
            },
            DROP("drop")
            {
                @Override
                public void calculateInitial(MorphParticle particle)
                {
                    particle.x = particle.prevX = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDX;
                    particle.y = particle.prevY = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDY;
                    particle.z = particle.prevZ = (particle.parent.rand.nextFloat() * 2 - 1) * (float) particle.parent.vanillaDZ;
                }

                @Override
                public void calculate(MorphParticle particle)
                {
                    if (particle.targetY < 5)
                    {
                        particle.targetY += 0.02F;
                    }

                    particle.y -= particle.targetY;
                }
            };

            public final String id;

            public static MovementType getType(String id)
            {
                for (MovementType type : values())
                {
                    if (type.id.equals(id)) return type;
                }

                return OUT;
            }

            private MovementType(String type)
            {
                this.id = type;
            }

            public abstract void calculateInitial(MorphParticle particle);

            public abstract void calculate(MorphParticle particle);
        }
    }

    public static enum ParticleMode
    {
        VANILLA("vanilla"), MORPH("morph");

        public final String type;

        private ParticleMode(String type)
        {
            this.type = type;
        }
    }
}