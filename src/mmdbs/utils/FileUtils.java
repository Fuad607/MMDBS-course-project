package mmdbs.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
    public static final File IMAGE_FOLDER = new File("101_ObjectCategories");

    public static List<File> getAllFiles()
    {
        List<File> list = new ArrayList<>();
        addFolder(IMAGE_FOLDER, list);
        return list;
    }

    private static void addFolder(File folder, List<File> list)
    {
        File[] files = folder.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    addFolder(file, list);
                }
                else
                {
                    list.add(file);
                }
            }
        }
    }
}
