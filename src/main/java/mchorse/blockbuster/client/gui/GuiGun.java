package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.utils.BBIcons;
import mchorse.blockbuster_pack.client.gui.GuiPosePanel.GuiPoseTransformations;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public GuiButtonElement pickDefault;
    public GuiButtonElement pickFiring;
    public GuiTextElement fireCommand;
    public GuiTrackpadElement delay;
    public GuiTrackpadElement projectiles;
    public GuiTrackpadElement scatter;
    public GuiToggleElement launch;

    /* Projectile options */
    public GuiElement projectileOptions;
    public GuiButtonElement pickProjectile;
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
    public GuiButtonElement pickImpact;
    public GuiTextElement impactCommand;
    public GuiTrackpadElement impactDelay;
    public GuiToggleElement vanish;
    public GuiToggleElement bounce;
    public GuiToggleElement sticks;
    public GuiTrackpadElement hits;
    public GuiTrackpadElement damage;
    public GuiTrackpadElement bounceFactor;

    /* Transforms */
    public GuiElement transformOptions;
    public GuiPoseTransformations gun;
    public GuiPoseTransformations projectile;

    public GuiGun(ItemStack stack)
    {
        this.props = Gun.get(stack).getProps();

        Minecraft mc = Minecraft.getMinecraft();
        IMorphing cap = Morphing.get(mc.player);

        /* Initialization of GUI elements */
        this.gunOptions = new GuiElement(mc);
        this.projectileOptions = new GuiElement(mc);
        this.transformOptions = new GuiElement(mc);
        this.impactOptions = new GuiElement(mc);

        this.panel = new GuiPanelBase<GuiElement>(mc);
        this.panel.setPanel(this.gunOptions);
        this.panel.registerPanel(this.gunOptions, I18n.format("blockbuster.gui.gun.fire_props"), Icons.GEAR);
        this.panel.registerPanel(this.projectileOptions, I18n.format("blockbuster.gui.gun.projectile_props"), BBIcons.BULLET);
        this.panel.registerPanel(this.impactOptions, I18n.format("blockbuster.gui.gun.impact_props"), Icons.DOWNLOAD);
        this.panel.registerPanel(this.transformOptions, I18n.format("blockbuster.gui.gun.transforms"), Icons.POSE);

        this.morphs = new GuiCreativeMorphsMenu(mc, this::setMorph);
        this.morphs.setVisible(false);

        /* Gun options */
        Area area = this.gunOptions.area;

        this.pickDefault = new GuiButtonElement(mc, I18n.format("blockbuster.gui.gun.default_morph"), (b) -> this.openMorphs(1));
        this.pickFiring = new GuiButtonElement(mc, I18n.format("blockbuster.gui.gun.fire_morph"), (b) -> this.openMorphs(2));
        this.fireCommand = new GuiTextElement(mc, 10000, (value) -> this.props.fireCommand = value);
        this.delay = new GuiTrackpadElement(mc, (value) -> this.props.delay = value.intValue());
        this.delay.tooltip(I18n.format("blockbuster.gui.gun.delay"));
        this.delay.limit(0, Integer.MAX_VALUE, true);
        this.projectiles = new GuiTrackpadElement(mc, (value) -> this.props.projectiles = value.intValue());
        this.projectiles.tooltip(I18n.format("blockbuster.gui.gun.projectiles"));
        this.projectiles.limit(0, Integer.MAX_VALUE, true);
        this.scatter = new GuiTrackpadElement(mc, (value) -> this.props.scatter = value);
        this.scatter.tooltip(I18n.format("blockbuster.gui.gun.scatter"));
        this.launch = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.launch"), false, (b) -> this.props.launch = b.isToggled());

        this.pickDefault.flex().relative(area).set(0, 0, 100, 20).x(0.25F, -50).y(1, -100);
        this.pickFiring.flex().relative(area).set(0, 0, 100, 20).x(0.75F, -50).y(1, -100);
        this.fireCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);
        this.delay.flex().relative(this.pickDefault.resizer()).set(0, 25, 100, 20);
        this.projectiles.flex().relative(this.pickFiring.resizer()).set(0, 25, 100, 20);
        this.scatter.flex().relative(area).set(0, 0, 0, 20).x(0.25F, 55).y(1, -75).w(0.5F, -110);
        this.launch.flex().relative(this.scatter.resizer()).set(0, -5 - (20 + 11) / 2, 100, 11);

        this.gunOptions.add(this.pickDefault, this.pickFiring, this.fireCommand, this.delay, this.projectiles, this.scatter, this.launch);

        /* Projectile options */
        area = this.projectileOptions.area;

        this.pickProjectile = new GuiButtonElement(mc, I18n.format("blockbuster.gui.gun.projectile_morph"), (b) -> this.openMorphs(3));
        this.tickCommand = new GuiTextElement(mc, 10000, (value) -> this.props.tickCommand = value);
        this.ticking = new GuiTrackpadElement(mc, (value) -> this.props.ticking = value.intValue());
        this.ticking.tooltip(I18n.format("blockbuster.gui.gun.ticking"));
        this.ticking.limit(0, Integer.MAX_VALUE, true);
        this.lifeSpan = new GuiTrackpadElement(mc, (value) -> this.props.lifeSpan = value.intValue());
        this.lifeSpan.tooltip(I18n.format("blockbuster.gui.gun.life_span"));
        this.lifeSpan.limit(0, Integer.MAX_VALUE, true);
        this.yaw = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.yaw"), false, (b) -> this.props.yaw = b.isToggled());
        this.pitch = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.pitch"), false, (b) -> this.props.pitch = b.isToggled());
        this.sequencer = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.sequencer"), false, (b) -> this.props.sequencer = b.isToggled());
        this.random = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.random"), false, (b) -> this.props.random = b.isToggled());
        this.hitboxX = new GuiTrackpadElement(mc, (value) -> this.props.hitboxX = value);
        this.hitboxX.tooltip(I18n.format("blockbuster.gui.gun.hitbox_x"));
        this.hitboxY = new GuiTrackpadElement(mc, (value) -> this.props.hitboxY = value);
        this.hitboxY.tooltip(I18n.format("blockbuster.gui.gun.hitbox_y"));
        this.speed = new GuiTrackpadElement(mc, (value) -> this.props.speed = value);
        this.speed.tooltip(I18n.format("blockbuster.gui.gun.speed"));
        this.friction = new GuiTrackpadElement(mc, (value) -> this.props.friction = value);
        this.friction.tooltip(I18n.format("blockbuster.gui.gun.friction"));
        this.gravity = new GuiTrackpadElement(mc, (value) -> this.props.gravity = value);
        this.gravity.tooltip(I18n.format("blockbuster.gui.gun.gravity"));
        this.fadeIn = new GuiTrackpadElement(mc, (value) -> this.props.fadeIn = value.intValue());
        this.fadeIn.tooltip(I18n.format("blockbuster.gui.gun.fade_in"));
        this.fadeIn.limit(0, Integer.MAX_VALUE, true);
        this.fadeOut = new GuiTrackpadElement(mc, (value) -> this.props.fadeOut = value.intValue());
        this.fadeOut.tooltip(I18n.format("blockbuster.gui.gun.fade_out"));
        this.fadeOut.limit(0, Integer.MAX_VALUE, true);

        this.pickProjectile.flex().relative(area).set(0, 0, 100, 20).x(0.5F, -50).y(1, -60);
        this.tickCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);
        this.ticking.flex().relative(area).set(0, 10, 100, 20).x(1, -110);
        this.lifeSpan.flex().relative(this.ticking.resizer()).set(0, 25, 100, 20);
        this.yaw.flex().relative(area).relative(this.lifeSpan.resizer()).set(0, 25, 50, 11);
        this.pitch.flex().relative(this.lifeSpan.resizer()).set(50, 25, 50, 11);
        this.sequencer.flex().relative(this.yaw.resizer()).set(0, 16, 100, 11);
        this.random.flex().relative(this.sequencer.resizer()).set(0, 16, 100, 11);
        this.hitboxX.flex().relative(this.gravity.resizer()).set(0, 25, 100, 20);
        this.hitboxY.flex().relative(this.hitboxX.resizer()).set(0, 25, 100, 20);
        this.speed.flex().relative(area).set(10, 10, 100, 20);
        this.friction.flex().relative(this.speed.resizer()).set(0, 25, 100, 20);
        this.gravity.flex().relative(this.friction.resizer()).set(0, 25, 100, 20);
        this.fadeIn.flex().relative(this.random.resizer()).set(0, 16, 100, 20);
        this.fadeOut.flex().relative(this.fadeIn.resizer()).set(0, 25, 100, 20);

        this.projectileOptions.add(this.pickProjectile, this.tickCommand);
        this.projectileOptions.add(this.yaw, this.pitch, this.sequencer, this.random);
        this.projectileOptions.add(this.ticking, this.hitboxX, this.hitboxY, this.lifeSpan, this.speed, this.friction, this.gravity, this.fadeIn, this.fadeOut);

        /* Impact options */
        area = this.impactOptions.area;

        this.pickImpact = new GuiButtonElement(mc, I18n.format("blockbuster.gui.gun.impact_morph"), (b) -> this.openMorphs(4));
        this.impactDelay = new GuiTrackpadElement(mc, (value) -> this.props.impactDelay = value.intValue());
        this.impactDelay.tooltip(I18n.format("blockbuster.gui.gun.impact_delay"));
        this.impactDelay.limit(0, Integer.MAX_VALUE, true);
        this.impactCommand = new GuiTextElement(mc, 10000, (value) -> this.props.impactCommand = value);
        this.vanish = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.vanish"), false, (b) -> this.props.vanish = b.isToggled());
        this.bounce = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.bounce"), false, (b) -> this.props.bounce = b.isToggled());
        this.sticks = new GuiToggleElement(mc, I18n.format("blockbuster.gui.gun.sticks"), false, (b) -> this.props.sticks = b.isToggled());
        this.hits = new GuiTrackpadElement(mc, (value) -> this.props.hits = value.intValue());
        this.hits.tooltip(I18n.format("blockbuster.gui.gun.hits"));
        this.hits.limit(0, Integer.MAX_VALUE, true);
        this.damage = new GuiTrackpadElement(mc, (value) -> this.props.damage = value);
        this.damage.tooltip(I18n.format("blockbuster.gui.gun.damage"));
        this.bounceFactor = new GuiTrackpadElement(mc, (value) -> this.props.bounceFactor = value);
        this.bounceFactor.tooltip(I18n.format("blockbuster.gui.gun.bounce_factor"));

        this.pickImpact.flex().relative(area).set(0, 0, 100, 20).x(0.5F, -50).y(1, -60);
        this.impactCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);
        this.impactDelay.flex().relative(this.hits.resizer()).set(0, 25, 100, 20);
        this.vanish.flex().relative(this.impactDelay.resizer()).set(0, 25, 100, 11);
        this.bounce.flex().relative(this.vanish.resizer()).set(0, 16, 100, 11);
        this.sticks.flex().relative(this.bounceFactor.resizer()).set(0, 25, 100, 11);
        this.hits.flex().relative(this.damage.resizer()).set(0, 25, 100, 20);
        this.damage.flex().relative(area).set(0, 10, 100, 20).x(1, -110);
        this.bounceFactor.flex().relative(this.bounce.resizer()).set(0, 16, 100, 20);

        this.impactOptions.add(this.pickImpact, this.impactCommand, this.impactDelay, this.vanish, this.bounce, this.sticks);
        this.impactOptions.add(this.damage, this.hits, this.bounceFactor);

        /* Gun transforms */
        area = this.transformOptions.area;

        this.gun = new GuiPoseTransformations(mc);
        this.projectile = new GuiPoseTransformations(mc);

        this.gun.flex().relative(area).set(0, 30, 190, 70).x(0.5F, -95);
        this.projectile.flex().relative(area).set(0, 30, 190, 70).x(0.5F, -95).y(1, -80);

        this.transformOptions.add(this.gun, this.projectile);

        /* Placement of the elements */
        this.morphs.flex().relative(this.viewport).wh(1F, 1F);
        this.panel.flex().relative(this.viewport).set(0, 35, 0, 0).w(1, 0).h(1, -35);

        /* Gun properties */
        this.fireCommand.setText(this.props.fireCommand);
        this.delay.setValue(this.props.delay);
        this.projectiles.setValue(this.props.projectiles);
        this.scatter.setValue(this.props.scatter);
        this.launch.toggled(this.props.launch);

        /* Projectile properties */
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
        this.impactCommand.setText(this.props.impactCommand);
        this.impactDelay.setValue(this.props.impactDelay);
        this.vanish.toggled(this.props.vanish);
        this.bounce.toggled(this.props.bounce);
        this.sticks.toggled(this.props.sticks);
        this.hits.setValue(this.props.hits);
        this.damage.setValue(this.props.damage);
        this.bounceFactor.setValue(this.props.bounceFactor);

        /* Gun transforms */
        this.gun.set(this.props.gunTransform);
        this.projectile.set(this.props.projectileTransform);

        this.root.add(this.panel, this.morphs);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void openMorphs(int i)
    {
        AbstractMorph morph = this.props.defaultMorph;

        if (i == 2) morph = this.props.firingMorph;
        else if (i == 3) morph = this.props.projectileMorph;
        else if (i == 4) morph = this.props.impactMorph;

        this.index = i;
        this.morphs.setVisible(true);
        this.morphs.setSelected(morph);
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.index == 1) this.props.defaultMorph = morph;
        else if (this.index == 2) this.props.firingMorph = morph;
        else if (this.index == 3) this.props.projectileMorph = morph;
        else if (this.index == 4) this.props.impactMorph = morph;
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
        this.drawDefaultBackground();

        Gui.drawRect(0, 0, this.width, 35, 0x88000000);
        this.drawGradientRect(0, 35, this.width, 45, 0x88000000, 0x00000000);
        this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.title"), 10, 15, 0xffffffff);

        EntityPlayer player = this.mc.player;
        int w = this.viewport.w / 4;

        if (this.panel.view.delegate == this.gunOptions)
        {
            if (this.props.defaultMorph != null)
            {
                this.props.defaultMorph.renderOnScreen(player, this.viewport.mx() - w, this.viewport.my(), w * 0.5F, 1);
            }

            if (this.props.firingMorph != null)
            {
                this.props.firingMorph.renderOnScreen(player, this.viewport.mx() + w, this.viewport.my(), w * 0.5F, 1);
            }

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.fire_command"), this.fireCommand.area.x, this.fireCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.projectileOptions)
        {
            if (this.props.projectileMorph != null)
            {
                this.props.projectileMorph.renderOnScreen(player, this.viewport.mx(), this.viewport.my(), w * 0.5F, 1);
            }

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.tick_command"), this.tickCommand.area.x, this.tickCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.impactOptions)
        {
            if (this.props.impactMorph != null)
            {
                this.props.impactMorph.renderOnScreen(player, this.viewport.mx(), this.viewport.my(), w * 0.5F, 1);
            }

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.impact_command"), this.impactCommand.area.x, this.impactCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.transformOptions)
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.gun_transforms"), this.gun.area.mx(), this.gun.area.y - 28, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.projectile_transforms"), this.projectile.area.mx(), this.projectile.area.y - 28, 0xffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}