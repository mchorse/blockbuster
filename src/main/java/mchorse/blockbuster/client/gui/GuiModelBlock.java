package mchorse.blockbuster.client.gui;

import java.io.IOException;

import mchorse.blockbuster.client.gui.elements.GuiMorphsPopup;
import mchorse.blockbuster.client.gui.widgets.GuiTrackpad;
import mchorse.blockbuster.client.gui.widgets.GuiTrackpad.ITrackpadListener;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.tileentity.TileEntityModel;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.PacketModifyModelBlock;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.client.gui.elements.GuiCreativeMorphs.MorphCell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiModelBlock extends GuiScreen implements ITrackpadListener
{
    /* Cached localization strings */
    private String stringTitle = I18n.format("blockbuster.gui.actor.title");

    /* Domain objects, they're provide data */
    private TileEntityModel model;

    /* GUI fields */
    private GuiButton done;
    private GuiButton pick;
    private GuiTrackpad rotateX;
    private GuiTrackpad rotateY;

    private GuiTrackpad x;
    private GuiTrackpad y;
    private GuiTrackpad z;
    private GuiMorphsPopup morphs;

    /**
     * Constructor for director block and skin manager item
     */
    public GuiModelBlock(TileEntityModel model)
    {
        ClientProxy.actorPack.pack.reload();

        this.model = model;
        this.morphs = new GuiMorphsPopup(6, model.morph, Morphing.get(Minecraft.getMinecraft().thePlayer));
    }

    @Override
    public void setTrackpadValue(GuiTrackpad trackpad, float value)
    {
        if (trackpad == this.rotateX)
        {
            this.model.rotateX = value;
        }
        else if (trackpad == this.rotateY)
        {
            this.model.rotateY = value;
        }
        else if (trackpad == this.x)
        {
            this.model.x = value;
        }
        else if (trackpad == this.y)
        {
            this.model.y = value;
        }
        else if (trackpad == this.z)
        {
            this.model.z = value;
        }
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

        /* Update model's morph */
        float rotateX = this.rotateX.value;
        float rotateY = this.rotateY.value;

        float x = this.rotateX.value;
        float y = this.rotateY.value;
        float z = this.rotateX.value;

        Dispatcher.sendToServer(new PacketModifyModelBlock(this.model.getPos(), morph == null ? null : morph.current().morph, rotateX, rotateY, x, y, z));
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
        this.rotateX.mouseClicked(mouseX, mouseY, mouseButton);
        this.rotateY.mouseClicked(mouseX, mouseY, mouseButton);
        this.x.mouseClicked(mouseX, mouseY, mouseButton);
        this.y.mouseClicked(mouseX, mouseY, mouseButton);
        this.z.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.morphs.isInside(mouseX, mouseY))
        {
            return;
        }

        super.mouseReleased(mouseX, mouseY, state);
        this.rotateX.mouseReleased(mouseX, mouseY, state);
        this.rotateY.mouseReleased(mouseX, mouseY, state);
        this.x.mouseReleased(mouseX, mouseY, state);
        this.y.mouseReleased(mouseX, mouseY, state);
        this.z.mouseReleased(mouseX, mouseY, state);
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

        /* Buttons */
        this.done = new GuiButton(0, x, y2, w, 20, I18n.format("blockbuster.gui.done"));
        this.pick = new GuiButton(1, x, 40, w, 20, I18n.format("blockbuster.gui.pick"));

        this.rotateX = new GuiTrackpad(this, this.fontRendererObj);
        this.rotateX.update(x, 40 + 30, w, 20);
        this.rotateX.title = I18n.format("blockbuster.gui.actor.yaw");

        this.rotateY = new GuiTrackpad(this, this.fontRendererObj);
        this.rotateY.update(x, 40 + 30 * 2, w, 20);
        this.rotateY.title = I18n.format("blockbuster.gui.actor.pitch");

        this.x = new GuiTrackpad(this, this.fontRendererObj);
        this.x.update(x, 40 + 30 * 3, w, 20);
        this.x.title = "X";

        this.y = new GuiTrackpad(this, this.fontRendererObj);
        this.y.update(x, 40 + 30 * 4, w, 20);
        this.y.title = "Y";

        this.z = new GuiTrackpad(this, this.fontRendererObj);
        this.z.update(x, 40 + 30 * 5, w, 20);
        this.z.title = "Z";

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.done);
        this.buttonList.add(this.pick);

        this.morphs.updateRect(120, 30, this.width - 120, this.height - 30);
    }

    private void fillData()
    {
        this.morphs.morphs.setSelected(this.model.morph);
        this.rotateX.setValue(this.model.rotateX);
        this.rotateY.setValue(this.model.rotateY);
        this.x.setValue(this.model.x);
        this.y.setValue(this.model.y);
        this.z.setValue(this.model.z);
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

        MorphCell cell = this.morphs.morphs.getSelected();

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
        this.rotateX.draw(mouseX, mouseY, partialTicks);
        this.rotateY.draw(mouseX, mouseY, partialTicks);
        this.x.draw(mouseX, mouseY, partialTicks);
        this.y.draw(mouseX, mouseY, partialTicks);
        this.z.draw(mouseX, mouseY, partialTicks);

        /* Apply yaw and pitch on the actor */
        this.model.morph = cell == null ? null : cell.current().morph;

        this.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}