package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.client.gui.dashboard.GuiDashboard;
import mchorse.blockbuster.common.GunInfo;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketGunInfo;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiPanelBase;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Resizer.Measure;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGun extends GuiBase
{
    public GunInfo info;
    public int index;

    public GuiPanelBase<IGuiElement> panel;

    /* Morphs configuration */
    public GuiCreativeMorphsMenu morphs;

    /* Gun options */
    public GuiElement gunOptions;
    public GuiButtonElement<GuiButton> pickDefault;
    public GuiButtonElement<GuiButton> pickFiring;
    public GuiTextElement fireCommand;
    public GuiTrackpadElement delay;
    public GuiTrackpadElement accuracy;
    public GuiTrackpadElement projectiles;

    /* Projectile options */
    public GuiElement projectileOptions;
    public GuiButtonElement<GuiButton> pickProjectile;
    public GuiTextElement tickCommand;
    public GuiTextElement impactCommand;
    public GuiButtonElement<GuiCheckBox> yaw;
    public GuiButtonElement<GuiCheckBox> pitch;
    public GuiButtonElement<GuiCheckBox> vanish;
    public GuiButtonElement<GuiCheckBox> bounce;
    public GuiButtonElement<GuiCheckBox> sequencer;
    public GuiButtonElement<GuiCheckBox> random;
    public GuiTrackpadElement damage;
    public GuiTrackpadElement ticking;
    public GuiTrackpadElement lifeSpan;
    public GuiTrackpadElement speed;
    public GuiTrackpadElement friction;
    public GuiTrackpadElement gravity;
    public GuiTrackpadElement hits;

    public GuiGun(ItemStack stack)
    {
        this.info = Gun.get(stack).getInfo();

        Minecraft mc = Minecraft.getMinecraft();
        IMorphing cap = Morphing.get(mc.thePlayer);

        /* Initialization of GUI elements */
        this.gunOptions = new GuiElement(mc);
        this.gunOptions.createChildren();
        this.projectileOptions = new GuiElement(mc);
        this.projectileOptions.createChildren();

        this.panel = new GuiPanelBase<IGuiElement>(mc);
        this.panel.setPanel(this.gunOptions);
        this.panel.registerPanel(this.gunOptions, GuiDashboard.GUI_ICONS, "Fire properties", 48, 0, 48, 16);
        this.panel.registerPanel(this.projectileOptions, GuiDashboard.GUI_ICONS, "Projectile properties", 32, 96, 32, 112);

        this.morphs = new GuiCreativeMorphsMenu(mc, 6, null, cap);
        this.morphs.callback = (morph) -> this.setMorph(morph);
        this.morphs.setVisible(false);

        /* Gun options */
        Area area = this.gunOptions.area;

        this.pickDefault = GuiButtonElement.button(mc, "Default morph", (b) -> this.openMorphs(1));
        this.pickFiring = GuiButtonElement.button(mc, "Firing morph", (b) -> this.openMorphs(2));
        this.fireCommand = new GuiTextElement(mc, 10000, (value) -> this.info.fireCommand = value);
        this.delay = new GuiTrackpadElement(mc, "Delay", (value) -> this.info.delay = value.intValue());
        this.delay.setLimit(0, Integer.MAX_VALUE, true);
        this.accuracy = new GuiTrackpadElement(mc, "Accuracy", (value) -> this.info.accuracy = value);
        this.projectiles = new GuiTrackpadElement(mc, "Projectiles", (value) -> this.info.projectiles = value.intValue());
        this.projectiles.setLimit(0, Integer.MAX_VALUE, true);

        this.pickDefault.resizer().parent(area).set(0, 0, 100, 20).x(0.25F, -50).y(1, -100);
        this.pickFiring.resizer().parent(area).set(0, 0, 100, 20).x(0.75F, -50).y(1, -100);
        this.fireCommand.resizer().parent(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);
        this.delay.resizer().relative(this.pickDefault.resizer()).set(0, 25, 100, 20);
        this.accuracy.resizer().parent(area).set(0, 0, 0, 20).x(0.25F, 55).y(1, -75).w(0.5F, -110);
        this.projectiles.resizer().relative(this.pickFiring.resizer()).set(0, 25, 100, 20);

        this.gunOptions.children.add(this.pickDefault, this.pickFiring, this.fireCommand, this.delay, this.accuracy, this.projectiles);

        /* Projectile options */
        area = this.projectileOptions.area;

        this.pickProjectile = GuiButtonElement.button(mc, "Projectile morph", (b) -> this.openMorphs(3));
        this.tickCommand = new GuiTextElement(mc, 10000, (value) -> this.info.tickCommand = value);
        this.impactCommand = new GuiTextElement(mc, 10000, (value) -> this.info.impactCommand = value);
        this.yaw = GuiButtonElement.checkbox(mc, "Yaw", false, (b) -> this.info.yaw = b.button.isChecked());
        this.pitch = GuiButtonElement.checkbox(mc, "Pitch", false, (b) -> this.info.pitch = b.button.isChecked());
        this.vanish = GuiButtonElement.checkbox(mc, "Vanish", false, (b) -> this.info.vanish = b.button.isChecked());
        this.bounce = GuiButtonElement.checkbox(mc, "Bounce", false, (b) -> this.info.bounce = b.button.isChecked());
        this.sequencer = GuiButtonElement.checkbox(mc, "Sequencer", false, (b) -> this.info.sequencer = b.button.isChecked());
        this.random = GuiButtonElement.checkbox(mc, "Random", false, (b) -> this.info.random = b.button.isChecked());
        this.damage = new GuiTrackpadElement(mc, "Damage", (value) -> this.info.damage = value);
        this.ticking = new GuiTrackpadElement(mc, "Ticking", (value) -> this.info.ticking = value.intValue());
        this.ticking.setLimit(0, Integer.MAX_VALUE, true);
        this.lifeSpan = new GuiTrackpadElement(mc, "Life span", (value) -> this.info.lifeSpan = value.intValue());
        this.lifeSpan.setLimit(0, Integer.MAX_VALUE, true);
        this.speed = new GuiTrackpadElement(mc, "Speed", (value) -> this.info.speed = value);
        this.friction = new GuiTrackpadElement(mc, "Friction", (value) -> this.info.friction = value);
        this.gravity = new GuiTrackpadElement(mc, "Gravity", (value) -> this.info.gravity = value);
        this.hits = new GuiTrackpadElement(mc, "Hits", (value) -> this.info.hits = value.intValue());
        this.hits.setLimit(1, Integer.MAX_VALUE, true);

        this.pickProjectile.resizer().parent(area).set(0, 0, 100, 20).x(0.5F, -50).y(1, -100);
        this.tickCommand.resizer().parent(area).set(10, 0, 0, 20).w(1, -20).y(1, -70);
        this.impactCommand.resizer().parent(area).set(10, 0, 0, 20).w(1, -20).y(1, -30);
        this.yaw.resizer().parent(area).set(115, 10, 100, 11);
        this.pitch.resizer().relative(this.yaw.resizer()).set(0, 16, 100, 11);
        this.vanish.resizer().relative(this.pitch.resizer()).set(0, 16, 100, 11);
        this.bounce.resizer().relative(this.vanish.resizer()).set(0, 16, 100, 11);
        this.sequencer.resizer().relative(this.bounce.resizer()).set(0, 16, 100, 11);
        this.random.resizer().relative(this.sequencer.resizer()).set(0, 16, 100, 11);
        this.damage.resizer().parent(area).set(0, 10, 100, 20).x(1, -110);
        this.ticking.resizer().relative(this.damage.resizer()).set(0, 25, 100, 20);
        this.lifeSpan.resizer().relative(this.ticking.resizer()).set(0, 25, 100, 20);
        this.speed.resizer().parent(area).set(10, 10, 100, 20);
        this.friction.resizer().relative(this.speed.resizer()).set(0, 25, 100, 20);
        this.gravity.resizer().relative(this.friction.resizer()).set(0, 25, 100, 20);
        this.hits.resizer().relative(this.gravity.resizer()).set(0, 25, 100, 20);

        this.projectileOptions.children.add(this.pickProjectile, this.tickCommand, this.impactCommand);
        this.projectileOptions.children.add(this.yaw, this.pitch, this.vanish, this.bounce, this.sequencer, this.random);
        this.projectileOptions.children.add(this.damage, this.ticking, this.lifeSpan, this.speed, this.friction, this.gravity, this.hits);

        /* Placement of the elements */
        this.morphs.resizer().parent(this.area).set(0, 0, 1, 1, Measure.RELATIVE);
        this.panel.resizer().parent(this.area).set(0, 35, 0, 0).w(1, 0).h(1, -35);

        this.delay.setValue(this.info.delay);
        this.accuracy.setValue(this.info.accuracy);
        this.projectiles.setValue(this.info.projectiles);
        this.yaw.button.setIsChecked(this.info.yaw);
        this.pitch.button.setIsChecked(this.info.pitch);
        this.vanish.button.setIsChecked(this.info.vanish);
        this.bounce.button.setIsChecked(this.info.bounce);
        this.sequencer.button.setIsChecked(this.info.sequencer);
        this.random.button.setIsChecked(this.info.random);
        this.fireCommand.setText(this.info.fireCommand);
        this.tickCommand.setText(this.info.tickCommand);
        this.impactCommand.setText(this.info.impactCommand);
        this.ticking.setValue(this.info.ticking);
        this.lifeSpan.setValue(this.info.lifeSpan);
        this.speed.setValue(this.info.speed);
        this.friction.setValue(this.info.friction);
        this.gravity.setValue(this.info.gravity);
        this.damage.setValue(this.info.damage);
        this.hits.setValue(this.info.hits);

        this.elements.add(this.panel, this.morphs);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void initGui()
    {
        this.area.set(0, 0, this.width, this.height);
        this.elements.resize(this.width, this.height);
    }

    private void openMorphs(int i)
    {
        AbstractMorph morph = this.info.defaultMorph;

        if (i == 2) morph = this.info.firingMorph;
        else if (i == 3) morph = this.info.projectileMorph;

        this.index = i;
        this.morphs.setVisible(true);
        this.morphs.setSelected(morph);
    }

    private void setMorph(AbstractMorph morph)
    {
        if (this.index == 1) this.info.defaultMorph = morph;
        else if (this.index == 2) this.info.firingMorph = morph;
        else if (this.index == 3) this.info.projectileMorph = morph;
    }

    @Override
    protected void closeScreen()
    {
        super.closeScreen();

        Dispatcher.sendToServer(new PacketGunInfo(this.info.toNBT(), 0));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        Gui.drawRect(0, 0, this.width, 35, 0x88000000);
        this.drawGradientRect(0, 35, this.width, 45, 0x88000000, 0x00000000);
        this.fontRendererObj.drawStringWithShadow("Gun configuration", 10, 15, 0xffffffff);

        EntityPlayer player = this.mc.thePlayer;
        int w = this.area.w / 4;

        if (this.panel.view.delegate == this.gunOptions)
        {
            if (this.info.defaultMorph != null)
            {
                this.info.defaultMorph.renderOnScreen(player, this.area.getX(0.5F) - w, this.area.getY(0.5F), w * 0.5F, 1);
            }

            if (this.info.firingMorph != null)
            {
                this.info.firingMorph.renderOnScreen(player, this.area.getX(0.5F) + w, this.area.getY(0.5F), w * 0.5F, 1);
            }

            this.fontRendererObj.drawStringWithShadow("Command on fire", this.fireCommand.area.x, this.fireCommand.area.y - 12, 0xffffff);
        }
        else if (this.panel.view.delegate == this.projectileOptions)
        {
            if (this.info.projectileMorph != null)
            {
                this.info.projectileMorph.renderOnScreen(player, this.area.getX(0.5F), this.area.getY(0.5F), w * 0.5F, 1);
            }

            this.fontRendererObj.drawStringWithShadow("Command on tick", this.tickCommand.area.x, this.tickCommand.area.y - 12, 0xffffff);
            this.fontRendererObj.drawStringWithShadow("Command on impact", this.impactCommand.area.x, this.impactCommand.area.y - 12, 0xffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}