package KMeans;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.*;

public class SampleData
{
    private Map<String, Double> features;

    public SampleData()
    { }

    public void setFeatures(Map<String, Double> features) {
        this.features = features;
    }

    public Map<String, Double> getFeatures() {
        return this.features;
    }

    private static SampleData ConvertStringToSampleData(Row row)
    {
        SampleData sample = new SampleData();
        Map<String, Double> features = new HashMap<String, Double>();
        for(int i=0; i< row.schema().fields().length; i++)
        {
            String variableName = row.schema().fields()[i].name();
            features.put(variableName, Double.parseDouble(row.get(i).toString()));
            sample.setFeatures(features);
        }
        return sample;
    }

    public static List<SampleData> GetSamplesListFromRowList(Dataset<Row> rowDataset)
    {
        List<Row> rowList = rowDataset.collectAsList();
        List<SampleData> samplesList = new ArrayList<SampleData>();
        for (Row row : rowList) {
            samplesList.add(SampleData.ConvertStringToSampleData(row));
        }
        return samplesList;
    }
}