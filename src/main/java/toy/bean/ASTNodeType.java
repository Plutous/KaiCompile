package toy.bean;

/**
 * AST节点的类型。
 */
public enum ASTNodeType {
    Program,           //程序入口，根节点

    NumDeclaration,     //整型变量声明
    ExpressionStmt,     //表达式语句，即表达式后面跟个分号
    AssignmentStmt,     //赋值语句

    Primary,            //基础表达式
    Multiplicative,     //乘法表达式（可以执行乘法或除法）
    Additive,           //加法表达式（可以执行加法和减法）
    Mod,                //取模
    Identifier,         //标识符
    Power, //    IntLiteral,       //整型字面量
    MulNumDeclare, NumLiteral,          //整型或浮点型状字面量
    IfStmt, //if(expr) blockStmt
    BooleanStmt,
    BlockStmt,  //{i = i + 1;}
    JudgeStmt,
    Func,
    StringDeclaration,
    MulStringDeclare,
    BoolDeclaration,
    StringLiteral,
    BoolLiteral,
    Compare,
    WhileStmt,
    BreakNode,
    ContinueNode, And,
}
