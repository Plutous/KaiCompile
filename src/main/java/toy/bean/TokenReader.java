package toy.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 方便对tokens进行操作
 * @author wwk
 * @since 2023/4/12
 */
public class TokenReader {
    List<Token> tokens = null;
    int pos = 0;

    public TokenReader(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * 预读，只读不删除
     *
     * @return
     */
    public Token peek() throws Exception {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return null;
    }

    /**
     * 读取并删除
     *
     * @return
     */
    public Token read() throws Exception {
        if (pos < tokens.size()) {
            return tokens.get(pos++);
        }
        return null;
    }

    public Token read(int i) throws Exception {
        if (i > tokens.size()) {
            return null;
        }
        return tokens.get(i);
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void add(Token token) {
        tokens.add(token);
    }

    //获取tokens列表的索引位置
    public int getPosition() {
        return pos;
    }

    public void setPosition(int pos) {
        this.pos = pos;
    }

    public void unread() {
        if (pos > 0) {
            pos--;
        }
    }
}
