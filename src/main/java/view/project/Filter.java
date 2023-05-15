package view.project;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class Filter extends FileFilter {
    String ends; // 文件后缀
    String description; // 文件描述文字

    public Filter(String ends, String description) { // 构造函数
        this.ends = ends; // 设置文件后缀
        this.description = description; // 设置文件描述文字
    }

    @Override
    // 只显示符合扩展名的文件，目录全部显示
    public boolean accept(File file) {
        if (file.isDirectory()) return true;
        String fileName = file.getName();
        if (fileName.toUpperCase().endsWith(this.ends.toUpperCase())) return true;
        return false;
    }

    @Override
    // 返回这个扩展名过滤器的描述
    public String getDescription() {
        return this.description;
    }

    // 返回这个扩展名过滤器的扩展名
    public String getEnds() {
        return this.ends;
    }
}
