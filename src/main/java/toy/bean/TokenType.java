package toy.bean;

/**
 * 每一个Token单词的状态，字母或者 > 或者 >= 或者数字
 *
 * @author wwk
 * @since 2023/3/8
 */
public class TokenType {
    //    public static final int Create = 1;
    public static final int Identifier = 1; //字母状态
    public static final int GT = 2;
    public static final int GE = 3;
    public static final int NumLiteral = 4;
    //这俩完全用不到,因为最后Token不可能处于中间状态
//    public static final int Id_int1 = 6;
//    public static final int Id_int2 = 7;
//    public static final int Id_int = 5; //整形字面量
    public static final int Id_num = 5; //整形/浮点型字面量
    public static final int Id_float = 6;
    public static final int Id_string = 7;
    public static final int Id_bool = 8;
    public static final int Assignment = 9; // =
    public static final int Equals = 10; // ==
    public static final int Plus = 11; // +
    public static final int Minus = 12; // -
    public static final int Star = 13; // *
    public static final int Slash = 14; // 除
    public static final int Modulo = 15; // %
    public static final int EOF = 16; // 换行
    public static final int LeftParen = 17; // (
    public static final int RightParen = 18; // )
    public static final int LeftBigParen = 19; // {
    public static final int RightBigParen = 20; // }
    public static int And = 21; // &&
    public static int OR = 22; // ||
    public static final int No = 23; // !
    public static final int NoEquals = 24; // !=
    public static int Power = 25; //**
    public static int Break = 26; //break关键字
    public static int If = 27; //if关键字
    public static int Else = 28; //else关键字
    public static int While = 29; //while关键字
    public static int Continue = 30; // continue关键字
    //    public static int True = 31; //true
//    public static int False = 32; //false
    public static int SemiColon = 33; //分号 ;
    public static int Comma = 34; // ,号
    public static int Func = 35;
    public static int StringLiteral = 36;
    public static int BoolLiteral = 37;
    public static final int LT = 38;
    public static final int LE = 39;
    public static final int EOL = 40;

    public static String[] arr = {"", "Identifier", "GT", "GE", "NumLiteral", "Id_num", "Id_float", "Id_string", "Id_bool",
            "Assignment", "Equals", "Plus", "Minus", "Star", "Slash", "Modulo", "EOF", "LeftPart", "RightPart", "LeftBigPart",
            "RightBigPart", "And", "OR", "No", "NoEquals", "Power", "Break", "If", "Else", "While", "Continue", "True", "False",
            "SemiColon", "Comma", "Func", "StringLiteral", "BoolLiteral", "LT", "LE", "EOL"};

}
