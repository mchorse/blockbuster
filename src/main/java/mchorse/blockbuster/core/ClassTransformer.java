package mchorse.blockbuster.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class ClassTransformer
{
    public byte[] transform(String name, byte[] bytes)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);

        classReader.accept(classNode, 0);

        this.process(name, classNode);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        classNode.accept(writer);

        return writer.toByteArray();
    }

    protected String checkName(MethodNode method, String notch, String notchSign, String mcp, String mcpSign)
    {
        if (BBCoreClassTransformer.obfuscated)
        {
            return method.name.equals(notch) && method.desc.equals(notchSign) ? notch : null;
        }

        return method.name.equals(mcp) && method.desc.equals(mcpSign) ? mcp : null;
    }

    protected LabelNode getFirstLabel(MethodNode method)
    {
        AbstractInsnNode node = method.instructions.getFirst();

        while (node != null)
        {
            if (node instanceof LabelNode)
            {
                return (LabelNode) node;
            }

            node = node.getNext();
        }

        return null;
    }

    public abstract void process(String name, ClassNode node);
}