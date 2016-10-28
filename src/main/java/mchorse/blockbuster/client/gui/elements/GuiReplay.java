package mchorse.blockbuster.client.gui.elements;

import java.io.IOException;
import java.util.List;

import mchorse.blockbuster.actor.ModelPack;
import mchorse.blockbuster.client.gui.GuiDirector;
import mchorse.blockbuster.client.gui.utils.GuiUtils;
import mchorse.blockbuster.client.gui.utils.TabCompleter;
import mchorse.blockbuster.client.gui.widgets.GuiCompleterViewer;
import mchorse.blockbuster.client.gui.widgets.buttons.GuiToggle;
import mchorse.blockbuster.common.ClientProxy;
import mchorse.blockbuster.common.entity.EntityActor;
import mchorse.blockbuster.common.tileentity.director.Replay;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.director.PacketDirectorEdit;
import mchorse.blockbuster.utils.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Subview in director block GUI
 *
 * This GUI is dependent on {@link GuiDirector} UI. This method is responsible
 * for manipulating the selected {@link Replay} from {@link GuiReplays}.
 */
@SideOnly(Side.CLIENT)
public class GuiReplay extends GuiScreen
{
    /* Cached localization strings used in drawScreen method */
    private String stringName = I18n.format("blockbuster.gui.actor.name");
    private String stringModel = I18n.format("blockbuster.gui.actor.model");
    private String stringFilename = I18n.format("blockbuster.gui.actor.filename");
    private String stringSkin = I18n.format("blockbuster.gui.actor.skin");
    private String stringInvincible = I18n.format("blockbuster.gui.actor.invincible");
    private String stringInvisible = I18n.format("blockbuster.gui.actor.invisible");
    private String stringAttached = I18n.format("blockbuster.gui.actor.attached");

    /* Domain objects, they provide data */
    private EntityActor actor;
    private Replay replay;
    private BlockPos pos;
    private int index;

    /* More cached stuff */
    private ModelPack pack;
    private List<String> skins;

    /* GUI fields */
    private GuiTextField name;
    private GuiTextField model;
    private GuiTextField filename;
    private GuiTextField skin;

    /* Buttons */
    private GuiButton detach;
    private GuiButton remove;
    private GuiToggle invincible;
    private GuiToggle invisible;

    /* Widgets and stuff */
    private TabCompleter completer;
    private GuiDirector parent;
    private GuiCompleterViewer skinViewer;

    /**
     * Constructor for director map block
     */
    public GuiReplay(GuiDirector parent, BlockPos pos)
    {
        this.pack = ClientProxy.actorPack.pack;
        this.pack.reload();

        this.actor = new EntityActor(Minecraft.getMinecraft().theWorld);
        this.skins = this.pack.getSkins(this.actor.model);

        this.parent = parent;
        this.pos = pos;
    }

    /**
     * Select the given replay with index. If replay is null (i.e. replay was
     * deselected). If replay isn't null, then fill the data in GUI fields.
     */
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
            this.replay.actor = null;
            this.parent.detach(this.index);
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
     * Save the replay
     *
     * Saves the replay, by sending the replay over to the server. Given
     * argument determines if the cast is going to be updated.
     */
    public void save(boolean update)
    {
        Replay value = new Replay();
        value.id = this.filename.getText();
        value.name = this.name.getText();
        value.invincible = this.invincible.getValue();

        value.model = this.model.getText();
        value.skin = RLUtils.fromString(this.skin.getText(), value.model);
        value.invisible = this.invisible.getValue();

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
        int y2 = this.height - y - margin;

        /* Initializing all GUI fields first */
        this.model = new GuiTextField(-1, this.fontRendererObj, x + 1, y + 1, w - 2, 18);
        this.skin = new GuiTextField(1, this.fontRendererObj, x + 1, y + 41, w - 2, 18);
        this.invisible = new GuiToggle(5, x, y + 80, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        this.name = new GuiTextField(-1, this.fontRendererObj, x + 1, y2 - 79, w - 2, 18);
        this.filename = new GuiTextField(-1, this.fontRendererObj, x + 1, y2 - 39, w - 2, 18);
        this.invincible = new GuiToggle(4, x, y2, w, 20, I18n.format("blockbuster.no"), I18n.format("blockbuster.yes"));

        /* Buttons */
        this.remove = new GuiButton(2, this.width - margin - 100, margin + 25, 100, 20, I18n.format("blockbuster.gui.remove"));
        this.detach = new GuiButton(3, this.width - margin - 100, margin, 100, 20, I18n.format("blockbuster.gui.detach"));

        /* And then, we're configuring them and injecting input data */
        this.fillData();

        this.buttonList.add(this.remove);
        this.buttonList.add(this.detach);
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

        this.skin.setMaxStringLength(120);

        this.name.setMaxStringLength(30);
        this.filename.setMaxStringLength(40);

        this.model.setText(this.replay.model);
        this.skin.setText(RLUtils.fromResource(this.replay.skin));
        this.invisible.setValue(this.replay.invisible);

        this.name.setText(this.replay.name);
        this.filename.setText(this.replay.id);
        this.invincible.setValue(this.replay.invincible);
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
            this.drawCenteredString(this.fontRendererObj, this.stringAttached, 120 + (this.width - 120) / 2, y2 + 16, 0xffffffff);
        }

        /* Draw entity in the center of the screen */
        int size = this.height / 4;

        y = this.height / 2 + size;
        x = x + (this.width - x) / 2;

        String model = this.actor.model;
        ResourceLocation skin = this.actor.skin;
        boolean invisible = this.actor.invisible;

        this.actor.model = this.model.getText();
        this.actor.skin = RLUtils.fromString(this.skin.getText(), this.actor.model);
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