package KNN;

public class DistanceCalculator
{
    public static double GetDistance(double[] sample1, double[] sample2) throws IllegalAccessException
    {
        if (sample1.length != sample2.length)
            throw new IllegalAccessException("2 samples length mismatch");

        double sum = 0;
        try
        {
            for (int i = 0; i < sample1.length; i++)
                sum += Math.pow(sample1[i] - sample2[i], 2);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return Math.sqrt(sum);
    }
}
