package mchorse.blockbuster.aperture.gui.panels.modifiers;

import mchorse.aperture.camera.data.Point;
import mchorse.aperture.client.gui.GuiModifiersManager;
import mchorse.aperture.client.gui.panels.modifiers.GuiAbstractModifierPanel;
import mchorse.blockbuster.aperture.camera.modifiers.TrackerModifier;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

public class GuiTrackerModifierPanel extends GuiAbstractModifierPanel<TrackerModifier>
{
    public GuiTextElement target;
    public GuiTrackpadElement x;
    public GuiTrackpadElement y;
    public GuiTrackpadElement z;
    public GuiTrackpadElement yaw;
    public GuiTrackpadElement pitch;
    public GuiTrackpadElement roll;
    public GuiToggleElement relative;
    public GuiToggleElement mainCam;
    
    public GuiTrackerModifierPanel(Minecraft mc, TrackerModifier modifier, GuiModifiersManager modifiers)
    {
        super(mc, modifier, modifiers);
        
        this.target = new GuiTextElement(mc, 500, (str) ->
        {
            this.modifiers.editor.postUndo(this.undo(this.modifier.selector, str));
            this.modifier.tryFindingEntity();
        });
        this.target.tooltip(IKey.lang("blockbuster.gui.aperture.modifiers.panels.tracker_tooltip"));

        this.x = new GuiTrackpadElement(mc, (value) ->
        {
            Point point = this.modifier.offset.get().copy();

            point.x = value;
            this.modifiers.editor.postUndo(this.undo(this.modifier.offset, point));
        });
        this.x.tooltip(IKey.lang("aperture.gui.panels.x"));

        this.y = new GuiTrackpadElement(mc, (value) ->
        {
            Point point = this.modifier.offset.get().copy();

            point.y = value;
            this.modifiers.editor.postUndo(this.undo(this.modifier.offset, point));
        });
        this.y.tooltip(IKey.lang("aperture.gui.panels.y"));

        this.z = new GuiTrackpadElement(mc, (value) ->
        {
            Point point = this.modifier.offset.get().copy();

            point.z = value;
            this.modifiers.editor.postUndo(this.undo(this.modifier.offset, point));
        });
        this.z.tooltip(IKey.lang("aperture.gui.panels.z"));

        this.yaw = new GuiTrackpadElement(mc, (value) ->
        {
            this.modifiers.editor.postUndo(this.undo(this.modifier.yaw, value.floatValue()));
        });
        this.yaw.tooltip(IKey.lang("aperture.gui.panels.yaw"));

        this.pitch = new GuiTrackpadElement(mc, (value) ->
        {
            this.modifiers.editor.postUndo(this.undo(this.modifier.pitch, value.floatValue()));
        });
        this.pitch.tooltip(IKey.lang("aperture.gui.panels.pitch"));

        this.roll = new GuiTrackpadElement(mc, (value) ->
        {
            this.modifiers.editor.postUndo(this.undo(this.modifier.roll, value.floatValue()));
        });
        this.roll.tooltip(IKey.lang("aperture.gui.panels.roll"));
        
        this.relative = new GuiToggleElement(mc, IKey.lang("aperture.gui.modifiers.panels.relative"), false, (b) ->
        {
            this.modifiers.editor.postUndo(this.undo(this.modifier.relative, b.isToggled()));
        });
        this.relative.tooltip(IKey.lang("aperture.gui.modifiers.panels.relative_tooltip"));
        
        this.mainCam = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.aperture.modifiers.panels.main_cam"), (b) ->
        {
            this.modifiers.editor.postUndo(this.undo(this.modifier.mainCam, b.isToggled()));
        });
        this.mainCam.tooltip(IKey.lang("blockbuster.gui.aperture.modifiers.panels.main_cam_tooltip"));

        this.fields.add(this.target, Elements.row(mc, 5, 0, 20, this.x, this.y, this.z), Elements.row(mc, 5, 0, 20, this.yaw, this.pitch, this.roll), this.relative, this.mainCam);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.target.setText(this.modifier.selector.get());
        this.x.setValue(this.modifier.offset.get().x);
        this.y.setValue(this.modifier.offset.get().y);
        this.z.setValue(this.modifier.offset.get().z);
        this.yaw.setValue(this.modifier.yaw.get());
        this.pitch.setValue(this.modifier.pitch.get());
        this.roll.setValue(this.modifier.roll.get());
        this.relative.toggled(this.modifier.relative.get());
        this.mainCam.toggled(this.modifier.mainCam.get());
    }

}
