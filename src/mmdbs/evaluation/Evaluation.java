package mmdbs.evaluation;

import mmdbs.CBIR;
import mmdbs.distance.Euclidean;
import mmdbs.features.ColorHistogram;
import mmdbs.ui.GUI;
import mmdbs.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Evaluation
{
    private Random random = new Random();

    /**
     * Tests that the difference between an image and itself is near zero
     */
    @Test
    public void testSameImage()
    {
        for (int noBins = 1; noBins <= 256; noBins*=2)
        {
            for (int noCells = 1; noCells <= 64; noCells++)
            {
                File image = getRandomImage(FileUtils.IMAGE_FOLDER);
                System.out.println("[same image] noBins=" + noBins + ", noCells=" + noCells + ", image=" + image);
                Assert.assertNotNull(image);
                try
                {
                    BufferedImage query = ImageIO.read(image);
                    Assert.assertTrue(query.getWidth() >= 64);
                    Assert.assertTrue(query.getHeight() >= 64);
                    BufferedImage test = ImageIO.read(image);
                    double[][] queryHistogram = ColorHistogram.getColorHistogram(query, noBins, noCells);
                    double[][] testHistogram = ColorHistogram.getColorHistogram(test, noBins, noCells);

                    double redDistance = Euclidean.calculateEuclideanDistance(queryHistogram[0], testHistogram[0], noCells);
                    double greenDistance = Euclidean.calculateEuclideanDistance(queryHistogram[1], testHistogram[1], noCells);
                    double blueDistance = Euclidean.calculateEuclideanDistance(queryHistogram[2], testHistogram[2], noCells);
                    double distance = (redDistance + greenDistance + blueDistance) / 3;
                    Assert.assertEquals(1.0D, distance, 0.0000000000001);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void createPrecisionRecallCurves()
    {
        GUI gui = new GUI();
        CBIR.setGui(gui);
        String folder = "101_ObjectCategories\\pizza";
        File query = new File(folder + "\\image_0003.jpg");
        CBIR.loadQuery(query);
        gui.query("Color Histogram", "Quadratic Form", 64, 4, 64);
        List<String> list = CBIR.getList();
        Collections.sort(list);
        Collections.reverse(list);
        int correct = 0;
        int total = 0;
        for (String s : list)
        {
            if (s.contains(folder))
                correct++;
            total++;
            System.out.println(s + "," + ((double)correct / total));
        }
        /* save image */
        BufferedImage image = new BufferedImage(gui.resultsPane.getWidth(), gui.resultsPane.getHeight(), BufferedImage.TYPE_INT_RGB);
        gui.resultsPane.paint(image.createGraphics());
        try
        {
            ImageIO.write(image, "PNG", new File("screenshot.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private File getRandomImage(File folder)
    {
        File[] files = folder.listFiles();
        if (files != null)
        {
            File file = files[random.nextInt(files.length)];
            return file.isDirectory() ? getRandomImage(file) : file;
        }
        return null;
    }
}
