package mchorse.blockbuster.api.formats.vox;

import mchorse.blockbuster.api.formats.vox.data.Vox;
import mchorse.blockbuster.api.formats.vox.data.VoxGroup;
import mchorse.blockbuster.api.formats.vox.data.VoxShape;
import mchorse.blockbuster.api.formats.vox.data.VoxTransform;

import java.util.ArrayList;
import java.util.List;

public class VoxDocument
{
	/**
	 * RGBA palette
	 */
	public int[] palette = VoxReader.DEFAULT_PALETTE;

	/**
	 * List of all chunks
	 */
	public List<Vox> chunks = new ArrayList<Vox>();

	public List<VoxTransform> transforms = new ArrayList<VoxTransform>();
	public List<VoxGroup> groups = new ArrayList<VoxGroup>();
	public List<VoxShape> shapes = new ArrayList<VoxShape>();
}