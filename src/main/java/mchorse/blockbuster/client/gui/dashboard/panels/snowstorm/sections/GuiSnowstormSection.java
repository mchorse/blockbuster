package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections;

import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSectionManager;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.GuiSnowstorm;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.utils.ColorUtils;
import net.minecraft.client.Minecraft;

public abstract class GuiSnowstormSection extends GuiElement
{
    public GuiLabel title;
    public GuiElement fields;

    protected BedrockScheme scheme;
    protected GuiSnowstorm parent;

    public GuiSnowstormSection(Minecraft mc, GuiSnowstorm parent)
    {
        super(mc);
        
        this.parent = parent;
        this.title = Elements.label(IKey.lang(this.getTitle())).background(() -> ColorUtils.HALF_BLACK + McLib.primaryColor.get());
        this.fields = new GuiElement(mc);
        this.fields.flex().column(5).stretch().vertical().height(20);

        this.flex().column(5).stretch().vertical();
        this.add(this.title);
        
        this.collapseState();
    }
    
    protected void collapseState()
    {
        if (!GuiSectionManager.isCollapsed(this.getClass().getSimpleName()))
        {
            this.add(this.fields);
        }
    }

    public abstract String getTitle();

    public MolangExpression parse(String string, GuiTextElement element, MolangExpression old)
    {
        if (string.isEmpty())
        {
            return MolangParser.ZERO;
        }

        try
        {
            MolangExpression expression = this.scheme.parser.parseExpression(string);

            element.field.setColor(0xffffff);
            this.parent.dirty();

            return expression;
        }
        catch (Exception e)
        {}

        element.field.setColor(0xff2244);

        return old;
    }

    public void set(GuiTextElement element, MolangExpression expression)
    {
        element.field.setColor(0xffffff);
        element.setText(expression.toString());
    }

    public void setScheme(BedrockScheme scheme)
    {
        this.scheme = scheme;
    }

    public void beforeSave(BedrockScheme scheme)
    {}

    /**
     * Toggle visibility of the field section
     */
    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseClicked(context))
        {
            return true;
        }

        if (this.title.area.isInside(context))
        {
            if (this.fields.hasParent())
            {
                this.fields.removeFromParent();
                GuiSectionManager.setCollapsed(this.getClass().getSimpleName(), true);
            }
            else
            {
                this.add(this.fields);
                GuiSectionManager.setCollapsed(this.getClass().getSimpleName(), false);
            }

            this.resizeParent();

            return true;
        }

        return false;
    }

    protected void resizeParent()
    {
        this.getParent().resize();
    }
}