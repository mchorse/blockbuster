package mchorse.blockbuster.client.gui;

import mchorse.blockbuster.capabilities.gun.Gun;
import mchorse.blockbuster.common.GunInfo;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketGunInfo;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.GuiTrackpadElement;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGun extends GuiBase
{
    public ItemStack stack;
    public GunInfo info;
    public int index;

    public GuiCreativeMorphsMenu morphs;
    public GuiButtonElement<GuiButton> pickDefault;
    public GuiButtonElement<GuiButton> pickFiring;
    public GuiButtonElement<GuiButton> pickProjectile;

    public GuiTrackpadElement firingDelay;
    public GuiTrackpadElement fireRate;

    public GuiButtonElement<GuiCheckBox> auto;
    public GuiTrackpadElement accuracy;
    public GuiTrackpadElement projectiles;

    public GuiTextElement fireCommand;
    public GuiTextElement tickCommand;
    public GuiTextElement impactCommand;

    public GuiTrackpadElement ticking;
    public GuiTrackpadElement lifeSpan;
    public GuiTrackpadElement speed;
    public GuiTrackpadElement friction;
    public GuiTrackpadElement gravity;
    public GuiButtonElement<GuiCheckBox> killOnImpact;

    public GuiGun(ItemStack stack)
    {
        this.stack = stack;
        this.info = Gun.get(stack).getInfo();

        Minecraft mc = Minecraft.getMinecraft();
        IMorphing cap = Morphing.get(mc.thePlayer);

        /* Initialization of GUI elements */
        this.morphs = new GuiCreativeMorphsMenu(mc, 6, null, cap);
        this.morphs.callback = (morph) -> this.setMorph(morph);
        this.morphs.setVisible(false);
        this.pickDefault = GuiButtonElement.button(mc, "Default morph", (b) -> this.openMorphs(1));
        this.pickFiring = GuiButtonElement.button(mc, "Firing morph", (b) -> this.openMorphs(2));
        this.pickProjectile = GuiButtonElement.button(mc, "Projectile morph", (b) -> this.openMorphs(3));

        this.firingDelay = new GuiTrackpadElement(mc, "Fire delay", (value) -> this.info.delay = value.intValue());
        this.firingDelay.setLimit(0, Integer.MAX_VALUE, true);
        this.fireRate = new GuiTrackpadElement(mc, "Fire rate", (value) -> this.info.fireRate = value.intValue());
        this.fireRate.setLimit(0, Integer.MAX_VALUE, true);

        this.auto = GuiButtonElement.checkbox(mc, "Automatic", false, (b) -> this.info.auto = b.button.isChecked());
        this.accuracy = new GuiTrackpadElement(mc, "Accuracy", (value) -> this.info.accuracy = value);
        this.projectiles = new GuiTrackpadElement(mc, "Projectiles", (value) -> this.info.projectiles = value.intValue());
        this.projectiles.setLimit(0, Integer.MAX_VALUE, true);

        this.fireCommand = new GuiTextElement(mc, 10000, (value) -> this.info.fireCommand = value);
        this.tickCommand = new GuiTextElement(mc, 10000, (value) -> this.info.tickCommand = value);
        this.impactCommand = new GuiTextElement(mc, 10000, (value) -> this.info.impactCommand = value);

        this.ticking = new GuiTrackpadElement(mc, "Ticking", (value) -> this.info.ticking = value.intValue());
        this.ticking.setLimit(0, Integer.MAX_VALUE, true);
        this.lifeSpan = new GuiTrackpadElement(mc, "Life span", (value) -> this.info.lifeSpan = value.intValue());
        this.lifeSpan.setLimit(0, Integer.MAX_VALUE, true);
        this.speed = new GuiTrackpadElement(mc, "Speed", (value) -> this.info.speed = value);
        this.friction = new GuiTrackpadElement(mc, "Friction", (value) -> this.info.friction = value);
        this.gravity = new GuiTrackpadElement(mc, "Gravity", (value) -> this.info.gravity = value);
        this.killOnImpact = GuiButtonElement.checkbox(mc, "Vanish projectile", false, (b) -> this.info.killOnImpact = b.button.isChecked());

        /* Placement of the elements */
        this.morphs.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
        this.pickDefault.resizer().parent(this.area).set(10, 10, 100, 20);
        this.pickFiring.resizer().relative(this.pickDefault.resizer()).set(0, 25, 100, 20);
        this.pickProjectile.resizer().relative(this.pickFiring.resizer()).set(0, 25, 100, 20);
        this.auto.resizer().relative(this.pickProjectile.resizer()).set(0, 25, 100, 11);
        this.killOnImpact.resizer().relative(this.auto.resizer()).set(0, 16, 100, 11);
        this.fireCommand.resizer().relative(this.killOnImpact.resizer()).set(0, 16, 100, 20);
        this.tickCommand.resizer().relative(this.fireCommand.resizer()).set(0, 25, 100, 20);
        this.impactCommand.resizer().relative(this.tickCommand.resizer()).set(0, 25, 100, 20);

        this.firingDelay.resizer().parent(this.area).set(0, 10, 100, 20).x(1, -110);
        this.fireRate.resizer().relative(this.firingDelay.resizer()).set(0, 25, 100, 20);
        this.accuracy.resizer().relative(this.fireRate.resizer()).set(0, 25, 100, 20);
        this.projectiles.resizer().relative(this.accuracy.resizer()).set(0, 25, 100, 20);
        this.ticking.resizer().relative(this.projectiles.resizer()).set(0, 25, 100, 20);
        this.lifeSpan.resizer().relative(this.ticking.resizer()).set(0, 25, 100, 20);
        this.speed.resizer().relative(this.lifeSpan.resizer()).set(0, 25, 100, 20);
        this.friction.resizer().relative(this.speed.resizer()).set(0, 25, 100, 20);
        this.gravity.resizer().relative(this.friction.resizer()).set(0, 25, 100, 20);

        this.firingDelay.setValue(this.info.delay);
        this.fireRate.setValue(this.info.fireRate);
        this.auto.button.setIsChecked(this.info.auto);
        this.accuracy.setValue(this.info.accuracy);
        this.projectiles.setValue(this.info.projectiles);
        this.fireCommand.setText(this.info.fireCommand);
        this.tickCommand.setText(this.info.tickCommand);
        this.impactCommand.setText(this.info.impactCommand);
        this.ticking.setValue(this.info.ticking);
        this.lifeSpan.setValue(this.info.lifeSpan);
        this.speed.setValue(this.info.speed);
        this.friction.setValue(this.info.friction);
        this.gravity.setValue(this.info.gravity);
        this.killOnImpact.button.setIsChecked(this.info.killOnImpact);

        this.elements.add(this.pickDefault, this.pickFiring, this.pickProjectile);
        this.elements.add(this.firingDelay, this.fireRate);
        this.elements.add(this.auto, this.accuracy, this.projectiles);
        this.elements.add(this.fireCommand, this.tickCommand, this.impactCommand);
        this.elements.add(this.ticking, this.lifeSpan, this.speed, this.friction, this.gravity, this.killOnImpact);
        this.elements.add(this.morphs);
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

        Dispatcher.sendToServer(new PacketGunInfo(this.info.toNBT()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}