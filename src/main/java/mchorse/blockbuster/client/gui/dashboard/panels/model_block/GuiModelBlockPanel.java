package mchorse.blockbuster.client.gui.dashboard.panels.model_block;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.client.gui.GuiImmersiveEditor;
import mchorse.blockbuster.client.gui.GuiImmersiveMorphMenu;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.common.block.BlockModel;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
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
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.MatrixUtils.RotationOrder;
import mchorse.mclib.utils.MatrixUtils.Transformation;
import mchorse.mclib.utils.OpHelper;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class GuiModelBlockPanel extends GuiBlockbusterPanel
{
    public static final List<BlockPos> lastBlocks = new ArrayList<BlockPos>();

    private TileEntityModel model;

    private GuiTrackpadElement yaw;
    private GuiTrackpadElement pitch;
    private GuiTrackpadElement body;

    private GuiModelBlockTransformations trans;

    private GuiNestedEdit pickMorph;
    private GuiCirculateElement order;
    private GuiToggleElement shadow;
    private GuiToggleElement global;
    private GuiToggleElement enabled;
    private GuiToggleElement excludeResetPlayback;
    private GuiToggleElement renderLast;
    private GuiTrackpadElement lightLevel;

    private GuiModelBlockList list;
    private GuiElement subChildren;

    private GuiSlotElement[] slots = new GuiSlotElement[6];

    private Map<BlockPos, TileEntityModel> old = new HashMap<BlockPos, TileEntityModel>();

    private AbstractMorph morph;

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

        this.subChildren = new GuiElement(mc).noCulling();
        this.subChildren.setVisible(false);
        this.add(this.subChildren);

        /* Transformations */
        this.trans = new GuiModelBlockTransformations(mc);
        this.trans.flex().relative(this).x(0.5F, 42).y(1F, -10).wh(250, 70).anchor(0.5F, 1F);

        this.subChildren.add(this.trans);

        /* Entity angles */
        this.subChildren.add(this.yaw = new GuiTrackpadElement(mc, (value) -> this.model.getSettings().setRotateYawHead(value.floatValue())));
        this.yaw.tooltip(IKey.lang("blockbuster.gui.model_block.yaw"));
        this.subChildren.add(this.pitch = new GuiTrackpadElement(mc, (value) -> this.model.getSettings().setRotatePitch(value.floatValue())));
        this.pitch.tooltip(IKey.lang("blockbuster.gui.model_block.pitch"));
        this.subChildren.add(this.body = new GuiTrackpadElement(mc, (value) -> this.model.getSettings().setRotateBody(value.floatValue())));
        this.body.tooltip(IKey.lang("blockbuster.gui.model_block.body"));

        this.yaw.flex().set(-85, 0, 80, 20).relative(this.trans);
        this.pitch.flex().set(0, 25, 80, 20).relative(this.yaw.resizer());
        this.body.flex().set(0, 25, 80, 20).relative(this.pitch.resizer());

        this.subChildren.add(this.order = new GuiCirculateElement(mc, (b) ->
        {
            int index = 0;

            if (this.order.getValue() == 0)
            {
                index = 5;
            }

            this.model.getSettings().setOrder(RotationOrder.values()[index]);
        }));
        this.order.addLabel(IKey.str("ZYX"));
        this.order.addLabel(IKey.str("XYZ"));
        this.order.flex().relative(this.trans.rx).set(40, -22, 40, 20);

        /* Buttons */
        GuiElement column = new GuiElement(mc);

        column.flex().relative(this).w(120).column(5).vertical().stretch().height(20).padding(10);

        this.pickMorph = new GuiNestedEdit(mc, (editing) -> 
        {
            if (Blockbuster.immersiveModelBlock.get())
            {
                GuiImmersiveEditor editor = ClientProxy.panels.showImmersiveEditor(editing, this.model.morph.get());

                editor.morphs.updateCallback = this::updateMorphEditor;
                editor.morphs.beforeRender = this::beforeEditorRender;
                editor.morphs.afterRender = this::afterEditorRender;
                editor.onClose = this::afterEditorClose;

                /* Avoid update. */
                this.morph = this.model.morph.get();
                this.model.morph.setDirect(MorphUtils.copy(this.morph));
            }
            else
            {
                ClientProxy.panels.addMorphs(this, editing, this.model.morph.get());
            }
        });

        GuiButtonElement look = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.model_block.look"), (button) ->
        {
            this.model.getSettings().setRy(180 - this.mc.player.rotationYaw);
            this.fillData();
        });

        this.shadow = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.shadow"), false, (button) -> this.model.getSettings().setShadow(button.isToggled()));

        this.global = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.global"), false, (button) -> this.model.getSettings().setGlobal(button.isToggled()));
        this.global.tooltip(IKey.lang("blockbuster.gui.model_block.global_tooltip"), Direction.BOTTOM);

        this.enabled = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.enabled"), false, (button) -> this.model.getSettings().setEnabled(button.isToggled()));
        this.enabled.tooltip(IKey.lang("blockbuster.gui.model_block.enabled_tooltip"), Direction.BOTTOM);

        this.excludeResetPlayback = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.exlude_reset_playback"), false, (button) -> this.model.getSettings().setExcludeResetPlayback(button.isToggled()));
        this.excludeResetPlayback.tooltip(IKey.lang("blockbuster.gui.model_block.exlude_reset_playback_tooltip"));

        this.renderLast = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.model_block.render_last"), false, (button) -> this.model.getSettings().setRenderLast(button.isToggled()));
        this.renderLast.tooltip(IKey.lang("blockbuster.gui.model_block.enabled_tooltip"), Direction.BOTTOM);

        this.lightLevel = new GuiTrackpadElement(mc, (value) ->
        {
            this.model.getSettings().setLightValue(value.intValue());

            this.model.getWorld().setBlockState(this.model.getPos(), this.model.getWorld().getBlockState(this.model.getPos()).withProperty(BlockModel.LIGHT, this.model.getSettings().getLightValue()) , 2);
        });
        this.lightLevel.integer().limit(0, 15);
        this.lightLevel.tooltip(IKey.lang("blockbuster.gui.model_block.light_level_tooltip"));

        column.add(this.pickMorph, look, this.shadow, this.global, this.enabled, this.excludeResetPlayback, this.renderLast, Elements.label(IKey.lang("blockbuster.gui.model_block.light_level")), this.lightLevel);
        this.subChildren.add(column);

        /* Model blocks */
        this.list = new GuiModelBlockList(mc, IKey.lang("blockbuster.gui.model_block.title"), (tile) -> this.setModelBlock(tile.get(0)));
        this.list.flex().relative(this.flex()).set(0, 0, 120, 0).h(1F).x(1F, -120);
        this.add(this.list);

        GuiIconElement toggle = new GuiIconElement(mc, Icons.BLOCK, (b) -> this.list.toggleVisible());
        toggle.flex().set(0, 2, 24, 24).relative(this).x(1F, -28);

        this.add(toggle);

        /* Inventory */
        for (int i = 0; i < this.slots.length; i++)
        {
            final int slot = i;

            this.slots[i] = new GuiSlotElement(mc, i, (stack) -> this.pickItem(stack, slot));
            this.slots[i].flex().relative(this.area).anchor(0.5F, 0.5F);
            this.subChildren.add(this.slots[i]);
        }

        this.slots[0].flex().x(0.5F - 0.125F).y(0.5F, -15);
        this.slots[1].flex().x(0.5F - 0.125F).y(0.5F, 15);
        this.slots[2].flex().x(0.5F + 0.125F).y(0.5F, 45);
        this.slots[3].flex().x(0.5F + 0.125F).y(0.5F, 15);
        this.slots[4].flex().x(0.5F + 0.125F).y(0.5F, -15);
        this.slots[5].flex().x(0.5F + 0.125F).y(0.5F, -45);
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    private void pickItem(ItemStack stack, int slot)
    {
        this.model.getSettings().setSlot(stack, slot);
        this.model.updateEntity();
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.model != null)
        {
            if (Blockbuster.immersiveModelBlock.get())
            {
                this.morph = morph;
            }
            else
            {
                this.model.morph.setDirect(morph);
            }
        }

        this.pickMorph.setMorph(morph);
    }

    private void updateMorphEditor(GuiImmersiveMorphMenu menu)
    {
        if (this.model == null)
        {
            return;
        }

        TileEntity te = this.model.getWorld().getTileEntity(this.model.getPos());

        if (te != this.model)
        {
            if (te instanceof TileEntityModel)
            {
                this.setModelBlock((TileEntityModel) te);
            }
        }

        menu.target = this.model.entity;
    }

    private void beforeEditorRender(GuiContext context)
    {
        GlStateManager.pushMatrix();

        ClientProxy.modelRenderer.transform(this.model);
    }

    private void afterEditorRender(GuiContext context)
    {
        GlStateManager.popMatrix();
    }

    private void afterEditorClose(GuiImmersiveEditor editor)
    {
        this.model.morph.setDirect(this.morph);
    }

    @Override
    public void appear()
    {
        super.appear();

        ClientProxy.panels.picker(this::setMorph);
    }

    @Override
    public void open()
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

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
        this.save(null);
    }

    public void save(TileEntityModel model)
    {
        this.save(model, false);
    }

    public void save(TileEntityModel model, boolean force)
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

        if (!force)
        {
            if (this.model == null || this.model == model)
            {
                return;
            }

            if (model != null && this.model.getPos().equals(model.getPos()))
            {
                return;
            }
        }

        if (ClientProxy.panels.morphs.hasParent())
        {
            ClientProxy.panels.morphs.finish();
            ClientProxy.panels.morphs.removeFromParent();
        }

        Dispatcher.sendToServer(new PacketModifyModelBlock(this.model.getPos(), this.model));

        if (Blockbuster.modelBlockRestore.get())
        {
            this.old.put(this.model.getPos(), this.model);
        }
    }

    public GuiModelBlockPanel openModelBlock(TileEntityModel model)
    {
        if (model != null && Blockbuster.modelBlockRestore.get() && this.old.containsKey(model.getPos()))
        {
            TileEntityModel old = this.old.get(model.getPos());

            model.copyData(old, false);
        }

        tryAddingBlock(model.getPos());

        this.updateList();
        this.list.setVisible(false);

        return this.setModelBlock(model);
    }

    public GuiModelBlockPanel setModelBlock(TileEntityModel model)
    {
        this.save(model);
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
            this.yaw.setValue(this.model.getSettings().getRotateYawHead());
            this.pitch.setValue(this.model.getSettings().getRotatePitch());
            this.body.setValue(this.model.getSettings().getRotateBody());

            this.trans.set(this.model);

            this.pickMorph.setMorph(this.model.morph.get());

            int orderIndex = this.model.getSettings().getOrder().ordinal();

            if (orderIndex == 5)
            {
                this.order.setValue(0);
            }
            else if (orderIndex == 0)
            {
                this.order.setValue(1);
            }

            this.shadow.toggled(this.model.getSettings().isShadow());
            this.global.toggled(this.model.getSettings().isGlobal());
            this.enabled.toggled(this.model.getSettings().isEnabled());
            this.excludeResetPlayback.toggled(this.model.getSettings().isExcludeResetPlayback());
            this.renderLast.toggled(this.model.getSettings().isRenderLast());

            int lightValue = this.model.getWorld().getBlockState(this.model.getPos()).getValue(BlockModel.LIGHT);
            this.model.getSettings().setLightValue(lightValue);

            this.lightLevel.setValue(lightValue);

            for (int i = 0; i < this.slots.length; i++)
            {
                this.slots[i].setStack(this.model.getSettings().getSlots()[i]);
            }
        }
    }

    @Override
    public void draw(GuiContext context)
    {
        if (this.model != null)
        {
            AbstractMorph morph = this.model.morph.get();

            if (morph != null)
            {
                int x = this.area.mx();
                int y = this.area.y + 30;

                int w = Math.max(this.font.getStringWidth(morph.name), this.font.getStringWidth(morph.getDisplayName()));

                Gui.drawRect(x - w / 2 - 3, y - 20, x + w / 2 + 3, y, ColorUtils.HALF_BLACK);

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
                this.model.getSettings().setUniform(toggle.isToggled());

                this.sy.setVisible(!this.model.getSettings().isUniform());
                this.sz.setVisible(!this.model.getSettings().isUniform());
            };
        }

        public void set(TileEntityModel model)
        {
            this.model = model;

            if (model != null)
            {
                this.fillT(model.getSettings().getX(), model.getSettings().getY(), model.getSettings().getZ());
                this.fillS(model.getSettings().getSx(), model.getSettings().getSy(), model.getSettings().getSz());
                this.fillR(model.getSettings().getRx(), model.getSettings().getRy(), model.getSettings().getRz());
                this.one.toggled(model.getSettings().isUniform());
                this.updateScaleFields();
            }
        }

        @Override
        public void setT(double x, double y, double z)
        {
            this.model.getSettings().setX((float) x);
            this.model.getSettings().setY((float) y);
            this.model.getSettings().setZ((float) z);
        }

        @Override
        public void setS(double x, double y, double z)
        {
            this.model.getSettings().setSx((float) x);
            this.model.getSettings().setSy((float) y);
            this.model.getSettings().setSz((float) z);
        }

        @Override
        public void setR(double x, double y, double z)
        {
            this.model.getSettings().setRx((float) x);
            this.model.getSettings().setRy((float) y);
            this.model.getSettings().setRz((float) z);
        }

        @Override
        protected void prepareRotation(Matrix4f mat)
        {
            RotationOrder order = RotationOrder.valueOf(this.model.getSettings().getOrder().toString());
            float[] rot = new float[] {(float) this.rx.value, (float) this.ry.value, (float) this.rz.value};
            Matrix4f trans = new Matrix4f();
            trans.setIdentity();
            trans.set(Transformation.getRotationMatrix(order.thirdIndex, rot[order.thirdIndex]));
            mat.mul(trans);
            trans.set(Transformation.getRotationMatrix(order.secondIndex, rot[order.secondIndex]));
            mat.mul(trans);
            trans.set(Transformation.getRotationMatrix(order.firstIndex, rot[order.firstIndex]));
            mat.mul(trans);
        }

        @Override
        protected void postRotation(Transformation transform)
        {
            Vector3f result = transform.getRotation(RotationOrder.valueOf(this.model.getSettings().getOrder().toString()), new Vector3f((float) this.rx.value, (float) this.ry.value, (float) this.rz.value));
            this.rx.setValueAndNotify(result.x);
            this.ry.setValueAndNotify(result.y);
            this.rz.setValueAndNotify(result.z);
        }
    }
}