package mchorse.blockbuster.client.gui;

import java.io.IOException;

import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiToggle;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyActor;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
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
    private String stringInvisible = I18n.format("blockbuster.gui.actor.invisible");

    /* Domain objects, they're provide data */
    private EntityActor actor;

    /* GUI fields */
    private GuiButton done;
    private GuiButton pick;
    private GuiToggle invisible;
    private GuiMorphsPopup morphs;

    /**
     * Constructor for director block and skin manager item
     */
    public GuiActor(EntityActor actor)
    {
        ClientProxy.actorPack.pack.reload();

        this.actor = actor;
        this.morphs = new GuiMorphsPopup(6, actor.getMorph(), Morphing.get(Minecraft.getMinecraft().thePlayer));
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
            this.morphs.morphs.setHidden(false);
        }
        else if (button.id == 2)
        {
            this.invisible.toggle();
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
        MorphCell morph = this.morphs.morphs.getSelected();

        if (morph != null)
        {
            boolean invisible = this.invisible.getValue();

            Dispatcher.sendToServer(new PacketModifyActor(this.actor.getEntityId(), morph.morph, invisible));
        }

        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    /* Setting up child GUI screens */

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.morphs.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        this.morphs.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        this.morphs.handleKeyboardInput();
        super.handleKeyboardInput();
    }

    /* Handling input */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.morphs.isInside(mouseX, mouseY))
        {
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
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
        this.invisible = new GuiToggle(2, x, y2 - 30, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        /* Buttons */
        this.done = new GuiButton(0, x, y2, w, 20, I18n.format("blockbuster.gui.done"));
        this.pick = new GuiButton(1, x, 40, w, 20, I18n.format("blockbuster.gui.pick"));

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.done);
        this.buttonList.add(this.pick);
        this.buttonList.add(this.invisible);

        this.morphs.updateRect(120, 40, this.width - 128, this.height - 50);
    }

    private void fillData()
    {
        this.invisible.setValue(this.actor.invisible);
        this.morphs.morphs.setSelected(this.actor.getMorph());
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

        /* Draw labels for visual properties */
        this.drawString(this.fontRendererObj, this.stringInvisible, x, this.height - 70, 0xffffffff);

        /* Draw entity in the center of the screen */
        int size = this.height / 3;

        y = this.height / 2 + (int) (size * 1.2);
        x = this.width / 2;

        MorphCell cell = this.morphs.morphs.getSelected();

        if (cell != null)
        {
            int center = this.width / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, -40);
            cell.morph.renderOnScreen(Minecraft.getMinecraft().thePlayer, center, this.height / 2 + this.height / 6, this.height / 4, 1.0F);
            GlStateManager.popMatrix();

            this.drawCenteredString(this.fontRendererObj, cell.name, center, 40, 0xffffffff);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}