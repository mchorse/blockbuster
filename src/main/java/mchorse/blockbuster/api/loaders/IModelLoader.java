package mchorse.blockbuster.api.loaders;

import mchorse.blockbuster.api.loaders.lazy.IModelLazyLoader;

import java.io.File;

/**
 * Model loader interface
 *
 * Detects whether a special model format can be loaded
 */
public interface IModelLoader
{
	public IModelLazyLoader load(File folder);
}