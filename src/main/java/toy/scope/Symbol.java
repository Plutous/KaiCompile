package toy.scope;


import toy.bean.ASTNode;

/**
 * @author wwk
 * @since 2023/5/17
 */
public abstract class Symbol {
    //符号的名称
    protected String name = null;

    //所属作用域
    protected Scope enclosingScope = null;

    //可见性，比如public还是private
    protected int visibility = 0;

    //Symbol关联的AST节点
    protected ASTNode astNode = null;

    public String getName() {
        return name;
    }

    public Scope getEnclosingScope() {
        return enclosingScope;
    }
}
