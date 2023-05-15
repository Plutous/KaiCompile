package toy.parser;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author wwk
 * @since 2023/4/27
 */
public class InterMethod {
    private HashMap<String, Method> interMethods = new HashMap<String, Method>();

    public InterMethod() {
        appendNatives();
    }

    private void appendNatives() {
        append("print", Natives.class, "print", Object.class);
        append("readInt", Natives.class, "readInt");
        //这几个read都是显示一个输入框，在具体调用方法里判断是调用哪个
        append("readString", Natives.class, "readInt");
        append("readBool", Natives.class, "readInt");
    }

    private void append(String name, Class<?> clazz, String methodName, Class<?>... params) {
        Method m = null;
        try {
            //通过方法名和参数找到方法
            m = clazz.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            System.out.println("cannot find a native function: "
                    + methodName);
        }
        //添加到map中
        interMethods.put(name, m);
    }

    public HashMap<String, Method> getInterMethods() {
        return interMethods;
    }

}
