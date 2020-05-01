package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import java.util.Map.Entry;

import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public abstract class GuiActionPanel<T extends Action> extends GuiElement
{
    public T action;
    private IKey title = IKey.lang("");
    private IKey description = IKey.lang("");

    public GuiActionPanel(Minecraft mc)
    {
        super(mc);

        this.hideTooltip();
    }

    public void fill(T action)
    {
        this.action = action;

        for (Entry<String, Class<? extends Action>> entry : ActionRegistry.NAME_TO_CLASS.entrySet())
        {
            if (entry.getValue() == action.getClass())
            {
                this.setKey(entry.getKey());

                break;
            }
        }
    }

    public void appear()
    {}

    public void setMorph(AbstractMorph morph)
    {}

    public void setKey(String key)
    {
        this.title.set("blockbuster.gui.record_editor.actions." + key + ".title");
        this.description.set("blockbuster.gui.record_editor.actions." + key + ".desc");
    }

    @Override
    public void draw(GuiContext context)
    {
        String title = this.title.get();

        if (!title.isEmpty())
        {
            this.font.drawStringWithShadow(title, this.area.x + 10, this.area.y + 10, 0xffffff);
            GuiDraw.drawMultiText(this.font, this.description.get(), this.area.x + 10, this.area.y + 30, 0xcccccc, this.area.w / 3);
        }

        super.draw(context);
    }
}