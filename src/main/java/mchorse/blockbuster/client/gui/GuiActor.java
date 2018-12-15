package mchorse.blockbuster.client.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import mchorse.blockbuster.client.gui.elements.GuiCreativeMorphsMenu;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketActorRotate;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.mclib.client.gui.framework.GuiTooltip;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Actor configuration GUI
 *
 * This GUI is opened via player.openGui and has an id of 1. Most of the code
 * below is easy to understand, so no comments are needed.
 */
@SideOnly(Side.CLIENT)
public class GuiActor extends GuiScreen
{
    /* Cached localization strings */
    private String stringTitle = I18n.format("blockbuster.gui.actor.title");

    /* Domain objects, they're provide data */
    private EntityActor actor;

    /* GUI fields */
    private GuiButton done;
    private GuiButton pick;
    private GuiCheckBox invisible;
    private GuiSlider rotateX;
    private GuiSlider rotateY;
    private GuiCreativeMorphsMenu morphs;
    private GuiTooltip tooltip = new GuiTooltip();

    /**
     * Constructor for director block and skin manager item
     */
    public GuiActor(EntityActor actor)
    {
        ClientProxy.actorPack.pack.reload();

        Minecraft mc = Minecraft.getMinecraft();
        IMorphing cap = Morphing.get(mc.thePlayer);

        this.actor = actor;
        this.morphs = new GuiCreativeMorphsMenu(mc, 6, null, cap);
        this.morphs.setVisible(false);
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /* Actions */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.saveAndQuit();
        }
        else if (button.id == 1)
        {
            this.morphs.setVisible(true);
        }
    }

    /**
     * Save and quit this screen
     *
     * Depends on the fact where does this GUI was opened from, it either sends
     * modify actor packet, which modifies entity's properties directly, or
     * sends edit action to director map block
     */
    private void saveAndQuit()
    {
        MorphCell morph = this.morphs.getSelected();

        /* Update actor's morph */
        if (morph != null)
        {
            boolean invisible = !this.invisible.isChecked();

            Dispatcher.sendToServer(new PacketModifyActor(this.actor.getEntityId(), morph.current().morph, invisible));
        }

        /* Rotate the actor */
        float yaw = (float) this.rotateX.getValue();
        float pitch = (float) this.rotateY.getValue();
        Dispatcher.sendToServer(new PacketActorRotate(this.actor.getEntityId(), yaw, pitch));

        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    /* Handling input */

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        if (this.morphs.isEnabled())
        {
            this.morphs.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        super.handleMouseInput();

        int scroll = -Mouse.getEventDWheel();

        if (scroll == 0)
        {
            return;
        }

        if (this.morphs.isEnabled())
        {
            this.morphs.mouseScrolled(x, y, scroll);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.morphs.area.isInside(mouseX, mouseY))
        {
            if (this.morphs.isEnabled())
            {
                this.morphs.mouseClicked(mouseX, mouseY, mouseButton);
            }

            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.morphs.area.isInside(mouseX, mouseY))
        {
            if (this.morphs.isEnabled())
            {
                this.morphs.mouseReleased(mouseX, mouseY, state);
            }

            return;
        }

        super.mouseReleased(mouseX, mouseY, state);
    }

    /* Initiating GUI and drawing */

    /**
     * I think Mojang should come up with something better than hardcoded
     * positions and sizes for buttons. Something like HTML. Maybe I should
     * write this library (for constructing minecraft GUIs). Hm...
     */
    @Override
    public void initGui()
    {
        int x = 10;
        int w = 100;
        int y2 = this.height - 30;

        /* Initializing all GUI fields first */
        this.invisible = new GuiCheckBox(2, x, y2 - 20, I18n.format("blockbuster.gui.actor.invisible"), this.actor.invisible);

        /* Buttons */
        this.done = new GuiButton(0, x, y2, w, 20, I18n.format("blockbuster.gui.done"));
        this.pick = new GuiButton(1, x, 40, w, 20, I18n.format("blockbuster.gui.pick"));

        this.rotateX = new GuiSlider(-1, x, 40 + 30, w, 20, I18n.format("blockbuster.gui.actor.yaw") + " ", "", -180, 180, this.actor.rotationYaw, false, true);
        this.rotateY = new GuiSlider(-2, x, 40 + 30 * 2, w, 20, I18n.format("blockbuster.gui.actor.pitch") + " ", "", -90, 90, this.actor.rotationPitch, false, true);

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.done);
        this.buttonList.add(this.pick);
        this.buttonList.add(this.invisible);

        this.buttonList.add(this.rotateX);
        this.buttonList.add(this.rotateY);

        Area area = new Area();

        area.set(0, 0, this.width, this.height);
        this.morphs.resizer().parent(area).set(120, 30, 0, 0).w(1, -120).h(1, -30);
        this.morphs.resize(this.width, this.height);
    }

    private void fillData()
    {
        this.invisible.setIsChecked(!this.actor.invisible);
        this.morphs.setSelected(this.actor.getMorph());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int x = 10;
        int y = 10;

        /* Draw background */
        this.drawDefaultBackground();
        Gui.drawRect(0, 0, this.width, 30, 0x88000000);

        /* Draw labels: title */
        this.drawString(this.fontRendererObj, this.stringTitle, 20, y + 1, 0xffffffff);

        /* Draw entity in the center of the screen */
        int size = this.height / 3;

        y = this.height / 2 + (int) (size * 1.2);
        x = this.width / 2;

        MorphCell cell = this.morphs.getSelected();

        if (cell != null)
        {
            int center = this.width / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, -40);
            cell.current().morph.renderOnScreen(this.mc.thePlayer, center, this.height / 2 + this.height / 6, this.height / 4, 1.0F);
            GlStateManager.popMatrix();

            this.drawCenteredString(this.fontRendererObj, cell.current().morph.name, center, 40, 0xffffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        /* Apply yaw and pitch on the actor */
        this.actor.renderYawOffset = this.actor.prevRenderYawOffset = this.actor.rotationYawHead = this.actor.prevRotationYawHead = this.actor.rotationYaw = this.actor.prevRotationYaw = (float) this.rotateX.getValue();
        this.actor.rotationPitch = this.actor.prevRotationPitch = (float) this.rotateY.getValue();

        if (this.morphs.isVisible())
        {
            this.morphs.draw(this.tooltip, mouseX, mouseY, partialTicks);
        }
    }
}