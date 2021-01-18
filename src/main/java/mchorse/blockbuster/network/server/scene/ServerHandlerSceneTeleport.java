package mchorse.blockbuster.network.server.scene;

import mchorse.blockbuster.commands.CommandRecord;
import mchorse.blockbuster.network.common.scene.PacketSceneTeleport;
import mchorse.blockbuster.recording.data.Frame;
import mchorse.blockbuster.recording.data.Record;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSceneTeleport extends ServerMessageHandler<PacketSceneTeleport>
{
	@Override
	public void run(EntityPlayerMP player, PacketSceneTeleport message)
	{
		try
		{
			String filename = message.id;
			int tick = message.offset;
			Record record = CommandRecord.getRecord(filename);

			Frame frame = record.frames.get(tick);

			player.connection.setPlayerLocation(frame.x, frame.y, frame.z, frame.yaw, frame.pitch);
		}
		catch (Exception e)
		{}
	}
}