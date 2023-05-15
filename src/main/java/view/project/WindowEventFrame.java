package view.project;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowEventFrame implements WindowListener {
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        String title = "";
        for (int i = 0; i < Element.titleList.size(); i++) {
            title = Element.titleList.get(i).getText();
            if (title.indexOf('*') >= 0) {
                int feedback = JOptionPane.showConfirmDialog(null,"还有文件尚未保存,是否确认关闭","确认关闭",JOptionPane.YES_NO_OPTION);
                if (feedback == 1) {
                    Element.frame.setDefaultCloseOperation(0);
                    return;
                } else {
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
