package toy.lexer;


import toy.bean.*;

import java.util.ArrayList;

/**
 * 词法解析器，执行main函数运行
 *
 * @author wwk
 * @since 2023/3/23
 */
public class Lexer {
    private StringBuilder tokenText = null; //临时保存Token中的值
    private ArrayList<Token> lineTokens = null; //保存一行解析出的tokens(按行)
    private TokenReader tokens = null;//最终保存所有解析出的tokens
    private Token token = null;
    private boolean hasMore = true;
    //该token的行和列，用于报错是输出信息
    int line = 1, row = 1;

    public Lexer() {
        token = new Token(line, row);
        tokenText = new StringBuilder();
        lineTokens = new ArrayList<Token>();
    }

    public TokenReader parseB(String script) throws Exception {
        parse(script);
        // 直接解析所有的code代码(如果用输入框的话把下下面代码注释了)
        tokens = new TokenReader((ArrayList<Token>) lineTokens);
        return tokens;
    }

    public TokenReader parse(String code) throws Exception {

//        String str = deleteWhitespace(code);
        DfaState state = DfaState.Initial;
        char ch = 0;
        //不需要去除字符串里的空格，因为如果空格进来自动机的状态依旧是init，for循环就会自动把空格去了
        //每执行一个for循环，列号加1
        for (int i = 0; i < code.length(); i++, row++) {
            ch = code.charAt(i);

            if (ch == '\n') {
                row = 1;
                line++;
            }
            switch (state) {
                case Initial:
                    state = initToken(ch);
                    break;
                case Identifier: //变量名后面是字母或者数字都可
                    if (isAlpha(ch) || isDigit(ch)) {
                        tokenText.append(ch);
                    } else {
                        //initToken方法会保存当前token
                        token.type = TokenType.Identifier;
                        //当前状态是关键字，并且后面跟的不是数字或字母，这个自动机状态就要转移了
                        state = initToken(ch); //重新判断状态，并保存前一个token
                    }
                    break;
                case Mark:
                    if (ch == '"') {
                        ch = ' ';
                        token.type = TokenType.StringLiteral;
                        state = initToken(ch);
                    } else {
                        tokenText.append(ch);
                    }
                    break;
                case EOL:
                    token.type = TokenType.EOF;
                    state = initToken(ch);
                    break;
                case Func_1:
                    if (ch != ' ') {
                        state = DfaState.Func;
                        token.type = TokenType.Func;
                        tokenText.append(ch);
                    } else {
                        throw new MyException("非法的内置函数", line, row);
                    }
                    break;
                case Func:
                    //readInt()后面是空格或者分隔符都可
                    if (isBlank(ch) || ch == ';') {
                        token.type = TokenType.Func;
                        state = initToken(ch);
                    } else {
                        throw new MyException("非法的内置函数", line, row);
                    }
                    break;
                case If_1:
                    if (ch == 'f') {
                        //转到if的状态去
                        state = DfaState.If_2;
                        tokenText.append(ch);
                        token.type = TokenType.If;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //不是int关键字，转成标准处理字符
                        state = DfaState.Identifier;
                        token.type = TokenType.Identifier;
                        //不应该添加，这里会导致=加入到
                        tokenText.append(ch);
                    } else {
                        //重新判断状态,例如 i = i + 1，
                        state = initToken(ch);
                    }
                    break;
                case Id_num1:
                    if (ch == 'u') {
                        //进入int第二个状态
                        state = DfaState.Id_num2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //不是int关键字，转成标准处理字符
                        state = DfaState.Identifier;
                        token.type = TokenType.Identifier;
                        //不应该添加，这里会导致=加入到
                        tokenText.append(ch);
                    } else {
                        //重新判断状态,例如 i = i + 1，
                        state = initToken(ch);
                    }
                    break;
                case Id_num2:
                    if (ch == 'm') {
                        //进入int第二个状态
                        state = DfaState.Id_num;
                        tokenText.append(ch);
                        token.type = TokenType.Id_num;
                    } else if (isDigit(ch) || isAlpha(ch)) {
                        //不是int关键字，转成标准处理字符
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);// 秒呀，只有字母或数字会引起自动机的变化，其他情况直接重新判断状态就行
                    }
                    break;
                case Id_num:
                    //如果int后还有字符或者数字（还得检测后面有没有空格）,如果有空格这就是个关键字，然后继续往下走
                    if (isBlank(ch)) {
                        token.type = TokenType.Id_num;
                        state = initToken(ch);//重新对这个字符进行判断（空格的话进去自动机状态不会变，所以应该不需要取空格）
                    } else {
                        state = DfaState.Identifier; //切换到普通的标志符状态
                        tokenText.append(ch);
                    }
                    break;
                case String_No1:
                    if (ch == 't') {
                        state = DfaState.String_No2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        //标识符的(Token)结束，重新判断ch
                        state = initToken(ch);
                    }
                    break;
                case String_No2:
                    if (ch == 'r') {
                        state = DfaState.String_No3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case String_No3:
                    if (ch == 'i') {
                        state = DfaState.String_No4;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case String_No4:
                    if (ch == 'n') {
                        state = DfaState.String_No5;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case String_No5:
                    if (ch == 'g') {
                        state = DfaState.String_No6;
                        tokenText.append(ch);
                        token.type = TokenType.Id_string;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case String_No6:
                    if (isBlank(ch)) {
                        //string 后是空格,保存string关键字
                        token.type = TokenType.Id_string;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //string后还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Boolean_No1:
                    if (ch == 'o') {
                        state = DfaState.Boolean_No2;
                        tokenText.append(ch);
                    } else if (ch == 'r') {
                        state = DfaState.Break_No2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Boolean_No2:
                    if (ch == 'o') {
                        state = DfaState.Boolean_No3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Boolean_No3:
                    if (ch == 'l') {
                        state = DfaState.Boolean_No4;
                        tokenText.append(ch);
                        token.type = TokenType.Id_bool;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Boolean_No4:
                    if (isBlank(ch)) {
                        token.type = TokenType.Id_bool;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Break_No2:
                    if (ch == 'e') {
                        state = DfaState.Break_No3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Break_No3:
                    if (ch == 'a') {
                        state = DfaState.Break_No4;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Break_No4:
                    if (ch == 'k') {
                        state = DfaState.Break_No5;
                        tokenText.append(ch);
                        token.type = TokenType.Break;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Break_No5:
                    if (isBlank(ch)) {
                        token.type = TokenType.Break;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case If_2:
                    if (isBlank(ch)) {
                        token.type = TokenType.If;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case While_No1:
                    if (ch == 'h') {
                        state = DfaState.While_No2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case While_No2:
                    if (ch == 'i') {
                        state = DfaState.While_No3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case While_No3:
                    if (ch == 'l') {
                        state = DfaState.While_No4;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case While_No4:
                    if (ch == 'e') {
                        state = DfaState.While_No5;
                        tokenText.append(ch);
                        token.type = TokenType.While;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case While_No5:
                    if (isBlank(ch)) {
                        token.type = TokenType.While;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No1:
                    if (ch == 'o') {
                        state = DfaState.Continue_No2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No2:
                    if (ch == 'n') {
                        state = DfaState.Continue_No3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No3:
                    if (ch == 't') {
                        state = DfaState.Continue_No4;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No4:
                    if (ch == 'i') {
                        state = DfaState.Continue_No5;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No5:
                    if (ch == 'n') {
                        state = DfaState.Continue_No6;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No6:
                    if (ch == 'u') {
                        state = DfaState.Continue_No7;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No7:
                    if (ch == 'e') {
                        state = DfaState.Continue_No8;
                        tokenText.append(ch);
                        token.type = TokenType.Continue;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Continue_No8:
                    if (isBlank(ch)) {
                        token.type = TokenType.Continue;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Else_NO1:
                    if (ch == 'l') {
                        state = DfaState.Else_NO2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Else_NO2:
                    if (ch == 's') {
                        state = DfaState.Else_NO3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Else_NO3:
                    if (ch == 'e') {
                        state = DfaState.Else_NO4;
                        tokenText.append(ch);
                        token.type = TokenType.Else;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case Else_NO4:
                    if (isBlank(ch)) {
                        token.type = TokenType.Else;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case False_No1:
                    if (ch == 'a') {
                        state = DfaState.False_No2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case False_No2:
                    if (ch == 'l') {
                        state = DfaState.False_No3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case False_No3:
                    if (ch == 's') {
                        state = DfaState.False_No4;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case False_No4:
                    if (ch == 'e') {
                        state = DfaState.False_No5;
                        tokenText.append(ch);
                        token.type = TokenType.BoolLiteral;
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case False_No5:
                    if (isBlank(ch)) {
                        token.type = TokenType.BoolLiteral;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case True_NO1:
                    if (ch == 'r') {
                        state = DfaState.True_NO2;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case True_NO2:
                    if (ch == 'u') {
                        state = DfaState.True_NO3;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case True_NO3:
                    if (ch == 'e') {
                        //如果这是switch语句最后一个字符，他就会无法判断类型，所以在这里加上他的类型
                        token.type = TokenType.BoolLiteral;
                        state = DfaState.True_NO4;
                        tokenText.append(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case True_NO4:
                    if (isBlank(ch)) {
                        token.type = TokenType.BoolLiteral;
                        state = initToken(ch);
                    } else if (isAlpha(ch) || isDigit(ch)) {
                        //bool后面还有字母或数字, 转成标识符状态
                        state = DfaState.Identifier;
                        tokenText.append(ch);
                    } else {
                        state = initToken(ch);
                    }
                    break;
                case GT: //当前状态为 > ，判断后面是否为等号
                    if (ch == '=') {
                        tokenText.append(ch);
                        //GE状态无后序状态了，从新转成init状态
                        state = DfaState.GE;
                    } else {
                        //符号为>，当前token结束
                        token.type = TokenType.GT;
                        //当前字符不能舍弃，继续判断类型
                        state = initToken(ch);
                    }
                    break;
                case GE:
                    if (isBlank(ch)) {
                        token.type = TokenType.GE;
                    }
                    //>=后面还有字符就不该他设置type了
                    state = initToken(ch);
                    break;
                case LT: //当前状态为 < ，判断后面是否为等号
                    if (ch == '=') {
                        tokenText.append(ch);
                        //GE状态无后序状态了，从新转成init状态
                        state = DfaState.LE;
                    } else {
                        //符号为>，当前token结束
                        token.type = TokenType.LT;
                        //当前字符不能舍弃，继续判断类型
                        state = initToken(ch);
                    }
                    break;
                case LE:
                    if (isBlank(ch)) {
                        token.type = TokenType.LE;
                    }
                    //>=后面还有字符就不该他设置type了
                    state = initToken(ch);
                    break;
                case NumLiteral:
                    //怎么排除数字后面是空格情况
                    // 想要整形或浮点型就加上这个 || ch == '.'
                    if (isDigit(ch) || ch == '.') {
                        tokenText.append(ch);
                    } else {
                        //当前状态结束，保存token
                        token.type = TokenType.NumLiteral;
                        state = initToken(ch);
                    }
                    break;
                case Assignment:
                    if (ch == '=') {
                        // 状态转移（应该是状态清除，这里已经是最后一个状态了）
                        tokenText.append(ch);
                        //状态机转移到Equals
                        state = DfaState.Equal;
                    } else {
                        //如果在等号后面是空格则说明是赋值语句 =
                        token.type = TokenType.Assignment;
                        //重新初始化这个字符
                        state = initToken(ch);
                    }
                    break;
                case Equal:
                    if (isBlank(ch)) {
                        token.type = TokenType.Equals;
                    }
                    state = initToken(ch);
                    break;
                case And_1:
                    if (ch == '&') {
                        //与运算符
                        tokenText.append(ch);
                        state = DfaState.And_2;
                    } else {
                        //非法字符
                        throw new MyException("非法字符", line, row);
                    }
                    break;
                case And_2:
                    if (isBlank(ch)) {
                        token.type = TokenType.And;
                        state = initToken(ch);
                    } else {
                        throw new MyException("输入的&&符不正确", line, row);
                    }
                    break;
                case OR_1:
                    if (ch == '|') {
                        //与运算符
                        tokenText.append(ch);
                        state = DfaState.OR_2;
                    } else {
                        throw new MyException("非法||运算符", line, row);
                    }
                    break;
                case OR_2:
                    if (isBlank(ch)) {
                        token.type = TokenType.OR;
                        state = initToken(ch);
                    } else {
                        throw new MyException("输入的OR符不正确", line, row);
                    }
                    break;
                case No:
                    if (ch == '=') {
                        tokenText.append(ch);
                        //状态转移到NoEqual
                        state = DfaState.NoEqual;
                    } else {
                        token.type = TokenType.No;
                        state = initToken(ch);
                    }
                    break;
                case NoEqual:
                    if (isBlank(ch)) {
                        //!= 成立
                        token.type = TokenType.NoEquals;
                        state = initToken(ch);
                    } else {
                        //否则报错
                        throw new RuntimeException();
                    }
                    break;
                case Plus:
                    token.type = TokenType.Plus;
                    state = initToken(ch);
                    break;
                case Minus:
                    token.type = TokenType.Minus;
                    state = initToken(ch);
                    break;
                case Star:
                    if (ch == '*') { //乘方运算
                        tokenText.append(ch);
                        token.type = TokenType.Power;
                    } else {
                        token.type = TokenType.Star;
                        state = initToken(ch);
                    }
                    break;
                case Slash:
                    token.type = TokenType.Slash;
                    state = initToken(ch);
                    break;
                case Modulo:
                    token.type = TokenType.Modulo;
                    state = initToken(ch);
                    break;
                case LeftPart:
                    //(a)，括号的后面必定有单词，所以这里不该判空
                    token.type = TokenType.LeftParen;
                    state = initToken(ch);
                    break;
                case RightPart:
                    token.type = TokenType.RightParen;
                    state = initToken(ch);
                    break;
                case LeftBigPart:
                    token.type = TokenType.LeftBigParen;
                    state = initToken(ch);
                    break;
                case RightBigPart:
                    token.type = TokenType.RightBigParen;
                    state = initToken(ch);
                    break;
                case SemiColon:
                    token.type = TokenType.SemiColon;
                    state = initToken(ch);
                    break;
                case Comma:
                    token.type = TokenType.Comma;
                    state = initToken(ch);
                    break;
            }
        }
        //switch遍历到最后一个字母,但是还没保存.
        //可能为int数,false,true,string,float数.用state来判断
        //判断最后一个字符，cao，这里判断的时候自动把最后一个字符加进入tokenText，难怪一直有bug
        if (tokenText.length() > 0) {
            initToken(ch);
        }
        //清空tokenText
        tokenText.delete(0, tokenText.length());

        return tokens;
    }

    /**
     * 自动机可以接收的第一个参数（自动机第一次能直接跳转的状态）
     */
    public DfaState initToken(char ch) {
        //mark == 0 代表着是 ""的第一个引号
        int mark = 0;

        //tokenText中有值，保存这个Token
        if (tokenText.length() > 0) {
            token.text = tokenText.toString();
            lineTokens.add(token);

            //清空TokenText，重新创建token对象
            token = new Token(line, row);

            tokenText.delete(0, tokenText.length());
        }

        DfaState newState = DfaState.Initial; //初始为状态1

        if (isAlpha(ch)) {
            token.type = TokenType.Identifier;
            //判断该字符是不是int关键字，秒呀在这里判断i，而不是解析的Id时候
            if (ch == 'n') {
                newState = DfaState.Id_num1;
                tokenText.append(ch);
            } else if (ch == 'i') { //if
                newState = DfaState.If_1;
                tokenText.append(ch);
            } else if (ch == 'f') { //float,false
                newState = DfaState.False_No1;
                tokenText.append(ch);
            } else if (ch == 's') { //string
                newState = DfaState.String_No1;
                tokenText.append(ch);
            } else if (ch == 'b') { //bool,break
                //先假设成bool,然后下一个阶段的状态机判断是否需要转移到break
                newState = DfaState.Boolean_No1;
                tokenText.append(ch);
            } else if (ch == 'w') { //while
                newState = DfaState.While_No1;
                tokenText.append(ch);
            } else if (ch == 'c') { //continue
                newState = DfaState.Continue_No1;
                tokenText.append(ch);
            } else if (ch == 'e') { //else关键字
                newState = DfaState.Else_NO1;
                tokenText.append(ch);
            } else if (ch == 't') { //true关键字
                newState = DfaState.True_NO1;
                tokenText.append(ch);
            } else { //否则当成标识符看待
                newState = DfaState.Identifier;
                token.type = TokenType.Identifier;
                tokenText.append(ch);
            }
        } else if (isDigit(ch)) {
            newState = DfaState.NumLiteral; //自动机状态设为数字
            token.type = TokenType.NumLiteral;
            tokenText.append(ch);
        } else if (ch == '>') {         // 第一个字符是 >  greater than
            newState = DfaState.GT;
            token.type = TokenType.GT;
            tokenText.append(ch);
        } else if (ch == '<') { //Less Than
            newState = DfaState.LT;
            token.type = TokenType.LT;
            tokenText.append(ch);
        } else if (ch == '=') { //第一个字符是=
            newState = DfaState.Assignment;
            token.type = TokenType.Assignment;
            tokenText.append(ch);
        } else if (ch == '+') { //第一个字符是+
            newState = DfaState.Plus;
            token.type = TokenType.Plus;
            tokenText.append(ch);
        } else if (ch == '-') { //第一个字符是-
            newState = DfaState.Minus;
            token.type = TokenType.Minus;
            tokenText.append(ch);
        } else if (ch == '*') { //第一个字符是*
            newState = DfaState.Star;
            token.type = TokenType.Star;
            tokenText.append(ch);
        } else if (ch == '/') { //第一个字符是/
            newState = DfaState.Slash;
            token.type = TokenType.Slash;
            tokenText.append(ch);
        } else if (ch == '%') { //取模Modulo
            newState = DfaState.Modulo;
            token.type = TokenType.Modulo;
            tokenText.append(ch);
        } else if (ch == '&') { //与 &&
            newState = DfaState.And;
            token.type = TokenType.And;
            tokenText.append(ch);
        } else if (ch == '|') { //或 ||
            newState = DfaState.OR_1;
            token.type = TokenType.OR;
            tokenText.append(ch);
        } else if (ch == '!') { //非 ！
            newState = DfaState.No;
            token.type = TokenType.No;
            tokenText.append(ch);
        } else if (ch == '(') {
            newState = DfaState.LeftPart;
            tokenText.append(ch);
            //左右括号也是有可能做最后一个字符，所以也是先添加状态
            token.type = TokenType.LeftParen;
        } else if (ch == ')') {
            newState = DfaState.RightPart;
            tokenText.append(ch);
            token.type = TokenType.RightParen;
        } else if (ch == '{') {
            newState = DfaState.LeftBigPart;
            tokenText.append(ch);
            token.type = TokenType.LeftBigParen;
        } else if (ch == '}') {
            newState = DfaState.RightBigPart;
            tokenText.append(ch);
            token.type = TokenType.RightBigParen;
        } else if (ch == ';') {
            newState = DfaState.SemiColon;
            tokenText.append(ch);
            token.type = TokenType.SemiColon;
        } else if (ch == ',') {
            newState = DfaState.Comma;
            tokenText.append(ch);
            token.type = TokenType.Comma;
        } else if (ch == '"') {
            newState = DfaState.Mark;
            //接下来解析字符串 "abcd"
//            tokenText.append(ch);
            token.type = TokenType.StringLiteral;
        } else if (ch == '\n') {
            // \n算一个字符
            newState = DfaState.EOL;
            //接下来解析字符串 "abcd"
            token.type = TokenType.EOL;
            lineTokens.add(Token.EOL);
        }
        return newState;
    }

    private boolean isBlank(char ch) {
        return ch == ' ';
    }


    public static boolean isAlpha(Character c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }

}
