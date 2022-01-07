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
import mchorse.mclib.client.gui.framework.elements.*;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
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
    /*
    Overlay options
    */
    public GuiElement aimOptions;
    public GuiTrackpadElement zoom;
    public GuiTrackpadElement recoilXMin;
    public GuiTrackpadElement recoilXMax;
    public GuiTrackpadElement srcShootX;
    public GuiTrackpadElement srcShootY;
    public GuiTrackpadElement srcShootZ;
    public GuiTrackpadElement inputAmmo;
    public GuiTrackpadElement inputReloadingTime;
    public GuiTrackpadElement recoilYMin;
    public GuiTrackpadElement recoilYMax;
    public GuiToggleElement recoilSimple;
    public GuiToggleElement resetTimerButtonRel;
    public GuiToggleElement enableCustomGuiMorph;
    public GuiToggleElement needToBeReloaded;
    public GuiNestedEdit pickHands;
    public GuiNestedEdit pickGuiMorph;
    public GuiNestedEdit pickReloadMorph;
    public GuiNestedEdit pickAimMorph;
    public GuiNestedEdit pickMorphOverlay;
    public GuiToggleElement enableOverlay;
    public GuiToggleElement acceptPressed;
    public GuiToggleElement hideHandOnZoom;
    public GuiToggleElement hideAimOnZoom;
    public GuiToggleElement hand_bow;
    public GuiToggleElement hand_bow_always;
    public GuiTrackpadElement inputTimeBetweenShoot;
    /* Impact options */
    public GuiElement impactOptions;
    public GuiNestedEdit pickImpact;
    public GuiTextElement impactCommand;
    public GuiTextElement impactEntityCommand;
    public GuiTrackpadElement impactDelay;
    public GuiToggleElement vanish;
    public GuiToggleElement bounce;
    public GuiToggleElement sticks;
    public GuiTrackpadElement hits;
    public GuiTrackpadElement damage;
    public GuiTrackpadElement knockbackHorizontal;
    public GuiTrackpadElement knockbackVertical;
    public GuiTrackpadElement bounceFactor;
    public GuiTextElement vanishCommand;
    public GuiTrackpadElement vanishDelay;
    public GuiTrackpadElement penetration;
    public GuiToggleElement ignoreBlocks;
    public GuiToggleElement ignoreEntities;
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
        this.aimOptions = new GuiElement(mc);
     //   this.enableOverlay = new GuiCheckBox(mc);
        this.transformOptions = new GuiElement(mc);
        this.impactOptions = new GuiElement(mc);

        this.panel = new GuiGunPanels(mc, this);
        this.panel.setPanel(this.gunOptions);
        this.panel.registerPanel(this.gunOptions, IKey.lang("blockbuster.gui.gun.fire_props"), Icons.GEAR);
        this.panel.registerPanel(this.projectileOptions, IKey.lang("blockbuster.gui.gun.projectile_props"), BBIcons.BULLET);
        this.panel.registerPanel(this.aimOptions, IKey.lang("blockbuster.gui.gun.aimOptions"), Icons.CURSOR);
        this.panel.registerPanel(this.impactOptions, IKey.lang("blockbuster.gui.gun.impact_props"), Icons.DOWNLOAD);
        this.panel.registerPanel(this.transformOptions, IKey.lang("blockbuster.gui.gun.transforms"), Icons.POSE);

        this.morphs = new GuiCreativeMorphsMenu(mc, this::setMorph);

        /* Gun options */
        Area area = this.gunOptions.area;

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
        this.ammoStack = new GuiSlotElement(mc, 0, this::pickItem);
        this.ammoStack.tooltip(IKey.lang("blockbuster.gui.gun.ammo_stack"));
        int firingOffset = 40;

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

        this.gunOptions.add(scatterBar, launchBar, this.delay, this.projectiles, this.pickDefault, this.pickFiring, this.fireCommand, this.ammoStack);

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
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.motion")).background(), this.speed, this.friction, this.gravity);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.hitbox")).background().marginTop(12), this.hitboxX, this.hitboxY);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.timers")).background().marginTop(12), this.ticking, this.lifeSpan);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.rotation")).background().marginTop(12), this.yaw, this.pitch);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.category.transition")).background().marginTop(12), this.fadeIn, this.fadeOut);
        projectileFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.sequencer")).background().marginTop(12), this.sequencer, this.random);
        this.projectileOptions.add(this.pickProjectile, this.tickCommand, projectileFields);

        /* AIM Options */
        area = this.aimOptions.area;

        this.enableOverlay = new GuiToggleElement(mc, IKey.str(""), false, (b) -> this.props.enableOverlay = b.isToggled());
        this.acceptPressed = new GuiToggleElement(mc, IKey.str(""),false,(b)->this.props.acceptPressed=b.isToggled());
        this.hideHandOnZoom = new GuiToggleElement(mc, IKey.str(""), false, (b) -> this.props.hideHandOnZoom = b.isToggled());
        this.hideAimOnZoom = new GuiToggleElement(mc, IKey.str(""),false,(b) -> this.props.hideAimOnZoom = b.isToggled());
        this.hand_bow = new GuiToggleElement(mc, IKey.str(""), false, (b) -> this.props.hand_bow = b.isToggled());
        this.enableCustomGuiMorph = new GuiToggleElement(mc,IKey.str(""),false,(b) -> this.props.enableCustomGuiMorph = b.isToggled());
        this.needToBeReloaded = new GuiToggleElement(mc, IKey.str(""),false, (b) -> this.props.needToBeReloaded = b.isToggled());
        this.hand_bow_always = new GuiToggleElement(mc, IKey.str(""), false, (b) -> this.props.hand_bow_always = b.isToggled());
        this.pickHands = new GuiNestedEdit(mc, (editing) -> this.openMorphs(5, editing));
        this.pickGuiMorph= new GuiNestedEdit(mc, (editing) -> this.openMorphs(7, editing));
        this.pickMorphOverlay = new GuiNestedEdit(mc, (editing) -> this.openMorphs(6, editing));
        this.pickReloadMorph = new GuiNestedEdit(mc, (editing) -> this.openMorphs(8, editing));
        this.pickAimMorph= new GuiNestedEdit(mc, (editing) -> this.openMorphs(9, editing));
        this.zoom = new GuiTrackpadElement(mc, (value) -> this.props.zoom = value.floatValue());
        this.recoilXMin = new GuiTrackpadElement(mc, (value) -> this.props.recoilXMin = value.floatValue());
        this.srcShootX= new GuiTrackpadElement(mc, (value) -> this.props.srcShootX = value.floatValue());
        this.srcShootY= new GuiTrackpadElement(mc, (value) -> this.props.srcShootY = value.floatValue());
        this.srcShootZ= new GuiTrackpadElement(mc, (value) -> this.props.srcShootZ = value.floatValue());
        this.resetTimerButtonRel = new GuiToggleElement(mc, IKey.lang(""),false,(b)->this.props.resetTimerButtonRel = b.isToggled());
        this.recoilSimple = new GuiToggleElement(mc, IKey.lang(""),false,(b)->this.props.recoilSimple = b.isToggled());
        this.recoilXMax = new GuiTrackpadElement(mc, (value) -> this.props.recoilXMax = value.floatValue());
        this.inputAmmo = new GuiTrackpadElement(mc,(value)->this.props.inputAmmo = value.intValue());
        this.inputReloadingTime = new GuiTrackpadElement(mc, (value)->this.props.inputReloadingTime = value.intValue());
        this.inputTimeBetweenShoot = new GuiTrackpadElement(mc, (value)->this.props.inputTimeBetweenShoot = value.intValue());
        this.recoilYMin = new GuiTrackpadElement(mc, (value) -> this.props.recoilYMin = value.floatValue());
        this.recoilYMax = new GuiTrackpadElement(mc, (value) -> this.props.recoilYMax = value.floatValue());
        this.zoom.limit(Float.MIN_VALUE,Float.MAX_VALUE,false);
        this.recoilXMin.limit(-200,200,false);
        this.recoilXMax.limit(-200,200,false);
        this.srcShootX.limit(-10,10,false);
        this.srcShootY.limit(-10,10,false);
        this.srcShootZ.limit(-10,10,false);
        this.inputTimeBetweenShoot.limit(0, Integer.MAX_VALUE);
        this.inputAmmo.limit(0,Integer.MAX_VALUE);
        this.inputReloadingTime.limit(0,Math.round(Long.MAX_VALUE/2F));
        this.recoilYMin.limit(-200,200,false);
        this.recoilYMax.limit(-200,200,false);


        /* ZOOM OVERLAY*/
        this.pickMorphOverlay.flex().relative(area).w(100).x(0.07F, -50).y(0.2F, 0);
        this.enableOverlay.flex().relative(pickMorphOverlay).w(100).y(-60).x(1,-60);
        this.hideHandOnZoom.flex().relative(pickMorphOverlay).w(100).y(-30).x(1,-60);
        this.zoom.flex().relative(pickMorphOverlay).set(0, 0, 100, 20).y(0).x(1,20);
        this.pickAimMorph.flex().relative(pickMorphOverlay.resizer()).w(100).x(1F, 20).y(3f, 0);
        GuiElement srcLabel = Elements.label(IKey.lang("blockbuster.gui.gun.fire_src")).background();
        srcLabel.flex().relative(pickMorphOverlay.resizer()).w(100).set(0, 0, 100, 20).y(-0.1F,90).x(1,0);
        this.srcShootX.flex().relative(pickMorphOverlay.resizer()).w(100).set(0, 0, 100, 20).y(-0.1F,120).x(1,20);
        this.srcShootY.flex().relative(pickMorphOverlay.resizer()).w(100).set(0, 0, 100, 20).y(-0.1F,160).x(1,20);
        this.srcShootZ.flex().relative(pickMorphOverlay.resizer()).w(100).set(0, 0, 100, 20).y(-0.1F,200).x(1,20);

        /* GUI*/
        this.pickGuiMorph.flex().relative(area).w(100).x(0.07F, -50).y(0.5F, 0);

        /* HANDS*/
        this.pickHands.flex().relative(area).w(100).x(0.07F, -50).y(0.8F, 0);
        this.hand_bow.flex().relative(pickHands.resizer()).w(100).x(1f,-60).y(1F, -60);
        this.hand_bow_always.flex().relative(pickHands.resizer()).w(100).x(1f,-60).y(1F, -40);
        this.enableCustomGuiMorph.flex().relative(pickHands.resizer()).w(100).y(-50).x(1f,-60).y(1F, -20);
        this.hideAimOnZoom.flex().relative(pickHands.resizer()).w(100).x(1f,-60).y(1F, 20);
        this.needToBeReloaded.flex().relative(pickHands.resizer()).w(100).x(1f,-60).y(1F, 0);

        /* RELOADING*/

        this.pickReloadMorph.flex().relative(area).w(100).x(0.8F, -50).y(0.2F, 0);
        this.inputTimeBetweenShoot.flex().relative(pickReloadMorph.resizer()).w(100).set(0, 0, 100, 20).y(-0.1F,60).x(-0.1F,5);
        this.inputReloadingTime.flex().relative(pickReloadMorph.resizer()).w(100).set(0, 0, 100, 20).y(-0.1F,100).x(-0.1F,5);
        this.inputAmmo.flex().relative(pickReloadMorph.resizer()).w(100).set(0, 0, 100, 20).y(-0.1F,140).x(-0.1F,5);

        this.recoilXMax.flex().relative(inputAmmo.resizer()).w(100).set(0, 0, 100, 20).y(3F,0).x(0.5F,20);
        this.recoilXMin.flex().relative(inputAmmo.resizer()).w(100).set(0, 0, 100, 20).y(3F,0).x(-0.5F,-20);
        this.recoilYMax.flex().relative(inputAmmo.resizer()).w(100).set(0, 0, 100, 20).y(5F,0).x(0.5F,20);
        this.recoilYMin.flex().relative(inputAmmo.resizer()).w(100).set(0, 0, 100, 20).y(5F,0).x(-0.5F,-20);
        this.recoilSimple.flex().relative(recoilYMin.resizer()).w(100).x(-1,0).y(1F, 0);
        this.resetTimerButtonRel.flex().relative(recoilSimple.resizer()).w(100).x(0,0).y(1F, 0);
        this.acceptPressed.flex().relative(resetTimerButtonRel.resizer()).w(100).x(0,0).y(2F, 0);
        
        this.aimOptions.add(acceptPressed, resetTimerButtonRel,recoilXMin, recoilSimple,recoilXMax, recoilYMin, recoilYMax,srcLabel,srcShootZ,srcShootY,srcShootX,hand_bow,hand_bow_always,enableCustomGuiMorph,pickAimMorph,hideAimOnZoom,inputTimeBetweenShoot,inputReloadingTime,inputAmmo,needToBeReloaded,pickHands,pickGuiMorph,pickReloadMorph,pickMorphOverlay,hideHandOnZoom,enableOverlay,zoom);





        /* Impact options */
        area = this.impactOptions.area;

        this.pickImpact = new GuiNestedEdit(mc, (editing) -> this.openMorphs(4, editing));
        this.impactDelay = new GuiTrackpadElement(mc, (value) -> this.props.impactDelay = value.intValue());
        this.impactDelay.limit(0, Integer.MAX_VALUE, true);
        this.impactCommand = new GuiTextElement(mc, 10000, (value) -> this.props.impactCommand = value);
        this.impactEntityCommand = new GuiTextElement(mc, 10000, (value) -> this.props.impactEntityCommand = value);
        this.vanish = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.props.vanish = b.isToggled());
        this.bounce = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.props.bounce = b.isToggled());
        this.sticks = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.director.enabled"), false, (b) -> this.props.sticks = b.isToggled());
        this.hits = new GuiTrackpadElement(mc, (value) -> this.props.hits = value.intValue());
        this.hits.tooltip(IKey.lang("blockbuster.gui.gun.hits"));
        this.hits.limit(0, Integer.MAX_VALUE, true);
        this.damage = new GuiTrackpadElement(mc, (value) -> this.props.damage = value.floatValue());
        this.knockbackHorizontal = new GuiTrackpadElement(mc, (value) -> this.props.knockbackHorizontal = value.floatValue());
        this.knockbackHorizontal.tooltip(IKey.lang("blockbuster.gui.gun.knockback_horizontal"));
        this.knockbackVertical = new GuiTrackpadElement(mc, (value) -> this.props.knockbackVertical = value.floatValue());
        this.knockbackVertical.tooltip(IKey.lang("blockbuster.gui.gun.knockback_vertical"));
        this.bounceFactor = new GuiTrackpadElement(mc, (value) -> this.props.bounceFactor = value.floatValue());
        this.bounceFactor.tooltip(IKey.lang("blockbuster.gui.gun.bounce_factor"));
        this.vanishCommand = new GuiTextElement(mc, 10000, (value) -> this.props.vanishCommand = value);
        this.vanishDelay = new GuiTrackpadElement(mc, (value) -> this.props.vanishDelay = value.intValue());
        this.vanishDelay.limit(0).integer().tooltip(IKey.lang("blockbuster.gui.gun.vanish_delay"));
        this.penetration = new GuiTrackpadElement(mc, (value) -> this.props.penetration = value.floatValue());
        this.penetration.block().tooltip(IKey.lang("blockbuster.gui.gun.penetration"));
        this.ignoreBlocks = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.ignore_blocks"), false, (b) -> this.props.ignoreBlocks = b.isToggled());
        this.ignoreBlocks.tooltip(IKey.lang("blockbuster.gui.gun.ignore_blocks_tooltip"));
        this.ignoreEntities = new GuiToggleElement(mc, IKey.lang("blockbuster.gui.gun.ignore_entities"), false, (b) -> this.props.ignoreEntities = b.isToggled());
        this.ignoreEntities.tooltip(IKey.lang("blockbuster.gui.gun.ignore_entities_tooltip"));

        this.pickImpact.flex().relative(area).w(100).x(0.75F, -40).y(1, -140);
        this.vanishCommand.flex().relative(area).set(10, 0, 0, 20).w(1, -20).y(1, -110);
        this.impactEntityCommand.flex().relative(this.vanishCommand).y(40).w(1F).h(20);
        this.impactCommand.flex().relative(this.impactEntityCommand).y(40).w(1F).h(20);

        GuiElement impactFields = new GuiElement(mc);

        impactFields.flex().relative(area).w(1F).h(1F, -120).column(5).width(100).height(20).padding(10);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.impact_delay")).background(), this.impactDelay);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.damage")).background().marginTop(12), this.damage, this.knockbackHorizontal, this.knockbackVertical);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.bounce")).background().marginTop(12), this.bounce, this.hits, this.bounceFactor);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.vanish")).background().marginTop(12), this.vanish, this.vanishDelay);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.sticks")).background().marginTop(12), this.sticks, this.penetration);
        impactFields.add(Elements.label(IKey.lang("blockbuster.gui.gun.collision")).background().marginTop(12), this.ignoreBlocks, this.ignoreEntities);

        this.impactOptions.add(this.pickImpact, this.vanishCommand, this.impactEntityCommand, this.impactCommand, impactFields);

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
        this.pickHands.setMorph(this.props.hands);
        this.pickGuiMorph.setMorph(this.props.guiMorph);
        this.pickReloadMorph.setMorph(this.props.reloadMorph);
        this.pickMorphOverlay.setMorph(this.props.morph_overlay);
        this.pickFiring.setMorph(this.props.firingMorph);
        this.pickAimMorph.setMorph(this.props.aimMorph);
        this.fireCommand.setText(this.props.fireCommand);
        this.delay.setValue(this.props.delay);
        this.projectiles.setValue(this.props.projectiles);
        this.zoom.setValue(this.props.zoom);
        this.recoilXMin.setValue(this.props.recoilXMin);
        this.recoilXMax.setValue(this.props.recoilXMax);
        this.srcShootX.setValue(this.props.srcShootX);
        this.srcShootY.setValue(this.props.srcShootY);
        this.srcShootZ.setValue(this.props.srcShootZ);
        this.recoilYMin.setValue(this.props.recoilYMin);
        this.inputAmmo.setValue(this.props.inputAmmo);
        this.inputReloadingTime.setValue(this.props.inputReloadingTime);
        this.recoilYMax.setValue(this.props.recoilYMax);
        this.inputTimeBetweenShoot.setValue(this.props.inputTimeBetweenShoot);
        this.recoilSimple.toggled(this.props.recoilSimple);
        this.resetTimerButtonRel.toggled(this.props.resetTimerButtonRel);
        this.scatterX.setValue(this.props.scatterX);
        this.scatterY.setValue(this.props.scatterY);
        this.launch.toggled(this.props.launch);
        this.useTarget.toggled(this.props.useTarget);
        this.ammoStack.setStack(this.props.ammoStack);

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
        this.enableOverlay.toggled(this.props.enableOverlay);
        this.hideHandOnZoom.toggled(this.props.hideHandOnZoom);
        this.hideAimOnZoom.toggled(this.props.hideAimOnZoom);
        this.hand_bow.toggled(this.props.hand_bow);
        this.enableCustomGuiMorph.toggled(this.props.enableCustomGuiMorph);
        this.needToBeReloaded.toggled(this.props.needToBeReloaded);
        this.hand_bow_always.toggled(this.props.hand_bow_always);
        this.acceptPressed.toggled(this.props.acceptPressed);
        /* Impact properties */
        this.pickImpact.setMorph(this.props.impactMorph);
        this.impactCommand.setText(this.props.impactCommand);
        this.impactEntityCommand.setText(this.props.impactEntityCommand);
        this.impactDelay.setValue(this.props.impactDelay);
        this.vanish.toggled(this.props.vanish);
        this.bounce.toggled(this.props.bounce);
        this.sticks.toggled(this.props.sticks);
        this.hits.setValue(this.props.hits);
        this.damage.setValue(this.props.damage);
        this.knockbackHorizontal.setValue(this.props.knockbackHorizontal);
        this.knockbackVertical.setValue(this.props.knockbackVertical);
        this.bounceFactor.setValue(this.props.bounceFactor);
        this.vanishCommand.setText(this.props.vanishCommand);
        this.vanishDelay.setValue(this.props.vanishDelay);
        this.penetration.setValue(this.props.penetration);
        this.ignoreBlocks.toggled(this.props.ignoreBlocks);
        this.ignoreEntities.toggled(this.props.ignoreEntities);

        /* Gun transforms */
        this.gun.set(this.props.gunTransform);
        this.projectile.set(this.props.projectileTransform);

        this.root.add(this.panel);
        this.root.keys().register(IKey.lang("blockbuster.gui.gun.keys.cycle"), Keyboard.KEY_TAB, this::cycle);
    }



    private void pickItem(ItemStack stack)
    {
        this.props.ammoStack = stack;
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
        else if (i==5){
            morph = this.props.hands;
        }
        else if (i==6){
            morph = this.props.morph_overlay;
        }else if (i==7){
            morph = this.props.guiMorph;
        }else if (i==8){
            morph = this.props.reloadMorph;
        }else if(i==9){
            morph = this.props.aimMorph;
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
        }else if(this.index ==5){
            this.props.hands = morph;
            this.props.setHands(MorphUtils.copy(morph));
            this.pickHands.setMorph(morph);
        }else if(this.index ==6){
            this.props.morph_overlay = morph;
            this.props.setCurrentOverlay(MorphUtils.copy(morph));
            this.pickMorphOverlay.setMorph(morph);
        }else if (this.index==7){
            this.props.guiMorph = morph;
            this.props.setGuiMorph(MorphUtils.copy(morph));
            this.pickGuiMorph.setMorph(morph);

        }
        else if (this.index==8){
            this.props.reloadMorph = morph;
            this.props.setReloadMorph(MorphUtils.copy(morph));
            this.pickReloadMorph.setMorph(morph);

        }else  if(this.index==9){
            this.props.aimMorph = morph;
            this.props.setAimMorph(MorphUtils.copy(morph));
            this.pickAimMorph.setMorph(morph);
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

        Gui.drawRect(0, 0, this.width, 35, ColorUtils.HALF_BLACK);
        this.drawGradientRect(0, 35, this.width, 45, ColorUtils.HALF_BLACK, 0);
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
        }else if (this.panel.view.delegate == this.aimOptions){
            if (this.props.hands != null)
            {
                this.props.hands.renderOnScreen(player, this.pickHands.area.mx(), this.pickHands.area.y-5, w /5F, 1);
            }
            if (this.props.morph_overlay != null)
            {

                this.props.morph_overlay.renderOnScreen(player, this.pickMorphOverlay.area.mx(), this.pickMorphOverlay.area.y-5, w /5F, 1);

            }
            if (this.props.guiMorph != null){

                this.props.guiMorph.renderOnScreen(player, this.pickGuiMorph.area.mx(), this.pickGuiMorph.area.y-5, w /5F, 1);
            }
            if (this.props.reloadMorph!=null){
                this.props.reloadMorph.renderOnScreen(player, this.pickReloadMorph.area.mx(), this.pickReloadMorph.area.y-5, w /5F, 1);

            }
            if (this.props.aimMorph!=null){
                this.props.aimMorph.renderOnScreen(player,this.pickAimMorph.area.mx(),this.pickAimMorph.area.y-20,w/5F,1);
            }

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.pick_hands"), this.pickHands.area.mx(), this.pickHands.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.pick_aim_morph"), this.pickAimMorph.area.mx(), this.pickAimMorph.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.pick_reload_morph"), this.pickReloadMorph.area.mx(), this.pickReloadMorph.area.y - 12, 0xffffff);

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.enable_overlay"), this.enableOverlay.area.mx()+100, this.enableOverlay.area.y+3, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.hide_zoom"), this.hideHandOnZoom.area.mx()+100, this.hideHandOnZoom.area.y+3, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.hide_aim_on_zoom"), this.hideAimOnZoom.area.mx()+100, this.hideAimOnZoom.area.y+3, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.need_to_be_reloaded"), this.needToBeReloaded.area.mx()+100, this.needToBeReloaded.area.y+3, 0xffffff);

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.hand_bow"), this.hand_bow.area.mx()+100, this.hand_bow.area.y+3, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.hand_bow_always"), this.hand_bow_always.area.mx()+100, this.hand_bow_always.area.y+3, 0xffffff);

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.enable_custom_gui_morph"), this.enableCustomGuiMorph.area.mx()+100, this.enableCustomGuiMorph.area.y+3, 0xffffff);

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.input_time_between_shoot"), this.inputTimeBetweenShoot.area.mx(), this.inputTimeBetweenShoot.area.y-15, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.input_ammo"), this.inputAmmo.area.mx(), this.inputAmmo.area.y-15, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.input_reloading_time"), this.inputReloadingTime.area.mx(), this.inputReloadingTime.area.y-15, 0xffffff);

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.pick_overlay"), this.pickMorphOverlay.area.mx(), this.pickMorphOverlay.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.pick_gui"), this.pickGuiMorph.area.mx(), this.pickGuiMorph.area.y - 12, 0xffffff);


            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.zoom"), this.zoom.area.mx(), this.zoom.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.src_shoot_x"), this.srcShootX.area.mx(), this.srcShootX.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.src_shoot_y"), this.srcShootY.area.mx(), this.srcShootY.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.src_shoot_z"), this.srcShootZ.area.mx(), this.srcShootZ.area.y - 12, 0xffffff);

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.recoil_simple"), this.recoilSimple.area.mx()+150, this.recoilSimple.area.y+3, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.accept_pressed"), this.acceptPressed.area.mx()+150, this.acceptPressed.area.y+3, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.reset_timer_button_rel"), this.resetTimerButtonRel.area.mx()+150, this.resetTimerButtonRel.area.y+3, 0xffffff);
    
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.recoil_x_min"), this.recoilXMin.area.mx(), this.recoilXMin.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.recoil_x_max"), this.recoilXMax.area.mx(), this.recoilXMax.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.recoil_y_min"), this.recoilYMin.area.mx(), this.recoilYMin.area.y - 12, 0xffffff);
            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.recoil_y_max"), this.recoilYMax.area.mx(), this.recoilYMax.area.y - 12, 0xffffff);

        }
        else if (this.panel.view.delegate == this.impactOptions)
        {
            if (this.props.impactMorph != null)
            {
                this.props.impactMorph.renderOnScreen(player, this.pickImpact.area.mx(), this.pickImpact.area.y - 20, w * 0.5F, 1);
            }

            this.drawCenteredString(this.fontRenderer, I18n.format("blockbuster.gui.gun.impact_morph"), this.pickImpact.area.mx(), this.pickImpact.area.y - 12, 0xffffff);

            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.impact_command"), this.impactCommand.area.x, this.impactCommand.area.y - 12, 0xffffff);
            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.impact_entity_command"), this.impactEntityCommand.area.x, this.impactEntityCommand.area.y - 12, 0xffffff);
            this.fontRenderer.drawStringWithShadow(I18n.format("blockbuster.gui.gun.vanish_command"), this.vanishCommand.area.x, this.vanishCommand.area.y - 12, 0xffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.panel.view.delegate == this.transformOptions)
        {
            String gun = I18n.format("blockbuster.gui.gun.gun_transforms");
            String trans = I18n.format("blockbuster.gui.gun.projectile_transforms");

            GuiDraw.drawTextBackground(this.context.font, gun, this.gun.area.mx(this.context.font.getStringWidth(gun)), this.arms.area.y + 15, 0xffffff, ColorUtils.HALF_BLACK);
            GuiDraw.drawTextBackground(this.context.font, trans, this.projectile.area.mx(this.context.font.getStringWidth(trans)), this.arms.area.y + 15, 0xffffff, ColorUtils.HALF_BLACK);
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
        protected void drawBackground(GuiContext context, int x, int y, int w, int h)
        {
            Gui.drawRect(x, y, x + w, y + h, 0xff080808);
        }
    }
}