package noname.blockbuster.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiSlider;
import noname.blockbuster.client.gui.elements.GuiChildScreen;
import noname.blockbuster.client.gui.elements.GuiParentScreen;
import noname.blockbuster.client.gui.elements.GuiToggle;
import noname.blockbuster.entity.EntityCamera;
import noname.blockbuster.network.Dispatcher;
import noname.blockbuster.network.common.PacketCameraAttributes;

/**
 * Camera configuration GUI
 *
 * This GUI is invoked player.openGui and has an id of 0. The code below is
 * pretty easy to understand, so no comments are needed.
 *
 * For more information, see vanilla GUI screens.
 */
public class GuiCamera extends GuiChildScreen
{
    /* Localized strings */
    private String title = I18n.format("blockbuster.gui.camera.title");
    private String stringName = I18n.format("blockbuster.gui.actor.name");
    private String stringSpeed = I18n.format("blockbuster.gui.camera.speed");
    private String stringRate = I18n.format("blockbuster.gui.camera.rate");
    private String stringMax = I18n.format("blockbuster.gui.camera.max");
    private String stringDir = I18n.format("blockbuster.gui.camera.dir");

    /* GUI fields */
    private GuiTextField name;

    private GuiSlider speed;
    private GuiSlider accelerationRate;
    private GuiSlider accelerationMax;
    private GuiToggle canFly;
    private GuiButton done;

    /* Input data */
    private EntityCamera camera;

    public GuiCamera(GuiParentScreen parent, EntityCamera entity)
    {
        super(parent);
        this.camera = entity;
    }

    /* Action handling */

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 4)
        {
            this.saveAndExit();
        }
        else if (button.id == 3)
        {
            this.canFly.toggle();
        }
    }

    private void saveAndExit()
    {
        String name = this.name.getText();
        int id = this.camera.getEntityId();
        float speed = (float) this.speed.getValue() / 4;
        float rate = (float) this.accelerationRate.getValue();
        float max = (float) this.accelerationMax.getValue() / 100;
        boolean canFly = this.canFly.getValue();

        Dispatcher.getInstance().sendToServer(new PacketCameraAttributes(id, name, speed, rate, max, canFly));

        this.close();
    }

    /* Input handling */

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.name.mouseClicked(mouseX, mouseY, mouseButton);
        this.speed.mousePressed(this.mc, mouseX, mouseY);
        this.accelerationRate.mousePressed(this.mc, mouseX, mouseY);
        this.accelerationMax.mousePressed(this.mc, mouseX, mouseY);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);

        this.speed.mouseReleased(mouseX, mouseY);
        this.accelerationRate.mouseReleased(mouseX, mouseY);
        this.accelerationMax.mouseReleased(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        this.name.textboxKeyTyped(typedChar, keyCode);
    }

    /* GUI and drawing */

    @Override
    public void initGui()
    {
        int w = 120;
        int x = 30;
        int y = 25;

        /* Initializing the fields */
        this.name = new GuiTextField(6, this.fontRendererObj, x + 1, y + 1, w - 2, 18);
        this.speed = new GuiSlider(0, x, y + 40, w, 20, "~", " blocks/s", 0, 5, 0, true, true);
        this.accelerationRate = new GuiSlider(1, x, y + 80, w, 20, "", "", 0, 0.5, 0, true, true);
        this.accelerationMax = new GuiSlider(2, x, y + 120, w, 20, "", "%", 0, 100, 0, true, true);
        this.canFly = new GuiToggle(3, x, y + 160, w, 20, I18n.format("blockbuster.gui.camera.canFly"), I18n.format("blockbuster.gui.camera.cantFly"));
        this.done = new GuiButton(4, x, this.height - 40, w, 20, I18n.format("blockbuster.gui.done"));

        /* Adding buttons to the list */
        this.buttonList.add(this.speed);
        this.buttonList.add(this.accelerationRate);
        this.buttonList.add(this.accelerationMax);
        this.buttonList.add(this.canFly);
        this.buttonList.add(this.done);

        /* Setting values */
        this.speed.precision = this.accelerationMax.precision = 1;
        this.accelerationRate.precision = 2;

        this.speed.setValue(this.camera.speed * 4);
        this.accelerationRate.setValue(this.camera.accelerationRate);
        this.accelerationMax.setValue(this.camera.accelerationMax * 100);

        this.speed.updateSlider();
        this.accelerationRate.updateSlider();
        this.accelerationMax.updateSlider();

        this.name.setText(this.camera.getCustomNameTag());
        this.canFly.setValue(this.camera.canFly);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int x = 30;
        int y = 15;

        this.drawDefaultBackground();
        this.drawString(this.fontRendererObj, this.title, x + 120 + 20, 15, 0xffffffff);

        this.drawString(this.fontRendererObj, this.stringName, x, y, 0xffcccccc);
        this.name.drawTextBox();
        this.drawString(this.fontRendererObj, this.stringSpeed, x, y + 40, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringRate, x, y + 80, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringMax, x, y + 120, 0xffcccccc);
        this.drawString(this.fontRendererObj, this.stringDir, x, y + 160, 0xffcccccc);

        int size = this.height / 2;
        y = this.height / 2 + size / 2;
        x = x + 120 + 30;
        x = x + (this.width - x) / 2;

        this.camera.renderName = false;
        GuiActor.drawEntityOnScreen(x, y, size, x - mouseX, y - mouseY, this.camera);
        this.camera.renderName = true;

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}