package mchorse.blockbuster.core;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@Name("CoreMod")
@MCVersion("1.11.2")
@SortingIndex(1)
public class BBCoreMod implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {BBCoreClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return BBCoreModInfo.class.getName();
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {}

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}