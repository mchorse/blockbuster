package mchorse.blockbuster.client.gui.dashboard.panels.recording_editor.actions;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.recording.actions.MorphAction;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;

public class GuiMorphActionPanel extends GuiActionPanel<MorphAction>
{
    public GuiButtonElement pick;

    public GuiMorphActionPanel(Minecraft mc)
    {
        super(mc);

        this.pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.pick"), (b) ->
        {
            ClientProxy.panels.morphs.flex().reset().relative(this.area).wh(1F, 1F);
            ClientProxy.panels.morphs.resize();
            this.add(ClientProxy.panels.morphs);
        });
        this.pick.flex().relative(this.area).set(0, 5, 60, 20).x(0.5F, -30);

        this.add(this.pick);
    }

    @Override
    public void setMorph(AbstractMorph morph)
    {
        this.action.morph = morph;
    }

    @Override
    public void fill(MorphAction action)
    {
        super.fill(action);

        ClientProxy.panels.morphs.setSelected(action.morph);
    }

    @Override
    public void appear()
    {
        if (this.action != null)
        {
            this.fill(action);
        }
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
}