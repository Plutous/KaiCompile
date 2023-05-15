package toy.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wwk
 * @since 2023/4/12
 */
public class SimpleASTNode implements ASTNode {
    ASTNode parent = null;
    List<ASTNode> children = new ArrayList<>();
    ASTNodeType type = null;
    String text = null;
    // 用来分别string num bool，和使用方法对变量赋值，相当于一个标识符
    String idType = null;
    Token token = null; // 用来保存token，出错时获取行号和列号


    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public SimpleASTNode(ASTNodeType type, String text) {
        this.type = type;
        this.text = text;
    }

    public SimpleASTNode(ASTNodeType type, String text, String idType) {
        this.idType = idType;
        this.type = type;
        this.text = text;
    }

    public void addChild(SimpleASTNode child) {
        children.add(child);
        child.parent = this;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public List<ASTNode> getChildren() {
        return children;
    }

    @Override
    public ASTNodeType getType() {
        return type;
    }

    @Override
    public String getText() {
        return text;
    }
}
