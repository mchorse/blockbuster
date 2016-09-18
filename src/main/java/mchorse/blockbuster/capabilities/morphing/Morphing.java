package mchorse.blockbuster.capabilities.morphing;

/**
 * Default implementation of {@link IMorphing} interface.
 *
 * This class is responsible for storing current morphing, setting and retrieval
 * of current morphing.
 */
public class Morphing implements IMorphing
{
    private String model = "";
    private String skin = "";

    @Override
    public String getModel()
    {
        return this.model;
    }

    @Override
    public String getSkin()
    {
        return this.skin;
    }

    @Override
    public void reset()
    {
        this.setModel("");
        this.setSkin("");
    }

    @Override
    public void setModel(String newModel)
    {
        this.model = newModel;
    }

    @Override
    public void setSkin(String newSkin)
    {
        this.skin = newSkin;
    }
}
