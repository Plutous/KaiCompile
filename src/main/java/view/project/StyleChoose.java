package view.project;
import com.formdev.flatlaf.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 主题选择
 */
public class StyleChoose extends JDialog {
    JFrame frame;
    File f;
    String lastTheme;
    public final int showStyleDialog(JFrame frame) {
        this.frame = frame;
        setLocationRelativeTo(this.frame);
        setVisible(true);
        return 0;
    }

    public StyleChoose() {
        this.setTitle("主题");
        lastTheme = Element.themeStyle;
        setModal(true);
        setResizable(false);
        this.setSize(400,150);
        this.setLocationRelativeTo(null);
        JPanel body = new JPanel();
        body.setLayout(null);
//        this.setLayout(null);
        String[] style = new String[]{
                "Flat Light",
                "Flat Dark",
                "Flat Intellij",
                "Flat Darcula",
                "Core",
                "GitHub Dark",
                "GitHub Dark Dimmed",
                "GitHub Light",
                "intellij",
                "Light",
                "one dark",
                "silkworm",
                "solarized dark theme",
                "solarized light theme",
                "Visual Studio 2019 Dark Theme"
        };
        JLabel label = new JLabel("选择主题：");
        final JComboBox styList = new JComboBox(style);
        styList.setSelectedIndex(Element.themeIndex);
        label.setBounds(50,20,100,30);
        styList.setBounds(110,20,200,30);
        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        okBtn.setBounds(130,70,80,25);
        cancelBtn.setBounds(230,70,80,25);
        body.add(label);
        body.add(styList);
        body.add(okBtn);
        body.add(cancelBtn);
        this.add(body);
        cancelBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Operation.setTheme(lastTheme,frame);
                dispose();
            }
        });

        okBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Operation.writeJSON(Element.props.getProperty("user.home") + "\\.EasyPad\\setting.json","themeStyle","" + styList.getSelectedItem().toString());
                    Operation.writeJSON(Element.props.getProperty("user.home") + "\\.EasyPad\\setting.json","themeIndex","" + styList.getSelectedIndex());
                    Operation.getConfig();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                dispose();
            }
        });
        styList.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chooseStyle = styList.getSelectedItem().toString();
                try {
                    switch (chooseStyle) {
                        case "Flat Light": {
                            UIManager.setLookAndFeel(new FlatLightLaf());
                            SwingUtilities.updateComponentTreeUI(frame);
                            Operation.setTheme(chooseStyle,Element.styleChoose);
                            return;
                        }
                        case "Flat Dark": {
                            UIManager.setLookAndFeel(new FlatDarkLaf());
                            SwingUtilities.updateComponentTreeUI(frame);
                            Operation.setTheme(chooseStyle,Element.styleChoose);
                            return;
                        }
                        case "Flat Intellij": {
                            UIManager.setLookAndFeel(new FlatIntelliJLaf());
                            SwingUtilities.updateComponentTreeUI(frame);
                            Operation.setTheme(chooseStyle,Element.styleChoose);
                            return;
                        }
                        case "Flat Darcula": {
                            UIManager.setLookAndFeel(new FlatDarculaLaf());
                            SwingUtilities.updateComponentTreeUI(frame);
                            Operation.setTheme(chooseStyle,Element.styleChoose);
                            return;
                        }
                        case "Core":f = new File("./resources/theme\\Core.theme.json");
                            break;
                        case "GitHub Dark":f = new File("./resources/theme\\GitHub_Dark.theme");
                            break;
                        case "GitHub Dark Dimmed":f = new File("./resources/theme\\GitHub_Dark_Dimmed.theme.json");
                            break;
                        case "GitHub Light":f = new File("./resources/theme\\GitHub_Light.theme.json");
                            break;
                        case "intellij":f = new File("./resources/theme\\intellij.theme.json");
                            break;
                        case "Light":f = new File("./resources/theme\\Light.theme.json");
                            break;
                        case "one dark":f = new File("./resources/theme\\one_dark.theme.json");
                            break;
                        case "silkworm":f = new File("./resources/theme\\silkworm.theme.json");
                            break;
                        case "solarized dark theme":f = new File("./resources/theme\\solarized_dark_theme.theme.json");
                            break;
                        case "solarized light theme":f = new File("./resources/theme\\solarized_light_theme.theme.json");
                            break;
                        case "Visual Studio 2019 Dark Theme":f = new File("./resources/theme\\Visual_Studio_2019_Dark_Theme.theme.json");
                            break;
                        default:{
                            UIManager.setLookAndFeel(new FlatLightLaf());
                            SwingUtilities.updateComponentTreeUI(frame);
                        }
                    }
                    InputStream s = new FileInputStream(f);
                    IntelliJTheme.install(s);
                    SwingUtilities.updateComponentTreeUI(frame);
                    SwingUtilities.updateComponentTreeUI(Element.jfc);
                    Operation.setTheme(chooseStyle,Element.styleChoose);
                }catch (Exception ex) {
                    return;
                }
            }
        });
    }
}
