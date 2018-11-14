package mchorse.blockbuster.client;

import net.minecraft.util.ResourceLocation;

public class URLDownload implements Runnable
{
    private ResourceLocation location;

    public URLDownload(ResourceLocation location)
    {
        this.location = location;
    }

    @Override
    public void run()
    {
        new Thread(new URLDownloadThread(this.location)).start();
    }
}