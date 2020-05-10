package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.GuiRecordingEditorPanel;
import mchorse.blockbuster.recording.actions.Action;
import mchorse.blockbuster.recording.actions.ActionRegistry;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map.Entry;

public abstract class GuiActionPanel<T extends Action> extends GuiElement
{
    public T action;
    public GuiRecordingEditorPanel panel;

    private IKey title = IKey.lang("");
    private IKey description = IKey.lang("");

    public GuiActionPanel(Minecraft mc, GuiRecordingEditorPanel panel)
    {
        super(mc);

        this.panel = panel;

        this.hideTooltip();
    }

    public void fill(T action)
    {
        this.action = action;

        String key = ActionRegistry.NAME_TO_CLASS.inverse().get(action.getClass());

        if (key != null)
        {
            this.setKey(key);
        }
    }

    public void disappear()
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