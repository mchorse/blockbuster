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
    private String stringTitle = I18n.format("blockbuster.gui.model_block.title");

    /* Domain objects, they're provide data */
    private TileEntityModel model;
    private TileEntityModel temp;

    /* GUI fields */
    private GuiButton done;
    private GuiButton pick;

    private GuiTrackpad yaw;
    private GuiTrackpad pitch;
    private GuiTrackpad body;

    private GuiTrackpad x;
    private GuiTrackpad y;
    private GuiTrackpad z;

    private GuiTrackpad sx;
    private GuiTrackpad sy;
    private GuiTrackpad sz;

    private GuiTrackpad rx;
    private GuiTrackpad ry;
    private GuiTrackpad rz;

    private GuiMorphsPopup morphs;

    /**
     * Constructor for director block and skin manager item
     */
    public GuiModelBlock(TileEntityModel model)
    {
        ClientProxy.actorPack.pack.reload();

        this.model = model;
        this.temp = new TileEntityModel();
        this.temp.copyData(model);

        this.morphs = new GuiMorphsPopup(6, model.morph, Morphing.get(Minecraft.getMinecraft().thePlayer));
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void setTrackpadValue(GuiTrackpad trackpad, float value)
    {
        if (trackpad == this.yaw)
        {
            this.model.rotateYawHead = value;
        }
        else if (trackpad == this.pitch)
        {
            this.model.rotatePitch = value;
        }
        else if (trackpad == this.body)
        {
            this.model.rotateBody = value;
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
        else if (trackpad == this.rx)
        {
            this.model.rx = value;
        }
        else if (trackpad == this.ry)
        {
            this.model.ry = value;
        }
        else if (trackpad == this.rz)
        {
            this.model.rz = value;
        }
        else if (trackpad == this.sx)
        {
            this.model.sx = value;
        }
        else if (trackpad == this.sy)
        {
            this.model.sy = value;
        }
        else if (trackpad == this.sz)
        {
            this.model.sz = value;
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
        PacketModifyModelBlock packet = new PacketModifyModelBlock(this.model.getPos(), morph == null ? null : morph.current().morph);

        packet.setBody(this.yaw.value, this.pitch.value, this.body.value);
        packet.setPos(this.x.value, this.y.value, this.z.value);
        packet.setRot(this.rx.value, this.ry.value, this.rz.value);
        packet.setScale(this.sx.value, this.sy.value, this.sz.value);

        Dispatcher.sendToServer(packet);
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

        this.yaw.mouseClicked(mouseX, mouseY, mouseButton);
        this.pitch.mouseClicked(mouseX, mouseY, mouseButton);
        this.body.mouseClicked(mouseX, mouseY, mouseButton);
        this.x.mouseClicked(mouseX, mouseY, mouseButton);
        this.y.mouseClicked(mouseX, mouseY, mouseButton);
        this.z.mouseClicked(mouseX, mouseY, mouseButton);
        this.rx.mouseClicked(mouseX, mouseY, mouseButton);
        this.ry.mouseClicked(mouseX, mouseY, mouseButton);
        this.rz.mouseClicked(mouseX, mouseY, mouseButton);
        this.sx.mouseClicked(mouseX, mouseY, mouseButton);
        this.sy.mouseClicked(mouseX, mouseY, mouseButton);
        this.sz.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.morphs.isInside(mouseX, mouseY))
        {
            return;
        }

        super.mouseReleased(mouseX, mouseY, state);

        this.yaw.mouseReleased(mouseX, mouseY, state);
        this.pitch.mouseReleased(mouseX, mouseY, state);
        this.body.mouseReleased(mouseX, mouseY, state);
        this.x.mouseReleased(mouseX, mouseY, state);
        this.y.mouseReleased(mouseX, mouseY, state);
        this.z.mouseReleased(mouseX, mouseY, state);
        this.rx.mouseReleased(mouseX, mouseY, state);
        this.ry.mouseReleased(mouseX, mouseY, state);
        this.rz.mouseReleased(mouseX, mouseY, state);
        this.sx.mouseReleased(mouseX, mouseY, state);
        this.sy.mouseReleased(mouseX, mouseY, state);
        this.sz.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.model.copyData(this.temp);

            super.keyTyped(typedChar, keyCode);
        }

        this.yaw.keyTyped(typedChar, keyCode);
        this.pitch.keyTyped(typedChar, keyCode);
        this.body.keyTyped(typedChar, keyCode);
        this.x.keyTyped(typedChar, keyCode);
        this.y.keyTyped(typedChar, keyCode);
        this.z.keyTyped(typedChar, keyCode);
        this.rx.keyTyped(typedChar, keyCode);
        this.ry.keyTyped(typedChar, keyCode);
        this.rz.keyTyped(typedChar, keyCode);
        this.sx.keyTyped(typedChar, keyCode);
        this.sy.keyTyped(typedChar, keyCode);
        this.sz.keyTyped(typedChar, keyCode);
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
        int x = this.width - 90;
        int y = this.height - 80;
        int w = 80;

        /* Buttons */
        this.done = new GuiButton(0, x, 5, w, 20, I18n.format("blockbuster.gui.done"));
        this.pick = new GuiButton(1, x - 90, 5, w, 20, I18n.format("blockbuster.gui.pick"));

        x = 10;

        this.yaw = new GuiTrackpad(this, this.fontRendererObj).update(x, y, w, 20).setTitle(I18n.format("blockbuster.gui.actor.yaw"));
        this.pitch = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 25, w, 20).setTitle(I18n.format("blockbuster.gui.actor.pitch"));
        this.body = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 50, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.body"));

        x += 90;

        this.x = new GuiTrackpad(this, this.fontRendererObj).update(x, y, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.x"));
        this.y = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 25, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.y"));
        this.z = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 50, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.z"));

        x = this.width - 180;

        this.rx = new GuiTrackpad(this, this.fontRendererObj).update(x, y, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.x"));
        this.ry = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 25, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.y"));
        this.rz = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 50, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.z"));

        x += 90;

        this.sx = new GuiTrackpad(this, this.fontRendererObj).update(x, y, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.x"));
        this.sy = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 25, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.y"));
        this.sz = new GuiTrackpad(this, this.fontRendererObj).update(x, y + 50, w, 20).setTitle(I18n.format("blockbuster.gui.model_block.z"));

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.done);
        this.buttonList.add(this.pick);

        this.morphs.updateRect(0, 30, this.width, this.height - 30);
    }

    private void fillData()
    {
        this.morphs.morphs.setSelected(this.model.morph);

        this.yaw.setValue(this.model.rotateYawHead);
        this.pitch.setValue(this.model.rotatePitch);
        this.body.setValue(this.model.rotateBody);
        this.x.setValue(this.model.x);
        this.y.setValue(this.model.y);
        this.z.setValue(this.model.z);
        this.rx.setValue(this.model.rx);
        this.ry.setValue(this.model.ry);
        this.rz.setValue(this.model.rz);
        this.sx.setValue(this.model.sx);
        this.sy.setValue(this.model.sy);
        this.sz.setValue(this.model.sz);
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

        this.yaw.draw(mouseX, mouseY, partialTicks);
        this.pitch.draw(mouseX, mouseY, partialTicks);
        this.body.draw(mouseX, mouseY, partialTicks);
        this.x.draw(mouseX, mouseY, partialTicks);
        this.y.draw(mouseX, mouseY, partialTicks);
        this.z.draw(mouseX, mouseY, partialTicks);
        this.rx.draw(mouseX, mouseY, partialTicks);
        this.ry.draw(mouseX, mouseY, partialTicks);
        this.rz.draw(mouseX, mouseY, partialTicks);
        this.sx.draw(mouseX, mouseY, partialTicks);
        this.sy.draw(mouseX, mouseY, partialTicks);
        this.sz.draw(mouseX, mouseY, partialTicks);

        this.drawString(this.fontRendererObj, I18n.format("blockbuster.gui.model_block.entity"), this.yaw.area.x + 2, this.yaw.area.y - 12, 0xcccccc);
        this.drawString(this.fontRendererObj, I18n.format("blockbuster.gui.model_block.translate"), this.x.area.x + 2, this.x.area.y - 12, 0xcccccc);
        this.drawString(this.fontRendererObj, I18n.format("blockbuster.gui.model_block.rotate"), this.rx.area.x + 2, this.rx.area.y - 12, 0xcccccc);
        this.drawString(this.fontRendererObj, I18n.format("blockbuster.gui.model_block.scale"), this.sx.area.x + 2, this.sx.area.y - 12, 0xcccccc);

        /* Apply yaw and pitch on the actor */
        this.model.morph = cell == null ? null : cell.current().morph;

        this.morphs.drawScreen(mouseX, mouseY, partialTicks);
    }
}