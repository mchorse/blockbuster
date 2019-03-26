package mchorse.blockbuster.network.client;

import mchorse.blockbuster.network.common.structure.PacketStructure;
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
                /* Remove the structure if null was sent */
                if (message.tag == null)
                {
                    StructureRenderer renderer = StructureMorph.STRUCTURES.get(message.name);

                    if (renderer != null)
                    {
                        renderer.delete();
                    }

                    return;
                }

                /* Finally clean the old one, if there was, and fill the structure */
                StructureRenderer renderer = createListFromTemplate(message);
                StructureRenderer old = StructureMorph.STRUCTURES.remove(message.name);

                if (old != null)
                {
                    old.delete();
                }

                StructureMorph.STRUCTURES.put(message.name, renderer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    /**
     * This method creates a renderable display list which allows 
     * rendering fully baked into a display list.
     * 
     * This was harder than I thought...
     * 
     * TODO: make translucent render correctly (at least with itself)
     */
    @SideOnly(Side.CLIENT)
    private StructureRenderer createListFromTemplate(PacketStructure message)
    {
        if (global == null)
        {
            global = new RenderGlobal(Minecraft.getMinecraft());
        }

        Profiler profiler = new Profiler();
        Template template = new Template();
        PlacementSettings placement = new PlacementSettings();

        WorldSettings settings = new WorldSettings(0, GameType.CREATIVE, true, false, WorldType.DEFAULT);
        WorldInfo info = new WorldInfo(settings, message.name);
        WorldProvider provider = new WorldProviderSurface();
        World world = new FakeWorld(null, info, provider, profiler, true);

        provider.registerWorld(world);
        template.read(message.tag);

        BlockPos origin = new BlockPos(1, 1, 1);
        int w = template.getSize().getX();
        int h = template.getSize().getY();
        int d = template.getSize().getZ();

        for (int x = 0, cx = (w + 2) / 16 + 1; x < cx; x++)
        {
            for (int z = 0, cz = (d + 2) / 16 + 1; z < cz; z++)
            {
                ((ChunkProviderClient) world.getChunkProvider()).loadChunk(x, z);
            }
        }

        template.addBlocksToWorld(world, origin, placement);

        /* Create display list */
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();
        int list = GLAllocation.generateDisplayLists(1);

        /* Centerize the geometry */
        GlStateManager.glNewList(list, 4864);
        buffer.begin(7, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(-w / 2F - origin.getX(), -origin.getY(), -d / 2F - origin.getZ());

        for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(origin, origin.add(w, h, d)))
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

        return new StructureRenderer(list, template.getSize());
    }

    /**
     * Fake world class
     * 
     * Because the base world isn't enough to make this thing work
     */
    @SideOnly(Side.CLIENT)
    public static class FakeWorld extends World
    {
        public ChunkProviderClient clientChunkProvider;

        public FakeWorld(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
        {
            super(saveHandlerIn, info, providerIn, profilerIn, client);

            /* If not called, there would be NPE any time blocks accessed */
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

        /**
         * This method fixes issues with lighting not being updated 
         * when structure template is being pasted into constructed 
         * world.
         */
        @Override
        public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty)
        {
            return true;
        }
    }
}