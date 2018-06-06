package mchorse.blockbuster.client.gui.dashboard.panels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.GuiSidebarButton;
import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup;
import mchorse.blockbuster.client.gui.framework.elements.GuiButtonElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElement;
import mchorse.blockbuster.client.gui.framework.elements.GuiElements;
import mchorse.blockbuster.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.blockbuster.client.gui.framework.elements.IGuiLegacy;
import mchorse.blockbuster.client.gui.utils.Area;
import mchorse.blockbuster.client.gui.utils.Resizer.Measure;
import mchorse.blockbuster.client.gui.widgets.GuiInventory;
import mchorse.blockbuster.client.gui.widgets.GuiInventory.IInventoryPicker;
import mchorse.blockbuster.client.gui.widgets.GuiSlot;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiCirculate;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel.RotationOrder;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import mchorse.metamorph.client.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiModelPanel extends GuiDashboardPanel implements IGuiLegacy, IInventoryPicker
{
    public static final List<BlockPos> lastBlocks = new ArrayList<BlockPos>();

    private TileEntityModel model;
    private TileEntityModel temp = new TileEntityModel();

    private GuiMorphsPopup morphs;

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

    private GuiModelBlockList list;
    private GuiElements subChildren;

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

    public GuiModelPanel(Minecraft mc)
    {
        super(mc);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        GuiElement element = null;

        this.morphs = new GuiMorphsPopup(6, null, Morphing.get(player));
        this.subChildren = new GuiElements();
        this.subChildren.setVisible(false);
        this.children.add(this.subChildren);

        /* Entity angles */
        this.subChildren.add(this.yaw = new GuiTrackpadElement(mc, "Yaw", (value) -> this.model.rotateYawHead = value));
        this.subChildren.add(this.pitch = new GuiTrackpadElement(mc, "Pitch", (value) -> this.model.rotatePitch = value));
        this.subChildren.add(this.body = new GuiTrackpadElement(mc, "Body", (value) -> this.model.rotateBody = value));

        this.yaw.resizer().set(10, 20, 80, 20).parent(this.area);
        this.pitch.resizer().set(0, 25, 80, 20).relative(this.yaw.resizer);
        this.body.resizer().set(0, 25, 80, 20).relative(this.pitch.resizer);

        /* Rotation */
        this.subChildren.add(this.rx = new GuiTrackpadElement(mc, "X", (value) -> this.model.rx = value));
        this.subChildren.add(this.ry = new GuiTrackpadElement(mc, "Y", (value) -> this.model.ry = value));
        this.subChildren.add(this.rz = new GuiTrackpadElement(mc, "Z", (value) -> this.model.rz = value));

        this.rx.resizer().set(0, 45, 80, 20).relative(this.body.resizer);
        this.ry.resizer().set(0, 25, 80, 20).relative(this.rx.resizer);
        this.rz.resizer().set(0, 25, 80, 20).relative(this.ry.resizer);

        /* Translation */
        this.subChildren.add(this.x = new GuiTrackpadElement(mc, "X", (value) -> this.model.x = value));
        this.subChildren.add(this.y = new GuiTrackpadElement(mc, "Y", (value) -> this.model.y = value));
        this.subChildren.add(this.z = new GuiTrackpadElement(mc, "Z", (value) -> this.model.z = value));

        this.x.resizer().set(0, 20, 80, 20).parent(this.area).x.set(1, Measure.RELATIVE, -90);
        this.y.resizer().set(0, 25, 80, 20).relative(this.x.resizer);
        this.z.resizer().set(0, 25, 80, 20).relative(this.y.resizer);

        /* Scale */
        this.subChildren.add(this.sx = new GuiTrackpadElement(mc, "X", (value) -> this.model.sx = value));
        this.subChildren.add(this.sy = new GuiTrackpadElement(mc, "Y", (value) -> this.model.sy = value));
        this.subChildren.add(this.sz = new GuiTrackpadElement(mc, "Z", (value) -> this.model.sz = value));

        this.sx.resizer().set(0, 45, 80, 20).relative(this.z.resizer);
        this.sy.resizer().set(0, 25, 80, 20).relative(this.sx.resizer);
        this.sz.resizer().set(0, 25, 80, 20).relative(this.sy.resizer);

        /* Buttons */
        this.subChildren.add(element = GuiButtonElement.button(mc, "Pick morph", (button) -> this.morphs.morphs.setHidden(false)));
        this.subChildren.add(this.one = GuiButtonElement.checkbox(mc, "One", false, (button) -> this.toggleOne()));

        element.resizer().set(0, 10, 70, 20).parent(this.area).x.set(0.5F, Measure.RELATIVE, -35);
        this.one.resizer().set(50, -14, 30, 11).relative(this.sx.resizer);

        GuiCirculate button = new GuiCirculate(0, 0, 0, 0, 0);
        button.addLabel("ZYX");
        button.addLabel("XYZ");

        this.subChildren.add(this.order = new GuiButtonElement<GuiCirculate>(mc, button, (b) -> this.model.order = RotationOrder.values()[b.button.getValue()]));
        this.order.resizer().set(40, -22, 40, 20).relative(this.rx.resizer);

        /* Model blocks */
        this.children.add(this.list = new GuiModelBlockList(mc, "Model blocks", (tile) -> this.setModelBlock(tile)));
        this.list.resizer().set(0, 0, 120, 0).parent(this.area).h.set(1, Measure.RELATIVE);
        this.list.resizer().x.set(1, Measure.RELATIVE, -120);

        this.children.add(element = new GuiButtonElement<GuiSidebarButton>(mc, new GuiSidebarButton(0, 0, 0, new ItemStack(Blockbuster.modelBlock)), (b) -> this.list.setVisible(!this.list.isVisible())));
        element.resizer().set(0, 2, 24, 24).parent(this.area).x.set(1, Measure.RELATIVE, -32);

        /* Inventory */
        this.inventory = new GuiInventory(this, player);

        for (int i = 0; i < this.slots.length; i++)
        {
            this.slots[i] = new GuiSlot(i);
        }
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
    public void open()
    {
        this.updateList();
    }

    @Override
    public void close()
    {
        if (this.model != null)
        {
            MorphCell morph = this.morphs.morphs.getSelected();

            /* Update model's morph */
            PacketModifyModelBlock packet = new PacketModifyModelBlock(this.model.getPos(), morph == null ? null : morph.current().morph);

            packet.setBody(this.yaw.trackpad.value, this.pitch.trackpad.value, this.body.trackpad.value);
            packet.setPos(this.x.trackpad.value, this.y.trackpad.value, this.z.trackpad.value);
            packet.setRot(this.rx.trackpad.value, this.ry.trackpad.value, this.rz.trackpad.value);
            packet.setScale(this.one.button.isChecked(), this.sx.trackpad.value, this.sy.trackpad.value, this.sz.trackpad.value);
            packet.setOrder(RotationOrder.values()[this.order.button.getValue()]);
            packet.setSlots(this.model.slots);

            Dispatcher.sendToServer(packet);
        }
    }

    public GuiModelPanel openModelBlock(TileEntityModel model)
    {
        tryAddingBlock(model.getPos());

        this.updateList();
        this.list.setVisible(false);

        return this.setModelBlock(model);
    }

    public GuiModelPanel setModelBlock(TileEntityModel model)
    {
        if (this.model == model)
        {
            return this;
        }

        if (this.model != null)
        {
            this.close();
        }

        this.subChildren.setVisible(true);
        this.model = model;
        this.temp.copyData(model);
        this.fillData();

        return this;
    }

    @Override
    public void resize(int width, int height)
    {
        if (height >= 400)
        {
            this.x.resizer().relative(this.rz.resizer).set(0, 0, 80, 20).x.set(0, Measure.PIXELS, 0);
            this.x.resizer().y.set(45, Measure.PIXELS, 0);
            this.yaw.resizer().y.set(0.5F, Measure.RELATIVE, -175);
        }
        else
        {
            this.x.resizer().parent(this.area).set(0, 20, 80, 20).x.set(1, Measure.RELATIVE, -90);
            this.x.resizer().y.set(0.5F, Measure.RELATIVE, -80);
            this.yaw.resizer().y.set(0.5F, Measure.RELATIVE, -80);
        }

        super.resize(width, height);

        this.slots[0].update(this.area.getX(0.5F) - this.area.w / 8 - 20, this.area.getY(0.5F) - 25);
        this.slots[1].update(this.area.getX(0.5F) - this.area.w / 8 - 20, this.area.getY(0.5F) + 5);

        this.slots[2].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) + 35);
        this.slots[3].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) + 5);
        this.slots[4].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) - 25);
        this.slots[5].update(this.area.getX(0.5F) + this.area.w / 8, this.area.getY(0.5F) - 55);
        this.inventory.update(this.area.getX(0.5F), this.area.getY(1) - 50);

        this.morphs.updateRect(this.area.x, this.area.y, this.area.w, this.area.h);
        this.morphs.setWorldAndResolution(this.mc, width, height);

        this.fillData();
    }

    private void updateList()
    {
        this.list.elements.clear();

        for (BlockPos pos : lastBlocks)
        {
            this.list.addBlock(pos);
        }
    }

    private void fillData()
    {
        if (this.model != null)
        {
            this.morphs.morphs.setSelected(this.model.morph);

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
    public boolean handleMouseInput(int mouseX, int mouseY) throws IOException
    {
        this.morphs.handleMouseInput();

        return !this.morphs.morphs.getHidden() && this.morphs.isInside(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

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
    public boolean handleKeyboardInput() throws IOException
    {
        this.morphs.handleKeyboardInput();

        return !this.morphs.morphs.getHidden();
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        MorphCell cell = this.morphs.morphs.getSelected();

        if (cell != null)
        {
            int x = this.area.getX(0.5F);
            int y = this.area.getY(0.65F);

            GuiScreen screen = this.mc.currentScreen;

            GuiUtils.scissor(this.area.x, this.area.y, this.area.w, this.area.h, screen.width, screen.height);
            cell.current().morph.renderOnScreen(this.mc.thePlayer, x, y, this.area.h / 4F, 1.0F);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        if (this.model != null)
        {
            this.model.morph = cell == null ? null : cell.current().morph;
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
        else
        {
            this.drawCenteredString(this.font, "Select a model block...", this.area.getX(0.5F), this.area.getY(0.5F) - 6, 0xffffff);
        }

        super.draw(mouseX, mouseY, partialTicks);

        this.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Model block list 
     */
    public static class GuiModelBlockList extends GuiBlockList<TileEntityModel>
    {
        public GuiModelBlockList(Minecraft mc, String title, Consumer<TileEntityModel> callback)
        {
            super(mc, title, callback);
        }

        @Override
        public boolean addBlock(BlockPos pos)
        {
            TileEntity tile = this.mc.theWorld.getTileEntity(pos);

            if (tile instanceof TileEntityModel)
            {
                this.elements.add((TileEntityModel) tile);

                this.scroll.setSize(this.elements.size());
                this.scroll.clamp();

                return true;
            }

            return false;
        }

        @Override
        public void render(int x, int y, TileEntityModel item, boolean hovered)
        {
            if (item.morph != null)
            {
                GuiScreen screen = this.mc.currentScreen;

                int mny = MathHelper.clamp_int(y, this.scroll.y, this.scroll.getY(1));
                int mxy = MathHelper.clamp_int(y + 20, this.scroll.y, this.scroll.getY(1));

                if (mxy - mny > 0)
                {
                    GuiUtils.scissor(x + this.scroll.w - 40, mny, 40, mxy - mny, screen.width, screen.height);
                    item.morph.renderOnScreen(this.mc.thePlayer, x + this.scroll.w - 20, y + 30, 20, 1);
                    GuiUtils.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, screen.width, screen.height);
                }
            }

            BlockPos pos = item.getPos();
            String label = String.format("(%s, %s, %s)", pos.getX(), pos.getY(), pos.getZ());

            this.font.drawStringWithShadow(label, x + 10, y + 6, hovered ? 16777120 : 0xffffff);
        }
    }
}