package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.recording.actions.DamageAction;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiDamageActionPanel extends GuiActionPanel<DamageAction>
{
    public GuiTrackpadElement damage;

    public GuiDamageActionPanel(Minecraft mc)
    {
        super(mc);

        this.damage = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.record_editor.damage"), (charge) -> this.action.damage = charge.intValue());
        this.damage.setLimit(0, Float.POSITIVE_INFINITY);
        this.damage.resizer().set(10, 0, 100, 20).parent(this.area).y(1, -30);

        this.children.add(this.damage);
    }

    @Override
    public void fill(DamageAction action)
    {
        super.fill(action);

        this.damage.setValue(action.damage);
    }
}