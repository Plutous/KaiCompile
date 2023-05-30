package toy.scope;

import java.util.LinkedList;
import java.util.List;

/**
 * 作用域
 *
 * @author wwk
 * @since 2023/5/17
 */
public abstract class Scope extends Symbol {
    //该作用域里的成员，包含变量，方法，类，等
    protected List<Symbol> symbols = new LinkedList<Symbol>();
}
