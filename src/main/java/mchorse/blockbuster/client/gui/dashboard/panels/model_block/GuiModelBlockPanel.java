package mchorse.blockbuster.client.gui.dashboard.panels.model_block;

import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTransformations;
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

    private GuiModelBlockTransformations trans;

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

        this.subChildren = new GuiElement(mc);
        this.subChildren.setVisible(false);
        this.add(this.subChildren);

        /* Transformations */
        this.trans = new GuiModelBlockTransformations(mc);
        this.trans.flex().relative(this).x(0.5F, 42).y(1F, -10).wh(250, 70).anchor(0.5F, 1F);

        this.subChildren.add(this.trans);

        /* Entity angles */
        this.subChildren.add(this.yaw = new GuiTrackpadElement(mc, (value) -> this.model.rotateYawHead = value));
        this.yaw.tooltip(IKey.lang("blockbuster.gui.model_block.yaw"));
        this.subChildren.add(this.pitch = new GuiTrackpadElement(mc, (value) -> this.model.rotatePitch = value));
        this.pitch.tooltip(IKey.lang("blockbuster.gui.model_block.pitch"));
        this.subChildren.add(this.body = new GuiTrackpadElement(mc, (value) -> this.model.rotateBody = value));
        this.body.tooltip(IKey.lang("blockbuster.gui.model_block.body"));

        this.yaw.flex().set(-85, 0, 80, 20).relative(this.trans);
        this.pitch.flex().set(0, 25, 80, 20).relative(this.yaw.resizer());
        this.body.flex().set(0, 25, 80, 20).relative(this.pitch.resizer());

        this.subChildren.add(this.order = new GuiCirculateElement(mc, (b) -> this.model.order = RotationOrder.values()[this.order.getValue()]));
        this.order.addLabel(IKey.str("ZYX"));
        this.order.addLabel(IKey.str("XYZ"));
        this.order.flex().relative(this.trans.rx).set(40, -22, 40, 20);

        /* Buttons */
        GuiElement column = new GuiElement(mc);

        column.flex().relative(this).w(120).column(5).vertical().stretch().height(20).padding(10);

        GuiButtonElement pick = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.pick"), (button) ->
        {
            ClientProxy.panels.morphs.flex().reset().relative(this.area).wh(1F, 1F);
            ClientProxy.panels.morphs.resize();
            ClientProxy.panels.morphs.setSelected(this.model.morph);
            this.add(ClientProxy.panels.morphs);
        });

        GuiButtonElement look = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.model_block.look"), (button) ->
        {
            this.model.ry = 180 - this.mc.player.rotationYaw;
            this.fillData();
        });

        this.shadow = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.shadow"), false, (button) -> this.model.shadow = button.isToggled());
        this.shadow.flex().h(14);
        this.global = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.global"), false, (button) -> this.model.global = button.isToggled());
        this.global.tooltip(IKey.lang("blockbuster.gui.model_block.global_tooltip"), Direction.BOTTOM);
        this.global.flex().h(14);
        this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.enabled"), false, (button) -> this.model.enabled = button.isToggled());
        this.enabled.tooltip(IKey.lang("blockbuster.gui.model_block.enabled_tooltip"), Direction.BOTTOM);
        this.enabled.flex().h(14);

        column.add(pick, look, this.shadow, this.global, this.enabled);
        this.subChildren.add(column);

        /* Model blocks */
        this.list = new GuiModelBlockList(mc, IKey.lang("blockbuster.gui.model_block.title"), (tile) -> this.setModelBlock(tile.get(0)));
        this.list.flex().relative(this.flex()).set(0, 0, 120, 0).h(1F).x(1F, -120);
        this.add(this.list);

        GuiIconElement toggle = new GuiIconElement(mc, MMIcons.BLOCK, (b) -> this.list.toggleVisible());
        toggle.flex().set(0, 2, 24, 24).relative(this).x(1F, -28);

        this.add(toggle);

        /* Inventory */
        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.inventory.setVisible(false);

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

        this.inventory.flex().relative(this.area).xy(0.5F, 0.5F).wh(10 * 20, 5 * 20).anchor(0.5F, 0.5F);
        this.subChildren.add(this.inventory);
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
            if (ClientProxy.panels.morphs.hasParent())
            {
                ClientProxy.panels.morphs.finish();
            }

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

        this.list.setCurrent(model);
        this.subChildren.setVisible(model != null);
        this.model = model;
        this.fillData();

        return this;
    }

    private void updateList()
    {
        this.list.clear();

        for (BlockPos pos : lastBlocks)
        {
            this.list.addBlock(pos);
        }

        this.list.setCurrent(this.model);
    }

    private void fillData()
    {
        if (this.model != null)
        {
            this.yaw.setValue(this.model.rotateYawHead);
            this.pitch.setValue(this.model.rotatePitch);
            this.body.setValue(this.model.rotateBody);

            this.trans.set(this.model);

            this.order.setValue(this.model.order.ordinal());
            this.shadow.toggled(this.model.shadow);
            this.global.toggled(this.model.global);
            this.enabled.toggled(this.model.enabled);

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].stack = this.model.slots[i];
            }
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.model != null)
        {
            AbstractMorph morph = this.model.morph;

            if (morph != null)
            {
                int x = this.area.mx();
                int y = this.area.y + 30;

                int w = Math.max(this.font.getStringWidth(morph.name), this.font.getStringWidth(morph.getDisplayName()));

                Gui.drawRect(x - w / 2 - 3, y - 20, x + w / 2 + 3, y, 0x88000000);

                this.drawCenteredString(this.font, morph.getDisplayName(), x, y - this.font.FONT_HEIGHT * 2, 0xffffff);
                this.drawCenteredString(this.font, morph.name, x, y - this.font.FONT_HEIGHT, 0xcccccc);
            }
        }

        if (this.subChildren.isVisible())
        {
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.entity"), this.yaw.area.x + 2, this.yaw.area.y - 12, 0xffffff);
        }
        else if (this.model == null)
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.model_block.not_selected"), this.area.mx(), this.area.my() - 6, 0xffffff);
        }

        super.draw(context);
    }

    public static class GuiModelBlockTransformations extends GuiTransformations
    {
        public TileEntityModel model;

        public GuiModelBlockTransformations(Minecraft mc)
        {
            super(mc);

            this.sx.callback = (value) -> this.setS(value, this.sy.value, this.sz.value);
            this.one.callback = (toggle) ->
            {
                this.model.one = toggle.isToggled();

                this.sy.setVisible(!this.model.one);
                this.sz.setVisible(!this.model.one);
            };
        }

        public void set(TileEntityModel model)
        {
            this.model = model;

            if (model != null)
            {
                this.fillT(model.x, model.y, model.z);
                this.fillS(model.sx, model.sy, model.sz);
                this.fillR(model.rx, model.ry, model.rz);
                this.one.toggled(model.one);
            }
        }

        @Override
        public void setT(float x, float y, float z)
        {
            this.model.x = x;
            this.model.y = y;
            this.model.z = z;
        }

        @Override
        public void setS(float x, float y, float z)
        {
            this.model.sx = x;
            this.model.sy = y;
            this.model.sz = z;
        }

        @Override
        public void setR(float x, float y, float z)
        {
            this.model.rx = x;
            this.model.ry = y;
            this.model.rz = z;
        }
    }
}