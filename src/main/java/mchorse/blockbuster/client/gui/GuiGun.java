package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.client.gui.dashboard.panels.model_editor.utils.GuiPoseTransformations;
import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.utils.NBTUtils;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiInventoryElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.MathUtils;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mchorse.metamorph.client.gui.creative.GuiNestedEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiGun extends GuiBase
{
    public GunProps props;
    public int index;

    public GuiPanelBase<GuiElement> panel;

    /* Morphs configuration */
    public GuiCreativeMorphsMenu morphs;

    /* Gun options */
    public GuiElement gunOptions;
    public GuiNestedEdit pickDefault;
    public GuiNestedEdit pickFiring;
    public GuiTextElement fireCommand;
    public GuiTrackpadElement delay;
    public GuiTrackpadElement projectiles;
    public GuiTrackpadElement scatterX;
    public GuiTrackpadElement scatterY;
    public GuiToggleElement launch;
    public GuiToggleElement useTarget;
    public GuiSlotElement ammoStack;

    public GuiInventoryElement inventory;

    /* Projectile options */
    public GuiElement projectileOptions;
    public GuiNestedEdit pickProjectile;
    public GuiTextElement tickCommand;
    public GuiTrackpadElement ticking;
    public GuiTrackpadElement lifeSpan;
    public GuiToggleElement yaw;
    public GuiToggleElement pitch;
    public GuiToggleElement sequencer;
    public GuiToggleElement random;
    public GuiTrackpadElement hitboxX;
    public GuiTrackpadElement hitboxY;
    public GuiTrackpadElement speed;
    public GuiTrackpadElement friction;
    public GuiTrackpadElement gravity;
    public GuiTrackpadElement fadeIn;
    public GuiTrackpadElement fadeOut;

    /* Impact options */
    public GuiElement impactOptions;
    public GuiNestedEdit pickImpact;
    public GuiTextElement impactCommand;
    public GuiTrackpadElement impactDelay;
    public GuiToggleElement vanish;
    public GuiToggleElement bounce;
    public GuiToggleElement sticks;
    public GuiTrackpadElement hits;
    public GuiTrackpadElement damage;
    public GuiTrackpadElement knockback;
    public GuiTrackpadElement bounceFactor;
    public GuiTrackpadElement vanishDelay;
    public GuiTrackpadElement penetration;

    /* Transforms */
    public GuiElement transformOptions;
    public GuiPoseTransformations gun;
    public GuiPoseTransformations projectile;
    public GuiMorphRenderer arms;
    public GuiProjectileModelRenderer bullet;

    public GuiGun(ItemStack stack)
    {
        TileEntityGunItemStackRenderer.GunEntry entry = TileEntityGunItemStackRenderer.models.get(stack);

        if (entry == null)
        {
            this.props = NBTUtils.getGunProps(stack);
        }
        else
        {
            this.props = entry.props;
        }

        Minecraft mc = Minecraft.getMinecraft();

        /* Initialization of GUI elements */
        this.gunOptions = new GuiElement(mc);
        this.projectileOptions = new GuiElement(mc);
        this.transformOptions = new GuiElement(mc);
        this.impactOptions = new GuiElement(mc);

        this.panel = new GuiGunPanels(mc, this);
        this.panel.setPanel(this.gunOptions);
        this.panel.registerPanel(this.gunOptions, IKey.lang("blockbuster.gui.gun.fire_props"), Icons.GEAR);
        this.panel.registerPanel(this.projectileOptions, IKey.lang("blockbuster.gui.gun.projectile_props"), BBIcons.BULLET);
        this.panel.registerPanel(this.impactOptions, IKey.lang("blockbuster.gui.gun.impact_props"), Icons.DOWNLOAD);
        this.panel.registerPanel(this.transformOptions, IKey.lang("blockbuster.gui.gun.transforms"), Icons.POSE);

        this.morphs = new GuiCreativeMorphsMenu(mc, this::setMorph);

        /* Gun options */
        Area area = this.gunOptions.area;

        this.inventory = new GuiInventoryElement(mc, this::pickItem);
        this.inventory.setVisible(false);

        this.pickDefault = new GuiNestedEdit(mc, (editing) -> this.openMorphs(1, editing));
        this.pickFiring = new GuiNestedEdit(mc, false, (editing) -> this.openMorphs(2, editing));
        this.fireCommand = new GuiTextElement(mc, 10000, (value) -> this.props.fireCommand = value);
        this.delay = new GuiTrackpadElement(mc, (value) -> this.props.delay = value.intValue());
        this.delay.limit(0, Integer.MAX_VALUE, true);
        this.projectiles = new GuiTrackpadElement(mc, (value) -> this.props.projectiles = value.intValue());
        this.projectiles.limit(0, Integer.MAX_VALUE, true);
        this.scatterX = new GuiTrackpadElement(mc, (value) -> this.props.scatterX = value.floatValue());
        this.scatterX.tooltip(IKey.lang("blockbuster.gui.gun.scatter_x"));
        this.scatterY = new GuiTrackpadElement(mc, (value) -> this.props.scatterY = value.floatValue());
        this.scatterY.tooltip(IKey.lang("blockbuster.gui.gun.scatter_y"));
        this.launch = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.launch"), false, (b) -> this.props.launch = b.isToggled());
        this.useTarget = new GuiToggleElement(mc, IKey.lang("metamorph.gui.body_parts.use_target"), false, (b) -> this.props.useTarget = b.isToggled());
        this.ammoStack = new GuiSlotElement(mc, 0, this.inventory);
        this.ammoStack.tooltip(IKey.lang("blockbuster.gui.gun.ammo_stack"));

        int firingOffset = 40;

        this.inventory.flex().relative(this.ammoStack.flex()).x(0.5F).y(-5).anchor(0.5F, 1F).bounds(this.root, 5);

        GuiElement scatterBar = new GuiElement(mc);

        scatterBar.flex().relative(area).set(0, 0, 0, 20).x(0.5F).y(1, -75).w(0.5F, -60).anchorX(0.5F).row(5);
        scatterBar.add(this.scatterX, this.scatterY);

        this.fireCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1F, -30);
        this.delay.flex().relative(scatterBar.resizer()).set(0, 0, 100, 20).x(-10).anchorX(1F);
        this.projectiles.flex().relative(scatterBar.resizer()).set(0, 0, 100, 20).x(1F, 10);
        this.pickDefault.flex().relative(this.delay.resizer()).w(1F).y(-5 - firingOffset);
        this.pickFiring.flex().relative(this.projectiles.resizer()).w(1F).y(-5 - firingOffset);
        this.ammoStack.flex().relative(this.pickFiring.resizer()).x(1F, 5).y(-2);

        GuiElement launchBar = new GuiElement(mc);

        launchBar.flex().relative(scatterBar.resizer()).y(-5 - firingOffset).w(1F).h(11).row(10);
        this.launch.flex().h(20);
        this.useTarget.flex().h(20);
        launchBar.add(this.launch, this.useTarget);

        this.gunOptions.add(scatterBar, launchBar, this.delay, this.projectiles, this.pickDefault, this.pickFiring, this.fireCommand, this.ammoStack, this.inventory);

        /* Projectile options */
        area = this.projectileOptions.area;

        this.pickProjectile = new GuiNestedEdit(mc, (editing) -> this.openMorphs(3, editing));
        this.tickCommand = new GuiTextElement(mc, 10000, (value) -> this.props.tickCommand = value);
        this.ticking = new GuiTrackpadElement(mc, (value) -> this.props.ticking = value.intValue());
        this.ticking.tooltip(IKey.lang("blockbuster.gui.gun.ticking"));
        this.ticking.limit(0, Integer.MAX_VALUE, true);
        this.lifeSpan = new GuiTrackpadElement(mc, (value) -> this.props.lifeSpan = value.intValue());
        this.lifeSpan.tooltip(IKey.lang("blockbuster.gui.gun.life_span"));
        this.lifeSpan.limit(0, Integer.MAX_VALUE, true);
        this.yaw = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.yaw"), false, (b) -> this.props.yaw = b.isToggled());
        this.pitch = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.pitch"), false, (b) -> this.props.pitch = b.isToggled());
        this.sequencer = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.props.sequencer = b.isToggled());
        this.random = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.random"), false, (b) -> this.props.random = b.isToggled());
        this.hitboxX = new GuiTrackpadElement(mc, (value) -> this.props.hitboxX = value.floatValue());
        this.hitboxX.tooltip(IKey.lang("blockbuster.gui.gun.hitbox_x"));
        this.hitboxY = new GuiTrackpadElement(mc, (value) -> this.props.hitboxY = value.floatValue());
        this.hitboxY.tooltip(IKey.lang("blockbuster.gui.gun.hitbox_y"));
        this.speed = new GuiTrackpadElement(mc, (value) -> this.props.speed = value.floatValue());
        this.speed.tooltip(IKey.lang("blockbuster.gui.gun.speed"));
        this.friction = new GuiTrackpadElement(mc, (value) -> this.props.friction = value.floatValue());
        this.friction.tooltip(IKey.lang("blockbuster.gui.gun.friction"));
        this.gravity = new GuiTrackpadElement(mc, (value) -> this.props.gravity = value.floatValue());
        this.gravity.tooltip(IKey.lang("blockbuster.gui.gun.gravity"));
        this.fadeIn = new GuiTrackpadElement(mc, (value) -> this.props.fadeIn = value.intValue());
        this.fadeIn.tooltip(IKey.lang("blockbuster.gui.gun.fade_in"));
        this.fadeIn.limit(0, Integer.MAX_VALUE, true);
        this.fadeOut = new GuiTrackpadElement(mc, (value) -> this.props.fadeOut = value.intValue());
        this.fadeOut.tooltip(IKey.lang("blockbuster.gui.gun.fade_out"));
        this.fadeOut.limit(0, Integer.MAX_VALUE, true);

        this.pickProjectile.flex().relative(area).w(100).x(0.75F, -50).y(1, -60);
        this.tickCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);

        GuiElement projectileFields = new GuiElement(mc);

        projectileFields.flex().relative(area).w(1F).h(1F, -40).column(5).width(100).height(20).padding(10);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.motion")).background(0x88000000), this.speed, this.friction, this.gravity);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.hitbox"), 20).background(0x88000000).anchor(0, 1), this.hitboxX, this.hitboxY);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.timers"), 20).background(0x88000000).anchor(0, 1), this.ticking, this.lifeSpan);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.rotation"), 20).background(0x88000000).anchor(0, 1), this.yaw, this.pitch);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.transition"), 20).background(0x88000000).anchor(0, 1), this.fadeIn, this.fadeOut);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.sequencer"), 20).background(0x88000000).anchor(0, 1), this.sequencer, this.random);
        this.projectileOptions.add(this.pickProjectile, this.tickCommand, projectileFields);

        /* Impact options */
        area = this.impactOptions.area;

        this.pickImpact = new GuiNestedEdit(mc, (editing) -> this.openMorphs(4, editing));
        this.impactDelay = new GuiTrackpadElement(mc, (value) -> this.props.impactDelay = value.intValue());
        this.impactDelay.limit(0, Integer.MAX_VALUE, true);
        this.impactCommand = new GuiTextElement(mc, 10000, (value) -> this.props.impactCommand = value);
        this.vanish = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.props.vanish = b.isToggled());
        this.bounce = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.props.bounce = b.isToggled());
        this.sticks = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.props.sticks = b.isToggled());
        this.hits = new GuiTrackpadElement(mc, (value) -> this.props.hits = value.intValue());
        this.hits.tooltip(IKey.lang("blockbuster.gui.gun.hits"));
        this.hits.limit(0, Integer.MAX_VALUE, true);
        this.damage = new GuiTrackpadElement(mc, (value) -> this.props.damage = value.floatValue());
        this.knockback = new GuiTrackpadElement(mc, (value) -> this.props.knockback = value.floatValue());
        this.knockback.tooltip(IKey.lang("blockbuster.gui.gun.knockback"));
        this.bounceFactor = new GuiTrackpadElement(mc, (value) -> this.props.bounceFactor = value.floatValue());
        this.bounceFactor.tooltip(IKey.lang("blockbuster.gui.gun.bounce_factor"));
        this.vanishDelay = new GuiTrackpadElement(mc, (value) -> this.props.vanishDelay = value.intValue());
        this.vanishDelay.limit(0).integer().tooltip(IKey.lang("blockbuster.gui.gun.vanish_delay"));
        this.penetration = new GuiTrackpadElement(mc, (value) -> this.props.penetration = value.floatValue());
        this.penetration.block().tooltip(IKey.lang("blockbuster.gui.gun.penetration"));

        this.pickImpact.flex().relative(area).w(100).x(0.75F, -50).y(1, -60);
        this.impactCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);

        GuiElement impactFields = new GuiElement(mc);

        impactFields.flex().relative(area).w(1F).h(1F, -40).column(5).width(100).height(20).padding(10);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.impact_delay")).background(0x88000000), this.impactDelay);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.damage"), 20).background(0x88000000).anchor(0, 1), this.damage, this.knockback);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.bounce"), 20).background(0x88000000).anchor(0, 1), this.bounce, this.hits, this.bounceFactor);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.vanish"), 20).background(0x88000000).anchor(0, 1), this.vanish, this.vanishDelay);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.sticks"), 20).background(0x88000000).anchor(0, 1), this.sticks, this.penetration);

        this.impactOptions.add(this.pickImpact, this.impactCommand, impactFields);

        /* Gun transforms */
        area = this.transformOptions.area;

        this.gun = new GuiPoseTransformations(mc);
        this.projectile = new GuiPoseTransformations(mc);

        this.arms = new GuiMorphRenderer(mc);
        this.arms.setRotation(61, -13);
        this.arms.setPosition(0.1048045F, 1.081198F, 0.22774392F);
        this.arms.setScale(1.5F);

        try
        {
            this.arms.morph = MorphManager.INSTANCE.morphFromNBT(JsonToNBT.getTagFromJson("{Name:\"blockbuster.fred\"}"));
            this.arms.getEntity().setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
        }
        catch (Exception e)
        {}

        this.bullet = new GuiProjectileModelRenderer(mc);
        this.bullet.projectile.props = this.props;
        this.bullet.projectile.morph.setDirect(this.props.projectileMorph);

        this.bullet.setRotation(-64, 16);
        this.bullet.setPosition(-0.042806394F, 0.40452564F, -0.001203875F);
        this.bullet.setScale(2.5F);

        this.gun.flex().relative(area).x(0.25F, -95).y(1, -80).wh(190, 70);
        this.projectile.flex().relative(area).x(0.75F, -95).y(1, -80).wh(190, 70);
        this.arms.flex().relative(area).wTo(this.bullet.flex()).h(1F);
        this.bullet.flex().relative(area).x(0.5F).wh(0.5F, 1F);

        this.transformOptions.add(this.arms, this.bullet, this.gun, this.projectile);

        /* Placement of the elements */
        this.morphs.flex().relative(this.viewport).wh(1F, 1F);
        this.panel.flex().relative(this.viewport).set(0, 35, 0, 0).w(1, 0).h(1, -35);

        /* Gun properties */
        this.pickDefault.setMorph(this.props.defaultMorph);
        this.pickFiring.setMorph(this.props.firingMorph);
        this.fireCommand.setText(this.props.fireCommand);
        this.delay.setValue(this.props.delay);
        this.projectiles.setValue(this.props.projectiles);
        this.scatterX.setValue(this.props.scatterX);
        this.scatterY.setValue(this.props.scatterY);
        this.launch.toggled(this.props.launch);
        this.useTarget.toggled(this.props.useTarget);
        this.ammoStack.stack = this.props.ammoStack;

        /* Projectile properties */
        this.pickProjectile.setMorph(this.props.projectileMorph);
        this.tickCommand.setText(this.props.tickCommand);
        this.ticking.setValue(this.props.ticking);
        this.lifeSpan.setValue(this.props.lifeSpan);
        this.yaw.toggled(this.props.yaw);
        this.pitch.toggled(this.props.pitch);
        this.sequencer.toggled(this.props.sequencer);
        this.random.toggled(this.props.random);
        this.hitboxX.setValue(this.props.hitboxX);
        this.hitboxY.setValue(this.props.hitboxY);
        this.speed.setValue(this.props.speed);
        this.friction.setValue(this.props.friction);
        this.gravity.setValue(this.props.gravity);
        this.fadeIn.setValue(this.props.fadeIn);
        this.fadeOut.setValue(this.props.fadeOut);

        /* Impact properties */
        this.pickImpact.setMorph(this.props.impactMorph);
        this.impactCommand.setText(this.props.impactCommand);
        this.impactDelay.setValue(this.props.impactDelay);
        this.vanish.toggled(this.props.vanish);
        this.bounce.toggled(this.props.bounce);
        this.sticks.toggled(this.props.sticks);
        this.hits.setValue(this.props.hits);
        this.damage.setValue(this.props.damage);
        this.knockback.setValue(this.props.knockback);
        this.bounceFactor.setValue(this.props.bounceFactor);
        this.vanishDelay.setValue(this.props.vanishDelay);
        this.penetration.setValue(this.props.penetration);

        /* Gun transforms */
        this.gun.set(this.props.gunTransform);
        this.projectile.set(this.props.projectileTransform);

        this.root.add(this.panel);
        this.root.keys().register(IKey.lang("blockbuster.gui.gun.keys.cycle"), Keyboard.KEY_TAB, this::cycle);
    }

    private void pickItem(ItemStack stack)
    {
        if (this.inventory.linked != null)
        {
            GuiSlotElement slot = this.inventory.linked;

            slot.stack = stack == null ? null : stack.copy();

            this.props.ammoStack = slot.stack == null ? ItemStack.EMPTY : slot.stack;
            this.inventory.unlink();
        }
    }

    protected void cycle()
    {
        int index = -1;

        for (int i = 0; i < this.panel.panels.size(); i++)
        {
            if (this.panel.view.delegate == this.panel.panels.get(i))
            {
                index = i;

                break;
            }
        }

        index += GuiScreen.isShiftKeyDown() ? 1 : -1;
        index = MathUtils.cycler(index, 0, this.panel.panels.size() - 1);

        this.panel.buttons.elements.get(index).clickItself(GuiBase.getCurrent());
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void openMorphs(int i, boolean editing)
    {
        AbstractMorph morph = this.props.defaultMorph;

        if (i == 2)
        {
            morph = this.props.firingMorph;
        }
        else if (i == 3)
        {
            morph = this.props.projectileMorph;
        }
        else if (i == 4)
        {
            morph = this.props.impactMorph;
        }

        if (this.morphs.hasParent())
        {
            if (i == this.index)
            {
                return;
            }
            else
            {
                this.morphs.finish();
                this.morphs.removeFromParent();
            }
        }

        this.index = i;
        this.morphs.resize();
        this.morphs.setSelected(morph);

        if (editing)
        {
            this.morphs.enterEditMorph();
        }

        this.root.add(this.morphs);
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.index == 1)
        {
            this.props.defaultMorph = morph;
            this.props.setCurrent(MorphUtils.copy(morph));
            this.pickDefault.setMorph(morph);
        }
        else if (this.index == 2)
        {
            this.props.firingMorph = morph;
            this.pickFiring.setMorph(morph);
        }
        else if (this.index == 3)
        {
            this.props.projectileMorph = morph;
            this.pickProjectile.setMorph(morph);
            this.bullet.projectile.morph.setDirect(this.props.projectileMorph);
        }
        else if (this.index == 4)
        {
            this.props.impactMorph = morph;
            this.pickImpact.setMorph(morph);
        }
    }

    @Override
    protected void closeScreen()
    {
        super.closeScreen();

        Dispatcher.sendToServer(new PacketGunInfo(this.props.toNBT(), 0));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GuiDraw.drawCustomBackground(0, 0, this.width, this.height);

        Gui.drawRect(0, 0, this.width, 35, 0x88000000);
        this.drawGradientRect(0, 35, this.width, 45, 0x88000000, 0x00000000);
        this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.title"), 10, 15, 0xffffffff);

        EntityPlayer player = this.mc.player;
        int w = this.viewport.w / 4;

        if (this.panel.view.delegate == this.gunOptions)
        {
            if (this.props.defaultMorph != null)
            {
                this.props.defaultMorph.renderOnScreen(player, this.pickDefault.area.mx(), this.pickDefault.area.y - 20, w * 0.5F, 1);
            }

            if (this.props.firingMorph != null)
            {
                this.props.firingMorph.renderOnScreen(player, this.pickFiring.area.mx(), this.pickFiring.area.y - 20, w * 0.5F, 1);

                GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
            }

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.default_morph"), this.pickDefault.area.mx(), this.pickFiring.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.fire_morph"), this.pickFiring.area.mx(), this.pickFiring.area.y - 12, 0xffffff);

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.delay"), this.delay.area.mx(), this.delay.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.scatter"), this.scatterX.area.ex() + 3, this.scatterX.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.projectiles"), this.projectiles.area.mx(), this.projectiles.area.y - 12, 0xffffff);

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.fire_command"), this.fireCommand.area.x, this.fireCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.projectileOptions)
        {
            if (this.props.projectileMorph != null)
            {
                this.props.projectileMorph.renderOnScreen(player, this.pickProjectile.area.mx(), this.pickProjectile.area.y - 20, w * 0.5F, 1);
            }

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.projectile_morph"), this.pickProjectile.area.mx(), this.pickProjectile.area.y - 12, 0xffffff);

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.tick_command"), this.tickCommand.area.x, this.tickCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.impactOptions)
        {
            if (this.props.impactMorph != null)
            {
                this.props.impactMorph.renderOnScreen(player, this.pickImpact.area.mx(), this.pickImpact.area.y - 20, w * 0.5F, 1);
            }

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.impact_morph"), this.pickImpact.area.mx(), this.pickImpact.area.y - 12, 0xffffff);

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.impact_command"), this.impactCommand.area.x, this.impactCommand.area.y - 12, 0xffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.panel.view.delegate == this.transformOptions)
        {
            String gun = I18n.format("blockbuster.gui.gun.gun_transforms");
            String trans = I18n.format("blockbuster.gui.gun.projectile_transforms");

            GuiDraw.drawTextBackground(this.context.font, gun, this.gun.area.mx(this.context.font.getStringWidth(gun)), this.arms.area.y + 15, 0xffffff, 0x88000000);
            GuiDraw.drawTextBackground(this.context.font, trans, this.projectile.area.mx(this.context.font.getStringWidth(trans)), this.arms.area.y + 15, 0xffffff, 0x88000000);
        }
    }

    public static class GuiProjectileModelRenderer extends GuiModelRenderer
    {
        public EntityGunProjectile projectile;

        public GuiProjectileModelRenderer(Minecraft mc)
        {
            super(mc);

            this.projectile = new EntityGunProjectile(mc.world);
        }

        @Override
        protected void drawUserModel(GuiContext context)
        {
            this.projectile.ticksExisted = this.projectile.props.fadeIn;
            this.mc.getRenderManager().renderEntity(this.projectile, 0, 0.5F, 0, 0, context.partialTicks, false);

            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0.5F, 0);

            GL11.glLineWidth(5);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0.25);
            GL11.glEnd();

            GL11.glLineWidth(3);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glColor3d(0, 1, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0.25);
            GL11.glEnd();
            GL11.glLineWidth(1);

            GL11.glPointSize(12);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glColor3d(0, 0, 0);
            GL11.glVertex3d(0, 0, 0);
            GL11.glEnd();

            GL11.glPointSize(10);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glColor3d(1, 1, 1);
            GL11.glVertex3d(0, 0, 0);
            GL11.glEnd();
            GL11.glPointSize(1);

            GlStateManager.popMatrix();

            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
    }

    public static class GuiGunPanels extends GuiPanelBase<GuiElement>
    {
        private GuiGun parentScreen;

        public GuiGunPanels(Minecraft mc, GuiGun parentScreen)
        {
            super(mc);

            this.parentScreen = parentScreen;
        }

        @Override
        public void setPanel(GuiElement panel)
        {
            super.setPanel(panel);

            if (this.parentScreen.inventory != null)
            {
                this.parentScreen.inventory.setVisible(false);
            }
        }

        @Override
        protected void drawBackground(GuiContext context, int x, int y, int w, int h)
        {
            Gui.drawRect(x, y, x + w, y + h, 0xff080808);
        }
    }
}