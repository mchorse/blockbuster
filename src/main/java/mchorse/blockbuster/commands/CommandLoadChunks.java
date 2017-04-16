package mchorse.blockbuster.commands;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Command /load_chunks
 *
 * This client side command is responsible for loading all chunks in the render
 * distance.
 */
public class CommandLoadChunks extends CommandBase
{
    @Override
    public String getName()
    {
        return "load_chunks";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/load_chunks - loads all chunks in render distance";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        RenderGlobal render = Minecraft.getMinecraft().renderGlobal;
        Field frustumField = null;
        Field chunkyField = null;

        /* Find all fields */
        for (Field field : render.getClass().getDeclaredFields())
        {
            if (chunkyField == null && field.getType().equals(ChunkRenderDispatcher.class))
            {
                chunkyField = field;
                chunkyField.setAccessible(true);
            }

            if (frustumField == null && field.getType().equals(ViewFrustum.class))
            {
                frustumField = field;
                frustumField.setAccessible(true);
            }

            if (chunkyField != null && frustumField != null)
            {
                break;
            }
        }

        /* Force chunk loading */
        if (chunkyField != null && frustumField != null)
        {
            try
            {
                ChunkRenderDispatcher chunks = (ChunkRenderDispatcher) chunkyField.get(render);
                ViewFrustum frustum = (ViewFrustum) frustumField.get(render);

                for (RenderChunk chunk : frustum.renderChunks)
                {
                    boolean isDummy = chunk.getCompiledChunk() == CompiledChunk.DUMMY;

                    if (isDummy)
                    {
                        chunks.updateChunkNow(chunk);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}