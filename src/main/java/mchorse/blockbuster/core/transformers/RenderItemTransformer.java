package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassMethodTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

public class RenderItemTransformer extends ClassMethodTransformer
{
	public RenderItemTransformer()
	{
		this.setMcp("renderItem", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V");
		this.setNotch("a", "(Laip;Lvp;Lbwc$b;Z)V");
	}

	@Override
	public void processMethod(String methodName, MethodNode method)
	{
		String entity = CoreClassTransformer.obfuscated ? "Lvp;" : "Lnet/minecraft/entity/EntityLivingBase;";

		InsnList before = new InsnList();

		before.add(new VarInsnNode(Opcodes.ALOAD, 2));
		before.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "setLastItemHolder", "(" + entity + ")V", false));

		InsnList after = new InsnList();

		after.add(new VarInsnNode(Opcodes.ALOAD, 2));
		after.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "resetLastItemHolder", "(" + entity + ")V", false));

		method.instructions.insert(before);

		AbstractInsnNode target = null;
		Iterator<AbstractInsnNode> it = method.instructions.iterator();

		while (it.hasNext())
		{
			AbstractInsnNode node = it.next();

			if (node.getOpcode() == Opcodes.RETURN)
			{
				target = node;

				break;
			}
		}

		if (target != null)
		{
			method.instructions.insertBefore(target, after);

			System.out.println("BBCoreMod: successfully patched renderItem!");
		}
	}
}