package view.project;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 在这里修改txt为ty类型
 */
public class Element {
    public static int deleteIndex = 0;
    public static int tabIndex = 0;
    public static int nowTabSelect = 0;
    public static String fontFamily = null;
    public static int fontSize = 0;
    public static int fontStyle = 0;
    public static String themeStyle = null;
    public static int themeIndex = 0;
    public static Font font;
    public static StyleChoose styleChoose;

    //用于获取系统信息
    public static Properties props = System.getProperties();

    public static JFrame frame;
    //创建文本框
    public static JTextArea mainJTA = new JTextArea();

    //把定义的JTextArea放到JScrollPane里面去
    public static JScrollPane scroll = new JScrollPane(mainJTA);

    //kai:创建显示框
    //kai:创建行号框
    public JTextArea outputArea = new JTextArea();
    JScrollPane outputScrollPane = new JScrollPane(outputArea);

    private JTextArea lineNumberArea = new JTextArea();
    JScrollPane lineNumberPane = new JScrollPane(lineNumberArea);


    //创建菜单工具栏
    public static JMenuBar menuBar = new JMenuBar();

    //创建菜单文件菜单
    public static JMenu fileMenu = new JMenu("文件");
    //创建文件菜单下的子菜单
    public static JMenuItem openJMI = new JMenuItem("打开");
    //    public static JMenuItem openTabJMI = new JMenuItem("在新的标签页中打开");
    public static JMenu newJML = new JMenu("新建");
    //    public static JMenuItem newJMI = new JMenuItem("新建");
    public static JMenuItem txtJMI = new JMenuItem("文本文档");
//    public static JMenuItem markDownJMI = new JMenuItem("MarkDown");
    public static JMenuItem saveJMI = new JMenuItem("保存");
    public static JMenuItem saveOtherJMI = new JMenuItem("另存为");
    public static JMenuItem searchJMI = new JMenuItem("查找");
//    public static JMenuItem printList = new JMenuItem("遍历");

    public static JPopupMenu pm = null;


    //创建编辑菜单
    public static JMenu editMenu = new JMenu("编辑");
    //创建编辑菜单子项
    public static JMenuItem copyJML = new JMenuItem("复制");
    public static JMenuItem deleteJML = new JMenuItem("删除");
    public static JMenuItem cutJML = new JMenuItem("剪切");
//    public static JMenuItem undoJML = new JMenuItem("撤销");
    public static JMenuItem pasteJML = new JMenuItem("粘贴");
    public static JMenuItem redoJML = new JMenuItem("返回");
    public static JMenuItem selectAllJML = new JMenuItem("全选");

    //创建设置菜单
    public static JMenu setMenu = new JMenu("设置");
    //创建设置菜单子项
//    public static JMenuItem fontJML = new JMenuItem("字体");
    public static JMenuItem styleJML = new JMenuItem("主题");
    public static JMenuItem FlatLightJML = new JMenuItem("Flat Light");
    public static JMenuItem FlatDarkJML = new JMenuItem("Flat Dark");
    public static JMenuItem FlatIntellijJML = new JMenuItem("Flat Intellij");
    public static JMenuItem FlatDarculaJML = new JMenuItem("Flat Darcula");
    public static JMenuItem SolarizedLightContrastJML = new JMenuItem("Solarized Light Contrast");
    public static JMenuItem ArcJML = new JMenuItem("Arc");
    public static JMenuItem ArcOrangeJML = new JMenuItem("Arc Orange");
    public static JMenuItem ArcDarkJML = new JMenuItem("Arc Dark");
    public static JMenuItem ArcDarkOrangeJML = new JMenuItem("Arc Dark Orange");
    public static JMenuItem CarbonJML = new JMenuItem("Carbon");
    public static JMenuItem CyanLightJML = new JMenuItem("Cyan Light");
    public static JMenuItem DarkFlatJML = new JMenuItem("Dark Flat");
    public static JMenuItem DarkPurpleJML = new JMenuItem("Dark purple");


    //创建关于菜单
    public static JMenu aboutMenu = new JMenu("运行");
    //创建关于菜单子项
    public static JMenuItem thisJML = new JMenuItem("运行Code");

    //创建文件选择对话框
    public static JFileChooser jfc = new JFileChooser(new File("."));

    //创建选择夹
    public static JTabbedPane mainJTP = new JTabbedPane();

    //    public static Hashtable<TabTitle,TabTextArea> hashItem = new Hashtable<TabTitle,TabTextArea>();
    public static ArrayList<JTextArea> textList = new ArrayList<JTextArea>();
    public static ArrayList<JLabel> titleList = new ArrayList<JLabel>();
    //    public static ArrayList<String> pathList = new ArrayList<String>();
    public static String pathList = null;

    //数据流
    public static File openFile;                        //文件类
    public static FileInputStream fileInputStream;       //字节文件输入流
    public static FileOutputStream fileOutputStream;     //字节文件输出流
    public static OutputStreamWriter outputStreamWriter; //字符文件输出流

    //文件过滤器
    public static Filter txtFilter = new Filter(".ty", "ty 文件 (*.ty)");
//    public static Filter mdFilter = new Filter(".md", "MarkDown 文件 (*.md)");

}
