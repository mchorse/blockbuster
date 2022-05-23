package mchorse.blockbuster.core.transformers;

import mchorse.blockbuster.client.render.tileentity.TileEntityGunItemStackRenderer;
import mchorse.blockbuster.utils.mclib.coremod.ClassMethodTransformer;
import mchorse.blockbuster.utils.mclib.coremod.CoreClassTransformer;
import mchorse.blockbuster.utils.mclib.coremod.ClassTransformer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class RenderItemTransformer extends ClassTransformer
{

    @Override
    public void process(String s, ClassNode classNode)
    {
        for (MethodNode method : classNode.methods)
        {
            String methodName = this.checkName(method, "a", "(Laip;Lvp;Lbwc$b;Z)V", "renderItem", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V");
            
            if (methodName != null)
            {
                this.processMethod(methodName,method);
            }

            methodName = this.checkName(method,"a","(Laip;Lcfy;Lbwc$b;Z)V","renderItemModel", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V");
            
            if (methodName != null)
            {
                this.processRenderItemModel(methodName,method);
            }
            
            methodName = this.checkName(method,"a","(Laip;Lbwc$b;)V","renderItem", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V");
            
            if (methodName != null)
            {
                this.processRenderItem(methodName,method);
            }
            
            methodName = this.checkName(method,"a","(Laip;IILcfy;)V","renderItemModelIntoGUI", "(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/renderer/block/model/IBakedModel;)V");
            
            if (methodName != null)
            {
                this.processRenderItemModelInGUI(methodName,method);
            }
        }
    }

    public void processMethod(String methodName, MethodNode method)
    {
        String entity = CoreClassTransformer.obfuscated ? "Lvp;" : "Lnet/minecraft/entity/EntityLivingBase;";
        String transform = CoreClassTransformer.obfuscated ? "Lbwc$b;" : "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;";
        InsnList before = new InsnList();

        before.add(new VarInsnNode(Opcodes.ALOAD, 2));
        before.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "setLastItemHolder", "(" + entity + ")V", false));

        method.instructions.insert(before);
        InsnList midle = new InsnList();
        midle.add(new VarInsnNode(Opcodes.ALOAD, 3));
        midle.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler",
                "setTSRTTransform", "(" + transform+ ")V", false));
        method.instructions.insert(midle);
        InsnList after = new InsnList();

        after.add(new VarInsnNode(Opcodes.ALOAD, 2));
        after.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "resetLastItemHolder", "(" + entity + ")V", false));


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

    public void processRenderItem(String methodName, MethodNode method)
    {
        String transform = CoreClassTransformer.obfuscated ? "Lbwc$b;" : "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;";
        InsnList before = new InsnList();
        before.add(new VarInsnNode(Opcodes.ALOAD, 2));
        before.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler",
                "setTSRTTransform", "(" + transform+ ")V", false));
        method.instructions.insert(before);
    }

    public void processRenderItemModel(String methodName, MethodNode method)
    {
        String transform = CoreClassTransformer.obfuscated ? "Lbwc$b;" : "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;";
        InsnList before = new InsnList();
        before.add(new VarInsnNode(Opcodes.ALOAD, 3));
        before.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler",
                "setTSRTTransform", "(" + transform+ ")V", false));
        method.instructions.insert(before);
    }

    public void processRenderItemModelInGUI(String methodName, MethodNode method)
    {
        InsnList list = new InsnList();
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType", "GUI", "Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/blockbuster/client/RenderingHandler", "setTSRTTransform", "(Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V", false));
        method.instructions.insert(list);
    }

}