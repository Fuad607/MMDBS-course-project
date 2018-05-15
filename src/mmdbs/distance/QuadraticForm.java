package mmdbs.distance;

import mmdbs.utils.Util;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

public class QuadraticForm
{
    /**
     * Calculates the quadratic form distance for the given histograms.
     * @param hist1 The first array.
     * @param hist2 The second array.
     * @param noBins The amount of values in each cell.
     * @param noCells The amount of cells.
     * @return A double between 1.0 and 0.0 where 1.0 = same and 0.0 = totally different.
     */
    public static double getDistance(double[] hist1, double[] hist2, int noBins, int noCells)
    {
        double[][] human = Util.createHumanPerceptionSimilarityMatrix(noBins);
        RealMatrix a = MatrixUtils.createRealMatrix(human);
        double d = 0;
        for (int i = 0; i < noCells; i++)
        {
            double[] hi1 = new double[noBins];
            double[] hi2 = new double[noBins];
            System.arraycopy(hist1, noBins * i, hi1, 0, noBins);
            System.arraycopy(hist2, noBins * i, hi2, 0, noBins);
            RealVector h1 = MatrixUtils.createRealVector(hi1);
            RealVector h2 = MatrixUtils.createRealVector(hi2);
            RealVector h = h1.subtract(h2);
            d += a.preMultiply(h).dotProduct(h);
        }
        return 1.0 - (d / noCells);
    }

    /** The similarity matrix values */
    private double[] similarity;

    /** The calculated p*V for the query vector for every cell */
    private double[][] query;

    /** The matrix that we multiply our test histograms with */
    private RealMatrix testMatrix;

    /**
     * Loads query histogram, creates human perception similarity matrix.
     * @param queryHistogram The histogram of the query image.
     * @param noBins The amount of bins.
     * @param noCells The amount of cells.
     */
    public void loadQuery(double[] queryHistogram, int noBins, int noCells)
    {
        double[][] human = Util.createHumanPerceptionSimilarityMatrix(noBins);
        RealMatrix a = MatrixUtils.createRealMatrix(human);
        SingularValueDecomposition svd = new SingularValueDecomposition(a);
        similarity = svd.getSingularValues();
        query = new double[noCells][noBins];
        for (int i = 0; i < noCells; i++)
        {
            double[] hi1 = new double[noBins];
            System.arraycopy(queryHistogram, noBins * i, hi1, 0, noBins);
            RealVector h1 = MatrixUtils.createRealVector(hi1);
            query[i] = svd.getU().preMultiply(h1).toArray();
        }
        testMatrix = svd.getV();
    }

    /**
     * Calculates the distance between the in {@link #loadQuery(double[], int, int)} loaded query and the given
     * test histogram.
     * @param testHistogram The histogram to compare the query to.
     * @param r The cutoff of comparison.
     * @param noBins The amount of bins.
     * @param noCells The amount of cells.
     * @return The distance between both histograms normalized between 0 and 1.
     */
    public double getDistanceToQuery(double[] testHistogram, int r, int noBins, int noCells)
    {
        double d = 0;
        for (int i = 0; i < noCells; i++)
        {
            double[] test = new double[noBins];
            System.arraycopy(testHistogram, noBins * i, test, 0, noBins);
            test = testMatrix.preMultiply(test);
            d += Euclidean.calculateWeightedEuclideanDistance(query[i], test, similarity, r, noBins, 1);
        }
        return d / noCells;
    }
}
