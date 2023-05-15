package toy.bean;

/**
 * @author wwk
 * @since 2023/4/26
 */
public class MyException extends RuntimeException {
    private int lineNumber;
    private int columnNumber;

    public MyException(String message, int lineNumber, int columnNumber) {
        super(message);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }
}
