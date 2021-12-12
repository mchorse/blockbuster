package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassMethodTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class RenderPlayerTransformer extends ClassMethodTransformer {
    public RenderPlayerTransformer()
    {
        super();

        this.setMcp("setModelVisibilities", "(Lnet/minecraft/client/entity/AbstractClientPlayer;)V");
        this.setNotch("cct/d", "(Lbua;)V");
    }
    @Override
    public void processMethod(String name, MethodNode method) {
        String player = CoreClassTransformer.obfuscated ? "Lbua;" : "Lnet/minecraft/client/entity/AbstractClientPlayer;";
        String model = CoreClassTransformer.obfuscated ? "Lbqj;" : "Lnet/minecraft/client/model/ModelPlayer;";
        //INSTRUCTIONS
        InsnList renderLitList = new InsnList();
        renderLitList.add(new VarInsnNode(Opcodes.ALOAD,1));
        renderLitList.add(new VarInsnNode(Opcodes.ALOAD,2));
        renderLitList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler",
                "changePlayerHand",
                "("+ player + "" + model+ ")V", false));
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
        if (target != null) {
            method.instructions.insertBefore(target,renderLitList);
        }
    }
}