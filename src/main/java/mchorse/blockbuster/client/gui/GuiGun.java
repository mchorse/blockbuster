package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.common.GunProps;
import mchorse.blockbuster.common.entity.EntityGunProjectile;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.guns.PacketGunInfo;
import mchorse.blockbuster.utils.mclib.BBIcons;
import mchorse.blockbuster_pack.client.gui.GuiPosePanel.GuiPoseTransformations;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.creative.GuiCreativeMorphsMenu;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
    public GuiMorphRenderer arms;
    public GuiProjectileModelRenderer bullet;

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

        this.panel = new GuiPanelBase<GuiElement>(mc)
        {
            @Override
            protected void drawBackground(GuiContext context, int x, int y, int w, int h)
            {
                Gui.drawRect(x, y, x + w, y + h, 0xff080808);
            }
        };
        this.panel.setPanel(this.gunOptions);
        this.panel.registerPanel(this.gunOptions, IKey.lang("blockbuster.gui.gun.fire_props"), Icons.GEAR);
        this.panel.registerPanel(this.projectileOptions, IKey.lang("blockbuster.gui.gun.projectile_props"), BBIcons.BULLET);
        this.panel.registerPanel(this.impactOptions, IKey.lang("blockbuster.gui.gun.impact_props"), Icons.DOWNLOAD);
        this.panel.registerPanel(this.transformOptions, IKey.lang("blockbuster.gui.gun.transforms"), Icons.POSE);

        this.morphs = new GuiCreativeMorphsMenu(mc, this::setMorph);

        /* Gun options */
        Area area = this.gunOptions.area;

        this.pickDefault = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.gun.default_morph"), (b) -> this.openMorphs(1));
        this.pickFiring = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.gun.fire_morph"), (b) -> this.openMorphs(2));
        this.fireCommand = new GuiTextElement(mc, 10000, (value) -> this.props.fireCommand = value);
        this.delay = new GuiTrackpadElement(mc, (value) -> this.props.delay = value.intValue());
        this.delay.tooltip(IKey.lang("blockbuster.gui.gun.delay"));
        this.delay.limit(0, Integer.MAX_VALUE, true);
        this.projectiles = new GuiTrackpadElement(mc, (value) -> this.props.projectiles = value.intValue());
        this.projectiles.tooltip(IKey.lang("blockbuster.gui.gun.projectiles"));
        this.projectiles.limit(0, Integer.MAX_VALUE, true);
        this.scatter = new GuiTrackpadElement(mc, (value) -> this.props.scatter = value);
        this.scatter.tooltip(IKey.lang("blockbuster.gui.gun.scatter"));
        this.launch = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.launch"), false, (b) -> this.props.launch = b.isToggled());

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

        this.pickProjectile = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.gun.projectile_morph"), (b) -> this.openMorphs(3));
        this.tickCommand = new GuiTextElement(mc, 10000, (value) -> this.props.tickCommand = value);
        this.ticking = new GuiTrackpadElement(mc, (value) -> this.props.ticking = value.intValue());
        this.ticking.tooltip(IKey.lang("blockbuster.gui.gun.ticking"));
        this.ticking.limit(0, Integer.MAX_VALUE, true);
        this.lifeSpan = new GuiTrackpadElement(mc, (value) -> this.props.lifeSpan = value.intValue());
        this.lifeSpan.tooltip(IKey.lang("blockbuster.gui.gun.life_span"));
        this.lifeSpan.limit(0, Integer.MAX_VALUE, true);
        this.yaw = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.yaw"), false, (b) -> this.props.yaw = b.isToggled());
        this.pitch = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.pitch"), false, (b) -> this.props.pitch = b.isToggled());
        this.sequencer = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.sequencer"), false, (b) -> this.props.sequencer = b.isToggled());
        this.random = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.random"), false, (b) -> this.props.random = b.isToggled());
        this.hitboxX = new GuiTrackpadElement(mc, (value) -> this.props.hitboxX = value);
        this.hitboxX.tooltip(IKey.lang("blockbuster.gui.gun.hitbox_x"));
        this.hitboxY = new GuiTrackpadElement(mc, (value) -> this.props.hitboxY = value);
        this.hitboxY.tooltip(IKey.lang("blockbuster.gui.gun.hitbox_y"));
        this.speed = new GuiTrackpadElement(mc, (value) -> this.props.speed = value);
        this.speed.tooltip(IKey.lang("blockbuster.gui.gun.speed"));
        this.friction = new GuiTrackpadElement(mc, (value) -> this.props.friction = value);
        this.friction.tooltip(IKey.lang("blockbuster.gui.gun.friction"));
        this.gravity = new GuiTrackpadElement(mc, (value) -> this.props.gravity = value);
        this.gravity.tooltip(IKey.lang("blockbuster.gui.gun.gravity"));
        this.fadeIn = new GuiTrackpadElement(mc, (value) -> this.props.fadeIn = value.intValue());
        this.fadeIn.tooltip(IKey.lang("blockbuster.gui.gun.fade_in"));
        this.fadeIn.limit(0, Integer.MAX_VALUE, true);
        this.fadeOut = new GuiTrackpadElement(mc, (value) -> this.props.fadeOut = value.intValue());
        this.fadeOut.tooltip(IKey.lang("blockbuster.gui.gun.fade_out"));
        this.fadeOut.limit(0, Integer.MAX_VALUE, true);

        this.pickProjectile.flex().relative(area).wh(100, 20).x(0.75F, -50).y(1, -60);
        this.tickCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);

        GuiElement projectileFields = new GuiElement(mc);

        projectileFields.flex().relative(area).w(1F).h(1F, -40).column(5).width(100).height(20).padding(10);
        projectileFields.add(this.speed, this.friction, this.gravity, this.hitboxX, this.hitboxY, this.yaw, this.pitch);
        projectileFields.add(this.ticking, this.lifeSpan, this.fadeIn, this.fadeOut, this.sequencer, this.random);
        this.projectileOptions.add(this.pickProjectile, this.tickCommand, projectileFields);

        /* Impact options */
        area = this.impactOptions.area;

        this.pickImpact = new GuiButtonElement(mc, IKey.lang("blockbuster.gui.gun.impact_morph"), (b) -> this.openMorphs(4));
        this.impactDelay = new GuiTrackpadElement(mc, (value) -> this.props.impactDelay = value.intValue());
        this.impactDelay.tooltip(IKey.lang("blockbuster.gui.gun.impact_delay"));
        this.impactDelay.limit(0, Integer.MAX_VALUE, true);
        this.impactCommand = new GuiTextElement(mc, 10000, (value) -> this.props.impactCommand = value);
        this.vanish = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.vanish"), false, (b) -> this.props.vanish = b.isToggled());
        this.bounce = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.bounce"), false, (b) -> this.props.bounce = b.isToggled());
        this.sticks = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.sticks"), false, (b) -> this.props.sticks = b.isToggled());
        this.hits = new GuiTrackpadElement(mc, (value) -> this.props.hits = value.intValue());
        this.hits.tooltip(IKey.lang("blockbuster.gui.gun.hits"));
        this.hits.limit(0, Integer.MAX_VALUE, true);
        this.damage = new GuiTrackpadElement(mc, (value) -> this.props.damage = value);
        this.damage.tooltip(IKey.lang("blockbuster.gui.gun.damage"));
        this.bounceFactor = new GuiTrackpadElement(mc, (value) -> this.props.bounceFactor = value);
        this.bounceFactor.tooltip(IKey.lang("blockbuster.gui.gun.bounce_factor"));

        this.pickImpact.flex().relative(area).wh(100, 20).x(0.75F, -50).y(1, -60);
        this.impactCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);

        GuiElement impactFields = new GuiElement(mc);

        impactFields.flex().relative(area).w(1F).h(1F, -40).column(5).width(100).height(20).padding(10);
        impactFields.add(this.impactDelay, this.vanish, this.bounce, this.sticks);
        impactFields.add(this.damage, this.hits, this.bounceFactor);

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

        this.root.add(this.panel);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void openMorphs(int i)
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

        this.index = i;
        this.morphs.resize();
        this.morphs.setSelected(morph);
        this.root.add(this.morphs);
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.index == 1)
        {
            this.props.defaultMorph = morph;
        }
        else if (this.index == 2)
        {
            this.props.firingMorph = morph;
        }
        else if (this.index == 3)
        {
            this.props.projectileMorph = morph;
            this.bullet.projectile.morph.setDirect(this.props.projectileMorph);
        }
        else if (this.index == 4)
        {
            this.props.impactMorph = morph;
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
                this.props.defaultMorph.renderOnScreen(player, this.pickDefault.area.mx(), this.pickDefault.area.y - 20, w * 0.5F, 1);
            }

            if (this.props.firingMorph != null)
            {
                this.props.firingMorph.renderOnScreen(player, this.pickFiring.area.mx(), this.pickFiring.area.y - 20, w * 0.5F, 1);
            }

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.fire_command"), this.fireCommand.area.x, this.fireCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.projectileOptions)
        {
            if (this.props.projectileMorph != null)
            {
                this.props.projectileMorph.renderOnScreen(player, this.pickProjectile.area.mx(), this.pickProjectile.area.y - 20, w * 0.5F, 1);
            }

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.tick_command"), this.tickCommand.area.x, this.tickCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.impactOptions)
        {
            if (this.props.impactMorph != null)
            {
                this.props.impactMorph.renderOnScreen(player, this.pickImpact.area.mx(), this.pickImpact.area.y - 20, w * 0.5F, 1);
            }

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
            this.projectile.timer = this.projectile.props.fadeIn;
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
}