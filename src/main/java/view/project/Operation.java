package view.project;

import com.formdev.flatlaf.*;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.*;

public class Operation {

    /**
     * 初始化JScrollPane控件，为文本框加入滚动条
     **/
    public static JScrollPane initScrollPane(JTextArea jTextArea) {
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jTextArea);
        //分别设置水平和垂直滚动条自动出现
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return jScrollPane;

    }

    /**
     * 获取文件名
     **/
    public static String getFileName(String path) {
        String fileName;
        String[] str;
        try {
            str = path.split("\\\\");
        } catch (Exception e) {
            return "新文件";
        }
        fileName = str[str.length - 1];
        return fileName;
    }


    /**
     * 获取文件类型
     **/
    public static String getFileType(String fileName) {
        String type;
        String[] str = fileName.split("\\.");
        try {
            type = str[str.length - 1];
        } catch (Exception e) {
            return "";
        }
        return type;
    }

    /**
     * 初始化菜单
     **/
    public static void initMenu() {
        //添加文件菜单
        Element.menuBar.add(Element.fileMenu);
        //添加文件菜单子项
        Element.fileMenu.add(Element.openJMI);
        Element.fileMenu.add(Element.newJML);
        Element.newJML.add(Element.txtJMI);
        Element.fileMenu.add(Element.saveJMI);
        Element.fileMenu.add(Element.saveOtherJMI);

        //添加编辑菜单
        Element.menuBar.add(Element.editMenu);
        //添加编辑菜单子项
        Element.editMenu.add(Element.selectAllJML);
        Element.editMenu.add(Element.deleteJML);
        Element.editMenu.add(new JSeparator());
        Element.editMenu.add(Element.copyJML);
        Element.editMenu.add(Element.cutJML);
        Element.editMenu.add(Element.pasteJML);
        Element.editMenu.add(new JSeparator());
        Element.editMenu.add(Element.redoJML);
        Element.editMenu.add(Element.searchJMI);

        //添加设置菜单
        Element.menuBar.add(Element.setMenu);
        //添加设置菜单子项
        Element.setMenu.add(Element.styleJML);

        //添加关于菜单
        Element.menuBar.add(Element.aboutMenu);
        //添加关于菜单子项
        Element.aboutMenu.add(Element.thisJML);

        //菜单添加快捷键
        Element.saveJMI.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        Element.openJMI.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
        Element.saveOtherJMI.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
        Element.selectAllJML.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));


        Element.copyJML.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
        Element.deleteJML.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK));
        Element.cutJML.setAccelerator(KeyStroke.getKeyStroke('T', InputEvent.CTRL_DOWN_MASK));
        Element.pasteJML.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
        Element.redoJML.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.CTRL_DOWN_MASK));
        Element.searchJMI.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK));
    }

    /**
     * 初始化文本框属性
     **/
    public static JTextArea initTextArea(String title) {
        JTextArea jTextArea = new JTextArea(title);
        jTextArea.setLineWrap(true);
        jTextArea.setTabSize(4);
        return jTextArea;
    }


    /**
     * 读取文件方法，读取文本文件内容，返回数据
     **/
    public static String readFile(String path) {
        String content = "";
        //System.out.println("选择的文件路径:" + path);
        Element.openFile = new File(path);
        try {
            if (!Element.openFile.exists()) {
                Element.openFile.createNewFile();
            }
            //打开文件，读取文件流
            Element.fileInputStream = new FileInputStream(Element.openFile);
            byte arr[] = new byte[(int) Element.openFile.length()];
            Element.fileInputStream.read(arr);
            //防止中文乱码
            content = new String(arr, "GBK");
            Element.fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    /**
     * 比较过滤器
     **/
    public static boolean equalsFilter(Filter filter) {
        if (filter.equals(Element.txtFilter)) return true;
        return false;
    }


    /**
     * 保存文件
     **/
    public static void saveFile() {
        int index = Element.mainJTP.getSelectedIndex();
        Element.jfc.setDialogType(JFileChooser.SAVE_DIALOG);
        //以*为分隔符，看文件是什么类型
        String type = Element.titleList.get(index).getText().replace("*", "").split("\\.")[1];
        switch (type) {
            case "ty":
                Element.jfc.setFileFilter(Element.txtFilter);
                break;
        }

        Element.jfc.setSelectedFile(new File(Element.titleList.get(index).getText().replace("*", "")));
        //设置打开文件的目录（打成jar包时打开code文件夹）
        Element.jfc.setCurrentDirectory(new File("./code"));
        // 显示文件保存的对话框
        int result = Element.jfc.showSaveDialog(Element.jfc);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = Element.jfc.getSelectedFile(); //获得文件名
            // 获得被选中的过滤器
            Filter filter = null;
            try {
                filter = (Filter) Element.jfc.getFileFilter();
            } catch (Exception e) {
                switch (type) {
                    case "ty":
                        Element.jfc.setFileFilter(Element.txtFilter);
                        break;
                }
                filter = (Filter) Element.jfc.getFileFilter();
            }
            // 获得过滤器的扩展名
            String ends = filter.getEnds();
            File newFile = null;
            if (file.getAbsolutePath().toUpperCase().endsWith(ends.toUpperCase())) {
                // 如果文件是以选定扩展名结束的，则使用原名
                newFile = file;
            } else {
                // 否则加上选定的扩展名
                newFile = new File(file.getAbsolutePath() + ends);
            }
            try {
                FileWriter writer = new FileWriter(newFile.getPath());
                writer.append(Element.textList.get(index).getText());
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element.pathList =  newFile.getPath();
            Operation.titleStart();
        }
    }

    /**
     * 设置文件对话框样式，创建一个对话框
     **/
    public static JFileChooser initJFileChooser(JFileChooser jFileChooser) {
        //Element.props.getProperty("user.home")
        jFileChooser = new JFileChooser(new File(Element.props.getProperty("user.home") + "\\Desktop")) {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                Image image = null;
                try {
                    image = ImageIO.read(new FileInputStream("./resources/images/bitbug_favicon.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                dialog.setIconImage(image);
                SwingUtilities.updateComponentTreeUI(this);
                return dialog;
            }
        };
        return jFileChooser;
    }


    public static void createConfig() throws IOException {
        File f = new File(Element.props.getProperty("user.home") + "\\.EasyPad");// 实例化File类的对象
        f.mkdir();    // 创建文件夹
        FileWriter writer = new FileWriter(Element.props.getProperty("user.home") + "\\.EasyPad\\setting.json");
        writer.append("{\n" +
                "    \"fontFamily\": \"微软雅黑\",\n" +
                "    \"fontSize\": \"16\",\n" +
                "    \"themeIndex\": \"0\",\n" +
                "    \"themeStyle\": \"Flat Light\",\n" +
                "    \"fontStyle\": \"0\"\n" +
                "}");
        writer.flush();
        writer.close();
    }

    /**
     * 获取设置文件
     **/
    public static void getConfig() throws IOException {
        try {
            Operation.getJSONContent(Operation.readFileContent(Element.props.getProperty("user.home") + "\\.EasyPad\\setting.json"));
        } catch (Exception e) {
            createConfig();
        }
    }

    /**
     * 将路径保存到pathList
     **/
    public static void addPath(File openFile) {
        openFile = Element.jfc.getSelectedFile();
        try {
            Element.pathList=openFile.getPath();
        } catch (Exception e) {
            Element.pathList=null;
        }
    }

    /**
     * 保存文件时*号的增减
     **/
    public static void titleStart() {
        String tabTitlt = Element.titleList.get(Element.mainJTP.getSelectedIndex()).getText();
        if (tabTitlt.indexOf('*') < 0) return;
        tabTitlt = tabTitlt.substring(0, tabTitlt.length() - 1);
        Element.titleList.get(Element.mainJTP.getSelectedIndex()).setText(tabTitlt);
    }

    /**
     * 传输窗口
     **/
    public static JFrame setFormNoClose(JFrame frame) {
        Element.frame = frame;
        return Element.frame;
    }

    /**
     * 序列化json文件
     **/
    public static void getJSONContent(String JSONText) throws IOException {
        JSONObject setJSONObject;
        try {
            setJSONObject = new JSONObject(JSONText);
            Element.fontFamily = setJSONObject.getString("fontFamily");
            Element.fontSize = setJSONObject.getInt("fontSize");
            Element.fontStyle = setJSONObject.getInt("fontStyle");
            Element.themeStyle = setJSONObject.getString("themeStyle");
            Element.themeIndex = setJSONObject.getInt("themeIndex");
        } catch (JSONException e) {
            Operation.createConfig();
            e.printStackTrace();
        }
    }

    /**
     * 读取json文件
     **/
    public static String readFileContent(String path) throws IOException {
        File file = new File(path);
        FileReader read = null;
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            read = new FileReader(file);
        } catch (Exception e) {
            createConfig();
            read = new FileReader(file);

        }
        try {

            reader = new BufferedReader(read);
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

    /**
     * 修改json文件
     **/
    public static void writeJSON(String path, String key, String value) throws IOException {
        JSONTokener jsonTokener = new JSONTokener(new FileInputStream(path));
        JSONObject jsonObject = new JSONObject(jsonTokener);
        //System.out.println(jsonObject);

        // 添加一些信息
        jsonObject.put(key, value);
        // 向文件中写入，缩进因子设置为4，缩进为0
        Writer write = jsonObject.write(new FileWriter(path), 4, 0);
        // 写入后及时关闭文件
        write.close();
    }

    /**
     * 设置选项卡图标，把ty改成了txt图标
     **/
    public static ImageIcon setIcon(String type) {
        type = type.replace(".", "");
        ImageIcon icon;
        switch (type) {
            case "ty":
                icon = new ImageIcon("./resources/images\\Txt.png");
            case "新文件":
                icon = new ImageIcon("./resources/images\\Txt.png");
                break;
            case "jpg":
            case "png":
            case "bmp":
                icon = new ImageIcon("./resources/images\\Image.png");
                break;
            case "pdf":
                icon = new ImageIcon("./resources/images\\Pdf.png");
                break;
            case "zip":
            case "7z":
            case "rar":
                icon = new ImageIcon("./resources/images\\Zip.png");
                break;
            case "doc":
            case "docx":
                icon = new ImageIcon("./resources/images\\Word.png");
                break;
            case "ppt":
            case "pptx":
                icon = new ImageIcon("./resources/images\\PPT.png");
                break;
            case "xls":
            case "xlsx":
                icon = new ImageIcon("./resources/images\\Excel.png");
                break;
            case "gif":
                icon = new ImageIcon("./resources/images\\Gif.png");
                break;
            case "mp4":
            case "rmvb":
            case "3pg":
            case "mkv":
            case "avi":
            case "flv":
                icon = new ImageIcon("./resources/images\\Video.png");
                break;
            case "mp3":
            case "wma":
            case "wav":
            case "ape":
            case "flac":
                icon = new ImageIcon("./resources/images\\Mp3.png");
                break;
            case "java":
            case "cs":
            case "c":
            case "cpp":
            case "vue":
            case "html":
            case "py":
            case "js":
            case "json":
            case "css":
                icon = new ImageIcon("./resources/images\\Code.png");
                break;
            case "md":
                icon = new ImageIcon("./resources/images\\markDown.png");
                break;
            default:
                icon = new ImageIcon("./resources/images\\Any.png");
                break;
        }
        return icon;
    }

    /**
     * 设置窗口主题
     **/
    public static void setTheme(String theme, Component component) {
        File f = null;
        try {
            switch (theme) {
                case "Flat Light": {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    SwingUtilities.updateComponentTreeUI(component);
                    SwingUtilities.updateComponentTreeUI(Element.jfc);
                    return;
                }
                case "Flat Dark": {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                    SwingUtilities.updateComponentTreeUI(component);
                    SwingUtilities.updateComponentTreeUI(Element.jfc);
                    return;
                }
                case "Flat Intellij": {
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    SwingUtilities.updateComponentTreeUI(component);
                    SwingUtilities.updateComponentTreeUI(Element.jfc);
                    return;
                }
                case "Flat Darcula": {
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                    SwingUtilities.updateComponentTreeUI(component);
                    SwingUtilities.updateComponentTreeUI(Element.jfc);
                    return;
                }
                case "Core":
                    f = new File("./resources/theme\\Core.theme.json");
                    break;
                case "GitHub Dark":
                    f = new File("./resources/theme\\GitHub_Dark.theme");
                    break;
                case "GitHub Dark Dimmed":
                    f = new File("./resources/theme\\GitHub_Dark_Dimmed.theme.json");
                    break;
                case "GitHub Light":
                    f = new File("./resources/theme\\GitHub_Light.theme.json");
                    break;
                case "intellij":
                    f = new File("./resources/theme\\intellij.theme.json");
                    break;
                case "Light":
                    f = new File("./resources/theme\\Light.theme.json");
                    break;
                case "one dark":
                    f = new File("./resources/theme\\one_dark.theme.json");
                    break;
                case "one dark italic":
                    f = new File("notepad\\./resources/theme\\one_dark_italic.theme.json");
                    break;
                case "one dark vivid":
                    f = new File("./resources/theme\\one_dark_vivid.theme.json");
                    break;
                case "one dark vivid italic":
                    f = new File("./resources/theme\\one_dark_vivid_italic.theme.json");
                    break;
                case "silkworm":
                    f = new File("./resources/theme\\silkworm.theme.json");
                    break;
                case "solarized dark theme":
                    f = new File("./resources/theme\\solarized_dark_theme.theme.json");
                    break;
                case "solarized light theme":
                    f = new File("./resources/theme\\solarized_light_theme.theme.json");
                    break;
                case "Visual Studio 2019 Dark Theme":
                    f = new File("./resources/theme\\Visual_Studio_2019_Dark_Theme.theme.json");
                    break;
                default: {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                    SwingUtilities.updateComponentTreeUI(component);
                    SwingUtilities.updateComponentTreeUI(Element.jfc);
                }
            }
            if (f == null) return;
            InputStream s = new FileInputStream(f);
            IntelliJTheme.install(s);
            SwingUtilities.updateComponentTreeUI(component);
            SwingUtilities.updateComponentTreeUI(Element.jfc);
        } catch (Exception ex) {
            return;
        }
    }

    /**
     * 将MarkDown语言转换为HTNL
     **/
    public static String mdTurnHTML(String markDown) {
        String HTML = "";
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(markDown);
        HTML = renderer.render(document);
        HTML = HTML.replaceAll("\\[x\\]", "<input type=\"checkbox\">");
        return HTML;
    }
}
