package toy;


import toy.parser.Natives;
import toy.parser.SimpleCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 主运行类，显示窗口
 */
public class InputOutputInterface extends JFrame {
    public JTextArea getOutputArea() {
        return outputArea;
    }

    private JTextArea inputArea;
    public JTextArea outputArea;
    private JTextArea lineNumberArea;
    SimpleCalculator calculator = new SimpleCalculator();

    public InputOutputInterface() {
        setTitle("输入输出界面");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建输入框
        inputArea = new JTextArea();
        inputArea.setFont(new Font("楷体", Font.PLAIN, 20)); // 设置字体大小为14
        inputArea.setLineWrap(true);
        //设置默认代码
        String text = "num a = 100, b = 105;\n" +
                "if(a > b) {\n" +
                "  print(a);\n" +
                "} else {\n" +
                "  print(b);\n" +
                "}";
        inputArea.setText(text);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 创建输出框
        outputArea = new JTextArea();
        outputArea.setFont(new Font("楷体", Font.PLAIN, 20)); // 设置字体大小为14
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 创建行号区域
        lineNumberArea = new JTextArea();
        //行号区域默认显示
        lineNumberArea.setText("1\n2\n3\n4\n5\n6\n");
        lineNumberArea.setFont(new Font("楷体", Font.PLAIN, 20)); // 设置字体大小为14
        lineNumberArea.setEditable(false);
        lineNumberArea.setBackground(Color.LIGHT_GRAY);
        lineNumberArea.setBorder(new EmptyBorder(0, 5, 0, 5));
        lineNumberArea.setFocusable(false);


        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
        });

        // 创建提交按钮
        JButton submitButton = new JButton("提交");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //先把输入框制空
                outputArea.setText("");

                String inputText = inputArea.getText();
                //把输入框赋值给print函数
                Natives.outputArea = outputArea;
                // 在这里处理提交操作，可以将 inputText 发送到后端进行处理
                calculator.evaluate(inputText);

                // 最终结果不在这里打印，而是在print函数中打印
                outputArea.append("-------------------\n" + "程序运行结束 exit!");
            }
        });

        // 创建左侧面板，包含行号区域
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(lineNumberArea, BorderLayout.CENTER);

        // 创建上下分割窗格，上方放置行号区域，下方放置输入框
        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, inputScrollPane);
        splitPane1.setResizeWeight(0.05); //设置行号面板和输入框面板的占比

        // 创建左右分割窗格，左侧放置上下分割窗格，右侧放置输出框
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane1, outputScrollPane);
        mainSplitPane.setResizeWeight(0.7);

        // 创建面板，放置左右分割窗格和提交按钮
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mainSplitPane, BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);

        // 添加面板到界面
        add(panel, BorderLayout.CENTER);

        setSize(600, 600);
        setVisible(true);
    }

    private void updateLineNumbers() {
        String text = inputArea.getText();
        int totalLines = text.split("\n").length;
        StringBuilder numbersText = new StringBuilder();
        for (int i = 1; i <= totalLines; i++) {
            numbersText.append(i).append("\n");
        }
        lineNumberArea.setText(numbersText.toString());
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(InputOutputInterface::new);
    }
}
