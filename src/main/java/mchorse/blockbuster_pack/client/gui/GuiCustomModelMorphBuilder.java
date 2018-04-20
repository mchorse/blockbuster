package mchorse.blockbuster_pack.client.gui;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.builder.GuiAbstractMorphBuilder;
import mchorse.metamorph.client.gui.utils.GuiDropDownField;
import mchorse.metamorph.client.gui.utils.GuiDropDownField.DropDownItem;
import mchorse.metamorph.client.gui.utils.GuiDropDownField.IDropDownListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiCustomModelMorphBuilder extends GuiAbstractMorphBuilder implements IDropDownListener
{
    public GuiTextField model;
    public GuiTextField texture;
    public GuiDropDownField poses;
    public GuiCheckBox poseOnSneak;

    public GuiCustomModelMorphBuilder()
    {
        super();

        this.model = new GuiTextField(0, font, 0, 0, 0, 0);
        this.model.setMaxStringLength(200);
        this.texture = new GuiTextField(0, font, 0, 0, 0, 0);
        this.texture.setMaxStringLength(500);
        this.poses = new GuiDropDownField(this.font, this);
        this.poseOnSneak = new GuiCheckBox(0, 0, 0, I18n.format("blockbuster.gui.builder.pose_sneak"), false);
    }

    @Override
    public boolean fromMorph(AbstractMorph morph)
    {
        if (morph instanceof CustomMorph)
        {
            CustomMorph actor = (CustomMorph) morph;

            this.model.setText(actor.name.replaceFirst("^blockbuster\\.", ""));
            this.model.setCursorPositionZero();
            this.texture.setText(actor.skin != null ? actor.skin.toString() : "");
            this.texture.setCursorPositionZero();
            this.poseOnSneak.setIsChecked(actor.currentPoseOnSneak);

            for (String pose : actor.model.poses.keySet())
            {
                this.poses.values.add(new DropDownItem(pose, pose));
            }

            this.poses.setSelected(actor.currentPose);

            if (this.poses.selected == -1)
            {
                this.poses.setSelected("standing");
            }

            this.updateMorph();

            return true;
        }

        return false;
    }

    @Override
    public void update(int x, int y, int w, int h)
    {
        super.update(x, y, w, h);

        this.model.x = this.texture.x = x + 61;
        this.model.y = y + 31;
        this.texture.y = y + 1 + 61;

        this.model.width = this.texture.width = w - 62;
        this.model.height = this.texture.height = 18;

        this.poses.x = x + 60;
        this.poses.y = y + 90;
        this.poses.w = w - 60;
        this.poses.h = 20;

        this.poseOnSneak.x = x;
        this.poseOnSneak.y = y + 120;
        this.updatePoses();
    }

    @Override
    public void clickedDropDown(GuiDropDownField dropDown, String value)
    {
        this.updateMorph();
    }

    private void updateMorph()
    {
        try
        {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setString("Name", "blockbuster." + this.model.getText());
            tag.setBoolean("Sneak", this.poseOnSneak.isChecked());

            if (!this.texture.getText().isEmpty())
            {
                tag.setString("Skin", this.texture.getText());
            }

            if (this.poses.selected >= 0 && this.poses.selected < this.poses.values.size())
            {
                tag.setString("Pose", this.poses.values.get(this.poses.selected).value);
            }

            this.cached = MorphManager.INSTANCE.morphFromNBT(tag);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        this.poses.mouseClicked(mouseX, mouseY, mouseButton);

        this.model.mouseClicked(mouseX, mouseY, mouseButton);
        this.texture.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.poseOnSneak.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY))
        {
            this.updateMorph();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state)
    {
        this.poses.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.model.textboxKeyTyped(typedChar, keyCode);
        this.texture.textboxKeyTyped(typedChar, keyCode);

        if (this.model.isFocused() || this.texture.isFocused())
        {
            this.updateMorph();
            this.updatePoses();
        }
    }

    private void updatePoses()
    {
        this.poses.values.clear();

        if (this.cached != null)
        {
            CustomMorph morph = Blockbuster.proxy.factory.morphs.get(this.model.getText());

            if (morph != null)
            {
                for (String pose : morph.model.poses.keySet())
                {
                    this.poses.values.add(new DropDownItem(pose, pose));
                }

                this.poses.setSelected(((CustomMorph) this.cached).currentPose);
            }
        }
        else
        {
            this.poses.values.add(new DropDownItem("standing", "standing"));
            this.poses.selected = 0;
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks)
    {
        Minecraft mc = Minecraft.getMinecraft();

        this.model.drawTextBox();
        this.texture.drawTextBox();
        this.poseOnSneak.drawButton(mc, mouseX, mouseY, partialTicks);

        this.poses.draw(mouseX, mouseY, mc.currentScreen.width, mc.currentScreen.height, partialTicks);

        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.model"), this.x, this.y + 37, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.skin"), this.x, this.y + 67, 0xffffff);
        this.font.drawStringWithShadow(I18n.format("blockbuster.gui.builder.pose"), this.x, this.y + 97, 0xffffff);
    }
}