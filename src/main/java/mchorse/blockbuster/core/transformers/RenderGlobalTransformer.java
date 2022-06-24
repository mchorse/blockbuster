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
        Iterator<AbstractInsnNode> it = method.instructions.iterator();

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (node instanceof MethodInsnNode)
            {
                MethodInsnNode methodInsnNode = (MethodInsnNode) node;

                /* this should indicate the end of the method (3rd method before end) */
                if (CoreClassTransformer.checkName(methodInsnNode.owner, "buy", "net/minecraft/client/renderer/RenderGlobal")
                    && CoreClassTransformer.checkName(methodInsnNode.name, "v", "postRenderDamagedBlocks")
                    && CoreClassTransformer.checkName(methodInsnNode.desc, "()V", "()V"))
                {
                    /* Render last entities */
                    method.instructions.insert(methodInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "renderLastEntities", "()V", false));

                    System.out.println("BBCoreMod: successfully patched renderEntities!");

                    return;
                }
            }
        }
    }
}