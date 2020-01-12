package mchorse.blockbuster.api.loaders.lazy;

import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.client.model.ModelCustom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Lazy model loader
 *
 * This class is actually responsible for creation of {@link Model}
 * or client side {@link ModelCustom} classes
 */
public interface IModelLazyLoader
{
	public long lastModified();

	public Model loadModel(String key) throws Exception;

	@SideOnly(Side.CLIENT)
	public ModelCustom loadClientModel(String key, Model model) throws Exception;
}