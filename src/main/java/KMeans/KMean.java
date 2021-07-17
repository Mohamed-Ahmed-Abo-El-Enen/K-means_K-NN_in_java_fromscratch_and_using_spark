package KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.ml.linalg.Vector;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

public class KMean
{
    public static Map<Centroid, List<SampleData>> fit(List<SampleData> Samples,
                                                  int k,
                                                  Distance distance,
                                                  int maxIterations)
    {
        List<Centroid> centroids = randomCentroids(Samples, k);
        Map<Centroid, List<SampleData>> clusters = new HashMap<>();
        Set<Centroid> previousCentroids = new HashSet<Centroid>();

        // iterate for a pre-defined number of times
        for (int i = 0; i < maxIterations; i++) {
            boolean isLastIteration = i == maxIterations - 1;

            // in each iteration we should find the nearest centroid for each record
            for (SampleData sample : Samples) {
                Centroid centroid = nearestCentroid(sample, centroids, distance);
                assignToCluster(clusters, sample, centroid);
            }

            // if the assignments do not change, then the algorithm terminates
            boolean shouldTerminate = isLastIteration || clusters.keySet().equals(previousCentroids);
            previousCentroids = clusters.keySet();
            if (shouldTerminate)
            {
                break;
            }

            // at the end of each iteration we should relocate the centroids
            centroids = relocateCentroids(clusters);
            clusters = new HashMap<>();
        }

        KMean.PrintCentroidsData(clusters);
        return clusters;
    }

    private static void PrintCentroidsData(Map<Centroid, List<SampleData>> clusters)
    {
        for (Centroid centroid: clusters.keySet())
        {
            centroid.getCoordinates().forEach((key, value) -> System.out.println(key + ":" + value));
            System.out.println("Samples for centroid: "+ centroid.toString());
            clusters.get(centroid).forEach(entry -> entry.getFeatures().forEach((key, value) -> System.out.println(key + ":" + value)));
        }
    }

    private static List<Centroid> randomCentroids(List<SampleData> Samples, int k) {
        List<Centroid> centroids = new ArrayList<>();
        Map<String, Double> maxs = new HashMap<>();
        Map<String, Double> mins = new HashMap<>();

        for (SampleData sample : Samples) {
            sample.getFeatures().forEach((key, value) -> {
                // compares the value with the current max and choose the bigger value between them
                maxs.compute(key, (k1, max) -> max == null || value > max ? value : max);

                // compare the value with the current min and choose the smaller value between them
                mins.compute(key, (k1, min) -> min == null || value < min ? value : min);
            });
        }

        Set<String> attributes = Samples.stream()
                .flatMap(e -> e.getFeatures().keySet().stream())
                .collect(Collectors.toSet());
        for (int i = 0; i < k; i++) {
            Map<String, Double> coordinates = new HashMap<>();
            for (String attribute : attributes) {
                double max = maxs.get(attribute);
                double min = mins.get(attribute);
                coordinates.put(attribute, new Random().nextDouble() * (max - min) + min);
            }
            centroids.add(new Centroid(coordinates));
        }

        return centroids;
    }

    private static Centroid nearestCentroid(SampleData sample, List<Centroid> centroids, Distance distance) {
        double minimumDistance = Double.MAX_VALUE;
        Centroid nearest = null;

        for (Centroid centroid : centroids) {
            double currentDistance = distance.calculate(sample.getFeatures(), centroid.getCoordinates());

            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance;
                nearest = centroid;
            }
        }

        return nearest;
    }

    private static void assignToCluster(Map<Centroid, List<SampleData>> clusters,
                                        SampleData sample,
                                        Centroid centroid) {
        clusters.compute(centroid, (key, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(sample);
            return list;
        });
    }

    private static Centroid average(Centroid centroid, List<SampleData> Samples) {
        if (Samples == null || Samples.isEmpty()) {
            return centroid;
        }

        Map<String, Double> average = centroid.getCoordinates();
        Samples.stream().flatMap(e -> e.getFeatures().keySet().stream())
                .forEach(k -> average.put(k, 0.0));

        for (SampleData sample : Samples) {
            sample.getFeatures().forEach(
                    (k, v) -> average.compute(k, (k1, currentValue) -> v + currentValue)
            );
        }

        average.forEach((k, v) -> average.put(k, v / Samples.size()));

        return new Centroid(average);
    }

    private static List<Centroid> relocateCentroids(Map<Centroid, List<SampleData>> clusters)
    {
        return clusters.entrySet().stream().map(e -> average(e.getKey(), e.getValue())).collect(toList());
    }

    public static void SparkKmeans(SparkSession sparkSession, Dataset<Row> dataset, int num_clusters, int num_iterations)
    {
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(dataset.columns())
                .setOutputCol("features");
        Dataset<Row> vectorized_df = assembler.transform(dataset);

        KMeans kmeans = new KMeans()
                .setK(num_clusters)
                .setSeed(num_iterations);
        KMeansModel model = kmeans.fit(vectorized_df);

        Vector[] centers = model.clusterCenters();
        System.out.println("Cluster Centers: ");
        for (Vector center: centers) {
            System.out.println(center);
        }
    }
}
