package mchorse.blockbuster.model_editor.elements.scrolls;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.model_editor.GuiModelEditor;
import mchorse.blockbuster.model_editor.modal.GuiScrollView;
import mchorse.metamorph.client.model.ModelCustom;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Models view GUI
 *
 * This GUI is responsible for displaying and selecting a model in the scroll
 * view.
 */
public class GuiModelsView extends GuiScrollView
{
    public List<ModelCell> models = new ArrayList<ModelCell>();
    public ModelCell selected;
    public EntityLivingBase dummy;

    /**
     * Initiate models from model repository
     */
    public GuiModelsView(GuiModelEditor parent)
    {
        super(parent);

        for (Map.Entry<String, ModelCustom> model : ModelCustom.MODELS.entrySet())
        {
            ModelCell cell = new ModelCell(model.getValue(), model.getKey());

            /* Steve is always selected by default */
            if (cell.key.equals("blockbuster.steve"))
            {
                this.selected = cell;
            }

            this.models.add(cell);
        }

        Collections.sort(this.models, new Comparator<ModelCell>()
        {
            @Override
            public int compare(ModelCell a, ModelCell b)
            {
                return a.name.compareTo(b.name);
            }
        });

        this.dummy = parent.dummy;
    }

    /**
     * Search the models
     *
     * This method will highlight all models which has given string in the
     * displayed name and will also scroll to the first model which was
     * highlighted.
     */
    public void search(String search)
    {
        int index = 0;
        int i = 0;

        for (ModelCell cell : this.models)
        {
            if (search.isEmpty())
            {
                cell.highlight = false;
            }
            else
            {
                cell.highlight = cell.name.toLowerCase().indexOf(search.toLowerCase()) != -1;

                if (cell.highlight && index == 0)
                {
                    index = i;
                }

                i++;
            }
        }

        this.scrollTo(index / 3 * this.w / 3);
    }

    @Override
    public void initiate()
    {
        int index = this.models.indexOf(this.selected);

        this.scrollHeight = MathHelper.ceiling_float_int(this.models.size() / 3.0F) * (this.w / 3);
        this.scrollTo(index == -1 ? 0 : index / 3 * this.w / 3);
    }

    /**
     * When mouse is clicked
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (!this.isInside(mouseX, mouseY))
        {
            return;
        }

        if (this.dragging)
        {
            return;
        }

        int x = mouseX - this.x;
        int y = mouseY - this.y + this.scrollY;

        int width = this.w / 3;
        int index = x / width + y / width * 3;

        if (index >= 0 && index < this.models.size())
        {
            this.selected = this.models.get(index);
        }
    }

    @Override
    protected void drawBackground()
    {}

    /**
     * Draw models and selection/search highlighted models
     */
    @Override
    protected void drawView(int mouseX, int mouseY, float partialTicks)
    {
        int i = 0;
        int w = this.w / 3;

        for (ModelCell model : this.models)
        {
            int x = this.x + (i % 3) * w;
            int y = this.y + (i / 3) * w;

            boolean selected = this.selected == model;

            if (model.texture != null)
            {
                this.mc.renderEngine.bindTexture(model.texture);
                model.model.pose = model.model.model.getPose("standing");

                float scale = selected ? w / 1.8F : w / 2.5F;
                int mY = y + (int) (w * 0.8F);

                this.drawModel(model.model, this.dummy, x + w / 2, mY, scale);
            }

            if (selected)
            {
                this.renderSelected(x, y, w, w, 0xffcccccc);
            }
            else if (model.highlight)
            {
                this.renderSelected(x, y, w, w, 0xff0088ff);
            }

            i++;
        }
    }

    /**
     * Render a colored outline around the given area.
     *
     * Basically, this method renders selection.
     */
    private void renderSelected(int x, int y, int width, int height, int color)
    {
        this.drawHorizontalLine(x, x + width - 1, y, color);
        this.drawHorizontalLine(x, x + width - 1, y + height - 1, color);

        this.drawVerticalLine(x, y, y + height - 1, color);
        this.drawVerticalLine(x + width - 1, y, y + height - 1, color);
    }

    /**
     * Draw a {@link ModelBase} without using the {@link RenderManager} (which
     * adds a lot of useless transformations and stuff to the screen rendering).
     */
    public void drawModel(ModelBase model, EntityLivingBase entity, int x, int y, float scale)
    {
        float factor = 0.0625F;

        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, -1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);

        GlStateManager.enableAlpha();

        model.setLivingAnimations(entity, 0, 0, 0);
        model.setRotationAngles(0, 0, 0, 0, 0, factor, entity);

        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        model.render(entity, 0, 0, 0, 0, 0, factor);

        GlStateManager.disableDepth();

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Model cell
     *
     * This class is responsible for holding information about a model for
     * display. Such features as texture, display name, model and key is stored
     * here.
     */
    public static class ModelCell
    {
        public String key;
        public String name;

        public ModelCustom model;
        public ResourceLocation texture;

        public boolean highlight = false;

        public ModelCell(ModelCustom model, String key)
        {
            String name = key;
            int index = name.indexOf(".");

            if (index != -1)
            {
                name = name.substring(index + 1);
            }

            this.model = model;
            this.key = key;
            this.name = name;

            if (model.model.defaultTexture != null)
            {
                this.texture = model.model.defaultTexture;
            }
            else
            {
                Map<String, File> skins = Blockbuster.proxy.models.pack.skins.get(name);

                if (skins != null && !skins.isEmpty())
                {
                    this.texture = new ResourceLocation("blockbuster.actors", name + "/" + skins.keySet().iterator().next());
                }
            }
        }
    }

}