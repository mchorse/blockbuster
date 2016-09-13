package mchorse.blockbuster.capabilities.morphing;

public interface IMorphing
{
    public String getModel();

    public String getSkin();

    public void reset();

    public void setModel(String newModel);

    public void setSkin(String newSkin);
}
