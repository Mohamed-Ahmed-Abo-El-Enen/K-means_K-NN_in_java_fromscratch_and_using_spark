package Main;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class FileHandler {

    private String filePath;
    private List<List<String>> Lines;

    FileHandler(String filePath) {
        this.filePath = filePath;
        this.Lines = new LinkedList<>();
    }

    public List<List<String>> getLines() {
        return Lines;
    }

    public List<List<String>> ReadCSVFile() {
        String splitRegs = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
        try {
            Thread thread = new Thread(() -> {
                try {

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                    String line = bufferedReader.readLine();
                    while ((line = bufferedReader.readLine()) != null) {
                        this.Lines.add(Arrays.asList(line.split(splitRegs)));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            thread.start();
            thread.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this.Lines;
    }

    public Dataset<Row> SparkReadCSVFile(SparkSession sparkSession, String filePath) {
        return sparkSession.read().option("header", "true").csv(filePath);
    }
}
