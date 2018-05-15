package mmdbs.features;

import java.awt.image.BufferedImage;

public class ColorHistogram
{
    /**
     * Calculates the color histogram from the given image.
     * @param image The image to calculate the color histogram from. May not be null.
     * @param noBins The number of bins, ie. how many different color values are used (per color).
     *               Must fulfill {@code 1 <= noBins <= 256}
     * @param noCells The number of cells, ie. in how many different parts the image is split per axis; the color histogram
     *                is calculated for each part separately. The actual number of cells is 2^noCells.
     *                Must fulfill {@code 1<= noCells <= min(image.width, image.height)}
     * @return The feature vector containing the color histogram.
     *         result[0] is the color histogram for red, result[1] is green and result[2] is blue
     */
    public static double[][] getColorHistogram(BufferedImage image, int noBins, int noCells)
    {
        if (image == null)
        {
            throw new IllegalArgumentException("image may not be null");
        }
        if (noBins < 0 || noBins > 256)
        {
            throw new IllegalArgumentException("noBins must not be outside of required range");
        }
        if (noCells < 0 || noCells > Math.min(image.getWidth(), image.getHeight()))
        {
            throw new IllegalArgumentException("noCells must not be outside of required range");
        }
        double[][] result = new double[3][noBins * noCells * noCells];
        int w = image.getWidth() / noCells;
        int h = image.getHeight() / noCells;
        for (int y = 0; y < noCells; y++)
        {
            for (int x = 0; x < noCells; x++)
            {
                double[][] hist = getColorHistogram(image.getSubimage(x * w, y * h, w, h), noBins);
                System.arraycopy(hist[0], 0, result[0], (y*noCells+x) * noBins, noBins);
                System.arraycopy(hist[1], 0, result[1], (y*noCells+x) * noBins, noBins);
                System.arraycopy(hist[2], 0, result[2], (y*noCells+x) * noBins, noBins);
            }
        }
        return result;
    }

    /**
     * Calculates the color histogram from the given image.
     * @param image The image to calculate the color histogram from.
     * @param noBins The number of bins, ie. how many different color values are used (per color).
     * @return The feature vector containing the color histogram. Normalized over the amount of total pixels in the image.
     */
    private static double[][] getColorHistogram(BufferedImage image, int noBins)
    {
        double[] red = new double[noBins];
        double[] green = new double[noBins];
        double[] blue = new double[noBins];
        int binSize = (int) Math.ceil(256d / noBins);

        int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for (int i = 0; i < rgb.length; i++)
        {
            int r = (rgb[i] >>> 16) & 255;
            int g = (rgb[i] >>> 8) & 255;
            int b = rgb[i] & 255;
            red[r / binSize]++;
            green[g / binSize]++;
            blue[b / binSize]++;
        }

        double[][] result = new double[3][noBins];
        int pixels = image.getWidth() * image.getHeight();
        for (int i = 0; i < noBins; i++)
        {
            result[0][i] = red[i] / pixels;
            result[1][i] = green[i] / pixels;
            result[2][i] = blue[i] / pixels;
        }
        return result;
    }
}
