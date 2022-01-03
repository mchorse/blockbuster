package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.utils.mclib.coremod.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * \* User: Evanechecssss
 * \* https://evanechecssss.github.io
 * \
 */
public class RenderEntityItemTransformer extends ClassTransformer
{
    @Override
    public void process(String s, ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            String methodName = this.checkName(method, "a", "(Lvg;DDDFF)V", "doRender", "(Lnet/minecraft/entity/item/EntityItem;DDDFF)V");
            if (methodName != null) {
                this.processMethod(methodName, method);
            }
        }
    }
    public void processMethod(String methodName, MethodNode method)
    {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType", "GROUND", "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "setTSRTTransform", "(Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V", false));
        method.instructions.insert(list);
    }
}