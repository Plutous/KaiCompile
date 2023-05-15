package view;

import toy.parser.Natives;
import toy.parser.SimpleCalculator;
import view.project.Element;
import view.project.Operation;
import view.project.TabItem;
import view.project.TabTextArea;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static view.project.TabItem.outputArea;

public class Frame extends JFrame {

    //输入框
    TabItem tabItem;
    SimpleCalculator calculator = null;

    public Frame(String title) {
        super(title);
        Container container = getContentPane();
        //初始化
        Operation.addPath(Element.openFile);
        tabItem = new TabItem("新文件.ty", null, Operation.setIcon("ty"), "ty");
        container.add(Element.mainJTP);
        Element.textList.add(tabItem.getTextArea());
        Element.titleList.add(tabItem.getLabelText());
        //jfc文本选择对话框
        Element.jfc = Operation.initJFileChooser(Element.jfc);

        //设置菜单属性
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        this.setJMenuBar(Element.menuBar);
        Operation.initMenu();

        Element.jfc.addChoosableFileFilter(Element.txtFilter);

        /**
         * 打开官网（我重新设置成执行代码）
         * **/
        Element.thisJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.获取当前打开界面的code
                int index = Element.mainJTP.getSelectedIndex();
                String code = Element.textList.get(index).getText();
                //代码运行前的filename
                String fileName = Element.titleList.get(index).getText();
                calculator = new SimpleCalculator();

                //先把输入框制空
                outputArea.setText("");
                //把输入框赋值给print函数
                Natives.outputArea = outputArea;
                // 在这里处理提交操作，可以将 inputText 发送到后端进行处理
                calculator.evaluate(code);

                // 最终结果不在这里打印，而是在print函数中打印
                outputArea.append("-------------------\n" + "程序运行结束 exit!");

                //如果运行前不等于运行后，名字依旧是运行前的
                String afterFileName = Element.titleList.get(index).getText();
                if (!fileName.equals(afterFileName)) {
                    //把*删除，不知道他怎么添加的，我自己手动删,代表保存了文件
                    tabItem.getLabelText().setText(fileName);
                    Element.titleList.set(index, tabItem.getLabelText());
                }
            }
        });

        /**
         * 全选菜单事件
         * **/
        Element.selectAllJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Element.textList.get(Element.mainJTP.getSelectedIndex()).selectAll();
            }
        });

        /**
         * 复制菜单事件
         * **/
        Element.copyJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Element.textList.get(Element.mainJTP.getSelectedIndex()).copy();
            }
        });

        /**
         * 粘贴菜单事件
         * **/
        Element.pasteJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Element.textList.get(Element.mainJTP.getSelectedIndex()).paste();
            }
        });

        /**
         * 剪切菜单事件
         * **/
        Element.cutJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Element.textList.get(Element.mainJTP.getSelectedIndex()).cut();
            }
        });

        /**
         * 删除菜单事件
         * **/
        Element.deleteJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Element.textList.get(Element.mainJTP.getSelectedIndex()).replaceSelection("");
            }
        });

        Element.redoJML.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TabTextArea.um.redo();
                } catch (Exception e1) {
                    return;
                }
            }
        });



        /**
         * 新建txt文档菜单被激活
         * **/
        Element.txtJMI.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog(null, "请输入文件名:\n", "新建文件", JOptionPane.PLAIN_MESSAGE);
                if (title == null) return;

                Element.tabIndex++;
                title = title.indexOf(".ty") < 0 ? title += ".ty" : title;
                Element.pathList = null;
//                tabItem = new TabItem(title, null, Operation.setIcon("ty"), "ty");
//                Element.textList.add(tabItem.getTextArea());
//                Element.titleList.add(tabItem.getLabelText());

                //文本替换
                tabItem.editText(title);
                tabItem.textArea.setText("");
                int index = Element.mainJTP.getSelectedIndex();
                Element.jfc.setDialogType(JFileChooser.SAVE_DIALOG);
                //把*删除，不知道他怎么添加的，我自己手动删,代表保存了文件
                tabItem.getLabelText().setText(title);
                Element.titleList.set(index, tabItem.getLabelText());
            }
        });


        /**
         * 打开菜单项被激活，读取文件
         * **/
        Element.openJMI.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出读取文件对话框
                Element.jfc.setDialogType(JFileChooser.OPEN_DIALOG);
                //设置打开文件的目录（打成jar包时打开code文件夹）
                Element.jfc.setCurrentDirectory(new File("./code"));
                //显示读取文件的框
                int result = Element.jfc.showOpenDialog(Element.jfc);
                if (result == JFileChooser.APPROVE_OPTION) {
                    //读取文件
                    if ((Element.openFile = Element.jfc.getSelectedFile()) == null) return;
                    String path = Element.openFile.getPath();

                    //将文件名显示到选择夹子项，将文件内容显示到文本框中
                    String fileName = Operation.getFileName(path);
                    String type = Operation.getFileType(fileName);
                    Operation.addPath(Element.openFile);
                    //不创建选项卡，使用文本替换
                    tabItem.editTitle(fileName);
                    tabItem.textArea.setText(Operation.readFile(path));

                    int index = Element.mainJTP.getSelectedIndex();
                    Element.jfc.setDialogType(JFileChooser.SAVE_DIALOG);
                    //把*删除，不知道他怎么添加的，我自己手动删,代表保存了文件
                    tabItem.getLabelText().setText(fileName);
                    Element.titleList.set(index, tabItem.getLabelText());
                }
            }
        });


        /**
         * 为选项卡添加侦听事件
         * **/
        Element.mainJTP.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Element.nowTabSelect = Element.mainJTP.getSelectedIndex();
            }
        });

        /**
         * 另存为菜单项被激活
         * **/
        Element.saveOtherJMI.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Operation.saveFile();
            }
        });

        /**
         * 保存菜单项被激活
         * **/
        Element.saveJMI.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = Element.mainJTP.getSelectedIndex();
                //获取保存文件地址
                if (Element.pathList == null) {
                    Operation.saveFile();
                } else {
                    try {
                        FileWriter writer = new FileWriter(Element.pathList);
                        writer.append(Element.textList.get(index).getText());
                        writer.flush();
                        writer.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    Operation.titleStart();
                }
            }
        });
    }

}
