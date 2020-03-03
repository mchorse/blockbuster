package mchorse.blockbuster.core.transformers;

import mchorse.mclib.utils.coremod.ClassMethodTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

public class EntityRendererTransformer extends ClassMethodTransformer
{
	public EntityRendererTransformer()
	{
		super();

		this.mcp = "renderWorldPass";
		this.mcpSign = "(IFJ)V";
		this.notch = "a";
		this.notchSign = "(IFJ)V";
	}

	@Override
	public void processMethod(String s, MethodNode methodNode)
	{
		AbstractInsnNode renderLitNode = null;
		AbstractInsnNode renderNode = null;

		String owner = CoreClassTransformer.obfuscated ? "bms" : "net/minecraft/client/particle/ParticleManager";
		String renderLit = CoreClassTransformer.obfuscated ? "b" : "renderLitParticles";
		String render = CoreClassTransformer.obfuscated ? "a" : "renderParticles";
		String desc = CoreClassTransformer.obfuscated ? "(Lrw;F)V" : "(Lnet/minecraft/entity/Entity;F)V";

		/* Find these alive */
		Iterator<AbstractInsnNode> it = methodNode.instructions.iterator();

		while (it.hasNext())
		{
			AbstractInsnNode node = it.next();

			if (node.getOpcode() == Opcodes.INVOKEVIRTUAL)
			{
				MethodInsnNode invoke = (MethodInsnNode) node;

				if (invoke.owner.equals(owner) && invoke.desc.equals(desc))
				{
					if (invoke.name.equals(renderLit))
					{
						renderLitNode = node;
					}
					else if (invoke.name.equals(render))
					{
						renderNode = node;
					}
				}
			}
		}

		if (renderLitNode != null && renderNode != null)
		{
			InsnList renderLitList = new InsnList();
			InsnList rendeList = new InsnList();

			renderLitList.add(new VarInsnNode(Opcodes.FLOAD, 2));
			renderLitList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderLitParticles", "(F)V", false));
			rendeList.add(new VarInsnNode(Opcodes.FLOAD, 2));
			rendeList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderParticles", "(F)V", false));

			methodNode.instructions.insert(renderLitNode, renderLitList);
			methodNode.instructions.insert(renderNode, rendeList);

			System.out.println("BBCoreMod: successfully patched renderWorldPass!");
		}
		else
		{
			System.out.println("BBCoreMod: failed to find particle nodes: " + renderLitNode + " and " +renderNode);
		}
	}
}