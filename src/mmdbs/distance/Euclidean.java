package mmdbs.distance;

/**
 * Created by Fuad on 6/24/2017.
 */
public class Euclidean
{
    /**
     * Calculates the euclidean distance between the arrays hist1 and hist2. Uses the number of cells to normalize the
     * output.
     * @param hist1 The first array.
     * @param hist2 The second array.
     * @param noCells The number of cells in the histograms.
     * @return A double between 1.0 and 0.0 where 1.0 = the histograms are the same and 0.0 = they are as different
     * as can be.
     */
    public static double calculateEuclideanDistance(double[] hist1, double[] hist2, int noCells)
    {
        double innerSum = 0.0;
        for (int i = 0; i < hist1.length; i++)
        {
            innerSum += Math.pow(hist1[i] - hist2[i], 2.0);
        }
        return 1 - (Math.sqrt(innerSum) / (noCells * Math.sqrt(2)));
    }

    /**
     * Calculates the weighted euclidean distance between the arrays hist1 and hist2. Uses the number of cells to
     * normalize the output.
     * @param hist1 The first array.
     * @param hist2 The second array.
     * @param w The array of weights.
     * @param r The cutoff for comparison.
     * @param noBins The amount of bins in the histogram.
     * @param noCells The number of cells in the histograms.
     * @return A double between 1.0 and 0.0 where 1.0 = the histograms are the same and 0.0 = they are as different
     * as can be.
     */
    public static double calculateWeightedEuclideanDistance(double[] hist1, double[] hist2, double[] w, int r, int noBins, int noCells)
    {
        double innerSum = 0.0;
        for (int i = 0; i < hist1.length; i++)
        {
            if (i % noBins >= r) continue;
            innerSum += w[i] * Math.pow(hist1[i] - hist2[i], 2.0);
        }
        return 1 - (Math.sqrt(innerSum) / (noCells * Math.sqrt(2)));
    }
}