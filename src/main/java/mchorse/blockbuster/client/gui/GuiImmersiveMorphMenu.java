package mchorse.blockbuster.client.gui;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.lwjgl.input.Keyboard;

import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster_pack.client.gui.GuiSequencerMorph.GuiSequencerMorphRenderer;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

/**
 * Immersive morph menu
 * 
 * It is the actual executor that synchronizes the model editing process to the game world
 */
public class GuiImmersiveMorphMenu extends GuiCreativeMorphsMenu
{
    public boolean immersionMode = true;
    public EntityLivingBase target;
    public Consumer<GuiImmersiveMorphMenu> updateCallback;
    public boolean hideGuiModel = true;

    public Function<Integer, Frame> frameProvider;

    private boolean useFov = false;

    private PreviewMorph preview = new PreviewMorph();
    private AbstractMorph lastMorph;

    private Stack<Boolean> stack = new Stack<>();

    public GuiImmersiveMorphMenu(Minecraft mc, Consumer<AbstractMorph> callback)
    {
        super(mc, true, callback);

        GuiButtonElement close = new GuiButtonElement(mc, IKey.str("X"), (b) -> this.exit());
        close.flex().w(20);

        this.bar.add(close);

        this.keys().register(IKey.lang("blockbuster.gui.morphs.keys.toggle_gui_model"), Keyboard.KEY_F3, () -> this.hideGuiModel = !this.hideGuiModel)
            .category(GuiImmersiveEditor.CATEGORY).active(() -> this.isImmersionMode());
    }

    @Override
    public void nestEdit(AbstractMorph selected, boolean editing, boolean keepViewport, Consumer<AbstractMorph> callback)
    {
        this.stack.add(this.immersionMode);
        this.immersionMode &= keepViewport;

        super.nestEdit(selected, editing, keepViewport, callback);
    }

    @Override
    public void restoreEdit()
    {
        super.restoreEdit();

        this.immersionMode = this.stack.pop();
    }

    @Override
    public void exit()
    {
        if (this.isEditMode() || this.isNested())
        {
            if (this.isEditMode())
            {
                this.editor.delegate.renderer.fov = 70F;
            }

            super.exit();
        }
        else
        {
            ((GuiImmersiveEditor) mc.currentScreen).closeThisScreen();
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        this.useFov = false;
        this.frameProvider = null;
    }

    @Override
    public void draw(GuiContext context)
    {
        if (!this.isImmersionMode())
        {
            Gui.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 0x33000000);
        }

        if (this.isEditMode())
        {
            this.refreshImmersive();
        }

        super.draw(context);
    }

    public void keepViewport()
    {
        if (!this.isNested() && this.target != null)
        {
            EntityPlayer player = this.mc.player;

            Vector3f temp = new Vector3f((float) player.posX, (float) player.posY + player.getEyeHeight(), (float) player.posZ);
            Vector3f vec = new Vector3f();
            vec.set(0.0F, 0.0F, 2.05F);

            Matrix4f mat = new Matrix4f();
            mat.rotX(player.rotationPitch / 180.0F * 3.1415927F);
            mat.transform(vec);
            mat.rotY(-player.rotationYaw / 180.0F * 3.1415927F);
            mat.transform(vec);

            temp.x += vec.x - this.target.posX;
            temp.y += vec.y - this.target.posY;
            temp.z += vec.z - this.target.posZ;

            mat.rotY((this.target.rotationYaw) / 180.0F * 3.1415927F);
            mat.transform(temp);

            this.useFov = true;
            this.keepViewport = true;
            this.lastPos.set(temp);
            this.lastPitch = player.rotationPitch;
            this.lastYaw = player.rotationYaw - this.target.rotationYaw - 180F;
            this.lastScale = 2F;

            if (this.isEditMode())
            {
                GuiModelRenderer renderer = this.editor.delegate.renderer;

                renderer.setPosition(this.lastPos.x, this.lastPos.y, this.lastPos.z);
                renderer.setRotation(this.lastYaw, this.lastPitch);
                renderer.setScale(this.lastScale);
            }
        }
    }

    public Frame getFrame(int tick)
    {
        if (this.frameProvider != null && !this.isNested())
        {
            return this.frameProvider.apply(tick);
        }
        else
        {
            return null;
        }
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event)
    {
        if (this.isEditMode() && this.immersionMode && event.phase == Phase.START)
        {
            this.preview.renderComplete = false;

            if (this.updateCallback != null)
            {
                this.updateCallback.accept(this);
            }
        }

        if (this.isImmersionMode())
        {
            if (event.phase == Phase.START)
            {
                this.lastMorph = EntityUtils.getMorph(this.target);

                if (this.target instanceof EntityActor)
                {
                    ((EntityActor) this.target).morph.setDirect(this.preview);
                }
                else if (this.target instanceof EntityPlayer)
                {
                    MorphAPI.morph((EntityPlayer) this.target, this.preview, true);
                }

                GuiModelRenderer renderer = this.editor.delegate.renderer;

                Vector3f temp = new Vector3f(renderer.pos);
                Vector3f vec = new Vector3f();
                vec.set(0.0F, 0.0F, -renderer.scale - 0.05F);

                renderer.pitch = MathUtils.clamp(renderer.pitch, -90F, 90F);

                Matrix4f mat = new Matrix4f();
                mat.rotX(renderer.pitch / 180.0F * 3.1415927F);
                mat.transform(vec);
                mat.rotY((180.0F - renderer.yaw) / 180.0F * 3.1415927F);
                mat.transform(vec);

                temp.x += vec.x;
                temp.y += vec.y;
                temp.z += vec.z;

                mat.rotY(-target.rotationYaw / 180.0F * 3.1415927F);
                mat.transform(temp);

                temp.x += this.target.posX;
                temp.y += this.target.posY;
                temp.z += this.target.posZ;

                EntityPlayer camera = this.mc.player;
                camera.setPositionAndRotation(temp.x, Math.max(temp.y - camera.getEyeHeight(), -64.0), temp.z, renderer.yaw + target.rotationYaw + 180, renderer.pitch);
                camera.setLocationAndAngles(temp.x, Math.max(temp.y - camera.getEyeHeight(), -64.0), temp.z, renderer.yaw + target.rotationYaw + 180, renderer.pitch);
                camera.motionX = camera.motionY = camera.motionZ = 0;
            }
            else
            {
                if (this.target instanceof EntityActor)
                {
                    ((EntityActor) this.target).morph.setDirect(this.lastMorph);
                }
                else if (this.target instanceof EntityPlayer)
                {
                    MorphAPI.morph((EntityPlayer) this.target, this.lastMorph, true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFovModifierEvent(FOVModifier event)
    {
        if (this.useFov)
        {
            event.setFOV(this.mc.gameSettings.fovSetting);
        }

        if (this.isImmersionMode())
        {
            if (this.useFov)
            {
                this.editor.delegate.renderer.fov = event.getFOV();
            }
            else
            {
                event.setFOV(this.editor.delegate.renderer.fov);
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event)
    {
        event.setCanceled(true);
    }

    public void refreshImmersive()
    {
        GuiModelRenderer renderer = this.editor.delegate.renderer;

        renderer.hideModel = this.isImmersionMode() && this.preview.renderComplete && this.hideGuiModel && (!this.doRenderOnionSkin || !this.haveOnionSkin());
        renderer.customEntity = this.isImmersionMode();
        renderer.fullScreen = this.isImmersionMode();

        if (renderer.customEntity)
        {
            renderer.entityPitch = this.target.rotationPitch;
            renderer.entityYawHead = this.target.rotationYawHead - this.target.rotationYaw;
            renderer.entityYawBody = this.target.renderYawOffset - this.target.rotationYaw;
            renderer.entityTicksExisted = this.target.ticksExisted;
        }
    }

    public boolean isImmersionMode()
    {
        return this.isEditMode() && this.immersionMode && this.target != null;
    }

    public class PreviewMorph extends AbstractMorph
    {
        public boolean renderComplete;

        @Override
        public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha)
        {}

        @Override
        public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
        {
            this.renderComplete = true;

            GuiImmersiveMorphMenu menu = GuiImmersiveMorphMenu.this;
            AbstractMorph morph = menu.editor.delegate.morph;
            GuiModelRenderer renderer = menu.editor.delegate.renderer;

            if (renderer instanceof GuiSequencerMorphRenderer)
            {
                ((GuiSequencerMorphRenderer) renderer).doRender(GuiBase.getCurrent(), entity, x, y, z);

                morph = null;
            }
            else if (renderer instanceof GuiMorphRenderer)
            {
                morph = ((GuiMorphRenderer) renderer).morph;
            }

            if (morph != null)
            {
                MorphUtils.render(morph, entity, x, y, z, entityYaw, partialTicks);
            }
        }

        @Override
        public AbstractMorph create()
        {
            return null;
        }

        @Override
        public float getWidth(EntityLivingBase target)
        {
            return 0;
        }

        @Override
        public float getHeight(EntityLivingBase target)
        {
            return 0;
        }
    }
}
