package view;

import com.formdev.flatlaf.FlatLightLaf;
import view.project.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainForm {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    createGUI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void createGUI() throws IOException {
        FlatLightLaf.install();
        initStyle();
        Operation.getConfig();
        Element.font = new Font(Element.fontFamily, Element.fontStyle, Element.fontSize);
        final Frame frame = new Frame("KaiPL");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(1000, 600);
        frame.setIconImage(new ImageIcon("./resources/images/bitbug_favicon.png").getImage());
        Operation.setTheme(Element.themeStyle, frame);

//        String currentPath = System.getProperty("user.dir");
//        System.out.println("当前路径：" + currentPath);
        Element.styleJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Element.styleChoose = new StyleChoose();
                Element.styleChoose.showStyleDialog(frame);
            }
        });

        int windowWidth = frame.getWidth(); //获得窗口宽
        int windowHeight = frame.getHeight(); //获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); //定义工具包
        Dimension screenSize = kit.getScreenSize(); //获取屏幕的尺寸
        int screenWidth = screenSize.width; //获取屏幕的宽
        int screenHeight = screenSize.height; //获取屏幕的高
        frame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);//设置窗口居中显示

        frame.addWindowListener(new WindowEventFrame());
        Operation.setFormNoClose(frame);
    }

    /**
     * 设置全局控件样式
     **/
    public static void initStyle() {
        UIManager.put("Menu.arc", 100);

        //设置按钮样式
        UIManager.put("Button.arc", 10);

        //设置文本框样式
        UIManager.put("TextComponent.arc", 1);

        //设置选择框样式
        UIManager.put("TabbedPane.showTabSeparators", true);
    }
}
