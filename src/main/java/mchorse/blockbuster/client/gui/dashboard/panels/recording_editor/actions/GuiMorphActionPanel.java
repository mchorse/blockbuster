package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.client.gui.GuiImmersiveEditor;
import mchorse.blockbuster.client.gui.GuiImmersiveMorphMenu;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.utils.DummyEntity;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class GuiMorphActionPanel extends GuiActionPanel<MorphAction>
{
    public GuiNestedEdit pickMorph;

    private DummyEntity actor;
    private int lastTick;

    public GuiMorphActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);

        this.pickMorph = new GuiNestedEdit(mc, this::doNestEdit);
        this.pickMorph.flex().relative(this.area).set(0, 5, 100, 20).x(0.5F, -30);

        this.add(this.pickMorph);

        this.actor = new DummyEntity(this.mc.world);
    }

    @Override
    public void setMorph(AbstractMorph morph)
    {
        this.action.morph = morph;
        this.pickMorph.setMorph(action.morph);
    }

    @Override
    public void fill(MorphAction action)
    {
        super.fill(action);

        ClientProxy.panels.morphs.removeFromParent();
        this.pickMorph.setMorph(action.morph);
    }

    @Override
    public void disappear()
    {
        ClientProxy.panels.morphs.finish();
        ClientProxy.panels.morphs.removeFromParent();

        this.action.morph = MorphUtils.copy(this.action.morph);

        super.disappear();
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.action.morph != null)
        {
            int x = this.area.mx();
            int y = this.area.y(0.8F);

            GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
            this.action.morph.renderOnScreen(this.mc.player, x, y, this.area.h / 3F, 1.0F);
            GuiDraw.unscissor(context);
        }

        super.draw(context);
    }

    public void doNestEdit(boolean editing)
    {
        if (CameraHandler.get() != null && CameraHandler.isCameraEditorOpen())
        {
            this.lastTick = -1;

            GuiImmersiveEditor editor = ClientProxy.panels.showImmersiveEditor(editing, this.action.morph, this::updateMorphEditor);

            this.updateMorphEditor(editor.morphs);
            editor.morphs.target = this.actor;
            editor.keepViewport();
        }
        else
        {
            ClientProxy.panels.addMorphs(this, editing, this.action.morph);
        }
    }

    public void updateMorphEditor(GuiImmersiveMorphMenu menu)
    {
        CameraHandler.updatePlayerPosition();

        int tick = menu.isEditMode() ? this.panel.selector.tick + menu.editor.delegate.getCurrentTick() : this.panel.selector.cursor;

        if (tick != this.lastTick)
        {
            this.lastTick = tick;

            Dispatcher.sendToServer(new PacketSceneGoto(CameraHandler.get(), tick, CameraHandler.actions.get()));

            Record record = ClientProxy.manager.records.get(this.panel.record.filename);

            if (record != null)
            {
                record.applyFrame(Math.max(tick - 1, 0), this.actor, true, true);

                Frame frame = record.getFrameSafe(tick - 1);

                if (frame.hasBodyYaw)
                {
                    this.actor.renderYawOffset = frame.bodyYaw;
                }
            }
        }

        boolean refreshTarget = true;

        if (menu.target != null && menu.target != this.actor && this.mc.world.getLoadedEntityList().contains(menu.target))
        {
            refreshTarget = false;
        }

        if (refreshTarget)
        {
            EntityLivingBase entity = null;

            for (EntityLivingBase actor : Minecraft.getMinecraft().world.getEntities(EntityLivingBase.class, actor ->
            {
                return actor.isEntityAlive() && EntityUtils.getRecordPlayer(actor) != null && EntityUtils.getRecordPlayer(actor).record != null && this.panel.record.filename.equals(EntityUtils.getRecordPlayer(actor).record.filename);
            }))
            {
                entity = actor;
                break;
            }

            if (entity == null)
            {
                entity = this.actor;
            }

            menu.target = entity;
        }
    }
}
