package toy.parser;


import toy.bean.*;
import toy.lexer.Lexer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wwk
 * @since 2023/4/12
 */
public class SimpleParser {
    ASTNode tree = null;
    InterMethod methods = new InterMethod();


    public ASTNode parse(String script) throws Exception {
        TokenReader tokens = null;
        //解析Token序列，原来的解析器是逐行解析的我们得想办法获取所有Token
        Lexer lexer = new Lexer();
        tokens = lexer.parseB(script);

        //首先遍历一遍tokens，把内置函数的类型改为func
        updateTokens(tokens);
        tree = prog(tokens);
        return tree;
    }

    /**
     * 将内置函数的类型修改为func，如readInt等
     *
     * @param tokens
     */
    private void updateTokens(TokenReader tokens) {
        List<Token> tokens1 = tokens.getTokens();
        //改一下内置方法的类型
        for (Token token : tokens1) {
            if (token.getType() == TokenType.Identifier) {
                HashMap<String, Method> interMethods = methods.getInterMethods();
                if (interMethods.containsKey(token.getText())) {
                    token.setType(TokenType.Func);
                }
            }
        }
        //把换行符token删除了
        ArrayList<Token> Token = new ArrayList<>();
        for (Token token : tokens1) {
            if (token.getType() != TokenType.EOL) {
                Token.add(token);
            }
        }
        tokens.setTokens(Token);
    }

    private ASTNode prog(TokenReader tokens) throws Exception {
        //根节点，下面可以有赋值表达式(int a = 100)，运算表达式（2+3+4）,每一个表达式都是这个节点的儿子
        SimpleASTNode root = new SimpleASTNode(ASTNodeType.Program, "pwc", null);
        Token token = tokens.peek();
        while (tokens.peek() != null) {
            //遇见右大括号直接返回，解析完if() {} 了
            if (tokens.peek().getType() == TokenType.RightBigParen) {
                tokens.read();// 吃掉右括号并退出
                break;
            }
            //解析变量声明规则，首先是 num开头 num a = 1; num a; num a = readInt();
            SimpleASTNode child = intDeclare(tokens);

            if (child == null) {
                child = stringDeclare(tokens);
            }
            if (child == null) {
                child = boolDeclare(tokens);
            }
            //说明该token不是赋值语句，则解析表达式expression
            //可以解析 a; 也可以解析 2+3+4; 2*3+4; a+b+c;
            if (child == null) {
                child = expressionStatement(tokens);
            }

            //匹配赋值语句, a = a + 1; 这里应该也要解析{i = i + 1;}
            if (child == null) {
                child = assignmentStatement(tokens);
            }
            if (child == null) {
                child = ifStatement(tokens);
            }
            if (child == null) {
                child = whileStatement(tokens);
            }
            if (child == null) {
                //解析内置方法
                child = keyWordStatement(tokens);
            }
            if (child == null) {
                //解析内置方法
                child = funcStatement(tokens);
            }
            if (child != null) {
                root.addChild(child);
            } else {
                throw new MyException("语法解析出错:未知的语句", token.getLineNumber(), token.getColumnNumber());
            }
        }
        return root;
    }

    /**
     * 解析关键字break;，
     *
     * @param tokens
     * @return
     */
    private SimpleASTNode keyWordStatement(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        SimpleASTNode mulNode = null;//多个变量声明的共同谷类
        Token token = tokens.peek();
        if (token != null && token.getType() == TokenType.Break) {
            token = tokens.read(); //消耗掉break
            //创建child节点
            node = new SimpleASTNode(ASTNodeType.BreakNode, token.getText(), null);
            node.setToken(token);
        }
        if (token != null && token.getType() == TokenType.Continue) {
            token = tokens.read(); //消耗掉break
            //创建child节点
            node = new SimpleASTNode(ASTNodeType.ContinueNode, token.getText(), null);
            node.setToken(token);
        }
        //匹配最后的分号
        if (node != null) {
            token = tokens.peek();
            if (token != null && token.getType() == TokenType.SemiColon) {
                tokens.read();
            }
        }
        return node;
    }

    private SimpleASTNode intDeclare(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        SimpleASTNode mulNode = null;//多个变量声明的共同谷类
        Token token = tokens.peek();
        if (token != null && token.type == TokenType.Id_num) {// 匹配int
            token = tokens.read(); //消耗掉int
            if (tokens.peek().type == TokenType.Identifier) {
                token = tokens.read(); //消耗掉标识符
                //创建child节点
                node = new SimpleASTNode(ASTNodeType.NumDeclaration, token.getText(), null);
                node.setToken(token);
                token = tokens.peek();
                if (token != null && token.type == TokenType.Assignment) { //匹配=
                    token = tokens.read();
                    //后面可能是int或加法表达式，所以用加法表达式解析token
                    SimpleASTNode child = additive(tokens);
                    if (child != null) {
                        node.addChild(child);
                    } else {
                        //右边是个方法readInt();
                        child = funcStatement(tokens);
                        node.addChild(child);
                        //额解析Func时会把最后的分号吃掉，所以下面吃掉分号会报错
                        //貌似想要解决需要我改语法规则
                        node.setIdType("NumDeclarationByFunc");
                    }
                }
            } else {
                throw new Exception("缺失变量名");
            }

            //匹配最后的分号
            if (node != null) {
                token = tokens.peek();
                if (token != null && token.getType() == TokenType.SemiColon) {
                    mulNode = node;
                    tokens.read(); //吃掉;
                } else if (token != null && token.getType() == TokenType.Comma) {
                    mulNode = new SimpleASTNode(ASTNodeType.MulNumDeclare, "多个变量声明", null);
                    node.setToken(token);
                    mulNode.addChild(node);
                    tokens.read();//消耗掉,号
                    //逗号意味着这是多个变量声明语句，接着写
                    mulIntDeclare(tokens, mulNode);
                } else if ("NumDeclarationByFunc".equals(node.getIdType())) {
                    //方法赋值 num a = readInt();
                    mulNode = node;
                }
            }
        }
        return mulNode;
    }


    private SimpleASTNode boolDeclare(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        SimpleASTNode mulNode = null;//多个变量声明的共同谷类
        Token token = tokens.peek();
        if (token != null && token.type == TokenType.Id_bool) {// 匹配int
            token = tokens.read(); //消耗掉string
            if (tokens.peek().type == TokenType.Identifier) {
                token = tokens.read(); //消耗掉string 标识符
                //创建child节点
                node = new SimpleASTNode(ASTNodeType.BoolDeclaration, token.getText(), null);
                node.setToken(token);
                token = tokens.peek();
                if (token != null && token.type == TokenType.Assignment) { //匹配=
                    token = tokens.read();
                    //后面可能是string或加法表达式，所以用加法表达式解析token
                    SimpleASTNode child = additive(tokens);
                    if (child != null) {
                        node.addChild(child);
                    } else {
                        if (!"readString".equals(tokens.peek().getText())) {
                            throw new MyException("bool类型变量只能readBool获取", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                        }
                        //右边是个方法readString();
                        child = funcStatement(tokens);
                        node.addChild(child);
//                        throw new Exception("Incorrect variable declaration");
                    }
                }
            } else {
                throw new Exception("缺失变量名");
            }

            //匹配最后的分号
            if (node != null) {
                token = tokens.peek();
                if (token != null && token.getType() == TokenType.SemiColon) {
                    mulNode = node;
                    tokens.read();
                } else if (token != null && token.getType() == TokenType.Comma) {
                    mulNode = new SimpleASTNode(ASTNodeType.MulStringDeclare, "多个变量声明", null);
                    node.setToken(token);
                    mulNode.addChild(node);
                    tokens.read();//消耗掉,号
                    //逗号意味着这是多个变量声明语句，接着写
                    mulIntDeclare(tokens, mulNode);
                }
            }
        }
        return mulNode;
    }

    private SimpleASTNode stringDeclare(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        SimpleASTNode mulNode = null;//多个变量声明的共同谷类
        Token token = tokens.peek();
        if (token != null && token.type == TokenType.Id_string) {// 匹配int
            token = tokens.read(); //消耗掉string
            if (tokens.peek().type == TokenType.Identifier) {
                token = tokens.read(); //消耗掉string 标识符
                //创建child节点
                node = new SimpleASTNode(ASTNodeType.StringDeclaration, token.getText(), null);
                node.setToken(token);
                token = tokens.peek();
                if (token != null && token.type == TokenType.Assignment) { //匹配=
                    token = tokens.read();
                    //后面可能是string或加法表达式，所以用加法表达式解析token
                    SimpleASTNode child = additive(tokens);
                    if (child != null) {
                        node.addChild(child);
                    } else {
                        if (!"readString".equals(tokens.peek().getText())) {
                            throw new MyException("String类型变量只能readString获取", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                        }
                        //右边是个方法readString();
                        child = funcStatement(tokens);
                        node.addChild(child);
                    }
                }
            } else {
                throw new Exception("缺失变量名");
            }

            //匹配最后的分号
            if (node != null) {
                token = tokens.peek();
                if (token != null && token.getType() == TokenType.SemiColon) {
                    mulNode = node;
                    tokens.read();
                } else if (token != null && token.getType() == TokenType.Comma) {
                    mulNode = new SimpleASTNode(ASTNodeType.MulStringDeclare, "多个变量声明", null);
                    node.setToken(token);
                    mulNode.addChild(node);
                    tokens.read();//消耗掉,号
                    //逗号意味着这是多个变量声明语句，接着写
                    mulIntDeclare(tokens, mulNode);
                }
            }
        }
        return mulNode;
    }

    private SimpleASTNode compare(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = additive(tokens);
        //如果只有一个add
        SimpleASTNode root = child1;
        Token token = tokens.peek();
        if (child1 != null && token != null) {
            if (token.type == TokenType.GE || token.type == TokenType.GT || token.type == TokenType.OR
                    || token.type == TokenType.NoEquals || token.getType() == TokenType.Equals
                    || token.type == TokenType.LT || token.type == TokenType.LE) {
                //吃掉符号，继续向下解析add非终结符
                tokens.read();
                SimpleASTNode child2 = additive(tokens);
                //解析完创建根节点
                root = new SimpleASTNode(ASTNodeType.Compare, token.getText(), null);
                root.setToken(token);
                if (child2 != null) {
                    root.addChild(child1);
                    root.addChild(child2);
                } else {
                    throw new Exception("错误的比较表达式，缺少右半部分");
                }
            }

        }
        return root;
    }


    private void mulIntDeclare(TokenReader tokens, SimpleASTNode mulNode) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        //匹配变量名
        if (tokens.peek().type == TokenType.Identifier) {
            token = tokens.read(); //消耗掉标识符
            //创建child节点
            node = new SimpleASTNode(ASTNodeType.NumDeclaration, token.getText(), null);
            node.setToken(token);
            token = tokens.peek();
            if (token != null && token.type == TokenType.Assignment) { //匹配=
                token = tokens.read();
                //后面可能是int或加法表达式，所以用加法表达式解析token
                SimpleASTNode child = additive(tokens);
                if (child != null) {
                    node.addChild(child);
                } else {
                    throw new Exception("Incorrect variable declaration");
                }
            }
        } else {
            throw new Exception("缺失变量名");
        }

        //匹配最后的分号
        if (node != null) {
            token = tokens.peek();
            mulNode.addChild(node); //挂载到多个变量声明上
            if (token != null && token.getType() == TokenType.SemiColon) {
                tokens.read();
            } else if (token != null && token.getType() == TokenType.Comma) {
                tokens.read();
                //逗号意味着这是多个变量声明语句，接着写
                mulIntDeclare(tokens, mulNode);
            } else {
                throw new Exception("invalid statement, expecting semicolon");
            }
        }
    }

    /**
     * add: mul | mul + add
     *
     * @param tokens
     * @return
     */
    private SimpleASTNode additive(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = mul(tokens);
        //如果是 add: mul，直接返回这个节点就行。
        // 如果是add: mul | mul + add需要创建两个节点
        SimpleASTNode root = child1;
        Token token = tokens.peek();
        if (child1 != null && token != null) {
            if (token.type == TokenType.Plus || token.type == TokenType.Minus) {
                //吃掉加号，继续向下解析add非终结符
                tokens.read();
                SimpleASTNode child2 = additive(tokens);
                //解析完创建根节点
                root = new SimpleASTNode(ASTNodeType.Additive, token.getText(), null);
                root.setToken(token);
                if (child2 != null) {
                    root.addChild(child1);
                    root.addChild(child2);
                } else {
                    throw new Exception("错误的加法表达式，缺少右半部分");
                }
            }

        }
        return root;
    }

    /**
     * mul: Int || Int * mul
     *
     * @param tokens
     * @return
     */
    private SimpleASTNode mul(TokenReader tokens) throws Exception {
        //解析基本的token。非终结符
        SimpleASTNode child1 = primary(tokens);
        //如果·mul: Int，则把这个直接当根节点
        SimpleASTNode root = child1;
        Token token = tokens.peek();
        if (child1 != null && token != null) {
            if (token.type == TokenType.Star || token.type == TokenType.Slash || token.type == TokenType.Modulo
                    || token.type == TokenType.Power) {
                //吃掉*运算符,处理mul
                token = tokens.read();
                SimpleASTNode child2 = mul(tokens);
                if (child2 != null) {
                    //创建*法节点
                    root = new SimpleASTNode(ASTNodeType.Multiplicative, token.getText(), null);
                    root.setToken(token);
                    root.addChild(child1);
                    root.addChild(child2);
                } else {
                    throw new Exception("乘法表达式错误，缺少右部分");
                }
            }
            //匹配分号
        }
        return root;
    }

    /**
     * 单独的元素可以是 数字 可以是字母 可以是 左括号，也就是 a;可以在这里解析
     *
     * @param tokens
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
                node.setToken(token);
            } else if (token.getType() == TokenType.Minus) { //如果是负数
                token = tokens.read(); //消耗 -
                token = tokens.read(); //消耗数字
                node = new SimpleASTNode(ASTNodeType.NumLiteral, "-" + token.getText());
                node.setToken(token);
            } else if (token.getType() == TokenType.StringLiteral) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.StringLiteral, token.getText(), "string");
                node.setToken(token);
            } else if (token.getType() == TokenType.BoolLiteral) {
                //(true) / (false)
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.BoolLiteral, token.getText(), "bool");
                node.setToken(token);
            } else if (token.getType() == TokenType.Identifier) { //字母
                token = tokens.read();
                //变量声明的时候看看这个变量是什么类型
                node = new SimpleASTNode(ASTNodeType.Identifier, token.getText());
                node.setToken(token);
            } else if (token.getType() == TokenType.Break) { //字母
                token = tokens.read();
                //变量声明的时候看看这个变量是什么类型
                node = new SimpleASTNode(ASTNodeType.BreakNode, token.getText());
                node.setToken(token);
            } else if (token.getType() == TokenType.Continue) { //字母
                token = tokens.read();
                //变量声明的时候看看这个变量是什么类型
                node = new SimpleASTNode(ASTNodeType.ContinueNode, token.getText());
                node.setToken(token);
            } else if (token.getType() == TokenType.LeftParen) { // 如果是左括号，（括号里面可以是加法可以是乘法）
                tokens.read();
                node = additive(tokens);
//                if (node != null) {
                token = tokens.peek();
                //吃掉右括号
                if (token != null && token.getType() == TokenType.RightParen) {
                    tokens.read();
                } else {
                    throw new MyException("primary方法中缺少右括号", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
                }
            }
        }
        return node;  //这个方法也做了AST的简化，就是不用构造一个primary节点，直接返回子节点。因为它只有一个子节点。
    }


    private SimpleASTNode funcStatement(TokenReader tokens) throws Exception {
        Token token = tokens.peek();
        SimpleASTNode node = null;
        if (token != null && token.getType() == TokenType.Func) {
            tokens.read();
            node = new SimpleASTNode(ASTNodeType.Func, token.getText(), null);
            node.setToken(token);
            token = tokens.peek();
            if (token != null && token.getType() == TokenType.LeftParen) {
                //开始解析表达式expression （expr）
                tokens.read();//吃掉左括号
                SimpleASTNode expression = expressionStatement(tokens);
                if (expression != null) {
                    node.addChild(expression);
                }
            }
        }
        return node;
    }

    private SimpleASTNode ifStatement(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if (token != null && token.getType() == TokenType.If) {
            tokens.read();// 吃掉if
            node = new SimpleASTNode(ASTNodeType.IfStmt, "if", null);
            node.setToken(token);
            token = tokens.peek();
            if (token != null && token.getType() == TokenType.LeftParen) {
                tokens.read();// 吃掉左括号
                //开始解析表达式expression
                SimpleASTNode expression = expressionStatement(tokens);

                node.addChild(expression);
                //解析statement大括号里的
                SimpleASTNode trueStmt = trueBlockStmt(tokens);
                node.addChild(trueStmt);
                SimpleASTNode falseStmt = falseBlockStmt(tokens);
                if (falseStmt != null) {
                    node.addChild(falseStmt);
                }
            } else {
                throw new MyException("If语句没有左括号", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
            }
        }
        return node;
    }

    private SimpleASTNode falseBlockStmt(TokenReader tokens) throws Exception {
        Token token = tokens.peek();
        SimpleASTNode node = null;
        if (token != null && token.getType() == TokenType.Else) {
            tokens.read(); // 吃掉else
            node = new SimpleASTNode(ASTNodeType.BlockStmt, "falseBlock", null);
            node.setToken(token);
            token = tokens.peek();
            if (token != null && token.getType() == TokenType.LeftBigParen) {
                tokens.read();//吃掉左括号(
                ASTNode prog = prog(tokens);
                //{stmt}，解析出来的每个语句都加入到BlockStmt中
                for (ASTNode child : prog.getChildren()) {
                    node.addChild((SimpleASTNode) child);
                }
            }
        }
        return node;
    }

    private SimpleASTNode trueBlockStmt(TokenReader tokens) throws Exception {
        Token token = tokens.peek();
        SimpleASTNode node = null;
        if (token != null && token.getType() == TokenType.LeftBigParen) {
            tokens.read(); // 吃掉{
            node = new SimpleASTNode(ASTNodeType.BlockStmt, "trueBlock", null);
            node.setToken(token);
            ASTNode prog = prog(tokens);
            //{stmt}，解析出来的每个语句都加入到BlockStmt中
            for (ASTNode child : prog.getChildren()) {
                node.addChild((SimpleASTNode) child);
            }
        }
        return node;
    }

    private SimpleASTNode whileStatement(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if (token != null && token.getType() == TokenType.While) {
            tokens.read();// 吃掉if
            node = new SimpleASTNode(ASTNodeType.WhileStmt, "WhileStmt", null);
            node.setToken(token);
            token = tokens.peek();
            if (token != null && token.getType() == TokenType.LeftParen) {
                tokens.read();// 吃掉左括号
                //开始解析表达式expression
                SimpleASTNode expression = expressionStatement(tokens);

                node.addChild(expression);
                //解析statement大括号里的
                SimpleASTNode trueStmt = trueBlockStmt(tokens);
                node.addChild(trueStmt);
            } else {
                throw new MyException("While语句没有左括号", node.getToken().getLineNumber(), node.getToken().getColumnNumber());
            }
        }
        return node;
    }

    /**
     * 赋值语句 age = 10 * 2;
     * 或者解析 a = readInt();函数
     *
     * @param tokens
     * @return
     */
    private SimpleASTNode assignmentStatement(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if (token != null && token.getType() == TokenType.Identifier) { //先匹配标识符
            tokens.read();
            node = new SimpleASTNode(ASTNodeType.AssignmentStmt, token.getText());
            node.setToken(token);
            token = tokens.peek();
            if (token != null && token.getType() == TokenType.Assignment) {//匹配等号
                tokens.read();
                SimpleASTNode child = additive(tokens);
                if (child != null) {
                    node.addChild(child);
                    token = tokens.peek();  //预读，看看后面是不是分号
                    if (token != null && token.getType() == TokenType.SemiColon) {
                        tokens.read();      //消耗掉这个分号
                    } else {                //报错，缺少分号
                        throw new Exception("invalid statement, expecting semicolon");
                    }
                } else {
                    //解析readInt();右边是个函数
                    child = funcStatement(tokens);
                    node.addChild(child);
                }
            } else {
                tokens.unread();            //回溯，吐出之前消化掉的标识符
                node = null;
            }
        }
        return node;
    }

    /**
     * 匹配表达式 2+3+4;，(a > b) 解析以后会吃掉右括号和分号
     * com-> add > add
     *
     * @param tokens
     * @return
     * @throws Exception
     */
    private SimpleASTNode expressionStatement(TokenReader tokens) throws Exception {
        int pos = tokens.getPosition();
        //解析加法表达式
        SimpleASTNode node = compare(tokens);
        //node等不等于null都吃掉右括号和分号
        tokens.read();// 吃掉右括号
        //匹配最后一个分号; 1+2+3;
        Token token = tokens.peek();
        if (token != null && token.getType() == TokenType.SemiColon) {
            tokens.read(); //吃掉分号，否则利用指针回溯
        } else if (token != null && token.getType() == TokenType.LeftBigParen) {
//                if (1+2+3) {}这样的表达式，解析完expression以后不一定是;
            return node;
        } else {
            node = null;
            tokens.setPosition(pos);
        }
        return node;
    }


    /**
     * 打印输出AST的树状结构
     *
     * @param node
     * @param indent 缩进字符，由tab组成，每一级多一个tab
     */
    void dumpAST(ASTNode node, String indent) {
        System.out.println(indent + node.getType() + " " + node.getText());
        for (ASTNode child : node.getChildren()) {
            dumpAST(child, indent + "\t");
        }
    }
}
