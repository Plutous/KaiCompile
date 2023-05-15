package toy.bean;

/**
 * 自动机的状态，自动机只有处于235状态下才会想去跳转下一个状态
 * 枚举类，枚举自动机的几种起始状态(例如!=，保存!状态就可以不用保存!=状态)
 *
 * @author wwk
 * @since 2023/3/8
 */
public enum DfaState {
    Initial, //自动机处于初始状态
    Identifier, //自动机处于字母状态
    GT,
    GE,
    NumLiteral,
    Id_num1, //num关键字的第一步
    Id_num2,
    Id_num,
    Assignment,// =
    Plus, // +
    Minus, // -
    Star, //*
    Slash, // 除
    LeftPart, // 左括号
    RightPart, // 右括号
    LeftBigPart, // 左大括号
    RightBigPart, // } 右大括号
    OR, And, Modulo, No,// || , && , % , !
    String_No1, String_No2, String_No3, String_No4, String_No5, String_No6, String, //形成string的6个阶段
    Boolean_No1, Boolean_No2, Boolean_No3, Boolean_No4, Bool, //形成bool的4个阶段
    Break_No1, Break_No2, Break_No3, Break_No4, Break_No5, Break,//形成break的5个阶段
    If_1, If_2, If, //if的两个阶段,第一个阶段先由int_No1,再判断转换
    While_No1, While_No2, While_No3, While_No4, While_No5, While,
    Continue_No1, Continue_No2, Continue_No3, Continue_No4, Continue_No5, Continue_No6, Continue_No7, Continue_No8, Continue,
    Else_NO1, Else_NO2, Else_NO3, Else_NO4, Else,//形成else的4个阶段
    False_No1, False_No2, False_No3, False_No4, False_No5, False,
    True_NO1, True_NO2, True_NO3, True_NO4, True, NoEqual, OR_1, OR_2, And_2, And_1, Equal, SemiColon,//形成true的4个阶段
    Func_1, Func, //Func_1是只匹配了左括号，Func是匹配了左右括号
    Comma, string_1,
    Mark,  // "
    LT, LE, EOL,
}

