package Main;


import org.apache.spark.sql.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EDA {

    private Dataset<Row> RowDataset;
    SparkSession sparkSession;

    public EDA(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    public EDA(SparkSession sparkSession, Dataset<Row> RowDataset) {

        this.RowDataset = RowDataset;
        this.sparkSession = sparkSession;
    }

    public EDA(SparkSession sparkSession, List<Job> jobList) {
        this.ConvertJobsToDatasetRow(jobList);
        this.sparkSession = sparkSession;
    }

    public Dataset<Row> getRowDataset() {
        return this.RowDataset;
    }

    public List<Row> getRowDataAsList(String[] ColumnsName) {
        return getRowDataset(ColumnsName).collectAsList();
    }

    public Dataset<Row> getRowDataset(String[] ColumnsName) {
        if (ColumnsName.length == 1) {
            return this.RowDataset.select(ColumnsName[0]);
        } else {
            return this.RowDataset.select(ColumnsName[0], Arrays.copyOfRange(ColumnsName, 1, ColumnsName.length));
        }
    }

    public static Dataset<Row> getRowDataset(Dataset<Row> rowDataset, String[] ColumnsName) {
        if (ColumnsName.length == 1) {
            return rowDataset.select(ColumnsName[0]);
        } else {
            return rowDataset.select(ColumnsName[0], Arrays.copyOfRange(ColumnsName, 1, ColumnsName.length));
        }
    }

    public Dataset<Row> ConvertJobsToDatasetRow(List<Job> jobList) {
        this.RowDataset = this.sparkSession.createDataFrame(jobList, Job.class);
        return this.RowDataset;
    }

    public void ShowDataset() {
        this.RowDataset.show();
    }

    public List<Row> GetSummeryDataset() { return this.RowDataset.summary().collectAsList(); }

    public void ShowSummeryDataset() { this.RowDataset.summary().show(); }

    public void ShowDatasetDescribe() {
        this.RowDataset.describe().show();
    }

    public List<Row> GetDatasetDescribe() { return this.RowDataset.describe().collectAsList(); }

    public void RemoveDuplicate() {
        this.RowDataset = this.RowDataset.distinct();
    }

    public void DropNullValue() {
        this.RowDataset = this.RowDataset.na().drop();
    }

    public Map<String, Long> GetSortedCompanyJobsCount(List<Job> jobList) {
        Map<String, Long> result = jobList.stream().collect(Collectors.groupingBy(Job::getCompany, Collectors.counting()));
        System.out.println("SortedCompanyJobsCount\n");
        result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(System.out::println);
        return result;
    }

    public Map<String, Long> GetSortedJobsTitleCount(List<Job> jobList) {
        Map<String, Long> result = jobList.stream().collect(Collectors.groupingBy(Job::getTitle, Collectors.counting()));
        System.out.println("SortedJobsTitleCount\n");
        result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(System.out::println);
        return result;
    }

    public Map<String, Long> GetSortedAreaCount(List<Job> jobList) {
        Map<String, Long> result = jobList.stream().collect(Collectors.groupingBy(Job::getLocation, Collectors.counting()));
        System.out.println("SortedAreaCount\n");
        result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(System.out::println);
        return result;
    }

    public Map<String, Long> GetSortedSkillsCount(List<Job> jobList) {
        List<String> skills = jobList.stream().map(Job::getSkills).flatMap(str -> Arrays.stream(
                str.trim().split(","))).collect(Collectors.toList());
        Map<String, Long> result
                = skills.stream().collect(Collectors.groupingBy(Function.identity(),
                        Collectors.counting()));
        result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(System.out::println);
        return result;
    }

}
