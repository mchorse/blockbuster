package mchorse.blockbuster.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import mchorse.mclib.utils.coremod.ClassTransformer;

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
}