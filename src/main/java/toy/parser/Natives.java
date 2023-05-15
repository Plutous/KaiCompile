package toy.parser;

import javax.swing.*;

/**
 * 单例模式
 *
 * @author wwk
 * @since 2023/4/27
 */
public class Natives {
    public static JTextArea outputArea;


    public static int print(Object obj) {
        //使用面板启动的时候，输出到面板
        try {
            outputArea.append(obj.toString() + "\n");
        } catch (Exception e) {
            //第一次运行会打印面板为空，无所谓
//            System.out.println("");
        }
//        System.out.println("内置方法打印:" + obj.toString());
        return 0;
    }

    public static String readInt() {
        return JOptionPane.showInputDialog(null);
    }
}
