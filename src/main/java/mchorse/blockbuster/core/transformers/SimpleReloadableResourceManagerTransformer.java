package mchorse.blockbuster.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import mchorse.blockbuster.core.BBCoreClassTransformer;
import mchorse.blockbuster.core.ClassTransformer;

public class SimpleReloadableResourceManagerTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            String methodName = this.checkName(method, "a", "(Lkq;)Lbzx;", "getResource", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/resources/IResource;");

            if (methodName != null)
            {
                this.processGetResource(method);
            }

            /* methodName = this.checkName(method, "b", "(Lkn;)Ljava/util/List;", "getAllResources", "(Lnet/minecraft/util/ResourceLocation;)Ljava/util/List;");
            
            if (methodName != null)
            {
                this.processGetAllResources(method);
            } */
        }
    }

    private void processGetResource(MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();

            String desc = "(Lmchorse/blockbuster/utils/MultiResourceLocation;)L" + (BBCoreClassTransformer.obfuscated ? "bzx" : "net/minecraft/client/resources/IResource") + ";";

            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "mchorse/blockbuster/utils/MultiResourceLocation"));
            list.add(new JumpInsnNode(Opcodes.IFEQ, label));
            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new TypeInsnNode(Opcodes.CHECKCAST, "mchorse/blockbuster/utils/MultiResourceLocation"));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/utils/RLUtils", "getStreamForMultiskin", desc, false));
            list.add(new InsnNode(Opcodes.ARETURN));

            method.instructions.insert(list);

            System.out.println("BBCoreMod: successfully patched getResource!");
        }
    }

    /* private void processGetAllResources(MethodNode method)
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
    } */
}