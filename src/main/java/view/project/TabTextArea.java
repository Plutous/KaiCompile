package view.project;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;

/**
 *kai:输入框，继承JTextArea
 */
public class TabTextArea extends JTextArea implements MouseListener {

    public TabTextArea() {
        this.setLineWrap(true);
        this.setTabSize(4);
        this.setFont(Element.font);
        init();
        selectAll.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));
        copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
        delete.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK));
        cut.setAccelerator(KeyStroke.getKeyStroke('T', InputEvent.CTRL_DOWN_MASK));
        undo.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK));
//        undo.setMnemonic(KeyStroke.getKeyStroke('Z',InputEvent.CTRL_DOWN_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
        redo.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.CTRL_DOWN_MASK));

    }

    private static final long serialVersionUID = 1L;
    private JMenuItem copy = null;
    private JMenuItem delete = null;
    private JMenuItem cut = null;
    private JMenuItem undo = null;
    private JMenuItem paste = null;
    private JMenuItem redo = null;
    private JMenuItem selectAll = null;

    public static UndoManager um = new UndoManager();

    private void init() {
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    String title = Element.titleList.get(Element.mainJTP.getSelectedIndex()).getText();
                    if (title.indexOf('*') >= 0) return;
                    Element.titleList.get(Element.mainJTP.getSelectedIndex()).setText(title + "*");
                } catch (Exception e1) {
                    return;
                }
            }

            /**
             * 当文档内容被删除时触发
             * @param e
             */
            @Override
            public void removeUpdate(DocumentEvent e) {
                String title = Element.titleList.get(Element.mainJTP.getSelectedIndex()).getText();
                if (title.indexOf('*') >= 0) return;
                Element.titleList.get(Element.mainJTP.getSelectedIndex()).setText(title + "*");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        this.addMouseListener(this);
        this.getDocument().addUndoableEditListener(um);
        Element.pm = new JPopupMenu();

        //全选
        selectAll = new JMenuItem("全选");
        selectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        //粘贴
        paste = new JMenuItem("粘贴");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });

        //复制
        copy = new JMenuItem("复制");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });

        //删除
        delete = new JMenuItem("删除");
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        //剪切
        cut = new JMenuItem("剪切");
        cut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });

        //撤消
        undo = new JMenuItem("撤消");
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        //返回
        redo = new JMenuItem("返回");
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        Element.pm.add(selectAll);
        Element.pm.add(delete);
        Element.pm.add(new JSeparator());
        Element.pm.add(copy);
        Element.pm.add(cut);
        Element.pm.add(paste);
        Element.pm.add(new JSeparator());
        Element.pm.add(undo);
        Element.pm.add(redo);
        this.add(Element.pm);
    }

    //
    public boolean isClipboardString() {
        boolean b = false;
        Clipboard clipboard = this.getToolkit().getSystemClipboard();
        Transferable content = clipboard.getContents(this);
        try {
            if (content.getTransferData(DataFlavor.stringFlavor) instanceof String)
                b = true;
        } catch (Exception e) {
        }
        return b;
    }

    //
    public boolean isCanCopy() {
        boolean b = false;
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start != end)
            b = true;
        return b;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * 按压输入框
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        if (e.getButton() == MouseEvent.BUTTON3) {
            delete.setEnabled(isCanCopy());
            copy.setEnabled(isCanCopy());
            paste.setEnabled(isClipboardString());
            cut.setEnabled(isCanCopy());
            undo.setEnabled(um.canUndo());
            redo.setEnabled(um.canRedo());
            Element.pm.show(this, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void action(ActionEvent e) {
        String str = e.getActionCommand();
        if (str.equals(copy.getText())) { // 复制
            this.copy();
        } else if (str.equals(paste.getText())) { // 粘贴
            this.paste();
        } else if (str.equals(cut.getText())) { // 剪切
            this.cut();
        } else if (str.equals(undo.getText())) {
            try {
                um.undo();//
            } catch (Exception e1) {
                return;
            }
        } else if (str.equals(redo.getText())) {
            try {
                um.redo();
            } catch (Exception e1) {
                return;
            }
        } else if (str.equals(delete.getText())) {
            this.replaceSelection("");
        } else if (str.equals(selectAll.getText())) {
            this.selectAll();
        }
    }
}
