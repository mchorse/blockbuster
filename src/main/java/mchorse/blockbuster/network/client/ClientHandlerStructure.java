package mchorse.blockbuster.network.client;

import mchorse.blockbuster.network.common.PacketStructure;
import mchorse.blockbuster_pack.morphs.StructureMorph;
import mchorse.blockbuster_pack.morphs.StructureMorph.StructureRenderer;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
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

        Profiler profiler = new Profiler();
        Template template = new Template();
        WorldSettings settings = new WorldSettings(0, GameType.CREATIVE, true, false, WorldType.DEFAULT);
        WorldInfo info = new WorldInfo(settings, message.name);
        WorldProvider provider = new WorldProviderSurface();
        World world = new FakeWorld(null, info, provider, profiler, true);

        provider.registerWorld(world);
        world.setWorldTime(6000);
        world.calculateInitialSkylight();
        world.calculateInitialWeatherBody();
        template.read(message.tag);

        int w = template.getSize().getX();
        int h = template.getSize().getY();
        int d = template.getSize().getZ();

        for (int x = 0, cx = w / 16 + 1; x < cx; x++)
        {
            for (int z = 0, cz = d / 16 + 1; z < cz; z++)
            {
                ((ChunkProviderClient) world.getChunkProvider()).loadChunk(x, z);
            }
        }

        template.addBlocksToWorld(world, new BlockPos(0, 2, 0), new PlacementSettings());

        /* Create buffer */
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        int i = GLAllocation.generateDisplayLists(1);

        GlStateManager.glNewList(i, 4864);
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-w / 2F, -2, -d / 2F);

        for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(new BlockPos(0, 2, 0), new BlockPos(w, h + 2, d)))
        {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
            {
                dispatcher.renderBlock(state, pos, world, buffer);
            }
        }

        buffer.setTranslation(0, 0, 0);
        tess.draw();
        GlStateManager.glEndList();

        /* Finally clean the old one, if there was, and fill the structure */
        StructureRenderer renderer = StructureMorph.STRUCTURES.get(message.name);

        if (renderer != null)
        {
            renderer.delete();
        }

        renderer = new StructureRenderer(i, new BlockPos(w, h, d));
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
}