package toy.parser;


import toy.bean.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;

/**
 * 运算结果
 */
public class SimpleCalculator {
    HashMap<String, String> variables = new HashMap<>();
    InterMethod methods = null;
    //控制第一个while
    boolean flag1 = true;

    /**
     * 执行脚本，并打印输出AST和求值过程。
     *
     * @param script
     * @return
     */
    public String evaluate(String script) {
        //重置flag状态，防止第二次执行while循环堵着
        flag1 = true;
        try {
            //解析代码，解析成小数是对的
            SimpleParser parser = new SimpleParser();
            toy.bean.ASTNode tree = parser.parse(script);
            //打印树的结构
//            dumpAST(tree, "");
            //执行运算
            return evaluate(tree, "");
        } catch (MyException e) {
            //打印错误信息
            Natives.print("error->行:" + e.getLineNumber() + "  " + "列:" + e.getColumnNumber() + "\n"
                    + e.getMessage());
        } catch (Exception e) {
            System.out.println("error:" + e.getMessage());
        }
        return "执行不出来";
    }

    /**
     * 对某个AST节点求值，并打印求值过程。（既可以计算整数也可以计算小数）
     *
     * @param node
     * @param indent 打印输出时的缩进量，用tab控制
     * @return
     */
    private String evaluate(ASTNode node, String indent) throws MyException, InvocationTargetException, IllegalAccessException {
        String result = "";
        //打印输出
//        System.out.println(indent + "Calculating: " + node.getType());

        switch (node.getType()) {
            case Program:
                //获取子节点，递归调用子节点
                for (ASTNode child : node.getChildren()) {
                    result = evaluate(child, indent + "\t");
                }
                break;
            case Additive:
                //获取第一个子节点，递归执行
                ASTNode child1 = node.getChildren().get(0);
                String value1 = evaluate(child1, indent + "\t");
                ASTNode child2 = node.getChildren().get(1);
                String value2 = evaluate(child2, indent + "\t");
                //字符串转成浮点数计算，再转成字符串
                if (node.getText().equals("+")) {
                    if (child1.getType() == ASTNodeType.StringLiteral || child2.getType() == ASTNodeType.StringLiteral) {
                        //("hello" + "wwk")解析字符串的相加
                        result = value1 + value2;
                    } else {
                        //(2 + 3)解析数字的相加
                        result = formatNum(String.valueOf(Double.parseDouble(value1) + Double.parseDouble(value2)));
                    }
                } else {
                    result = formatNum(String.valueOf(Double.parseDouble(value1) - Double.parseDouble(value2)));
                }
                break;
            case Multiplicative:
                child1 = node.getChildren().get(0);
                value1 = evaluate(child1, indent + "\t");
                child2 = node.getChildren().get(1);
                value2 = evaluate(child2, indent + "\t");
                if (node.getText().equals("*")) {
                    result = formatNum(String.valueOf(Double.parseDouble(value1) * Double.parseDouble(value2)));
                } else if (node.getText().equals("%")) { //取模运算
                    result = formatNum(String.valueOf(Double.parseDouble(value1) % Double.parseDouble(value2)));
                } else if (node.getText().equals("/")) {
                    //被除数不能为0
                    if (Double.parseDouble(value2) == 0) {
                        throw new MyException("除零异常", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                    }
                    result = formatNum(String.valueOf(Double.parseDouble(value1) / Double.parseDouble(value2)));
                } else if (node.getText().equals("**")) {
                    //小数点的乘方不能用 2^3
                    result = formatNum(String.valueOf(Math.pow(Double.parseDouble(value1), Double.parseDouble(value2))));
                }
                break;
            case NumLiteral:
            case StringLiteral:
            case BoolLiteral:
                //字符串转成浮点数
                result = node.getText();
                break;
            case BooleanStmt:
                String expr = node.getText();
                if ("true".equals(expr)) {
                    return "true"; //true
                } else {
                    return "false"; //false
                }
            case Identifier:
                //节点类型是变量名，
                String varName = node.getText();
                if (variables.containsKey(varName)) {
                    //如果包含这个变量，则返回他的值
                    String value = variables.get(varName);
                    if (value != null) {
                        result = value;
                    } else {
                        throw new MyException("variable " + varName + " has not been set any value", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                    }
                } else {
                    throw new MyException("变量" + varName + "未初始化", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                }
                break;
            case AssignmentStmt:
                varName = node.getText();
                if (!variables.containsKey(varName)) {
                    throw new MyException("unknown variable: " + varName, node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                }   //接着执行下面的代码(太秒了)
            case NumDeclaration:
            case StringDeclaration:
            case BoolDeclaration:
                //节点是整形声明节点
                varName = node.getText();
                if (node.getChildren() != null) {
                    if (node.getChildren().size() <= 0) {
                        //变量未赋值,设置默认值
                        if (node.getType() == ASTNodeType.NumLiteral) {
                            result = "0";
                        } else if (node.getType() == ASTNodeType.StringLiteral) {
                            result = "";
                        } else if (node.getType() == ASTNodeType.BoolLiteral) {
                            result = "false";
                        }
                    } else {
                        ASTNode child = node.getChildren().get(0);
                        //递归计算子节点
                        result = evaluate(child, "");
                    }
                }
                variables.put(varName, result);
                break;
            case MulNumDeclare:
                //多个变量声明，依次执行每个
                for (ASTNode child : node.getChildren()) {
                    evaluate(child, "");
                }
                break;
            case Compare:
                child1 = node.getChildren().get(0);
                value1 = evaluate(child1, indent + "\t");
                child2 = node.getChildren().get(1);
                value2 = evaluate(child2, indent + "\t");
                double valueOf1 = Double.parseDouble(value1);
                double valueOf2 = Double.parseDouble(value2);
                if (">".equals(node.getText())) {
                    if (valueOf1 > valueOf2) {
                        result = "true";
                    } else {
                        result = "false";
                    }
                } else if ("<".equals(node.getText())) {
                    if (valueOf1 < valueOf2) {
                        result = "true";
                    } else {
                        result = "false";
                    }
                } else if (">=".equals(node.getText())) {
                    if (valueOf1 >= valueOf2) {
                        result = "true";
                    } else {
                        result = "false";
                    }

                } else if ("<=".equals(node.getText())) {
                    if (valueOf1 <= valueOf2) {
                        result = "true";
                    } else {
                        result = "false";
                    }
                } else if ("!=".equals(node.getText())) {
                    if (valueOf1 != valueOf2) {
                        result = "true";
                    } else {
                        result = "false";
                    }
                } else if ("!".equals(node.getText())) {

                } else if ("==".equals(node.getText())) {
                    if (valueOf1 == valueOf2) {
                        result = "true";
                    } else {
                        result = "false";
                    }
                }
                break;
            case IfStmt:
                //执行expr节点
                ASTNode exprNode = node.getChildren().get(0);
                result = evaluate(exprNode, "");
                if ("true".equals(result)) {
                    //解析执行每一个节点
                    evaluate(node.getChildren().get(1), "");
                } else {
                    //修复else语句块未空时的bug
                    int size = node.getChildren().size();
                    if (size > 2) {
                        ASTNode falseNode = node.getChildren().get(2);
                        if (falseNode != null) {
                            evaluate(falseNode, "");
                        }
                    }
                }
                break;
            case WhileStmt:
                //先从NativeVar中获取值并计算结果
                child1 = node.getChildren().get(0);
                while (flag1) {
                    result = evaluate(child1, "");
                    //如果是true执行第二个节点
                    if ("true".equals(result)) {
                        child2 = node.getChildren().get(1);
                        evaluate(child2, "");
                    } else {
                        //否则退出循环
                        break;
                    }
                }
                break;
            case BreakNode:
                //跳出循环
                flag1 = false;
                break;
            case BlockStmt:
                for (ASTNode child : node.getChildren()) {
                    //如果flag改变，则不执行语句块后面得代码，直接退出
                    if (flag1) {
                        result = evaluate(child, "");
                    }
                }
                break;
            case Func:
                String methodName = node.getText();
                methods = new InterMethod();
                result = "init";
                //有(expr)节点
                if (node.getChildren().size() > 0) {
                    ASTNode paramNode = node.getChildren().get(0);
                    result = evaluate(paramNode, "");
                }
                //查找这个内置方法
                if (methods.getInterMethods().containsKey(methodName)) {
                    //如果存在这个方法
                    Method method = methods.getInterMethods().get(methodName);
                    if (!"init".equals(result)) {
                        //print()打印函数，只有print(a)能带参数
                        if ("num".equals(node.getIdType())) {
                            //如果这个类型是数字，就把他格式化以下
                            try {
                                result = (Double.parseDouble(result) == (int) Double.parseDouble(result)) ? String.format("%.0f", Double.parseDouble(result)) : String.valueOf(result);
                            } catch (NumberFormatException e) {
                                throw new MyException("Func方法中，String无法转换成数字类型", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                            }
                        }
                        method.invoke(null, result);
                    } else {
                        if (Objects.equals(methodName, "readInt")) {
                            //readInt函数
                            Object res = method.invoke(null, null);
                            try {
                                //格式化readInt()读取的参数
                                result = formatNum((String) res);
                            } catch (NumberFormatException e) {
                                throw new MyException("readInt()函数只能读取整数;", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                            }
                        } else if ("readString".equals(methodName)) {
                            //readString函数
                            Object res = method.invoke(null, null);
                            result = (String) res;
                        } else if ("readBool".equals(methodName)) {
                            //readBool函数
                            Object res = method.invoke(null, null);
                            result = (String) res;
                            if (!"true".equals(result) && !"false".equals(result)) {
                                throw new MyException("readBool()方法错误", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                            }
                        }
//                        System.out.println("反射返回的值" + result);
                    }
                }
                break;
            default:
        }
        return result;
    }


    /**
     * 语法解析：加法表达式
     *
     * @return
     * @throws Exception
     */
    private SimpleASTNode additive(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = multiplicative(tokens);
        SimpleASTNode node = child1;

        Token token = tokens.peek();
        if (child1 != null && token != null) {
            //(additive) : (mul) | (mul) + (additive)
            if (token.getType() == TokenType.Plus || token.getType() == TokenType.Minus) {
                token = tokens.read(); //消耗掉token，继续解析additive
                SimpleASTNode child2 = additive(tokens);
                if (child2 != null) {
                    //child1是左节点,child2是右节点
                    node = new SimpleASTNode(ASTNodeType.Additive, token.getText());
                    node.addChild(child1);
                    node.addChild(child2);
                } else {
                    throw new Exception("invalid additive expression, expecting the right part.");
                }
            }
        }
        return node;
    }

    /**
     * 语法解析：乘法表达式
     * mul : Int | Int * Mul
     *
     * @return
     * @throws Exception
     */
    private SimpleASTNode multiplicative(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = primary(tokens);
        SimpleASTNode node = child1;

        Token token = tokens.peek();
        if (child1 != null && token != null) {
            if (token.getType() == TokenType.Star || token.getType() == TokenType.Slash) {
                token = tokens.read();
                SimpleASTNode child2 = multiplicative(tokens);
                if (child2 != null) {
                    node = new SimpleASTNode(ASTNodeType.Multiplicative, token.getText());
                    node.addChild(child1);
                    node.addChild(child2);
                } else {
                    throw new Exception("invalid multiplicative expression, expecting the right part.");
                }
            }
        }
        return node;
    }

    /**
     * 语法解析：基础表达式
     *
     * @return
     * @throws Exception
     */
    private SimpleASTNode primary(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if (token != null) {
            if (token.getType() == TokenType.NumLiteral) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.NumLiteral, token.getText());
            } else if (token.getType() == TokenType.StringLiteral) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.StringLiteral, token.getText());
            } else if (token.getType() == TokenType.BoolLiteral) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.BoolLiteral, token.getText());
            } else if (token.getType() == TokenType.Identifier) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.Identifier, token.getText());
            } else if (token.getType() == TokenType.LeftParen) { // 如果是左括号，（括号里面可以是加法可以是乘法）
                tokens.read();
                node = additive(tokens);
                if (node != null) {
                    token = tokens.peek();
                    if (token != null && token.getType() == TokenType.RightParen) {
                        tokens.read();
                    } else {
                        throw new Exception("expecting right parenthesis");
                    }
                } else {
                    throw new Exception("expecting an additive expression inside parenthesis");
                }
            }
        }
        return node;  //这个方法也做了AST的简化，就是不用构造一个primary节点，直接返回子节点。因为它只有一个子节点。
    }

    /**
     * 打印输出AST的树状结构
     *
     * @param node
     * @param indent 缩进字符，由tab组成，每一级多一个tab
     */
    private void dumpAST(ASTNode node, String indent) {
        System.out.println(indent + node.getType() + " " + node.getText());
        for (ASTNode child : node.getChildren()) {
            dumpAST(child, indent + "\t");
        }
    }

    /**
     * 格式化整数类型字符串
     *
     * @param result
     * @return
     */
    private String formatNum(String result) {
        return (Double.parseDouble(result) ==
                (int) Double.parseDouble(result)) ? String.format("%.0f", Double.parseDouble(result)) : String.valueOf(result);
    }
}
