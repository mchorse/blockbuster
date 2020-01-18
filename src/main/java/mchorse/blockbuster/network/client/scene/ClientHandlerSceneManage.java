package mchorse.blockbuster.network.client.scene;

import mchorse.blockbuster.network.common.scene.PacketSceneManage;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerSceneManage extends ClientMessageHandler<PacketSceneManage>
{
	@Override
	@SideOnly(Side.CLIENT)
	public void run(EntityPlayerSP entityPlayerSP, PacketSceneManage packetSceneManage)
	{

	}
}