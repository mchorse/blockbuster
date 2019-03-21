package mchorse.blockbuster.network.client;

import mchorse.blockbuster.network.common.PacketStructure;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.blockbuster_pack.morphs.StructureMorph.StructureRenderer;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerStructure extends ClientMessageHandler<PacketStructure>
{
    @SideOnly(Side.CLIENT)
    public static RenderGlobal global;

    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketStructure message)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            try
            {
                createFakeWorld(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    private void createFakeWorld(PacketStructure message)
    {
        if (global == null)
        {
            global = new RenderGlobal(Minecraft.getMinecraft());
        }

        if (message.tag == null)
        {
            /* TODO: remove world */

            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Profiler profiler = new Profiler();
        Template template = new Template();
        WorldSettings settings = new WorldSettings(0, GameType.CREATIVE, true, false, WorldType.DEFAULT);
        WorldProvider provider = new WorldProviderSurface();
        World world = new FakeWorld(null, new WorldInfo(settings, message.name), provider, profiler, true);

        provider.registerWorld(world);
        template.read(message.tag);

        int w = template.getSize().getX();
        int h = template.getSize().getY();
        int d = template.getSize().getZ();
        int cx = w / 16 + 1;
        int cy = h / 16 + 1;
        int cz = d / 16 + 1;
        int i = 0;

        for (int x = 0; x < cx; x++)
        {
            for (int z = 0; z < cz; z++)
            {
                ((ChunkProviderClient) world.getChunkProvider()).loadChunk(x, z);
            }
        }

        template.addBlocksToWorld(world, BlockPos.ORIGIN, new PlacementSettings());

        double px = player.posX;
        double py = player.posY;
        double pz = player.posZ;

        player.posX = 0;
        player.posY = 0;
        player.posZ = 0;

        ChunkRenderContainer container = new RenderList();
        ChunkRenderDispatcher dispatcher = new ChunkRenderDispatcher();
        RenderChunk[] chunks = new RenderChunk[cx * cy * cz];
        container.initialize(0, 0, 0);

        for (int x = 0; x < cx; x++)
        {
            for (int y = 0; y < cy; y++)
            {
                for (int z = 0; z < cz; z++)
                {
                    RenderChunk chunk = new FakeChunk(world, global, i);

                    chunk.setPosition(x * 16, y * 16, z * 16);
                    dispatcher.updateChunkNow(chunk);
                    chunks[i] = chunk;

                    i++;
                }
            }
        }

        player.posX = px;
        player.posY = py;
        player.posZ = pz;

        /* Finally clean the old one, if there was, and fill the structure */
        StructureRenderer renderer = StructureMorph.STRUCTURES.get(message.name);

        if (renderer != null)
        {
            renderer.delete();
        }

        renderer = new StructureRenderer(container, chunks, new BlockPos(w, h, d));
        StructureMorph.STRUCTURES.put(message.name, renderer);
    }

    public static class FakeWorld extends World
    {
        public ChunkProviderClient clientChunkProvider;

        public FakeWorld(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
        {
            super(saveHandlerIn, info, providerIn, profilerIn, client);

            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
        {
            return allowEmpty || !this.getChunkProvider().provideChunk(x, z).isEmpty();
        }

        @Override
        protected IChunkProvider createChunkProvider()
        {
            this.clientChunkProvider = new ChunkProviderClient(this);
            return this.clientChunkProvider;
        }
    }

    public static class FakeChunk extends ListedRenderChunk
    {
        public FakeChunk(World p_i47120_1_, RenderGlobal p_i47120_2_, int p_i47120_3_)
        {
            super(p_i47120_1_, p_i47120_2_, p_i47120_3_);
        }

        @Override
        protected ChunkCache createRegionRenderCache(World world, BlockPos from, BlockPos to, int subtract)
        {
            return new FakeChunkCache(world, from, to, subtract);
        }

        @Override
        public void multModelviewMatrix()
        {}
    }

    public static class FakeChunkCache extends ChunkCache
    {
        public FakeChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
        {
            super(worldIn, posFromIn, posToIn, subIn);
            this.hasExtendedLevels = false;
        }
    }
}