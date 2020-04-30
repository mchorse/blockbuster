package mchorse.blockbuster.client.gui.dashboard.panels.model_block;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.util.MMIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class GuiModelBlockPanel extends GuiBlockbusterPanel
{
    public static final List<BlockPos> lastBlocks = new ArrayList<BlockPos>();

    private TileEntityModel model;

    private GuiTrackpadElement yaw;
    private GuiTrackpadElement pitch;
    private GuiTrackpadElement body;

    private GuiTrackpadElement x;
    private GuiTrackpadElement y;
    private GuiTrackpadElement z;

    private GuiTrackpadElement sx;
    private GuiTrackpadElement sy;
    private GuiTrackpadElement sz;

    private GuiTrackpadElement rx;
    private GuiTrackpadElement ry;
    private GuiTrackpadElement rz;

    private GuiToggleElement one;
    private GuiCirculateElement order;
    private GuiToggleElement shadow;
    private GuiToggleElement global;
    private GuiToggleElement enabled;

    private GuiModelBlockList list;
    private GuiElement subChildren;

    private GuiInventoryElement inventory;
    private GuiSlotElement[] slots = new GuiSlotElement[6];

    /**
     * Try adding a block position, if it doesn't exist in list already 
     */
    public static void tryAddingBlock(BlockPos pos)
    {
        for (BlockPos stored : lastBlocks)
        {
            if (pos.equals(stored))
            {
                return;
            }
        }

        lastBlocks.add(pos);
    }

    public GuiModelBlockPanel(Minecraft mc, GuiDashboard dashboard)
    {
        super(mc, dashboard);

        GuiElement element = null;

        this.subChildren = new GuiElement(mc);
        this.subChildren.setVisible(false);
        this.add(this.subChildren);

        /* Entity angles */
        this.subChildren.add(this.yaw = new GuiTrackpadElement(mc, (value) -> this.model.rotateYawHead = value));
        this.yaw.tooltip(IKey.lang("blockbuster.gui.model_block.yaw"));
        this.subChildren.add(this.pitch = new GuiTrackpadElement(mc, (value) -> this.model.rotatePitch = value));
        this.pitch.tooltip(IKey.lang("blockbuster.gui.model_block.pitch"));
        this.subChildren.add(this.body = new GuiTrackpadElement(mc, (value) -> this.model.rotateBody = value));
        this.body.tooltip(IKey.lang("blockbuster.gui.model_block.body"));

        this.yaw.flex().set(10, 20, 80, 20).relative(this.area);
        this.pitch.flex().set(0, 25, 80, 20).relative(this.yaw.resizer());
        this.body.flex().set(0, 25, 80, 20).relative(this.pitch.resizer());

        /* Translation */
        this.subChildren.add(this.x = new GuiTrackpadElement(mc, (value) -> this.model.x = value));
        this.x.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
        this.subChildren.add(this.y = new GuiTrackpadElement(mc, (value) -> this.model.y = value));
        this.y.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
        this.subChildren.add(this.z = new GuiTrackpadElement(mc, (value) -> this.model.z = value));
        this.z.tooltip(IKey.lang("blockbuster.gui.model_block.z"));

        this.x.flex().set(0, 45, 80, 20).relative(this.body.resizer());
        this.y.flex().set(0, 25, 80, 20).relative(this.x.resizer());
        this.z.flex().set(0, 25, 80, 20).relative(this.y.resizer());

        /* Scale */
        this.subChildren.add(this.sx = new GuiTrackpadElement(mc, (value) -> this.model.sx = value));
        this.sx.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
        this.subChildren.add(this.sy = new GuiTrackpadElement(mc, (value) -> this.model.sy = value));
        this.sy.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
        this.subChildren.add(this.sz = new GuiTrackpadElement(mc, (value) -> this.model.sz = value));
        this.sz.tooltip(IKey.lang("blockbuster.gui.model_block.z"));

        this.sx.flex().set(0, 20, 80, 20).relative(this.area).x(1, -90);
        this.sy.flex().set(0, 25, 80, 20).relative(this.sx.resizer());
        this.sz.flex().set(0, 25, 80, 20).relative(this.sy.resizer());

        /* Rotation */
        this.subChildren.add(this.rx = new GuiTrackpadElement(mc, (value) -> this.model.rx = value));
        this.rx.tooltip(IKey.lang("blockbuster.gui.model_block.x"));
        this.subChildren.add(this.ry = new GuiTrackpadElement(mc, (value) -> this.model.ry = value));
        this.ry.tooltip(IKey.lang("blockbuster.gui.model_block.y"));
        this.subChildren.add(this.rz = new GuiTrackpadElement(mc, (value) -> this.model.rz = value));
        this.rz.tooltip(IKey.lang("blockbuster.gui.model_block.z"));

        this.rx.flex().set(0, 45, 80, 20).relative(this.sz.resizer());
        this.ry.flex().set(0, 25, 80, 20).relative(this.rx.resizer());
        this.rz.flex().set(0, 25, 80, 20).relative(this.ry.resizer());

        /* Buttons */
        this.subChildren.add(element = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.pick"), (button) ->
        {
            ClientProxy.panels.morphs.flex().reset().relative(this.area).wh(1F, 1F);
            ClientProxy.panels.morphs.resize();
            this.add(ClientProxy.panels.morphs);
        }));
        this.subChildren.add(this.one = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.one"), false, (button) -> this.toggleOne()));
        this.one.tooltip(IKey.lang("blockbuster.gui.model_block.one_tooltip"), Direction.LEFT);
        this.subChildren.add(this.shadow = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.shadow"), false, (button) -> this.model.shadow = button.isToggled()));
        this.subChildren.add(this.global = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.global"), false, (button) -> this.model.global = button.isToggled()));
        this.global.tooltip(IKey.lang("blockbuster.gui.model_block.global_tooltip"), Direction.BOTTOM);
        this.subChildren.add(this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.enabled"), false, (button) -> this.model.enabled = button.isToggled()));
        this.enabled.tooltip(IKey.lang("blockbuster.gui.model_block.enabled_tooltip"), Direction.BOTTOM);

        element.flex().set(0, 10, 90, 20).relative(this.area).x(0.5F, -45);
        this.shadow.flex().set(100, 4, 90, 11).relative(element.resizer());
        this.global.flex().set(0, 16, 90, 11).relative(this.shadow.resizer());
        this.enabled.flex().set(0, 16, 90, 11).relative(this.global.resizer());
        this.one.flex().set(50, -14, 30, 11).relative(this.sx.resizer());

        GuiElement second = element;

        this.subChildren.add(element = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.model_block.look"), (button) ->
        {
            this.model.ry = 180 - this.mc.player.rotationYaw;
            this.fillData();
        }));

        element.flex().relative(second.resizer()).set(0, 25, 90, 20);

        this.subChildren.add(this.order = new GuiCirculateElement(mc, (b) -> this.model.order = RotationOrder.values()[this.order.getValue()]));
        this.order.addLabel(IKey.str("ZYX"));
        this.order.addLabel(IKey.str("XYZ"));
        this.order.flex().set(40, -22, 40, 20).relative(this.rx.resizer());

        /* Model blocks */
        this.list = new GuiModelBlockList(mc, IKey.lang("blockbuster.gui.model_block.title"), (tile) -> this.setModelBlock(tile.get(0)));
        this.list.flex().relative(this.flex()).set(0, 0, 120, 0).h(1F).x(1F, -120);
        this.add(this.list);

        element = new GuiIconElement(mc, MMIcons.BLOCK, (b) -> this.list.toggleVisible());
        element.flex().set(0, 2, 24, 24).relative(this).x(1F, -28);

        this.add(element);

        /* Inventory */
        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.inventory.setVisible(false);
        this.subChildren.add(this.inventory);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlotElement(mc, i, this.inventory);
            this.slots[i].flex().relative(this.area).wh(24, 24).anchor(0.5F, 0.5F);
            this.subChildren.add(this.slots[i]);
        }

        this.slots[0].flex().x(0.5F - 0.125F).y(0.5F, -15);
        this.slots[1].flex().x(0.5F - 0.125F).y(0.5F, 15);
        this.slots[2].flex().x(0.5F + 0.125F).y(0.5F, 45);
        this.slots[3].flex().x(0.5F + 0.125F).y(0.5F, 15);
        this.slots[4].flex().x(0.5F + 0.125F).y(0.5F, -15);
        this.slots[5].flex().x(0.5F + 0.125F).y(0.5F, -45);

        this.inventory.flex().relative(this.area).x(0.5F).y(1F, -10).wh(10 * 20, 5 * 20).anchor(0.5F, 1F);
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    private void pickItem(ItemStack stack)
    {
        if (this.inventory.linked != null)
        {
            GuiSlotElement slot = this.inventory.linked;

            slot.stack = stack == null ? null : stack.copy();

            this.model.slots[slot.slot] = slot.stack;
            this.model.updateEntity();

            this.inventory.linked = null;
            this.inventory.setVisible(false);
        }
    }

    @Override
    public void appear()
    {
        super.appear();

        ClientProxy.panels.morphs.callback = (morph) ->
        {
            if (this.model != null)
            {
                this.model.morph = morph;
            }
        };

        this.setModelBlock(this.model);
    }

    @Override
    public void open()
    {
        this.updateList();

        /* Resetting the current model block, if it was removed from the world */
        if (this.model != null && this.mc.world.getTileEntity(this.model.getPos()) == null)
        {
            this.setModelBlock(null);
        }
    }

    @Override
    public void close()
    {
        if (this.model != null)
        {
            ClientProxy.panels.morphs.finish();
            Dispatcher.sendToServer(new PacketModifyModelBlock(this.model.getPos(), this.model));
        }
    }

    public GuiModelBlockPanel openModelBlock(TileEntityModel model)
    {
        tryAddingBlock(model.getPos());

        this.updateList();
        this.list.setVisible(false);

        return this.setModelBlock(model);
    }

    public GuiModelBlockPanel setModelBlock(TileEntityModel model)
    {
        if (this.model == model && model != null)
        {
            this.close();
        }

        this.subChildren.setVisible(model != null);
        this.model = model;
        this.fillData();

        return this;
    }

    @Override
    public void resize()
    {
        if (GuiBase.getCurrent().screen.height >= 400)
        {
            this.sx.flex().relative(this.z.resizer()).set(0, 0, 80, 20).x(0).y(45);
            this.yaw.flex().y(0.5F, -175);
        }
        else
        {
            this.sx.flex().relative(this.area).set(0, 20, 80, 20).x(1, -90).y(0.5F, -80);
            this.yaw.flex().y(0.5F, -80);
        }

        super.resize();
    }

    private void updateList()
    {
        this.list.clear();

        for (BlockPos pos : lastBlocks)
        {
            this.list.addBlock(pos);
        }
    }

    private void fillData()
    {
        if (this.model != null)
        {
            ClientProxy.panels.morphs.setSelected(this.model.morph);

            this.yaw.setValue(this.model.rotateYawHead);
            this.pitch.setValue(this.model.rotatePitch);
            this.body.setValue(this.model.rotateBody);

            this.x.setValue(this.model.x);
            this.y.setValue(this.model.y);
            this.z.setValue(this.model.z);

            this.rx.setValue(this.model.rx);
            this.ry.setValue(this.model.ry);
            this.rz.setValue(this.model.rz);

            this.sx.setValue(this.model.sx);
            this.sy.setValue(this.model.sy);
            this.sz.setValue(this.model.sz);

            this.one.toggled(this.model.one);
            this.order.setValue(this.model.order.ordinal());
            this.shadow.toggled(this.model.shadow);
            this.global.toggled(this.model.global);
            this.enabled.toggled(this.model.enabled);

            this.toggleOne();

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].stack = this.model.slots[i];
            }
        }
    }

    private void toggleOne()
    {
        boolean checked = this.one.isToggled();

        this.model.one = checked;
        this.sy.setVisible(!checked);
        this.sz.setVisible(!checked);
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.model != null && !ClientProxy.panels.morphs.hasParent())
        {
            AbstractMorph morph = ClientProxy.panels.morphs.getSelected();

            if (morph != null)
            {
                int x = this.area.mx();
                int y = this.area.ey() - 10;

                int w = Math.max(this.font.getStringWidth(morph.name), this.font.getStringWidth(morph.getDisplayName()));

                Gui.drawRect(x - w / 2 - 3, y - 20, x + w / 2 + 3, y, 0x88000000);

                this.drawCenteredString(this.font, morph.getDisplayName(), x, y - this.font.FONT_HEIGHT * 2, 0xffffff);
                this.drawCenteredString(this.font, morph.name, x, y - this.font.FONT_HEIGHT, 0xcccccc);
            }
        }

        if (this.subChildren.isVisible())
        {
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.entity"), this.yaw.area.x + 2, this.yaw.area.y - 12, 0xffffff);
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.translate"), this.x.area.x + 2, this.x.area.y - 12, 0xffffff);
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x + 2, this.rx.area.y - 12, 0xffffff);
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x + 2, this.sx.area.y - 12, 0xffffff);
        }
        else if (this.model == null)
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.model_block.not_selected"), this.area.mx(), this.area.my() - 6, 0xffffff);
        }

        super.draw(context);
    }
}