package toy.scope;


/**
 * 栈帧
 *
 * @author wwk
 * @since 2023/5/17
 */
public class StackFrame {
    //这个栈帧所对应的作用域
    Scope scope = null;

    //父作用域对应的栈帧
    //当一个变量先在自己本地作用域里查找，找不到时去父作用域
    //例如main函数的父作用域就是全局作用域
    StackFrame parentFrame = null;

    //实际存放变量
    MyObject map = null;
}
