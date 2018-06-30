package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.recording.actions.DamageAction;
import net.minecraft.client.Minecraft;

public class GuiDamageActionPanel extends GuiActionPanel<DamageAction>
{
    public GuiTrackpadElement damage;

    public GuiDamageActionPanel(Minecraft mc, String title)
    {
        super(mc);

        this.title = title;
        this.damage = new GuiTrackpadElement(mc, "Damage", (charge) -> this.action.damage = charge.intValue());
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