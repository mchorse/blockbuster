package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import java.util.Map.Entry;

import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.utils.April;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public abstract class GuiActionPanel<T extends Action> extends GuiElement
{
    public T action;
    private String title = "";
    private String description = "";

    public GuiActionPanel(Minecraft mc)
    {
        super(mc);
        this.createChildren();
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
        this.title = I18n.format("blockbuster.gui.record_editor.actions." + key + ".title");
        this.description = I18n.format("blockbuster.gui.record_editor.actions." + key + ".desc");
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        return super.mouseClicked(mouseX, mouseY, mouseButton) || this.area.isInside(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int scroll)
    {
        return super.mouseScrolled(mouseX, mouseY, scroll) || this.area.isInside(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        tooltip.set(null, null);

        super.draw(tooltip, mouseX, mouseY, partialTicks);

        if (!this.title.isEmpty())
        {
            this.font.drawStringWithShadow(this.title, this.area.x + 10, this.area.y + 10, April.aprilColor("ASDAS"));
            this.font.drawSplitString(this.description, this.area.x + 10, this.area.y + 30, this.area.w / 3, April.aprilColor("ASDdsaAS"));
        }
    }
}