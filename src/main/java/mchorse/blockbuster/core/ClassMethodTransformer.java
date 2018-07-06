package mchorse.blockbuster.core;

import java.util.Iterator;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class ClassMethodTransformer extends ClassTransformer
{
    public String mcp = "";
    public String mcpSign = "";
    public String notch = "";
    public String notchSign = "";

    @Override
    public void process(String name, ClassNode node)
    {
        Iterator<MethodNode> methods = node.methods.iterator();

        while (methods.hasNext())
        {
            MethodNode method = methods.next();
            String methodName = this.checkName(method);

            if (methodName != null)
            {
                this.processMethod(methodName, method);
            }
        }
    }

    protected String checkName(MethodNode method)
    {
        if (BBCoreClassTransformer.obfuscated)
        {
            return method.name.equals(this.notch) && method.desc.equals(this.notchSign) ? this.notch : null;
        }

        return method.name.equals(this.mcp) && method.desc.equals(this.mcpSign) ? this.mcp : null;
    }

    public abstract void processMethod(String name, MethodNode method);
}