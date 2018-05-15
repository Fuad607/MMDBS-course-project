package mmdbs;

import mmdbs.ui.GUI;
import mmdbs.ui.ImagePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CBIR
{
    private static GUI gui;
    private static BufferedImage query;
    private static Map<String, BufferedImage> map = new TreeMap<>(Comparator.reverseOrder());
    private static java.util.List<String> list = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        gui = new GUI();
    }

    public static boolean loadQuery(File file)
    {
        if (file != null)
        {
            try
            {
                query = ImageIO.read(file);
                return query != null;
            }
            catch (IOException e)
            {
                return false;
            }
        }
        else
        {
            query = null;
            return true;
        }
    }

    public static BufferedImage getQuery()
    {
        return query;
    }

    public static void clearMap()
    {
        map.clear();
    }

    public static void addResult(String name, BufferedImage image, double similarity)
    {
        while (map.size() > 2000)
        {
            NavigableMap<String, BufferedImage> reverseMap = ((TreeMap<String, BufferedImage>)map).descendingMap();
            map.remove(reverseMap.firstKey());
        }
        map.put(similarity + "-" + name, image);
        list.add(similarity + "-" + name);
    }

    public static void redrawResults()
    {
        gui.resultsPanel.removeAll();
        for (Map.Entry<String, BufferedImage> e : map.entrySet())
        {
            String entrySimilarityString = e.getKey().substring(0, e.getKey().indexOf("-"));
            String entryName = e.getKey().substring(e.getKey().indexOf("-") + 1);
            double entrySimilarity = Double.parseDouble(entrySimilarityString);
            ImagePanel ip = new ImagePanel();
            ip.setImage(e.getValue());
            ip.setPreferredSize(new Dimension(100, 100));
            ip.setToolTipText("<html>" + entryName + "<br>" + String.format("%.2f", entrySimilarity * 100) + "%</html>");
            gui.resultsPanel.add(ip);
        }
        gui.resultsPanel.revalidate();
        gui.resultsPane.revalidate();
    }

    public static java.util.List<String> getList()
    {
        return list;
    }

    public static void setGui(GUI g)
    {
        gui = g;
    }
}
