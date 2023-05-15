package view.project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * left上北放搜索框，中间放输入框，下面放输出框
 * <p>
 * <p>
 * body:选项卡子项总面板
 * textArea:文本框
 * panel:选择夹子标题项面板，用于存放选择夹子项图标、标题文字、关闭按钮
 * Icon:用来设置选择夹子项图标和选择夹子项关闭按钮
 * label:用于显示选择夹子项标题文字
 * panel:选择夹子项面板
 * labeltext:选择夹子项标题文字
 * title:选择夹子项标题文字
 * text:选择夹子项文本框内容
 * labClose:设置关闭按钮
 **/
public class TabItem extends JPanel {
    public static Highlighter highlighter;
    public static Highlighter subhighlighter;
    public static Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
    public static Highlighter.HighlightPainter subpainter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
    public static ArrayList<int[][]> coordinate = new ArrayList<int[][]>();
    public static int searchIndex = -1;
    //输入框，执行结果输出框，
    public static TabTextArea textArea, outputArea, lineNumberArea;
    ImageIcon icon;
    JPanel panel, body, left;
    JLabel labelText, fileImg, markDownLab, state;
    String title, text;
    final JLabel labClose;
    String type;
    private static JTextField searchTxt;
    private static JLabel value;

    public JLabel getState() {
        return this.state;
    }

    public TabTextArea getTextArea() {
        return this.textArea;
    }

    /**
     * 关闭程序
     */
    public void deleteItem() {
        int index = Element.mainJTP.indexOfTabComponent(panel);
        String title = "";
        title = Element.titleList.get(index).getText();
        if (title.indexOf('*') >= 0) {
            int feedback = JOptionPane.showConfirmDialog(null, "当前文件尚未保存,是否确认关闭", "确认关闭", JOptionPane.YES_NO_OPTION);
            if (feedback == 1) {
                return;
            }
        }
        Element.titleList.remove(index);
        Element.textList.remove(index);
        Element.mainJTP.remove(index);
    }

    public void editTitle(String title) {
        this.title = title;
        this.labelText.setText(this.title);
    }

    public String getType() {
        return this.type;
    }

    public void editText(String text) {
        this.text = text;
        this.textArea.setText(this.text);
    }

    public JLabel getLabelText() {
        return this.labelText;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public TabItem(String title, String text, ImageIcon icon, String type) {
        this.title = title;
        this.text = text;
        this.type = type;
        //新建一个选择夹子项
        body = new JPanel();
        left = new JPanel(new BorderLayout());
        //一共有几行几列，网格布局模式
        body.setLayout(new GridLayout(1, 2, 0, 0));

        JPanel searchBar = new JPanel(new BorderLayout());
        JPanel txtBar = new JPanel(new BorderLayout());
        JPanel repBar = new JPanel(new BorderLayout());
        txtBar.setBorder(new EmptyBorder(0, 0, 0, 26));
        searchTxt = new JTextField();
        JTextField repTxt = new JTextField();

        value = new JLabel("", JLabel.CENTER);
        JButton repBtn = new JButton("替换");
        JButton repallBtn = new JButton("替换全部");

        txtBar.add(searchTxt, BorderLayout.CENTER);
        txtBar.add(value, BorderLayout.EAST);
        repBar.add(repTxt, BorderLayout.CENTER);
        repBar.setBorder(new EmptyBorder(0, 0, 0, 20));

        JPanel serBtnBar = new JPanel(new GridLayout(0, 4, 10, 0));
        JPanel repBtnBar = new JPanel(new GridLayout(0, 2, 10, 0));
        repBtnBar.setBorder(new EmptyBorder(0, 10, 0, 0));
        repBtnBar.add(repBtn);
        repBtnBar.add(repallBtn);
        repBar.add(repBtnBar, BorderLayout.EAST);
        JLabel up = new JLabel();
        up.setIcon(new ImageIcon("./resources/images\\up.png"));
        up.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        up.setToolTipText("上一个");
        up.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel down = new JLabel();
        down.setIcon(new ImageIcon("./resources/images\\down.png"));
        down.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        down.setToolTipText("下一个");
        down.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel searchClose = new JLabel();
        searchClose.setIcon(new ImageIcon("./resources/images\\icon_closed.png"));
        searchClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchClose.setToolTipText("关闭");
        searchClose.setBorder(new EmptyBorder(0, 10, 0, 10));

        serBtnBar.add(value);
        serBtnBar.add(up);
        serBtnBar.add(down);
        serBtnBar.add(searchClose);

        searchBar.add(txtBar, BorderLayout.CENTER);
        searchBar.add(serBtnBar, BorderLayout.EAST);
        searchBar.add(repBar, BorderLayout.SOUTH);
        searchBar.setVisible(false);
        left.add(searchBar, BorderLayout.NORTH);

        //新建一个文本框
        textArea = new TabTextArea();
        textArea.setFont(new Font("楷体", Font.PLAIN, 20)); // 设置字体大小为14
        //设置默认代码
        textArea.setText(text);
        //将文本框加入JScrollPane控件中，使其滚动条出现
        JScrollPane inputScrollPane = new JScrollPane(textArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //输出框区域
        outputArea = new TabTextArea();
        outputArea.setFont(new Font("楷体", Font.PLAIN, 20)); // 设置字体大小为14
        outputArea.setEditable(false);
        JScrollPane outputScrollPanel = new JScrollPane(outputArea);
        outputScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        //行号区域
        lineNumberArea = new TabTextArea();
        lineNumberArea.setFont(new Font("楷体", Font.PLAIN, 20)); // 设置字体大小为14
        lineNumberArea.setEditable(false);
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setBorder(new EmptyBorder(5, 5, 0, 0));
        lineNumberArea.setFocusable(false);
        lineNumberArea.setSize(new Dimension(5,10));
        inputScrollPane.setRowHeaderView(lineNumberArea);


        // 创建左右分割窗格，左侧放置上下分割窗格，下方放置输出框
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScrollPane, outputScrollPanel);
        mainSplitPane.setResizeWeight(0.8);

        //嵌套一层，然后放到left的center中，解决了输入框布局问题
        JPanel apanel = new JPanel(new BorderLayout());
        apanel.add(mainSplitPane, BorderLayout.CENTER);

        //输入框和行号框
        left.add(apanel, BorderLayout.CENTER);

        body.add(left);

        //新建一个选择夹子项面板
        panel = new JPanel();

        //设置夹子项标题文字
        labelText = new JLabel(this.title);
        state = new JLabel("*");
        state.setVisible(false);

        //设置关闭按钮图标
        this.icon = new ImageIcon("./resources/images\\icon_closed.png");
        labClose = new JLabel();
        labClose.setIcon(this.icon);
        labClose.setOpaque(false);
        labClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labClose.setToolTipText("关闭");

        Element.mainJTP.addTab(title, body);
        Element.mainJTP.setTabComponentAt(Element.mainJTP.indexOfComponent(body), panel);
        Element.mainJTP.setSelectedComponent(body);

        //设置文件图标
        this.icon = icon;
        this.icon.setImage(this.icon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
        fileImg = new JLabel();
        fileImg.setIcon(icon);


        //将文件图标展示到选择夹子项标题文字左边
        labelText.add(fileImg);
        labelText.setOpaque(false);
        panel.setOpaque(false);
        panel.add(fileImg);
        panel.add(labelText);
        panel.add(state);
        panel.add(labClose);

        Element.searchJMI.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBar.setVisible(true);
                highlighter = textArea.getHighlighter();
                subhighlighter = textArea.getHighlighter();
                renovateHightLine();
            }
        });

        /**
         * 关闭按钮相关事件
         * **/
        labClose.addMouseListener(new MouseListener() {
            /**
             * 鼠标单击选项卡头部关闭按钮
             * **/
            @Override
            public void mouseClicked(MouseEvent e) {
                deleteItem();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            /**
             * 鼠标移入选项卡头部关闭按钮
             * **/
            @Override
            public void mouseEntered(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\tips_wrong.png");
                labClose.setIcon(icon);
            }

            /**
             * 鼠标移出选项卡头部关闭按钮
             * **/
            @Override
            public void mouseExited(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\icon_closed.png");
                labClose.setIcon(icon);
            }
        });

        /**
         * 文本框侦听事件
         * **/
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (type.equals("md")) {
                    String HTML = Operation.mdTurnHTML(textArea.getText());
                    markDownLab.setText("<html>\n" + HTML);
                }
                //修改行号
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (type.equals("md")) {
                    String HTML = Operation.mdTurnHTML(textArea.getText());
                    markDownLab.setText("<html>\n" + HTML);
                }
                //修改行号
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        up.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (highlighter == null || coordinate.size() == 0) return;
                highlighter.removeAllHighlights();
                searchIndex--;
                if (searchIndex < 0) {
                    searchIndex = coordinate.size() - 1;
                }
                int p0 = coordinate.get(searchIndex)[0][0];
                int p1 = coordinate.get(searchIndex)[0][1];
                try {
                    subhighlighter.addHighlight(p0, p1, subpainter);

                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\bg-up.png");
                up.setIcon(icon);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\up.png");
                up.setIcon(icon);
                super.mouseExited(e);
            }
        });
        down.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (highlighter == null || coordinate.size() == 0) return;
                highlighter.removeAllHighlights();
                searchIndex++;
                if (searchIndex >= coordinate.size()) {
                    searchIndex = 0;
                }
                int p0 = coordinate.get(searchIndex)[0][0];
                int p1 = coordinate.get(searchIndex)[0][1];
                try {
                    highlighter.addHighlight(p0, p1, painter);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\bg-down.png");
                down.setIcon(icon);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\down.png");
                down.setIcon(icon);
                super.mouseExited(e);
            }
        });
        searchClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchBar.setVisible(false);
                if (highlighter != null) {
                    highlighter.removeAllHighlights();
                }
                coordinate.clear();
                super.mouseClicked(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\tips_wrong.png");
                searchClose.setIcon(icon);
                super.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Icon icon = new ImageIcon("./resources/images\\icon_closed.png");
                searchClose.setIcon(icon);
                super.mouseExited(e);
            }
        });


        searchTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                renovateHightLine();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (searchTxt.getText().equals("")) {
                    highlighter.removeAllHighlights();
                    return;
                }
                highlighter.removeAllHighlights();
                renovateHightLine();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        repallBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = textArea.getText();
                String result = str.replaceAll(searchTxt.getText(), repTxt.getText());
                textArea.setText(result);
                renovateHightLine();

            }
        });

        repBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int p0 = coordinate.get(searchIndex)[0][0];
                    int p1 = coordinate.get(searchIndex)[0][1];
                    String result = repStr(textArea.getText(), p0, p1, repTxt.getText());
                    textArea.setText("");
                    textArea.setText(result);
                    renovateHightLine();
                } catch (Exception e1) {
                    return;
                }

            }
        });
    }

    private static void renovateHightLine() {
        coordinate.clear();
        highlighter.removeAllHighlights();
        String subStr = searchTxt.getText();
        if (subStr.equals("") || subStr == null) return;
        int count = 0;
        value.setText(count + "个");
        for (int i = 0; i < textArea.getText().length() - subStr.length() + 1; i++) {
            if (textArea.getText().substring(i, i + subStr.length()).equals(subStr)) {
                count++;
                value.setText(count + "个");
                int p0 = i;
                int p1 = p0 + subStr.length();
                int[][] ints = {{p0, p1}};
                coordinate.add(ints);
                try {
                    highlighter.addHighlight(p0, p1, painter);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static String repStr(String str, int p0, int p1, String repStr) {
        String result = "";
        String a = str.substring(0, p0);
        String b = str.substring(p1, str.length());
        result = a + repStr + b;
        return result;

    }

    private void updateLineNumbers() {
        String text = textArea.getText();
        int totalLines = text.split("\n").length;
        StringBuilder numbersText = new StringBuilder();
        for (int i = 1; i <= totalLines; i++) {
            numbersText.append(i).append("\n");
        }
        lineNumberArea.setText(numbersText.toString());
    }
}
