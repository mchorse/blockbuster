package mchorse.blockbuster.aperture.camera.modifiers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mchorse.aperture.camera.CameraProfile;
import mchorse.aperture.camera.data.Position;
import mchorse.aperture.camera.fixtures.AbstractFixture;
import mchorse.aperture.camera.modifiers.AbstractModifier;
import mchorse.aperture.camera.modifiers.EntityModifier;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.blockbuster_pack.morphs.TrackerMorph;
import mchorse.blockbuster_pack.trackers.ApertureCamera;
import mchorse.mclib.config.values.ValueBoolean;
import mchorse.mclib.config.values.ValueFloat;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.bodypart.BodyPart;
import mchorse.metamorph.bodypart.BodyPartManager;
import mchorse.metamorph.bodypart.IBodyPartProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class TrackerModifier extends EntityModifier
{
    public final ValueFloat yaw = new ValueFloat("yaw");
    public final ValueFloat pitch = new ValueFloat("pitch");
    public final ValueFloat roll = new ValueFloat("roll");
    public final ValueBoolean relative = new ValueBoolean("relative", true);
    public final ValueBoolean mainCam = new ValueBoolean("main_cam", true);

    public TrackerModifier()
    {
        super();

        this.register(this.yaw);
        this.register(this.pitch);
        this.register(this.roll);
        this.register(this.relative);
        this.register(this.mainCam);
    }

    @Override
    public AbstractModifier create()
    {
        return new TrackerModifier();
    }

    @Override
    public void modify(long ticks, long offset, AbstractFixture fixture, float partialTick, float previewPartialTick, CameraProfile profile, Position pos)
    {
        if (this.checkForDead())
        {
            this.tryFindingEntity();
        }

        if (this.entities == null)
        {
            return;
        }

        this.position.copy(pos);

        if (fixture != null && this.relative.get())
        {
            fixture.applyFixture(0, 0, 0, profile, this.position);
        }

        this.position.point.x = pos.point.x - this.position.point.x;
        this.position.point.y = pos.point.y - this.position.point.y;
        this.position.point.z = pos.point.z - this.position.point.z;

        this.position.angle.yaw = pos.angle.yaw - this.position.angle.yaw;
        this.position.angle.pitch = pos.angle.pitch - this.position.angle.pitch;
        this.position.angle.roll = pos.angle.roll - this.position.angle.roll;

        ApertureCamera.tracking = this.selector.get();

        ApertureCamera.offsetPos.x = (float) this.offset.get().x;
        ApertureCamera.offsetPos.y = (float) this.offset.get().y;
        ApertureCamera.offsetPos.z = (float) this.offset.get().z;

        ApertureCamera.offsetRot.x = this.pitch.get();
        ApertureCamera.offsetRot.y = this.yaw.get();
        ApertureCamera.offsetRot.z = this.roll.get();

        if (this.mainCam.get())
        {
            ApertureCamera.offsetPos.x += this.position.point.x;
            ApertureCamera.offsetPos.y += this.position.point.y;
            ApertureCamera.offsetPos.z += this.position.point.z;

            ApertureCamera.offsetRot.x += this.position.angle.pitch;
            ApertureCamera.offsetRot.y += this.position.angle.yaw;
            ApertureCamera.offsetRot.z += this.position.angle.roll;
        }

        Entity entity = this.entities.get(0);

        Render<Entity> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
        double baseX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTick;
        double baseY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTick;
        double baseZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTick;
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTick;
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ApertureCamera.enable = true;
        render.doRender(entity, baseX, baseY, baseZ, yaw, partialTick);
        ApertureCamera.enable = false;
        GL11.glPopMatrix();
        GlStateManager.disableLighting();

        pos.point.set(ApertureCamera.pos.x, ApertureCamera.pos.y, ApertureCamera.pos.z);
        if (this.mainCam.get())
        {
            pos.angle.set(ApertureCamera.rot.y, ApertureCamera.rot.x, ApertureCamera.rot.z, pos.angle.fov);
        }
        else
        {
            pos.point.x += this.position.point.x;
            pos.point.y += this.position.point.y;
            pos.point.z += this.position.point.z;
        }
    }

    @Override
    public void tryFindingEntity()
    {
        String selector = this.selector.get();
        this.entities = null;
        if (selector != null && !selector.isEmpty() && FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            queryActor(selector);
        }
    }

    @Override
    protected boolean checkForDead()
    {
        if (!super.checkForDead())
        {
            Iterator<Entity> it = this.entities.iterator();
            while (it.hasNext())
            {
                if (!checkTracker(mchorse.metamorph.api.EntityUtils.getMorph((EntityLivingBase) it.next()), this.selector.get()))
                {
                    it.remove();
                }
            }

            if (this.entities.isEmpty())
            {
                this.entities = null;
            }
        }

        return this.entities == null;
    }

    private void queryActor(String selector)
    {
        if (CameraHandler.get() == null)
        {
            return;
        }

        List<String> replays = new ArrayList<String>();
        List<Entity> entities = new ArrayList<Entity>();

        for (Replay replay : ClientProxy.panels.scenePanel.getReplays())
        {
            replays.add(replay.id);
        }

        for (EntityLivingBase actor : Minecraft.getMinecraft().world.getEntities(EntityLivingBase.class, actor ->
        {
            return actor.isEntityAlive() && EntityUtils.getRecordPlayer(actor) != null && EntityUtils.getRecordPlayer(actor).record != null && replays.contains(EntityUtils.getRecordPlayer(actor).record.filename) && mchorse.metamorph.api.EntityUtils.getMorph(actor) != null;
        }))
        {
            if (checkTracker(mchorse.metamorph.api.EntityUtils.getMorph(actor), selector))
            {
                entities.add(actor);
            }
        }

        if (!entities.isEmpty())
        {
            this.entities = entities;
        }
    }

    private boolean checkTracker(AbstractMorph morph, String selector)
    {
        while (true)
        {
            if (morph instanceof TrackerMorph && ((TrackerMorph) morph).tracker instanceof ApertureCamera && selector.equals(((TrackerMorph) morph).tracker.name))
            {
                return true;
            }

            if (morph instanceof IBodyPartProvider)
            {
                BodyPartManager mgr = ((IBodyPartProvider) morph).getBodyPart();
                for (BodyPart part : mgr.parts)
                {
                    if (part.enabled)
                    {
                        if (checkTracker(part.morph.get(), selector))
                        {
                            return true;
                        }
                    }
                }
            }

            if (morph instanceof IMorphProvider)
            {
                morph = ((IMorphProvider) morph).getMorph();
            }
            else
            {
                break;
            }
        }

        return false;
    }
}
