package mchorse.blockbuster.client.gui.utils;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.formats.obj.ShapeKey;
import mchorse.blockbuster_pack.client.gui.GuiCustomMorph;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.List;
import java.util.function.Supplier;

public class GuiShapeKeysEditor extends GuiElement
{
    public GuiListElement<ShapeKey> shapes;
    public GuiTrackpadElement factor;
    public GuiToggleElement relative;

    private Supplier<Model> supplier;

    public GuiShapeKeysEditor(Minecraft mc, Supplier<Model> supplier)
    {
        super(mc);

        this.supplier = supplier;

        this.shapes = new GuiCustomMorph.GuiShapeKeyListElement(mc, (str) -> this.setFactor(str.get(0)));
        this.shapes.sorting().background();
        this.shapes.context(() ->
        {
            GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);

            menu.action(Icons.ADD, IKey.lang("blockbuster.gui.builder.context.add"), () ->
            {
                Model model = this.supplier == null ? null : this.supplier.get();

                if (model == null)
                {
                    return;
                }

                GuiSimpleContextMenu nested = new GuiSimpleContextMenu(mc);

                for (String key : model.shapes)
                {
                    nested.action(Icons.ADD, IKey.format("blockbuster.gui.builder.context.add_to", key), () ->
                    {
                        ShapeKey shapeKey = new ShapeKey(key, 0);

                        this.shapes.getList().add(shapeKey);
                        this.shapes.update();
                        this.shapes.setCurrent(shapeKey);
                        this.setFactor(shapeKey);
                    });
                }

                GuiBase.getCurrent().replaceContextMenu(nested);
            });

            if (this.shapes.getIndex() != -1)
            {
                menu.action(Icons.REMOVE, IKey.lang("blockbuster.gui.builder.context.remove"), () ->
                {
                    int index = this.shapes.getIndex();

                    this.shapes.getList().remove(index);
                    index = MathUtils.clamp(index, 0, this.shapes.getList().size() - 1);

                    this.shapes.setIndex(index);
                    this.setFactor(this.shapes.getCurrentFirst());
                });
            }

            return menu;
        });
        this.factor = new GuiTrackpadElement(mc, (value) -> this.setFactor(value.floatValue()));
        this.factor.tooltip(IKey.lang("blockbuster.gui.builder.shape_keys_factor_tooltip"), Direction.TOP);

        this.relative = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.builder.relative"), (b) -> this.shapes.getCurrentFirst().relative = b.isToggled());
        this.relative.tooltip(IKey.lang("blockbuster.gui.builder.relative_tooltip"), Direction.TOP);

        this.shapes.flex().relative(this).y(12).w(1F).hTo(this.factor.flex(), -17);
        this.factor.flex().relative(this.relative.flex()).y(-25).w(1F).h(20);
        this.relative.flex().relative(this).y(1F).w(1F).anchorY(1F);

        this.add(this.relative, this.factor, this.shapes);
    }

    private void setFactor(ShapeKey key)
    {
        this.factor.setEnabled(key != null);
        this.relative.setEnabled(key != null);

        if (key != null)
        {
            this.factor.setValue(key.value);
            this.relative.toggled(key.relative);
        }
    }

    private void setFactor(float value)
    {
        this.shapes.getCurrentFirst().value = value;
    }

    public void fillData(List<ShapeKey> shapeKeys)
    {
        this.shapes.setList(shapeKeys);

        if (!shapeKeys.isEmpty())
        {
            this.shapes.setIndex(0);
            this.setFactor(this.shapes.getCurrentFirst());
        }
        else
        {
            this.setFactor(null);
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        if (this.shapes.isVisible())
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.shape_keys"), this.shapes.area.x, this.shapes.area.y - 12, 0xffffff);
        }

        if (this.factor.isVisible())
        {
            this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.shape_keys_factor"), this.factor.area.x, this.factor.area.y - 12, 0xffffff);
        }
    }
}
