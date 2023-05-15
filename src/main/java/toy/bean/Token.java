package toy.bean;

/**
 * @author wwk
 * @since 2023/3/8
 */
public class Token {
    public static final Token EOF = new Token(TokenType.EOF, "\n", 1, 1);
    public static final Token EOL = new Token(TokenType.EOL, "\n", -1, -1);
    public int type;
    public String text;
    private int lineNumber;
    private int columnNumber;

    public Token(int type, String text, int lineNumber, int columnNumber) {
        this.type = type;
        this.text = text;
    }

    public Token(int lineNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return TokenType.arr[type] + ": " + text;
    }
}
