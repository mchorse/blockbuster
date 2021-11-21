package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassMethodTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import mchorse.mclib.utils.coremod.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class RenderItemTransformer extends ClassTransformer
{

    @Override
    public void process(String s, ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = this.checkName(method, "a", "(Laip;Lvp;Lbwc$b;Z)V", "renderItem", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V");
            if (methodName != null) {
                this.processMethod(methodName,method);
            }
          /*  methodName = this.checkName(method, "a", "(Laip;Lcfy;)V", "renderItem", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");
            if (methodName != null) {
                this.processItemRender(methodName,method);
            }
*/
        }
    }

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

    public void processItemRender(String methodName, MethodNode method){

    }
}