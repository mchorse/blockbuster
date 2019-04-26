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
    public GuiButtonElement<GuiCheckBox> pitch;

    public GuiTrackpadElement delay;
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
    public GuiButtonElement<GuiCheckBox> vanish;
    public GuiTrackpadElement damage;

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
        this.pitch = GuiButtonElement.checkbox(mc, "Pitch", false, (b) -> this.info.pitch = b.button.isChecked());

        this.delay = new GuiTrackpadElement(mc, "Delay", (value) -> this.info.delay = value.intValue());
        this.delay.setLimit(0, Integer.MAX_VALUE, true);

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
        this.vanish = GuiButtonElement.checkbox(mc, "Vanish projectile", false, (b) -> this.info.vanish = b.button.isChecked());
        this.damage = new GuiTrackpadElement(mc, "Damage", (value) -> this.info.damage = value);

        /* Placement of the elements */
        this.morphs.resizer().parent(this.area).set(0, 0, 0, 0).w(1, 0).h(1, 0);
        this.pickDefault.resizer().parent(this.area).set(10, 10, 100, 20);
        this.pickFiring.resizer().relative(this.pickDefault.resizer()).set(0, 25, 100, 20);
        this.pickProjectile.resizer().relative(this.pickFiring.resizer()).set(0, 25, 100, 20);
        this.pitch.resizer().relative(this.pickProjectile.resizer()).set(0, 25, 100, 11);
        this.vanish.resizer().relative(this.pitch.resizer()).set(0, 16, 100, 11);
        this.fireCommand.resizer().relative(this.vanish.resizer()).set(0, 16, 100, 20);
        this.tickCommand.resizer().relative(this.fireCommand.resizer()).set(0, 25, 100, 20);
        this.impactCommand.resizer().relative(this.tickCommand.resizer()).set(0, 25, 100, 20);

        this.delay.resizer().parent(this.area).set(0, 10, 100, 20).x(1, -110);
        this.accuracy.resizer().relative(this.delay.resizer()).set(0, 25, 100, 20);
        this.projectiles.resizer().relative(this.accuracy.resizer()).set(0, 25, 100, 20);
        this.ticking.resizer().relative(this.projectiles.resizer()).set(0, 25, 100, 20);
        this.lifeSpan.resizer().relative(this.ticking.resizer()).set(0, 25, 100, 20);
        this.speed.resizer().relative(this.lifeSpan.resizer()).set(0, 25, 100, 20);
        this.friction.resizer().relative(this.speed.resizer()).set(0, 25, 100, 20);
        this.gravity.resizer().relative(this.friction.resizer()).set(0, 25, 100, 20);
        this.damage.resizer().relative(this.gravity.resizer()).set(0, 25, 100, 20);

        this.pitch.button.setIsChecked(this.info.pitch);
        this.delay.setValue(this.info.delay);
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
        this.vanish.button.setIsChecked(this.info.vanish);
        this.damage.setValue(this.info.damage);

        this.elements.add(this.pickDefault, this.pickFiring, this.pickProjectile, this.pitch);
        this.elements.add(this.delay, this.accuracy, this.projectiles);
        this.elements.add(this.fireCommand, this.tickCommand, this.impactCommand);
        this.elements.add(this.ticking, this.lifeSpan, this.speed, this.friction, this.gravity, this.vanish, this.damage);
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

        Dispatcher.sendToServer(new PacketGunInfo(this.info.toNBT(), 0));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}