package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.List;

import mchorse.blockbuster.actor.ModelPack;
import mchorse.blockbuster.client.gui.GuiDirectorNew;
import mchorse.blockbuster.client.gui.utils.GuiUtils;
import mchorse.blockbuster.client.gui.utils.TabCompleter;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorEdit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Actor configuration GUI
 *
 * This GUI is opened via player.openGui and has an id of 1. Most of the code
 * below is easy to understand, so no comments are needed.
 */
@SideOnly(Side.CLIENT)
public class GuiReplay extends GuiScreen
{
    /* Cached localization strings */
    private String stringName = I18n.format("blockbuster.gui.actor.name");
    private String stringModel = I18n.format("blockbuster.gui.actor.model");
    private String stringFilename = I18n.format("blockbuster.gui.actor.filename");
    private String stringSkin = I18n.format("blockbuster.gui.actor.skin");
    private String stringInvincible = I18n.format("blockbuster.gui.actor.invincible");
    private String stringInvisible = I18n.format("blockbuster.gui.actor.invisible");

    /* Domain objects, they're provide data */
    private EntityActor actor;
    private Replay replay;
    private BlockPos pos;
    private int index;

    private ModelPack pack;
    private List<String> skins;

    /* GUI fields */
    private GuiTextField name;
    private GuiTextField model;
    private GuiTextField filename;
    private GuiTextField skin;
    private GuiCompleterViewer skinViewer;

    private GuiButton restore;
    private GuiButton remove;
    private GuiToggle invincible;
    private GuiToggle invisible;

    private TabCompleter completer;
    private GuiDirectorNew parent;

    /**
     * Constructor for director map block
     */
    public GuiReplay(GuiDirectorNew parent, BlockPos pos)
    {
        this.pack = ClientProxy.actorPack.pack;
        this.pack.reload();

        this.actor = new EntityActor(Minecraft.getMinecraft().theWorld);
        this.skins = this.pack.getSkins(this.actor.model);

        this.parent = parent;
        this.pos = pos;
    }

    public void select(Replay replay, int index)
    {
        this.replay = replay;
        this.index = index;

        if (replay != null)
        {
            this.fillData();
        }
    }

    /* Actions */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 2)
        {
            this.parent.remove(this.index);
        }
        else if (button.id == 3)
        {
            this.model.setText("");
            this.skin.setText("");
            this.invisible.setValue(false);
        }
        else if (button.id == 4)
        {
            this.invincible.toggle();
        }
        else if (button.id == 5)
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
    public void save(boolean update)
    {
        String filename = this.filename.getText();
        String name = this.name.getText();
        String model = this.model.getText();
        String skin = this.skin.getText();
        boolean invincible = this.invincible.getValue();
        boolean invisible = this.invisible.getValue();

        Replay value = new Replay();
        value.id = filename;
        value.name = name;
        value.invincible = invincible;

        value.model = model;
        value.skin = skin;
        value.invisible = invisible;

        value.actor = this.replay.actor;

        Dispatcher.sendToServer(new PacketDirectorEdit(this.pos, value, this.index, update));
    }

    /* Setting up child GUI screens */

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);
        this.skinViewer.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.skinViewer.handleMouseInput();
    }

    /* Handling input */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.skinViewer.isInside(mouseX, mouseY)) return;

        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean modelUsedFocused = this.model.isFocused();
        boolean skinUsedFocused = this.skin.isFocused();

        this.name.mouseClicked(mouseX, mouseY, mouseButton);
        this.model.mouseClicked(mouseX, mouseY, mouseButton);
        this.filename.mouseClicked(mouseX, mouseY, mouseButton);
        this.skin.mouseClicked(mouseX, mouseY, mouseButton);

        /* Populate the tab completer */
        if (!modelUsedFocused && this.model.isFocused())
        {
            List<String> models = this.pack.getModels();

            models.add(0, "steve");
            models.add(0, "alex");

            this.completer.setAllCompletions(models);
            this.completer.setField(this.model);
            this.skinViewer.updateRect(128, 20 + 20 - 1, 120, 100);
        }
        else if (!skinUsedFocused && this.skin.isFocused())
        {
            this.skins = this.pack.getSkins(this.model.getText());
            this.completer.setAllCompletions(this.skins);
            this.completer.setField(this.skin);
            this.skinViewer.updateRect(128, 20 + 60 - 1, 120, 100);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        /* 15 = tab */
        if (keyCode == 15 && (this.skin.isFocused() || this.model.isFocused()))
        {
            this.completer.complete();

            int size = this.completer.getCompletions().size();
            this.skinViewer.setHeight(size * 20);
            this.skinViewer.setHidden(size == 0);
        }
        else
        {
            this.completer.resetDidComplete();
            this.skinViewer.setHidden(true);
        }

        this.name.textboxKeyTyped(typedChar, keyCode);
        this.model.textboxKeyTyped(typedChar, keyCode);
        this.filename.textboxKeyTyped(typedChar, keyCode);
        this.skin.textboxKeyTyped(typedChar, keyCode);
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
        int margin = 8;

        int x = 120 + margin;
        int w = 100;
        int y = 20;
        int x2 = this.width - margin - w;
        int y2 = this.height - y - margin;

        /* Initializing all GUI fields first */
        this.model = new GuiTextField(-1, this.fontRendererObj, x + 1, y + 1, w - 2, 18);
        this.skin = new GuiTextField(1, this.fontRendererObj, x + 1, y + 41, w - 2, 18);
        this.invisible = new GuiToggle(5, x, y + 80, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        this.name = new GuiTextField(-1, this.fontRendererObj, x + 1, y2 - 79, w - 2, 18);
        this.filename = new GuiTextField(-1, this.fontRendererObj, x + 1, y2 - 39, w - 2, 18);
        this.invincible = new GuiToggle(4, x, y2, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        /* Buttons */
        this.restore = new GuiButton(3, this.width - margin - 100, margin, 100, 20, I18n.format("blockbuster.gui.restore"));
        this.remove = new GuiButton(2, this.width - margin - 100, margin + 25, 100, 20, I18n.format("blockbuster.gui.remove"));

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.remove);
        this.buttonList.add(this.restore);
        this.buttonList.add(this.invincible);
        this.buttonList.add(this.invisible);

        /* Additional utilities */
        this.completer = new TabCompleter(this.skin);
        this.completer.setAllCompletions(this.skins);

        this.skinViewer = new GuiCompleterViewer(this.completer);
        this.skinViewer.updateRect(x, y + 60 - 1, w, 100);
        this.skinViewer.setHidden(true);
    }

    private void fillData()
    {
        if (this.replay == null) return;

        this.model.setText(this.replay.model);
        this.skin.setText(this.replay.skin);
        this.invisible.setValue(this.replay.invisible);

        this.name.setText(this.replay.name);
        this.filename.setText(this.replay.id);
        this.invincible.setValue(this.replay.invincible);

        this.name.setMaxStringLength(30);
        this.filename.setMaxStringLength(40);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.replay == null) return;

        int x = 128;
        int y = 8;

        int y2 = this.height - 40;

        /* Draw labels for visual properties */
        this.drawString(this.fontRendererObj, this.stringModel, x, y, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringSkin, x, y + 40, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringInvisible, x, y + 80, 0xffcccccc);

        /* Draw labels for meta properties */
        this.drawString(this.fontRendererObj, this.stringName, x, y2 - 80, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringFilename, x, y2 - 40, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringInvincible, x, y2, 0xffcccccc);

        if (this.replay.actor != null)
        {
            this.drawCenteredString(this.fontRendererObj, "Attached to Actor", 120 + (this.width - 120) / 2, y2 + 16, 0xffffffff);
        }

        /* Draw entity in the center of the screen */
        int size = this.height / 4;

        y = this.height / 2 + size;
        x = x + (this.width - x) / 2;

        String model = this.actor.model;
        String skin = this.actor.skin;
        boolean invisible = this.actor.invisible;

        this.actor.model = this.model.getText();
        this.actor.skin = this.skin.getText();
        this.actor.invisible = this.invisible.getValue();
        this.actor.renderName = false;
        GuiUtils.drawEntityOnScreen(x, y, size, x - mouseX, (y - size) - mouseY, this.actor);
        this.actor.renderName = true;
        this.actor.model = model;
        this.actor.skin = skin;
        this.actor.invisible = invisible;

        /* Draw GUI elements */
        this.name.drawTextBox();
        this.model.drawTextBox();
        this.filename.drawTextBox();
        this.skin.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.skinViewer.drawScreen(mouseX, mouseY, partialTicks);
    }
}