package mchorse.blockbuster.client.gui.dashboard.panels.model_block;

import java.util.ArrayList;
import java.util.List;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.client.gui.dashboard.GuiSidebarButton;
import mchorse.blockbuster.client.gui.dashboard.panels.GuiDashboardPanel;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.framework.GuiTooltip.TooltipDirection;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiElements;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.widgets.GuiInventory;
import mchorse.mclib.client.gui.widgets.GuiInventory.IInventoryPicker;
import mchorse.mclib.client.gui.widgets.GuiSlot;
import mchorse.mclib.client.gui.widgets.buttons.GuiCirculate;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelBlockPanel extends GuiDashboardPanel implements IInventoryPicker
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

    private GuiButtonElement<GuiCheckBox> one;
    private GuiButtonElement<GuiCirculate> order;
    private GuiButtonElement<GuiCheckBox> shadow;
    private GuiButtonElement<GuiCheckBox> global;
    private GuiButtonElement<GuiCheckBox> enabled;

    private GuiModelBlockList list;
    private GuiElements<IGuiElement> subChildren;

    private GuiInventory inventory;
    private GuiSlot[] slots = new GuiSlot[6];
    private GuiSlot active;

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

        EntityPlayer player = Minecraft.getMinecraft().player;
        GuiElement element = null;

        this.subChildren = new GuiElements<>();
        this.subChildren.setVisible(false);
        this.children.add(this.subChildren);

        /* Entity angles */
        this.subChildren.add(this.yaw = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.yaw"), (value) -> this.model.rotateYawHead = value));
        this.subChildren.add(this.pitch = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.pitch"), (value) -> this.model.rotatePitch = value));
        this.subChildren.add(this.body = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.body"), (value) -> this.model.rotateBody = value));

        this.yaw.resizer().set(10, 20, 80, 20).parent(this.area);
        this.pitch.resizer().set(0, 25, 80, 20).relative(this.yaw.resizer);
        this.body.resizer().set(0, 25, 80, 20).relative(this.pitch.resizer);

        /* Translation */
        this.subChildren.add(this.x = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.model.x = value));
        this.subChildren.add(this.y = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.model.y = value));
        this.subChildren.add(this.z = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.model.z = value));

        this.x.resizer().set(0, 45, 80, 20).relative(this.body.resizer);
        this.y.resizer().set(0, 25, 80, 20).relative(this.x.resizer);
        this.z.resizer().set(0, 25, 80, 20).relative(this.y.resizer);

        /* Scale */
        this.subChildren.add(this.sx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.model.sx = value));
        this.subChildren.add(this.sy = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.model.sy = value));
        this.subChildren.add(this.sz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.model.sz = value));

        this.sx.resizer().set(0, 20, 80, 20).parent(this.area).x(1, -90);
        this.sy.resizer().set(0, 25, 80, 20).relative(this.sx.resizer);
        this.sz.resizer().set(0, 25, 80, 20).relative(this.sy.resizer);

        /* Rotation */
        this.subChildren.add(this.rx = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.x"), (value) -> this.model.rx = value));
        this.subChildren.add(this.ry = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.y"), (value) -> this.model.ry = value));
        this.subChildren.add(this.rz = new GuiTrackpadElement(mc, I18n.format("blockbuster.gui.model_block.z"), (value) -> this.model.rz = value));

        this.rx.resizer().set(0, 45, 80, 20).relative(this.sz.resizer);
        this.ry.resizer().set(0, 25, 80, 20).relative(this.rx.resizer);
        this.rz.resizer().set(0, 25, 80, 20).relative(this.ry.resizer);

        /* Buttons */
        this.subChildren.add(element = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.pick"), (button) ->
        {
            this.children.unfocus();
            this.dashboard.morphs.setVisible(true);
        }));
        this.subChildren.add(this.one = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.model_block.one"), false, (button) -> this.toggleOne()).tooltip(I18n.format("blockbuster.gui.model_block.one_tooltip"), TooltipDirection.LEFT));
        this.subChildren.add(this.shadow = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.model_block.shadow"), false, (button) -> this.model.shadow = button.button.isChecked()));
        this.subChildren.add(this.global = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.model_block.global"), false, (button) -> this.model.global = button.button.isChecked()).tooltip(I18n.format("blockbuster.gui.model_block.global_tooltip"), TooltipDirection.BOTTOM));
        this.subChildren.add(this.enabled = GuiButtonElement.checkbox(mc, I18n.format("blockbuster.gui.model_block.enabled"), false, (button) -> this.model.enabled = button.button.isChecked()).tooltip(I18n.format("blockbuster.gui.model_block.enabled_tooltip"), TooltipDirection.BOTTOM));

        element.resizer().set(0, 10, 70, 20).parent(this.area).x(0.5F, -35);
        this.shadow.resizer().set(80, 4, this.shadow.button.width, 11).relative(element.resizer);
        this.global.resizer().set(0, 16, this.global.button.width, 11).relative(this.shadow.resizer());
        this.enabled.resizer().set(0, 16, this.enabled.button.width, 11).relative(this.global.resizer());
        this.one.resizer().set(50, -14, 30, 11).relative(this.sx.resizer);

        GuiCirculate button = new GuiCirculate(0, 0, 0, 0, 0);
        button.addLabel("ZYX");
        button.addLabel("XYZ");

        this.subChildren.add(this.order = new GuiButtonElement<GuiCirculate>(mc, button, (b) -> this.model.order = RotationOrder.values()[b.button.getValue()]));
        this.order.resizer().set(40, -22, 40, 20).relative(this.rx.resizer);

        /* Model blocks */
        this.children.add(this.list = new GuiModelBlockList(mc, I18n.format("blockbuster.gui.model_block.title"), (tile) -> this.setModelBlock(tile)));
        this.list.resizer().set(0, 0, 120, 0).parent(this.area).h(1F, 0).x(1F, -120);

        this.children.add(element = new GuiButtonElement<GuiSidebarButton>(mc, new GuiSidebarButton(0, 0, 0, new ItemStack(Blockbuster.modelBlock)), (b) -> this.list.toggleVisible()));
        element.resizer().set(0, 2, 24, 24).parent(this.area).x(1F, -28);

        /* Inventory */
        this.inventory = new GuiInventory(this, player);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlot(i);
        }

        this.children.add(this.dashboard.morphDelegate);
    }

    @Override
    public void pickItem(GuiInventory inventory, ItemStack stack)
    {
        if (this.active != null)
        {
            this.active.stack = stack == null ? null : stack.copy();
            this.model.slots[this.active.slot] = this.active.stack;
            this.model.updateEntity();
            this.inventory.visible = false;
        }
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public void appear()
    {
        this.dashboard.morphs.callback = (morph) ->
        {
            if (this.model != null)
            {
                this.model.morph = morph;
            }
        };
        this.dashboard.morphDelegate.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
        this.dashboard.morphDelegate.resize(this.dashboard.width, this.dashboard.height);

        this.setModelBlock(this.model);
    }

    @Override
    public void open()
    {
        this.updateList();
        this.inventory.player = this.mc.player;

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
            /* Update model's morph */
            if (this.model != null)
            {
                this.dashboard.morphs.finish();
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

        this.subChildren.setVisible(model != null);
        this.model = model;
        this.fillData();

        return this;
    }

    @Override
    public void resize(int width, int height)
    {
        if (height >= 400)
        {
            this.sx.resizer().relative(this.z.resizer).set(0, 0, 80, 20).x(0).y(45);
            this.yaw.resizer().y(0.5F, -175);
        }
        else
        {
            this.sx.resizer().parent(this.area).set(0, 20, 80, 20).x(1, -90).y(0.5F, -80);
            this.yaw.resizer().y(0.5F, -80);
        }

        super.resize(width, height);

        this.slots[0].update(this.area.getX(0.5F) - this.area.w / 8 - 20, this.area.getY(0.5F) - 25);
        this.slots[1].update(this.area.getX(0.5F) - this.area.w / 8 - 20, this.area.getY(0.5F) + 5);

        this.slots[2].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) + 35);
        this.slots[3].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) + 5);
        this.slots[4].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) - 25);
        this.slots[5].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) - 55);
        this.inventory.update(this.area.getX(0.5F), this.area.getY(1) - 50);
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
            this.dashboard.morphs.setSelected(this.model.morph);

            this.yaw.trackpad.setValue(this.model.rotateYawHead);
            this.pitch.trackpad.setValue(this.model.rotatePitch);
            this.body.trackpad.setValue(this.model.rotateBody);

            this.x.trackpad.setValue(this.model.x);
            this.y.trackpad.setValue(this.model.y);
            this.z.trackpad.setValue(this.model.z);

            this.rx.trackpad.setValue(this.model.rx);
            this.ry.trackpad.setValue(this.model.ry);
            this.rz.trackpad.setValue(this.model.rz);

            this.sx.trackpad.setValue(this.model.sx);
            this.sy.trackpad.setValue(this.model.sy);
            this.sz.trackpad.setValue(this.model.sz);

            this.one.button.setIsChecked(this.model.one);
            this.order.button.setValue(this.model.order.ordinal());
            this.shadow.button.setIsChecked(this.model.shadow);
            this.global.button.setIsChecked(this.model.global);
            this.enabled.button.setIsChecked(this.model.enabled);

            this.toggleOne();

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].stack = this.model.slots[i];
            }
        }
    }

    private void toggleOne()
    {
        boolean checked = this.one.button.isChecked();

        this.model.one = checked;
        this.sy.setVisible(!checked);
        this.sz.setVisible(!checked);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        this.inventory.mouseClicked(mouseX, mouseY, mouseButton);
        this.active = null;

        for (GuiSlot slot : this.slots)
        {
            if (slot.area.isInside(mouseX, mouseY))
            {
                this.active = slot;
                this.inventory.visible = true;
            }
        }

        return this.area.isInside(mouseX, mouseY);
    }

    @Override
    public void draw(GuiTooltip tooltip, int mouseX, int mouseY, float partialTicks)
    {
        if (this.model != null && !this.dashboard.morphs.isVisible())
        {
            MorphCell cell = this.dashboard.morphs.getSelected();

            if (cell != null)
            {
                int x = this.area.getX(0.5F);
                int y = this.area.getY(1) - 10;

                int w = Math.max(this.font.getStringWidth(cell.current().name), this.font.getStringWidth(cell.current().morph.name));

                Gui.drawRect(x - w / 2 - 3, y - 20, x + w / 2 + 3, y, 0x88000000);

                this.drawCenteredString(this.font, cell.current().name, x, y - this.font.FONT_HEIGHT * 2, 0xffffff);
                this.drawCenteredString(this.font, cell.current().morph.name, x, y - this.font.FONT_HEIGHT, 0xcccccc);
            }
        }

        if (this.subChildren.isVisible())
        {
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.entity"), this.yaw.area.x + 2, this.yaw.area.y - 12, 0xffffff);
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.translate"), this.x.area.x + 2, this.x.area.y - 12, 0xffffff);
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x + 2, this.rx.area.y - 12, 0xffffff);
            this.drawString(this.font, I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x + 2, this.sx.area.y - 12, 0xffffff);

            for (GuiSlot slot : this.slots)
            {
                slot.draw(mouseX, mouseY, partialTicks);
            }

            if (this.active != null)
            {
                Area a = this.active.area;

                Gui.drawRect(a.x, a.y, a.x + a.w, a.y + a.h, 0x880088ff);
            }

            this.inventory.draw(mouseX, mouseY, partialTicks);
        }
        else if (this.model == null)
        {
            this.drawCenteredString(this.font, I18n.format("blockbuster.gui.model_block.not_selected"), this.area.getX(0.5F), this.area.getY(0.5F) - 6, 0xffffff);
        }

        super.draw(tooltip, mouseX, mouseY, partialTicks);
    }
}