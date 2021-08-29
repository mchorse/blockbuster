package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.aperture.CameraHandler;
import mchorse.blockbuster.client.gui.GuiImmersiveEditor;
import mchorse.blockbuster.client.gui.GuiImmersiveMorphMenu;
import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.scene.sync.PacketSceneGoto;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.blockbuster.recording.data.Record.FoundAction;
import mchorse.blockbuster.recording.scene.Replay;
import mchorse.blockbuster.utils.EntityUtils;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Color;
import mchorse.mclib.utils.DummyEntity;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsList;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsList.OnionSkin;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class GuiMorphActionPanel extends GuiActionPanel<MorphAction>
{
    public GuiNestedEdit pickMorph;
    public GuiColorElement onionSkin;
    public GuiElement onionSkinPanel;

    private DummyEntity actor;
    private int lastTick;

    private OnionSkin skin;

    private boolean isImmersiveEditing;
    private boolean showRecordList;
    private int cursor;

    public GuiMorphActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc, panel);

        this.pickMorph = new GuiNestedEdit(mc, this::doNestEdit);
        this.pickMorph.flex().relative(this.area).set(0, 5, 100, 20).x(0.5F, -30);

        this.onionSkin = new GuiColorElement(mc, Blockbuster.morphActionOnionSkinColor);

        this.onionSkinPanel = Elements.column(mc, 10, 5, Elements.label(IKey.lang("blockbuster.config.onion_skin.title")), this.onionSkin);
        this.onionSkinPanel.flex().relative(this.area).x(0F, 10).y(1F, -20).w(150).anchorY(1F);

        this.add(this.pickMorph, this.onionSkinPanel);

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

        this.onionSkinPanel.setVisible(CameraHandler.get() != null && CameraHandler.isCameraEditorOpen());
    }

    @Override
    public void disappear()
    {
        ClientProxy.panels.morphs.finish();
        ClientProxy.panels.morphs.removeFromParent();

        if (this.isImmersiveEditing)
        {
            ClientProxy.panels.closeImmersiveEditor();
        }

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

            if (Blockbuster.immersiveRecordEditor.get())
            {
                this.cursor = Math.max(0, CameraHandler.getOffset());

                CameraHandler.detachOutside();

                GuiImmersiveEditor editor = ClientProxy.panels.showImmersiveEditor(editing, this.action.morph);

                editor.morphs.updateCallback = this::updateMorphEditor;
                editor.morphs.frameProvider = this::getFrame;
                editor.onClose = this::onImmersiveEditorClose;

                this.panel.records.removeFromParent();
                this.panel.records.flex().relative(editor.outerPanel);
                this.panel.selector.removeFromParent();
                this.panel.selector.flex().relative(editor.outerPanel);

                editor.outerPanel.add(this.panel.records, this.panel.selector);

                this.addOnionSkin(editor.morphs);

                this.isImmersiveEditing = true;
                this.showRecordList = this.panel.records.isVisible();
                this.panel.records.setVisible(true);
            }
            else
            {
                ClientProxy.panels.addMorphs(this, editing, this.action.morph);

                this.addOnionSkin(ClientProxy.panels.morphs);
            }
        }
        else
        {
            ClientProxy.panels.addMorphs(this, editing, this.action.morph);
        }
    }

    public void updateMorphEditor(GuiImmersiveMorphMenu menu)
    {
        Record record = ClientProxy.manager.records.get(this.panel.record.filename);
        int tick = this.panel.selector.tick;

        if (menu.isNested())
        {
            tick = this.lastTick;
        }
        else
        {
            tick += menu.editor.delegate.getCurrentTick();
        }

        if (tick != this.lastTick)
        {
            Dispatcher.sendToServer(new PacketSceneGoto(CameraHandler.get(), tick, CameraHandler.actions.get()));

            if (record != null && record.getFrameSafe(0) != null)
            {
                record.applyFrame(Math.max(tick - 1, 0), this.actor, true, true);

                Frame frame = record.getFrameSafe(tick - 1);

                if (frame.hasBodyYaw)
                {
                    this.actor.renderYawOffset = frame.bodyYaw;
                }
            }
            else
            {
                menu.target = null;
            }

            this.lastTick = tick;
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

        if (menu.target instanceof EntityActor)
        {
            ((EntityActor) menu.target).morph.setDirect(null);
        }
        else if (menu.target instanceof EntityPlayer)
        {
            MorphAPI.morph((EntityPlayer) menu.target, null, true);
        }

        if (record != null && !record.frames.isEmpty() && this.skin != null && menu.target != null)
        {
            Frame last = record.getFrameSafe(this.panel.selector.tick - 1);
            EntityLivingBase actor = menu.target;
            float yaw = actor.rotationYaw;

            Vec3d pos = new Vec3d(last.x - actor.posX, last.y - actor.posY, last.z - actor.posZ);
            pos = pos.rotateYaw((float) Math.toRadians(yaw));

            this.skin.offset(pos.x, pos.y, pos.z, last.pitch, last.yawHead - yaw, last.bodyYaw - yaw);
        }
    }

    public Frame getFrame(int tick)
    {
        Record record = ClientProxy.manager.records.get(this.panel.record.filename);

        if (record != null)
        {
            return record.getFrameSafe(this.panel.selector.tick + tick - 1);
        }
        else
        {
            return null;
        }
    }

    public void addOnionSkin(GuiCreativeMorphsList morphs)
    {
        if (this.onionSkin.picker.color.a < 0.003921F)
        {
            return;
        }

        List<OnionSkin> skins = new ArrayList<OnionSkin>();
        Record record = this.panel.record;
        Color color = this.onionSkin.picker.color;

        if (record != null)
        {
            FoundAction found = record.seekMorphAction(this.panel.selector.tick, this.action);
            AbstractMorph morph = null;
            int tick = 0;

            if (found != null)
            {
                morph = found.action.morph;
                tick = found.tick;
            }
            else
            {
                for (Replay replay : ClientProxy.panels.scenePanel.getReplays())
                {
                    if (replay.id.equals(this.panel.record.filename))
                    {
                        morph = replay.morph;
                        break;
                    }
                }
            }

            if (morph != null)
            {
                MorphUtils.pause(morph, null, Math.max(0, this.panel.selector.tick - tick));

                this.skin = new OnionSkin().color(color.r, color.g, color.b, color.a).morph(morph);
                skins.add(this.skin);
            }
        }

        morphs.lastOnionSkins = skins;
    }

    public void onImmersiveEditorClose(GuiImmersiveEditor editor)
    {
        this.isImmersiveEditing = false;

        this.panel.records.setVisible(this.showRecordList);

        CameraHandler.updatePlayerPosition();
        CameraHandler.attachOutside();
        CameraHandler.moveRecordPanel(this.panel);

        Dispatcher.sendToServer(new PacketSceneGoto(CameraHandler.get(), this.cursor, CameraHandler.actions.get()));
    }
}
