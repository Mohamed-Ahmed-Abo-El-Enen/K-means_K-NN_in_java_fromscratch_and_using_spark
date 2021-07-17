package Main;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class JobDAOImpl {

    List<String> ReversedWord = Arrays.asList("6th of october", "branch", "upper egypt", "multinational", "saudi");

    private String RemoveReversedWord(String value) {
        for (String Word : this.ReversedWord) {
            value = value.toLowerCase().replace(Word, "");
        }
        return value;
    }

    private String CalculateYearsExp(String value) {
        value = value.toLowerCase().replace("yrs of exp", "");
        value = value.replace("+", "");
        String[] temp_values = value.trim().split("-");
        if (temp_values.length == 1) {
            return temp_values[0].replace("null", "0");
        } else {
            return temp_values[1];
        }
        //return String.valueOf(Float.parseFloat(temp_values[1]) / Float.parseFloat(temp_values[0]));
    }

    private List<Job> ConvertLinesToListJobs(List<List<String>> lines) {
        List<Job> jobList = new ArrayList<>();
        try {
            for (List<String> line : lines) {
                String Location = line.get(2);
                String Country = line.get(6);
                String Title = line.get(0)//.replaceAll("[^\\x00-\\x7F]", "")
                        .replace(Location, "")
                        .replace(Country, "")
                        .replaceAll("\\s{2,}", " ")
                        .replaceAll("[^a-zA-Z0-9\\s]", "");
                Title = RemoveReversedWord(Title);
                //if (Title.contains("mechanical engineer"))
                //{
                //    int x = 0;
                //}
                String Company = line.get(1)//.replaceAll("[^\\x00-\\x7F]", "")
                        .replace(Location, "")
                        .replaceAll("\\s{2,}", " ")
                        .replace(Country, "")
                        .replaceAll("[^a-zA-Z0-9\\s]", "");
                Company = RemoveReversedWord(Company);

                String Type = line.get(3);

                String Level = line.get(4);

                String YearsExp = CalculateYearsExp(line.get(5));

                String Skills = line.get(7).substring(1, line.get(7).length() - 1)
                        .replaceAll("[^\\x00-\\x7F]", "");

                jobList.add(new Job(Title, Company, Location, Type, Level, YearsExp, Country, Skills));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jobList;
    }

    public List<Job> ReadCSVFile(String filePath) {
        return ConvertLinesToListJobs(new FileHandler(filePath).ReadCSVFile());
    }

    public Dataset<Row> SparkReadCSVFile(SparkSession sparkSession, String filePath) {
        return new FileHandler(filePath).SparkReadCSVFile(sparkSession, filePath);
    }

    public static Job ConvertStringToJob(Row row) {
        Job job = new Job();
        for (int i = 0; i < row.schema().fields().length; i++) {
            String variableName = row.schema().fields()[i].name();
            Object value = row.get(i).toString();
            job.setVariablesByName(variableName, value.toString());
        }
        return job;
    }

    public static List<Job> ConvertRowDatasetToList(Dataset<Row> rowDataset) {
        List<Row> jobRowList = rowDataset.collectAsList();
        List<Job> jobList = new ArrayList<Job>();
        for (Row row : jobRowList) {
            jobList.add(JobDAOImpl.ConvertStringToJob(row));
        }
        return jobList;
    }
}
