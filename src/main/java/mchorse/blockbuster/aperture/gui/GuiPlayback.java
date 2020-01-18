package mchorse.blockbuster.aperture.gui;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.CameraAPI;
import mchorse.aperture.camera.CameraProfile;
import mchorse.aperture.camera.destination.AbstractDestination;
import mchorse.aperture.camera.destination.ClientDestination;
import mchorse.aperture.client.gui.GuiProfilesManager;
import mchorse.blockbuster.aperture.network.common.PacketPlaybackButton;
import mchorse.blockbuster.aperture.network.common.PacketRequestProfiles;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiButtonElement;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.widgets.buttons.GuiCirculate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * GUI playback
 *
 * This GUI is responsible for attaching some properties to playback button's
 * ItemStack.
 */
@SideOnly(Side.CLIENT)
public class GuiPlayback extends GuiBase
{
    private String stringTitle = I18n.format("blockbuster.gui.playback.title");
    private String stringCameraMode = I18n.format("blockbuster.gui.playback.camera_mode");
    private String stringProfile = I18n.format("blockbuster.gui.playback.profile");

    private GuiButtonElement<GuiCirculate> cameraMode;
    private GuiButtonElement<GuiButton> done;

    public GuiProfilesManager.GuiCameraProfilesList profiles;
    public Area frame = new Area();

    private String profile;
    private CameraProfile dummy = new CameraProfile(null);
    private String scene;
    private BlockPos director;

    public GuiPlayback()
    {
        Minecraft mc = Minecraft.getMinecraft();
        GuiCirculate circulate = new GuiCirculate(0, 0, 0, 0, 0);

        circulate.addLabel(I18n.format("blockbuster.gui.playback.nothing"));
        circulate.addLabel(I18n.format("blockbuster.gui.playback.play"));
        circulate.addLabel(I18n.format("blockbuster.gui.playback.load_profile"));

        this.profiles = new GuiProfilesManager.GuiCameraProfilesList(mc, (profile) -> {});
        this.profiles.setBackground();
        this.cameraMode = new GuiButtonElement<GuiCirculate>(mc, circulate, (b) -> this.setValue(b.button.getValue()));
        this.done = GuiButtonElement.button(mc, I18n.format("blockbuster.gui.done"), (b) -> this.saveAndQuit());

        this.profiles.resizer().set(0, 35, 0, 0).parent(this.frame).w(1, 0).h(1, -100);
        this.cameraMode.resizer().parent(this.frame).set(0, 0, 0, 20).y(1, -45).w(1, 0);
        this.done.resizer().parent(this.frame).set(0, 0, 0, 20).y(1, -20).w(1, 0);

        this.elements.add(this.profiles, this.cameraMode, this.done);

        /* Fill data */
        for (String filename : CameraAPI.getClientProfiles())
        {
            this.addDestination(new ClientDestination(filename));
        }

        this.profiles.sort();

        if (ClientProxy.server)
        {
            Dispatcher.sendToServer(new PacketRequestProfiles());
        }

        NBTTagCompound compound = Minecraft.getMinecraft().player.getHeldItemMainhand().getTagCompound();

        if (compound != null)
        {
            if (compound.hasKey("CameraPlay"))
            {
                this.setValue(1);
            }
            else if (compound.hasKey("CameraProfile"))
            {
                this.setValue(2, compound.getString("CameraProfile"));
            }
            else
            {
                this.setValue(0);
            }
        }
        else
        {
            this.setValue(0);
        }
    }

    public GuiPlayback setScene(String scene)
    {
        this.scene = scene;

        return this;
    }

    public GuiPlayback setDirector(BlockPos pos)
    {
        this.director = pos;

        return this;
    }

    public void addDestination(AbstractDestination destination)
    {
        this.profiles.add(new GuiProfilesManager.CameraProfileEntry(destination, this.dummy));
    }

    public void setValue(int value)
    {
        this.cameraMode.button.setValue(value);
        this.profiles.setVisible(value == 2);
    }

    public void setValue(int value, String profile)
    {
        this.profile = profile;
        this.setValue(value);
        this.selectCurrent(profile);
    }

    public void selectCurrent()
    {
        this.selectCurrent(this.profile);
    }

    public void selectCurrent(String profile)
    {
        for (int i = 0; i < this.profiles.getList().size(); i ++)
        {
            if (this.profiles.getList().get(i).destination.toResourceLocation().toString().equals(profile))
            {
                this.profiles.current = i;

                break;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    private void saveAndQuit()
    {
        Dispatcher.sendToServer(new PacketPlaybackButton(this.cameraMode.button.getValue(), this.getSelected(), this.scene, this.director));

        this.mc.displayGuiScreen(null);
    }

    private String getSelected()
    {
        GuiProfilesManager.CameraProfileEntry current = this.profiles.getCurrent();

        if (current != null)
        {
            return current.destination.toResourceLocation().toString();
        }

        return "";
    }

    @Override
    public void initGui()
    {
        this.frame.set(this.width / 2 - 75, 10, 150, this.height - 20);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        boolean isCameraProfile = this.cameraMode.button.getValue() == 2;

        this.drawDefaultBackground();
        this.drawString(this.fontRenderer, this.stringTitle, this.frame.x, this.frame.y, 0xffffffff);
        this.drawString(this.fontRenderer, this.stringCameraMode, this.frame.x, this.cameraMode.area.y - 12, 0xffcccccc);

        if (isCameraProfile)
        {
            this.drawString(this.fontRenderer, this.stringProfile, this.frame.x, this.profiles.area.y - 12, 0xffcccccc);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}