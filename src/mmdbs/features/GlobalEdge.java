package mmdbs.features;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GlobalEdge
{
    /**
     * Calculates both the horizontal and vertical sobel operator on the image, adds them up and passes the resulting
     * image to the ColorHistogram class.
     * @param image The input image.
     * @param noBins The amount of bins in the histogram. {@see ColorHistogram.getColorHistogram()}
     * @param noCells The amount of cells in the image. {@see ColorHistogram.getColorHistogram()}
     * @return The color histogram. {@see ColorHistogram.getColorHistogram()}
     */
    public static double[][] getGlobalEdgeColorHistogram(BufferedImage image, int noBins, int noCells)
    {
        Kernel kernel = new Kernel(3, 3, new float[] {1,0,-1,2,0,-2,1,0,-1});
        Kernel kernel2 = new Kernel(3, 3, new float[] {1,2,1,0,0,0,-1,-2,-1});
        ConvolveOp op = new ConvolveOp(kernel);
        ConvolveOp op2 = new ConvolveOp(kernel2);
        BufferedImage conv1 = op.filter(image, null);
        BufferedImage conv2 = op2.filter(image, null);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                /* add maximum of each channel to result image */
                int c1 = conv1.getRGB(x, y);
                int c2 = conv2.getRGB(x, y);
                int r = Math.max((c1 >>> 16) & 255, (c2 >>> 16) & 255);
                int g = Math.max((c1 >>> 8) & 255, (c2 >>> 8) & 255);
                int b = Math.max(c1 & 255, c2 & 255);
                result.setRGB(x, y, (r << 16) + (g << 8) + b);
            }
        }
        return ColorHistogram.getColorHistogram(result, noBins, noCells);
    }
}
