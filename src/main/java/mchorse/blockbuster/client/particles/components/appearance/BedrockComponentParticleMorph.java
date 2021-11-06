package mchorse.blockbuster.client.particles.components.appearance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.blockbuster.client.particles.components.BedrockComponentBase;
import mchorse.blockbuster.client.particles.components.IComponentParticleInitialize;
import mchorse.blockbuster.client.particles.components.IComponentParticleMorphRender;
import mchorse.blockbuster.client.particles.emitter.BedrockEmitter;
import mchorse.blockbuster.client.particles.emitter.BedrockParticle;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.math.molang.MolangException;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.utils.Interpolations;
import mchorse.metamorph.api.Morph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class BedrockComponentParticleMorph extends BedrockComponentBase implements IComponentParticleMorphRender, IComponentParticleInitialize
{
    public boolean enabled;
    public boolean renderTexture;
    public Morph morph = new Morph();

    @Override
    public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
    {
        if (!elem.isJsonObject()) return super.fromJson(elem, parser);

        JsonObject element = elem.getAsJsonObject();

        if (element.has("enabled")) this.enabled = element.get("enabled").getAsBoolean();
        if (element.has("render_texture")) this.renderTexture = element.get("render_texture").getAsBoolean();

        if (element.has("nbt"))
        {
            try
            {
                this.morph.setDirect(MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson(element.get("nbt").getAsString())));
            }
            catch(NBTException e) { }
        }

        return super.fromJson(element, parser);
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject object = new JsonObject();

        object.addProperty("enabled", this.enabled);
        object.addProperty("render_texture", this.renderTexture);
        if (!this.morph.isEmpty()) object.addProperty("nbt", this.morph.toNBT().toString());

        return object;
    }

    @Override
    public void apply(BedrockEmitter emitter, BedrockParticle particle)
    {
        if (this.enabled && !this.morph.isEmpty())
        {
            particle.morph.set(MorphUtils.copy(this.morph.get()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void render(BedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks)
    {
        Entity camera = Minecraft.getMinecraft().getRenderViewEntity();

        if (camera == null || this.morph.isEmpty() || !this.enabled)
        {
            return;
        }

        EntityLivingBase dummy = particle.getDummy(emitter);

        double x = Interpolations.lerp(particle.prevPosition.x, particle.position.x, partialTicks);
        double y = Interpolations.lerp(particle.prevPosition.y, particle.position.y, partialTicks);
        double z = Interpolations.lerp(particle.prevPosition.z, particle.position.z, partialTicks);

        Vector3d position = this.calculatePosition(emitter, particle, x, y, z);
        x = position.x;
        y = position.y;
        z = position.z;

        if (!GuiModelRenderer.isRendering())
        {
            x -= Interpolations.lerp(camera.prevPosX, camera.posX, partialTicks);
            y -= Interpolations.lerp(camera.prevPosY, camera.posY, partialTicks);
            z -= Interpolations.lerp(camera.prevPosZ, camera.posZ, partialTicks);
        }

        int combinedBrightness  = dummy.getBrightnessForRender();
        int brightnessX = combinedBrightness % 65536;
        int brightnessY = combinedBrightness / 65536;

        GlStateManager.color(1, 1, 1, 1);
        RenderHelper.enableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        if (particle.relativeScaleBillboard)
        {
            GlStateManager.scale(emitter.scale[0], emitter.scale[1], emitter.scale[2]);
        }

        MorphUtils.render(this.morph.get(), dummy, 0, 0, 0, 0, partialTicks);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    protected Vector3d calculatePosition(BedrockEmitter emitter, BedrockParticle particle, double px, double py, double pz)
    {
        if (particle.relativePosition && particle.relativeRotation)
        {
            Vector3f vector = new Vector3f((float) px, (float) py, (float) pz);
            emitter.rotation.transform(vector);

            px = vector.x;
            py = vector.y;
            pz = vector.z;

            if (particle.relativeScale)
            {
                Vector3d pos = new Vector3d(px, py, pz);

                Matrix3d scale = new Matrix3d(emitter.scale[0], 0, 0,
                        0, emitter.scale[1], 0,
                        0, 0, emitter.scale[2]);

                scale.transform(pos);

                px = pos.x;
                py = pos.y;
                pz = pos.z;
            }

            px += emitter.lastGlobal.x;
            py += emitter.lastGlobal.y;
            pz += emitter.lastGlobal.z;
        }
        else if (particle.relativeScale)
        {
            Vector3d pos = new Vector3d(px, py, pz);

            Matrix3d scale = new Matrix3d(emitter.scale[0], 0, 0,
                    0, emitter.scale[1], 0,
                    0, 0, emitter.scale[2]);

            pos.sub(emitter.lastGlobal); //transform back to local
            scale.transform(pos);
            pos.add(emitter.lastGlobal); //transform back to global

            px = pos.x;
            py = pos.y;
            pz = pos.z;
        }

        return new Vector3d(px, py, pz);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
    {
        if (this.enabled && !particle.morph.isEmpty() && particle.morph.get() != null)
        {
            particle.morph.get().renderOnScreen(Minecraft.getMinecraft().player, x, y, scale, 1F);
        }
    }

    @Override
    public void preRender(BedrockEmitter emitter, float partialTicks)
    {}

    @Override
    public void postRender(BedrockEmitter emitter, float partialTicks)
    {}

    @Override
    public int getSortingIndex()
    {
        return 99;
    }
}
