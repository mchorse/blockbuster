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

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class RenderPlayerTransformer extends ClassMethodTransformer
{
    public RenderPlayerTransformer()
    {
        super();

        this.setMcp("setModelVisibilities", "(Lnet/minecraft/client/entity/AbstractClientPlayer;)V");
        this.setNotch("d", "(Lbua;)V");
    }

    @Override
    public void processMethod(String name, MethodNode method)
    {
        String player = CoreClassTransformer.obfuscated ? "Lbua;" : "Lnet/minecraft/client/entity/AbstractClientPlayer;";
        String model = CoreClassTransformer.obfuscated ? "Lbqj;" : "Lnet/minecraft/client/model/ModelPlayer;";
        //INSTRUCTIONS
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new VarInsnNode(Opcodes.ALOAD, 2));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "changePlayerHand", "(" + player + "" + model + ")V", false));
        //INSTRUCTION ITERATIONS
        Iterator<AbstractInsnNode> it = method.instructions.iterator();
        AbstractInsnNode target = null;
        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (node.getOpcode() == Opcodes.RETURN)
            {
                target = node;
                break;
            }
        }
        //INVOKING
        if (target != null)
        {
            method.instructions.insertBefore(target, list);
        }
    }
}