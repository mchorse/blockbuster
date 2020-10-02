package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Iterator;

public class RenderGlobalTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            String methodName = this.checkName(method, "a", "(FI)V", "renderSky", "(FI)V");

            if (methodName != null)
            {
                this.processRenderSky(method);
            }

            methodName = this.checkName(method, "a", "(FIDDD)V", "renderClouds", "(FIDDD)V");

            if (methodName != null)
            {
                this.processRenderClouds(method);
            }

            methodName = this.checkName(method, "a", "(Lvg;Lbxy;F)V", "renderEntities", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V");

            if (methodName != null)
            {
                this.processRenderEntities(method);
            }
        }
    }

    private void processRenderSky(MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();

            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "isGreenSky", "()Z", false));
            list.add(new JumpInsnNode(Opcodes.IFEQ, label));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderGreenSky", "()V", false));
            list.add(new InsnNode(Opcodes.RETURN));

            method.instructions.insert(list);

            System.out.println("BBCoreMod: successfully patched renderSky!");
        }
    }

    private void processRenderClouds(MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();

            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "isGreenSky", "()Z", false));
            list.add(new JumpInsnNode(Opcodes.IFEQ, label));
            list.add(new InsnNode(Opcodes.RETURN));

            method.instructions.insert(list);

            System.out.println("BBCoreMod: successfully patched renderClouds!");
        }
    }

    private void processRenderEntities(MethodNode method)
    {
        LabelNode releaseLabel = null;
        LabelNode renderEntityLabel = null;
        LabelNode lastLabel = null;
        Iterator<AbstractInsnNode> it = method.instructions.iterator();
        boolean captureNext = false;
        int renderCounter = 0;

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (node instanceof LabelNode)
            {
                lastLabel = (LabelNode) node;

                if (captureNext && renderEntityLabel == null)
                {
                    renderEntityLabel = lastLabel;
                    captureNext = false;
                }
            }
            else if (node instanceof MethodInsnNode)
            {
                MethodInsnNode invoke = (MethodInsnNode) node;

                if (
                    CoreClassTransformer.checkName(invoke.owner, "bzf", "net/minecraft/client/renderer/entity/RenderManager") &&
                    CoreClassTransformer.checkName(invoke.name, "a", "renderEntityStatic") &&
                    CoreClassTransformer.checkName(invoke.desc, "(Lvg;FZ)V", "(Lnet/minecraft/entity/Entity;FZ)V")
                ) {
                    if (renderCounter == 1)
                    {
                        captureNext = true;
                    }

                    renderCounter += 1;
                }

                if (
                    CoreClassTransformer.checkName(invoke.owner, "et$b", "net/minecraft/util/math/BlockPos$PooledMutableBlockPos") &&
                    CoreClassTransformer.checkName(invoke.name, "t", "release") &&
                    invoke.desc.equals("()V")
                ) {
                    releaseLabel = lastLabel;

                    break;
                }
            }
        }

        if (renderEntityLabel != null && releaseLabel != null)
        {
            /* In non-Optifine Minecraft, the index of the entity variable is 27,
             * however due to Optifine modifications, it's another index, but it
             * should be the last local variable... */
            final String entity = CoreClassTransformer.obfuscated ? "Lvg;" : "Lnet/minecraft/entity/Entity;";
            int localIndex = 0;

            for (LocalVariableNode var : method.localVariables)
            {
                if (var.desc.equals(entity))
                {
                    localIndex = Math.max(localIndex, var.index);
                }
            }

            /* Add render entity */
            InsnList list = new InsnList();

            list.add(new VarInsnNode(Opcodes.ALOAD, localIndex));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "addRenderActor", CoreClassTransformer.obfuscated ? "(" + entity + ")V" : "(" + entity + ")V", false));

            method.instructions.insert(renderEntityLabel, list);

            /* Render entities */
            method.instructions.insert(releaseLabel, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderActors", "()V", false));

            System.out.println("BBCoreMod: successfully patched renderEntities!");
        }
    }
}